/******************************************************************************************
LAB 03
Gestione interattiva di una scena 3D mediante controllo da mouse e da tastiera.
I modelli geometrici in scena sono mesh poligonali in formato *.obj

CTRL+WHEEL = pan orizzontale della telecamera
SHIFT+WHEEL = pan verticale della telecamera
WHELL = ZOOM IN/OUT se si ? in navigazione, altrimenti agisce sulla trasformazione dell'oggetto
g r s	per le modalit? di lavoro: traslate/rotate/scale
x y z	per l'asse di lavoro
wcs/ocs selezionabili dal menu pop-up

OpenGL Mathematics (GLM) is a header only C++ mathematics library for graphics software 
based on the OpenGL Shading Language (GLSL) specifications.
*******************************************************************************************/

#include <stdio.h>
#include <unistd.h>
#include <math.h>
#include <vector>
#include <string>
#include <unordered_map>

#include <GL/glew.h>
#include <GL/freeglut.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#define WHEEL_UP 3
#define WHEEL_DOWN 4

#define CONTROL_POINTS 5

using namespace std;
using namespace glm;

typedef struct {
	vector<vec3> vertices;
	vector<vec3> normals;
	vector<GLushort> indices;
	GLuint vertexArrayObjID;
	GLuint vertexBufferObjID;
	GLuint normalBufferObjID;
	GLuint indexBufferObjID;
} Mesh;
typedef enum {
	RED_PLASTIC,
	EMERALD,
	BRASS,
	SLATE
} MaterialType;
typedef struct {
	string name;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	GLfloat shiness;
} Material;
typedef struct {
	Mesh mesh;
	MaterialType material;
	GLfloat modelMatrix[16];
	string name;
} Object;

struct {
    vec4 position;
    vec4 target;
    vec4 upVector;
} ViewSetup;
struct {
    float fovY, aspect, nearPlane, farPlane;
} PerspectiveSetup;
typedef enum {
    WIREFRAME,
    FACE_FILL,
    FLAT_SHADING,
    SMOOTH_SHADING,
    CULLING_ON,
    CULLING_OFF,
    CHANGE_TO_WCS,
    CHANGE_TO_OCS
} MenuOption;
enum {
    NAVIGATE,
    CAMERA_MOVING,
    TRANSLATE,
    ROTATE,
    SCALE
} OperationMode;
enum {
    X,
    Y,
    Z
} WorkingAxis;
enum {
    OCS, // Object Coordinate System
    WCS // World Coordinate System
} TransformMode;

static int width = 1366, height = 768;
GLfloat aspectRatio = 16.0f / 9.0f;

static string MeshDir;

// Materiali disponibili
vec3 red_plastic_ambient = { 0.0, 0.0, 0.0 }, red_plastic_diffuse = { 0.5, 0.0, 0.0 }, red_plastic_specular = { 0.7, 0.6, 0.6 }; GLfloat red_plastic_shininess = 32.0;
vec3 brass_ambient = { 0.33, 0.22, 0.03 }, brass_diffuse = { 0.78, 0.57, 0.11 }, brass_specular = { 0.99, 0.91, 0.81 }; GLfloat brass_shininess = 27.8;
vec3 emerald_ambient = { 0.0215, 0.1745, 0.0215 }, emerald_diffuse = { 0.07568, 0.61424, 0.07568 }, emerald_specular = { 0.633, 0.727811, 0.633 }; GLfloat emerald_shininess = 78.8;
vec3 slate_ambient = { 0.02, 0.02, 0.02 }, slate_diffuse = { 0.02, 0.01, 0.01 }, slate_specular{ 0.4, 0.4, 0.4 }; GLfloat slate_shiness = .78125f;

static vector<Object> objects;
static vector<Material> materials;

GLfloat objectsPos[3][3] = { {-5, 1, 0}, {1, 0, 5}, {0, 0, 0} };


static vec4 lightPos = { 5.0f, 5.0f, 5.0f, 1.0f };

// Camera structures
constexpr float CAMERA_ZOOM_SPEED = 0.1f, CAMERA_TRASLATION_SPEED = 0.01f;

static bool movingTrackball = 0;
static int lastMousePosX, lastMousePosY;
vec4 cameraLookAt = {0.0, 0.0, 0.0, 1.0}, currentFocus = {0.0, 0.0, 0.0, 1.0};

float controlPoints[CONTROL_POINTS][3];
bool moveCamera = false;
int motionPortion = 0, cameraTransition = 0;

static int selectedObject = 0;



// Create Bézier curve to move camera
void cameraMotion() {
    // Sphere equation: (x - Px)^2 + (y - Py)^2 + (z - Pz)^2 = r^2
    // Get z: z = Pz +- sqrt(r^2 - (x - Px) - (y - Py))

    float Px = objectsPos[selectedObject][0], Py = objectsPos[selectedObject][1], Pz = objectsPos[selectedObject][2]; // Center of the sphere
    float Dx = abs(Px - ViewSetup.position[0]), Dy = abs(Py - ViewSetup.position[1]), Dz = abs(Pz - ViewSetup.position[2]);

    // 1. Start in current camera position
    controlPoints[0][0] = ViewSetup.position[0];
    controlPoints[0][1] = ViewSetup.position[1];
    controlPoints[0][2] = ViewSetup.position[2];

    // 2. control point on the right
    controlPoints[1][0] = Px >= ViewSetup.position[0] ? (Px - Dz) : (Px + Dz);
    controlPoints[1][1] = -ViewSetup.position[1];
    controlPoints[1][2] = Pz >= ViewSetup.position[2] ? (Pz + Dx) : (Pz - Dx);

    // 3. antipodal control point
    controlPoints[2][0] = Px >= ViewSetup.position[0] ? 1.5 * (Px + Dx) : 1.5 * (Px - Dx);
    controlPoints[2][1] = ViewSetup.position[1];
    controlPoints[2][2] = Pz >= ViewSetup.position[2] ? 1.5 * (Pz + Dz) : 1.5 * (Pz - Dz);

    // 4. control point on the left
    controlPoints[3][0] = Px >= ViewSetup.position[0] ? (Px + Dz) : (Px - Dz);
    controlPoints[3][1] = -ViewSetup.position[1];
    controlPoints[3][2] = Pz >= ViewSetup.position[2] ? (Pz - Dx) : (Pz + Dx);

    // 5. End in current camera position
    controlPoints[4][0] = ViewSetup.position[0];
    controlPoints[4][1] = ViewSetup.position[1];
    controlPoints[4][2] = ViewSetup.position[2];

    moveCamera = true;
    motionPortion = 0;
    cameraTransition = 0;
}

// Gestione delle voci principali del menu
void mainMenuFunction(int option) {
	switch (option) {
        case MenuOption::FLAT_SHADING:
            glShadeModel(GL_FLAT);
            break;
        case MenuOption::SMOOTH_SHADING:
            glShadeModel(GL_SMOOTH);
            break;
        case MenuOption::WIREFRAME:
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            break;
        case MenuOption::FACE_FILL:
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            break;
        case MenuOption::CULLING_ON:
            glEnable(GL_CULL_FACE);
            break;
        case MenuOption::CULLING_OFF:
            glDisable(GL_CULL_FACE);
            break;
        case MenuOption::CHANGE_TO_OCS:
            TransformMode = OCS;
            break;
        case MenuOption::CHANGE_TO_WCS:
            TransformMode = WCS;
            break;
        default:
            break;
	}
}

// Gestione delle voci principali del sub menu per i materiali
void materialMenuFunction(int option) {
    switch (option) {
        case MaterialType::RED_PLASTIC:
            objects[selectedObject].material = MaterialType::RED_PLASTIC;
            break;
        case MaterialType::EMERALD:
            objects[selectedObject].material = MaterialType::EMERALD;
            break;
        case MaterialType::BRASS:
            objects[selectedObject].material = MaterialType::BRASS;
            break;
        case MaterialType::SLATE:
            objects[selectedObject].material = MaterialType::SLATE;
            break;
        default:
            break;
    }
}

// Costruisce i menu openGL
void buildOpenGLMenu() {
    // Material Submenu
	int materialSubMenu = glutCreateMenu(materialMenuFunction);
	glutAddMenuEntry(materials[MaterialType::RED_PLASTIC].name.c_str(), MaterialType::RED_PLASTIC);
	glutAddMenuEntry(materials[MaterialType::EMERALD].name.c_str(), MaterialType::EMERALD);
	glutAddMenuEntry(materials[MaterialType::BRASS].name.c_str(), MaterialType::BRASS);
	glutAddMenuEntry(materials[MaterialType::SLATE].name.c_str(), MaterialType::SLATE);

	// Main Menu
    glutCreateMenu(mainMenuFunction); // richiama mainMenuFunction() alla selezione di una voce menu
	glutAddMenuEntry("Options", -1); //-1 significa che non si vuole gestire questa riga
	glutAddMenuEntry("", -1);
	glutAddMenuEntry("Wireframe", MenuOption::WIREFRAME);
	glutAddMenuEntry("Face fill", MenuOption::FACE_FILL);
	glutAddMenuEntry("Smooth Shading", MenuOption::SMOOTH_SHADING);
	glutAddMenuEntry("Flat Shading", MenuOption::FLAT_SHADING);
	glutAddMenuEntry("Culling: ON", MenuOption::CULLING_ON);
	glutAddMenuEntry("Culling: OFF", MenuOption::CULLING_OFF);
	glutAddSubMenu("Material", materialSubMenu);
	glutAddMenuEntry("World coordinate system", MenuOption::CHANGE_TO_WCS);
	glutAddMenuEntry("Object coordinate system", MenuOption::CHANGE_TO_OCS);
	glutAttachMenu(GLUT_RIGHT_BUTTON);
}

// Trackball: converte un punto 2D sullo schermo in un punto 3D sulla trackball
vec3 getTrackBallPoint(float x, float y) {
	float zTemp;
	vec3 point;
	//map to [-1;1]
	point.x = (2.0f * x - width) / width;
	point.y = (height - 2.0f * y) / height;

	zTemp = 1.0f - pow(point.x, 2.0) - pow(point.y, 2.0);
	point.z = (zTemp > 0.0f) ? sqrt(zTemp) : 0.0;
	return point;
}

void zoom(float dir) {
    // P1 = P0 + tv
    float v[3];
    float t = 0.1f * dir;

    for (int i = 0; i < 3; i++) {
        v[i] = currentFocus[i] - ViewSetup.position[i]; // v = vector from camera to focused point
        v[i] *= t; // tv = multiply vector by direction
        ViewSetup.position[i] += v[i]; // P1 = camera position + vector to origin multiplied by some value
    }
}

void horizontalPan(float dir) {
    vec3 lookAt(currentFocus), position(ViewSetup.position), upVector(ViewSetup.upVector);

    vec3 direction = lookAt - position;
    vec3 normalToPlane = normalize(cross(direction, upVector));
    vec4 increment(normalToPlane.x * dir, normalToPlane.y * dir, normalToPlane.z * dir, 1.0);

    currentFocus += increment;
    ViewSetup.position += increment;
}

void verticalPan(float dir) {
    vec3 lookAt(currentFocus), position(ViewSetup.position), upVector(ViewSetup.upVector);

    vec3 direction = lookAt - position;
    vec3 normalToPlane = normalize(cross(direction, upVector));
    vec3 normalToNormalToPlane = -normalize(cross(direction, normalToPlane));
    vec4 increment(normalToNormalToPlane.x * dir, normalToNormalToPlane.y * dir, normalToNormalToPlane.z * dir, 1.0);

    currentFocus += increment;
    ViewSetup.position += increment;
}

void modifyModelMatrix(vec4 translation_vector, vec4 rotation_vector, GLfloat angle, GLfloat scale_factor) {
	glPushMatrix();
        glLoadIdentity();

        switch (TransformMode) {
            case WCS:
                glTranslatef(-objectsPos[selectedObject][0], -objectsPos[selectedObject][1], -objectsPos[selectedObject][2]); // Starting object position
                    glRotatef(angle, rotation_vector.x, rotation_vector.y, rotation_vector.z);
                    glScalef(scale_factor, scale_factor, scale_factor);
                    glTranslatef(translation_vector.x, translation_vector.y, translation_vector.z);
                glTranslatef(objectsPos[selectedObject][0], objectsPos[selectedObject][1], objectsPos[selectedObject][2]);

                glMultMatrixf(objects[selectedObject].modelMatrix);

                break;
            case OCS:
                glMultMatrixf(objects[selectedObject].modelMatrix);

                glRotatef(angle, rotation_vector.x, rotation_vector.y, rotation_vector.z);
                glScalef(scale_factor, scale_factor, scale_factor);
                glTranslatef(translation_vector.x, translation_vector.y, translation_vector.z);

                break;
        }

        glGetFloatv(GL_MODELVIEW_MATRIX, objects[selectedObject].modelMatrix);
	glPopMatrix();
}

// Genera i buffer per la mesh in input e ne salva i puntatori di openGL
void generateAndLoadBuffers(Mesh *mesh) {
	// Genero 1 Vertex Array Object
	glGenVertexArrays(1, &mesh->vertexArrayObjID);
	glBindVertexArray(mesh->vertexArrayObjID);

	// Genero 1 Vertex Buffer Object per i vertici
	glGenBuffers(1, &mesh->vertexBufferObjID);
	glBindBuffer(GL_ARRAY_BUFFER, mesh->vertexBufferObjID);
	glBufferData(GL_ARRAY_BUFFER, mesh->vertices.size() * sizeof(vec3), &mesh->vertices[0], GL_STATIC_DRAW);
	glVertexPointer(
		3,                  // size
		GL_FLOAT,           // type
		0,                  // stride
		(void*)0            // array buffer offset
	);

	// Caricamento normali in memoria
    glGenBuffers(1, &mesh->normalBufferObjID);
    glBindBuffer(GL_ARRAY_BUFFER, mesh->normalBufferObjID);
    glBufferData(GL_ARRAY_BUFFER, mesh->normals.size() * sizeof(vec3), &mesh->normals[0], GL_STATIC_DRAW);
    glNormalPointer(
            GL_FLOAT,           // type
            0,                  // stride
            (void*)0            // array buffer offset
    );

	// Genero 1 Element Buffer Object per gli indici, Nota: GL_ELEMENT_ARRAY_BUFFER
	glGenBuffers(1, &mesh->indexBufferObjID);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh->indexBufferObjID);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh->indices.size() * sizeof(GLshort), mesh->indices.data(), GL_STATIC_DRAW);
}

// Legge un file obj ed inizializza i vector della mesh in input
void loadObjFile(string file_path, Mesh* mesh) {
	FILE * file = fopen(file_path.c_str(), "r");
	if (file == NULL) {
		printf("Impossible to open the file !\n");
		return;
	}
	char lineHeader[128];
	while (fscanf(file, "%s", lineHeader) != EOF) {
		if (strcmp(lineHeader, "v") == 0) {
			vec3 vertex;
			fscanf(file, " %f %f %f\n", &vertex.x, &vertex.y, &vertex.z);
			mesh->vertices.push_back(vertex);
		}
		else if (strcmp(lineHeader, "f") == 0) {
			std::string vertex1, vertex2, vertex3;
			GLuint a, b, c;
			fscanf(file, " %d %d %d\n", &a, &b, &c);
			a--; b--; c--;
			mesh->indices.push_back(a); mesh->indices.push_back(b); mesh->indices.push_back(c);
		}
	}

	// Calcolo normali
    mesh->normals.resize(mesh->vertices.size(), vec3(0.0,0.0,0.0)); // Alloco lo spazio necessario alle normali, che è uguale al numero di vertici della mesh
    for (int i = 0; i < mesh->indices.size(); i += 3) {
        GLuint a = mesh->indices[i];
        GLuint b = mesh->indices[i + 1];
        GLuint c = mesh->indices[i + 2];

        vec3 normal = normalize(cross(mesh->vertices[b] - mesh->vertices[a], mesh->vertices[c]- mesh->vertices[a]));

        mesh->normals[a] += normal;
        mesh->normals[b] += normal;
        mesh->normals[c] += normal;
    }

    for (int i = 0; i < mesh->normals.size(); i++) // Normalizzazione in quanto si è fatta la media delle normali di ogni vertice per le coppie di lati
        mesh->normals[i] = normalize(mesh->normals[i]);
}

// Disegna l'origine del assi
void drawAxis(float scale, int drawLetters) {
	glPushMatrix();
	glScalef(scale, scale, scale);
	glBegin(GL_LINES);

	glColor4d(1.0, 0.0, 0.0, 1.0);
	if (drawLetters) {
		glVertex3f(.8f, 0.05f, 0.0);  glVertex3f(1.0, 0.25f, 0.0); /* Letter X */
		glVertex3f(0.8f, .25f, 0.0);  glVertex3f(1.0, 0.05f, 0.0);
	}
	glVertex3f(0.0, 0.0, 0.0);  glVertex3f(1.0, 0.0, 0.0); /* X axis      */


	glColor4d(0.0, 1.0, 0.0, 1.0);
	if (drawLetters) {
		glVertex3f(0.10f, 0.8f, 0.0);   glVertex3f(0.10f, 0.90f, 0.0); /* Letter Y */
		glVertex3f(0.10f, 0.90f, 0.0);  glVertex3f(0.05, 1.0, 0.0);
		glVertex3f(0.10f, 0.90f, 0.0);  glVertex3f(0.15, 1.0, 0.0);
	}
	glVertex3f(0.0, 0.0, 0.0);  glVertex3f(0.0, 1.0, 0.0); /* Y axis      */


	glColor4d(0.0, 0.0, 1.0, 1.0);
	if (drawLetters) {
		glVertex3f(0.05f, 0, 0.8f);  glVertex3f(0.20f, 0, 0.8f); /* Letter Z*/
		glVertex3f(0.20f, 0, 0.8f);  glVertex3f(0.05, 0, 1.0);
		glVertex3f(0.05f, 0, 1.0);   glVertex3f(0.20, 0, 1.0);
	}
	glVertex3f(0.0, 0.0, 0.0);  glVertex3f(0.0, 0.0, 1.0); /* Z axis    */

	glEnd();
	glPopMatrix();
}

// Disegna la griglia del piano xz (white)
void drawGrid(float scale, int dimension) {
	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glPushMatrix();
	glScalef(scale, scale, scale);
	glBegin(GL_LINES);
	for (int x = -dimension; x < dimension; x++) {
		glVertex3f(x, 0.0f, -dimension);
		glVertex3f(x, 0.0f, dimension);
	}
	for (int z = -dimension; z < dimension; z++) {
		glVertex3f(-dimension, 0.0f, z);
		glVertex3f(dimension, 0.0f, z);
	}
	glEnd();
	glPopMatrix();
}

// Keyboard: g translate r rotate s scale x,y,z axis esc
void keyboardListener(unsigned char key, int x, int y) {
    switch (key) {
        // Selezione della modalità di trasformazione
        case 'g':
            OperationMode = TRANSLATE;
            break;
        case 'r':
            OperationMode = ROTATE;
            break;
        case 's':
            OperationMode = SCALE;
            break;
        case ' ':
            OperationMode = CAMERA_MOVING;
            if(!moveCamera) cameraMotion();
            break;
        case 27:
            glutLeaveMainLoop();
            break;
        case 'x':
            WorkingAxis = X;
            break;
        case 'y':
            WorkingAxis = Y;
            break;
        case 'z':
            WorkingAxis = Z;
            break;
        default:
            break;
    }
    glutPostRedisplay();
}

// Special key arrow: select active object (arrows left,right)
void special(int key, int x, int y) {
    switch (key) {
        case GLUT_KEY_LEFT:
            if(!moveCamera)
                selectedObject = selectedObject - 1 < 0 ? objects.size() - 1 : selectedObject - 1;
            break;
        case GLUT_KEY_RIGHT:
            if(!moveCamera)
                selectedObject = (selectedObject + 1) % objects.size();
            break;
        case GLUT_KEY_DOWN:
            zoom(-CAMERA_ZOOM_SPEED);
            break;
        case GLUT_KEY_UP:
            zoom(CAMERA_ZOOM_SPEED);
            break;
        default:
            break;
    }
    glutPostRedisplay();
}

// Trackball: effettua la rotazione del vettore posizione sulla trackball
void mouseMotionListener(int x, int y) {
    // Spostamento su trackball del vettore posizione Camera
    if (!movingTrackball) return;
    vec3 destination = getTrackBallPoint(x, y);
    vec3 origin = getTrackBallPoint(lastMousePosX, lastMousePosY);
    float dx, dy, dz;
    dx = destination.x - origin.x;
    dy = destination.y - origin.y;
    dz = destination.z - origin.z;
    if (dx || dy || dz) {
        // rotation angle = acos( (v dot w) / (len(v) * len(w)) ) o approssimato da ||dest-orig||;
        float angle = sqrt(dx * dx + dy * dy + dz * dz) * (180.0 / pi<float>());
        // rotation axis = (dest vec orig) / (len(dest vec orig))
        vec3 rotation_vec = cross(origin, destination);
        // calcolo del vettore direzione w = C - A
        vec4 direction = ViewSetup.position - ViewSetup.target;
        // rotazione del vettore direzione w
        // determinazione della nuova posizione della camera
        ViewSetup.position = ViewSetup.target + rotate(mat4(1.0f), radians(-angle), rotation_vec) * direction;
    }
    lastMousePosX = x; lastMousePosY = y;
    glutPostRedisplay();
}

void mouseListener(int button, int state, int x, int y) {
    if(moveCamera) return;

    glutPostRedisplay();
    int modifiers = glutGetModifiers();
    if (modifiers == GLUT_ACTIVE_SHIFT) {
        switch (button) {
            case WHEEL_UP:
                verticalPan(CAMERA_TRASLATION_SPEED);
                break;
            case WHEEL_DOWN:
                verticalPan(-CAMERA_TRASLATION_SPEED);
                break;
        }
        return;
    } else if (modifiers == GLUT_ACTIVE_CTRL) {
        switch (button) {
            case WHEEL_UP:
                horizontalPan(CAMERA_TRASLATION_SPEED);
                break;
            case WHEEL_DOWN:
                horizontalPan(-CAMERA_TRASLATION_SPEED);
                break;
        }
        return;
    }

    vec4 axis;
    float amount = 0.1f;
    // Imposto il valore della trasformazione
    switch (button) {
        case WHEEL_UP:// scroll wheel up
            amount *= 1;
            break;
        case WHEEL_DOWN:// scroll wheel down
            amount *= -1;
            break;
        case GLUT_LEFT_BUTTON:
            if (state == GLUT_DOWN)
                movingTrackball = true;
            if (state == GLUT_UP)
                movingTrackball = false;
            OperationMode = NAVIGATE;
            lastMousePosX = x;
            lastMousePosY = y;
            break;
        default:
            break;
    }
    // Selezione dell'asse per le trasformazioni
    switch (WorkingAxis) {
        case X:
            axis = vec4(1.0, 0.0, 0.0, 0.0);
            break;
        case Y:
            axis = vec4(0.0, 1.0, 0.0, 0.0);
            break;
        case Z:
            axis = vec4(0.0, 0.0, 1.0, 0.0);
            break;
        default:
            break;
    }

    switch (OperationMode) {
        case TRANSLATE:
            modifyModelMatrix(axis * amount, axis, 0.0f, 1.0f);
            break;
        case ROTATE:
            modifyModelMatrix(vec4(0), axis, amount * 20.0f, 1.0f);
            break;
        case SCALE:
            modifyModelMatrix(vec4(0), axis, 0.0f, 1.0f + amount);
            break;
        case NAVIGATE:
            // Wheel reports as button 3(scroll up) and button 4(scroll down)
            if (button == WHEEL_UP)
                zoom(CAMERA_ZOOM_SPEED);
            else if (button == WHEEL_DOWN)
                zoom(-CAMERA_ZOOM_SPEED);
            break;
        default:
            break;
    }
}

bool isFocused(float *A) {
    return A[0] == currentFocus.x && A[1] == currentFocus.y && A[2] == currentFocus.z;
}

void setupLookAt() {
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(PerspectiveSetup.fovY, PerspectiveSetup.aspect, PerspectiveSetup.nearPlane, PerspectiveSetup.farPlane);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    gluLookAt(ViewSetup.position.x, ViewSetup.position.y, ViewSetup.position.z,
              cameraLookAt.x, cameraLookAt.y, cameraLookAt.z,
              ViewSetup.upVector.x, ViewSetup.upVector.y, ViewSetup.upVector.z);
}

void resize(int w, int h) {
    if (h == 0)	return; // Window is minimized
    int w2 = h * aspectRatio;           // width is adjusted for aspect ratio
    int left = (w - w2) / 2;
    // Set Viewport to window dimensions
    glViewport(left, 0, w2, h);
    width = w;
    height = h;

    setupLookAt();
}

// Logging to screen
void printInfo(vector<string> lines) {
    string str;
    for (unsigned int i = 0; i < lines.size(); i++) {
        int total_width = 0, total_height = glutStrokeHeight(GLUT_STROKE_ROMAN);

        for (unsigned int j = 0; j < lines[i].length(); j++)
            total_width += glutStrokeWidth(GLUT_STROKE_ROMAN, lines[i][j]);
        glPushMatrix();
        glTranslatef(10.0, 10.0 + total_height * i * 0.2, 0.0);
        glScalef(0.2, 0.2, 1.0);
        glColor4f(0.0, 0.0, 0.0, 1.0);
        glLineWidth(3.0f);

        str = lines[i];
        glRasterPos3f(0.0, 0.0, 0.0);
        for (unsigned int j = 0; j < str.length(); j++)
            glutStrokeCharacter(GLUT_STROKE_ROMAN, str[j]);

        glPopMatrix();
        glPushMatrix();
        glTranslatef(10.0, 10.0 + total_height * i * 0.2, 0.0);
        glScalef(0.2, 0.2, 1.0);
        glColor4f(0.0, 0.0, 1.0, 1.0);
        glLineWidth(1.0f);

        str = lines[i];
        glRasterPos3f(0.0, 0.0, 0.0);
        for (unsigned int j = 0; j < str.length(); j++)
            glutStrokeCharacter(GLUT_STROKE_ROMAN, str[j]);

        glPopMatrix();
    }
}

void printToScreen() {
    string axis = "Axis: ";
    string mode = "Navigate/Edit: ";
    string obj = "Object: " + objects[selectedObject].name;
    string ref = "System WCS/OCS: ";
    string mat = "Material: " + materials[objects[selectedObject].material].name;

    switch (WorkingAxis) {
        case X:
            axis += "X";
            break;
        case Y:
            axis += "Y";
            break;
        case Z:
            axis += "Z";
            break;
    }

    switch (OperationMode) {
        case TRANSLATE:
            mode += "Translate";
            break;
        case ROTATE:
            mode += "Rotate";
            break;
        case SCALE:
            mode += "Scale";
            break;
        case CAMERA_MOVING:
            mode += "Camera Moving";
            break;
        case NAVIGATE:
            mode += "Navigate";
            break;
    }

    switch (TransformMode) {
        case OCS:
            ref += "OCS";
            break;
        case WCS:
            ref += "WCS";
            break;
    }

    vector<string> lines;
    lines.push_back(mat);
    lines.push_back(obj);
    lines.push_back(axis);
    lines.push_back(mode);
    lines.push_back(ref);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    gluOrtho2D(0, height * aspectRatio, 0, height);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    glDisable(GL_DEPTH_TEST);
    glDisable(GL_LIGHTING);
    printInfo(lines);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);

    resize(width, height);
}

void deCasteljau(float t) {
    float temp[10][3];
    int i;

    for (i = 0; i < CONTROL_POINTS; i++) { // Copio i punti per non sovrascriverli
        temp[i][0] = controlPoints[i][0];
        temp[i][1] = controlPoints[i][1];
        temp[i][2] = controlPoints[i][2];
    }

    for (i = 1; i < CONTROL_POINTS; i++)
        for (int j = 0; j < CONTROL_POINTS - i; j++) { // Interpolazione lineare (lerp)
            temp[j][0] = t * temp[j+1][0] + (1-t) * temp[j][0];
            temp[j][1] = t * temp[j+1][1] + (1-t) * temp[j][1];
            temp[j][2] = t * temp[j+1][2] + (1-t) * temp[j][2];
        }

    for (i = 0; i < 3; i++)
        ViewSetup.position[i] = temp[0][i];
}

void display() {
    // Background color
    glClearColor(0.75, 1.0, 0.95, 1);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    //Static scene elements
    glDisable(GL_LIGHTING);
    glLightfv(GL_LIGHT0, GL_POSITION, value_ptr(lightPos));

    glPushMatrix();
    glTranslatef(lightPos.x, lightPos.y, lightPos.z);
    glColor4d(1, 1, 1, 1);
    glutSolidSphere(0.2, 10, 10); // Light ball
    glPopMatrix();

    drawAxis(3.0, 1); // The central Axis point of reference
    drawGrid(10.0, 100); // The horizontal grid
    glEnable(GL_LIGHTING);

    for(int i = 0; i < objects.size(); i++) {
        glPushMatrix();
            glTranslatef(objectsPos[i][0], objectsPos[i][1], objectsPos[i][2]);
            glMultMatrixf(objects[i].modelMatrix);

            glDisable(GL_LIGHTING);
            drawAxis(1.0, 0);
            glEnable(GL_LIGHTING);

            // Set material
            Material mat = materials[objects[i].material];
            glLightfv(GL_LIGHT0, GL_AMBIENT, value_ptr(mat.ambient));
            glMaterialfv(GL_FRONT, GL_DIFFUSE, value_ptr(mat.diffuse));
            glMaterialfv(GL_FRONT, GL_SPECULAR, value_ptr(mat.specular));
            glMaterialf(GL_FRONT, GL_SHININESS, mat.shiness);

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_NORMAL_ARRAY);
            glBindVertexArray(objects[i].mesh.vertexArrayObjID);
            glDrawElements(GL_TRIANGLES, objects[i].mesh.indices.size(), GL_UNSIGNED_SHORT, (void *) 0);
        glPopMatrix();
    }

    if(moveCamera) {
        if(motionPortion == 0 && cameraTransition <= 50) { // Effettua la transizione dal punto che si sta guardando al nuovo punto
            ++cameraTransition;
            if(!isFocused(objectsPos[selectedObject])) { // If selected object is on the focused point do not make initial transition
                vec4 diff = vec4(objectsPos[selectedObject][0], objectsPos[selectedObject][1], objectsPos[selectedObject][2], 1.0) - currentFocus;
                cameraLookAt = vec4(
                        currentFocus.x + cameraTransition * diff.x / 50,
                        currentFocus.y + cameraTransition * diff.y / 50,
                        currentFocus.z + cameraTransition * diff.z / 50,
                        1.0
                );
            } else
                cameraTransition += 50;
        } else if(motionPortion > 200 && cameraTransition > 0) { // Effettua la transizione dal punto in cui sta puntando al momento la camera al punto puntato prima dell'animazione
            --cameraTransition;
            vec4 diff = vec4(objectsPos[selectedObject][0], objectsPos[selectedObject][1], objectsPos[selectedObject][2], 1.0) - currentFocus;
            cameraLookAt = vec4(
                    currentFocus.x + cameraTransition * diff.x / 50,
                    currentFocus.y + cameraTransition * diff.y / 50,
                    currentFocus.z + cameraTransition * diff.z / 50,
                    1.0
            );

            if(cameraTransition == 1) {
                moveCamera = false;
                OperationMode = NAVIGATE;
            } else
                moveCamera = true;
        } else // Move the camera along Bezier curve
            deCasteljau((float) (motionPortion++ / 200.0));
    } else
        cameraLookAt = currentFocus;

    printToScreen();
    glutSwapBuffers();
    glutPostRedisplay();
}

// Inizializzazione generale
void init() {
    // Set current working directory
    char cwd[PATH_MAX];
    getcwd(cwd, sizeof(cwd));
    strcat(cwd, "/../");
    string CWD(cwd);
    MeshDir = CWD + "Mesh/";

    // Default render settings
    OperationMode = NAVIGATE;

    glEnable(GL_DEPTH_TEST);	// Hidden surface removal
    glCullFace(GL_BACK);	// remove faces facing the background

    glEnable(GL_LINE_SMOOTH);
    glShadeModel(GL_FLAT);
    //FOG Setup for nice backgound transition
    glEnable(GL_FOG);
    glFogi(GL_FOG_MODE, GL_LINEAR);
    GLfloat fog_color[4] = { 0.5,0.5,0.5,1.0 };
    glFogfv(GL_FOG_COLOR, fog_color);
    glFogf(GL_FOG_START, 50.0f);
    glFogf(GL_FOG_END, 500.0f);
    // Light Setup
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);


    // Materials setup
    materials.resize(4);
    materials[MaterialType::RED_PLASTIC].name = "Red Plastic";
    materials[MaterialType::RED_PLASTIC].ambient = red_plastic_ambient;
    materials[MaterialType::RED_PLASTIC].diffuse = red_plastic_diffuse;
    materials[MaterialType::RED_PLASTIC].specular = red_plastic_specular;
    materials[MaterialType::RED_PLASTIC].shiness = red_plastic_shininess;

    materials[MaterialType::EMERALD].name = "Emerald";
    materials[MaterialType::EMERALD].ambient = emerald_ambient;
    materials[MaterialType::EMERALD].diffuse = emerald_diffuse;
    materials[MaterialType::EMERALD].specular = emerald_specular;
    materials[MaterialType::EMERALD].shiness = emerald_shininess;

    materials[MaterialType::BRASS].name = "Brass";
    materials[MaterialType::BRASS].ambient = brass_ambient;
    materials[MaterialType::BRASS].diffuse = brass_diffuse;
    materials[MaterialType::BRASS].specular = brass_specular;
    materials[MaterialType::BRASS].shiness = brass_shininess;

    materials[MaterialType::SLATE].name = "Slate";
    materials[MaterialType::SLATE].ambient = slate_ambient;
    materials[MaterialType::SLATE].diffuse = slate_diffuse;
    materials[MaterialType::SLATE].specular = slate_specular;
    materials[MaterialType::SLATE].shiness = slate_shiness;

    // Camera Setup
    ViewSetup = {};
    ViewSetup.position = vec4(7.0, 4.0, 7.0, 1.0);
    ViewSetup.target = currentFocus;
    ViewSetup.upVector = vec4(0.0, 1.0, 0.0, 0.0);
    PerspectiveSetup = {};
    PerspectiveSetup.aspect = (GLfloat) width / (GLfloat) height;
    PerspectiveSetup.fovY = 45.0f;
    PerspectiveSetup.farPlane = 2000.0f;
    PerspectiveSetup.nearPlane = 1.0f;


    // Load all the meshes

    // Airplane
    Mesh airplane = {};
    loadObjFile(MeshDir + "airplane.obj", &airplane);
    generateAndLoadBuffers(&airplane);
    // Object Setup
    Object obj1 = {}; // Mesh, Material, Model Matrix, Name
    obj1.mesh = airplane;
    obj1.material = MaterialType::RED_PLASTIC;
    glLoadIdentity();
    glGetFloatv(GL_MODELVIEW_MATRIX, obj1.modelMatrix);
    obj1.name = "Airplane";
    objects.push_back(obj1);

    // Triceratops
    Mesh triceratops = {};
    loadObjFile(MeshDir + "triceratops.obj", &triceratops);
    generateAndLoadBuffers(&triceratops);
    // Object Setup
    Object obj2 = {}; // Mesh, Material, Model Matrix, Name
    obj2.mesh = triceratops;
    obj2.material = MaterialType::RED_PLASTIC;
    glLoadIdentity();
    glGetFloatv(GL_MODELVIEW_MATRIX, obj2.modelMatrix);
    obj2.name = "Triceratops";
    objects.push_back(obj2);

    // Cow
    Mesh cow = {};
    loadObjFile(MeshDir + "cow.obj", &cow);
    generateAndLoadBuffers(&cow);
    // Object Setup
    Object obj3 = {}; // Mesh, Material, Model Matrix, Name
    obj3.mesh = cow;
    obj3.material = MaterialType::RED_PLASTIC;
    glLoadIdentity();
    glGetFloatv(GL_MODELVIEW_MATRIX, obj3.modelMatrix);
    obj3.name = "Cow";
    objects.push_back(obj3);
}

int main(int argc, char** argv) {
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_RGBA | GLUT_DEPTH | GLUT_DOUBLE);
    glutInitWindowSize(width, height);
    glutInitWindowPosition(100, 100);
    glutCreateWindow("Model Viewer");

    glutDisplayFunc(display);
    glutReshapeFunc(resize);
    glutKeyboardFunc(keyboardListener);
    glutMouseFunc(mouseListener);
    glutMotionFunc(mouseMotionListener);
    glutSpecialFunc(special);

    glewExperimental = GL_TRUE;
    glewInit();

    init();
    buildOpenGLMenu();

    glutMainLoop();

    return 0;
}