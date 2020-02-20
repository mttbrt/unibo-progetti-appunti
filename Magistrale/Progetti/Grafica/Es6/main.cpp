/******************************************************************************************
LAB 06
Gestione interattiva di una scena 3D mediante controllo da mouse e da tastiera.
I modelli geometrici in scena sono mesh poligonali caricati da file in formato *.obj,  
con associata una parametrizzazione (sfere e cubo), oggetti poligonali creati in modo procedurale
(toroide).  
W/w		incrementa/decrementa NumWrap toro
N/n		incrementa/decrementa NumPerWrap toro

Si possono ereditare dalla esercitazione 3 i seguenti tool:
CTRL+WHEEL = pan orizzontale della telecamera
SHIFT+WHEEL = pan verticale della telecamera
WHELL = ZOOM IN/OUT se si � in navigazione, altrimenti agisce sulla trasformazione dell'oggetto
g r s	per le modalit� di lavoro: traslate/rotate/scale
x y z	per l'asse di lavoro
wcs/ocs selezionabili dal menu pop-up

OpenGL Mathematics (GLM) is a header only C++ mathematics library for graphics software
based on the OpenGL Shading Language (GLSL) specifications.
*******************************************************************************************/

#include "main.h"

static string MeshDir, TextureDir, ShaderDir;
static Object Axis, Grid;
static vector<Object> objects;
static vector<Material> materials;
static int selectedObj = 0;
GLfloat objectsPos[6][3] = { {-5., 0., -5.}, {-5., 0., 5.}, {5., 0., 5.}, {3., 0., -6.}, {6., 0., -3.}, {0., -2., 0.} };

struct {
	// Variables controlling the torus mesh resolution
	int NumWraps = 10;
	int NumPerWrap = 8;
	// Variables controlling the size of the torus
	float MajorRadius = 3.0;
	float MinorRadius = 1.0;
	int torus_index;
} TorusSetup;

// Materiali disponibili
glm::vec3 red_plastic_ambient = { 0.1, 0.0, 0.0}, red_plastic_diffuse = { 0.6, 0.1, 0.1}, red_plastic_specular = { 0.7, 0.6, 0.6}; GLfloat red_plastic_shininess = 32.0f;
glm::vec3 brass_ambient = { 0.1, 0.06, 0.015}, brass_diffuse = { 0.78, 0.57, 0.11}, brass_specular = { 0.99, 0.91, 0.81}; GLfloat brass_shininess = 27.8f;
glm::vec3 emerald_ambient = { 0.0215, 0.04745, 0.0215}, emerald_diffuse = { 0.07568, 0.61424, 0.07568}, emerald_specular = { 0.633, 0.727811, 0.633}; GLfloat emerald_shininess = 78.8f;
glm::vec3 slate_ambient = { 0.02, 0.02, 0.02}, slate_diffuse = { 0.1, 0.1, 0.1}, slate_specular{ 0.4, 0.4, 0.4}; GLfloat slate_shininess = 1.78125f;

typedef struct {
	glm::vec3 position; 
	glm::vec3 scale;
	glm::vec3 color;
	GLfloat power;
} pointLight;

static pointLight light;

// Camera structures
glm::vec4 cameraLookAt = {0.0, 0.0, 0.0, 1.0}, currentFocus = {0.0, 0.0, 0.0, 1.0};

struct {
	glm::vec4 position;
	glm::vec4 target;
	glm::vec4 upVector;
} ViewSetup;

struct {
	float fovY, aspect, near_plane, far_plane;
} PerspectiveSetup;

typedef enum {
	WIRE_FRAME,
	FACE_FILL,
	CULLING_ON,
	CULLING_OFF,
	CHANGE_TO_WCS,
	CHANGE_TO_OCS,
} MenuOption;

enum {
	NAVIGATION,
	CAMERA_MOVING,
	TRASLATING,
	ROTATING,
	SCALING
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

static bool movingTrackball = 0;
static int lastMousePosY;
static int lastMousePosX;

//Shaders Uniforms 
static vector<LightShaderUniform> lightUniforms; // for shaders with light
static vector<BaseShaderUniform> baseUniforms; // for ALL shaders
static vector<GLuint> shadersIDs; //Pointers to the shader programs
static GLuint currentTime; // time in sine wave

int main(int argc, char** argv) {
	GLboolean GlewInitResult;
	glutInit(&argc, argv);
	glutSetOption(GLUT_MULTISAMPLE, 4);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH | GLUT_MULTISAMPLE);
	glutInitWindowSize(WindowWidth, WindowHeight);
	glutInitWindowPosition(100, 100);
	glutCreateWindow("Model Viewer with Shaders ");

	glutDisplayFunc(display);
	glutReshapeFunc(resize);
	glutKeyboardFunc(keyboardListener);
	glutMouseFunc(mouseListener);
	glutMotionFunc(mouseMotionListener);
	glutSpecialFunc(special);

	glewExperimental = GL_TRUE;
	GlewInitResult = glewInit();
	if (GLEW_OK != GlewInitResult) {
		fprintf(
			stderr,
			"ERROR: %s\n",
			glewGetErrorString(GlewInitResult)
		);
		exit(EXIT_FAILURE);
	}
	fprintf(
		stdout,
		"INFO: OpenGL Version: %s\n",
		glGetString(GL_VERSION)
	);

	init();
	buildOpenGLMenu();

	glutMainLoop();

	return 1;
}

void initLightObject() {
	Mesh sphereS = {};
	loadObjFile(MeshDir + "sphere_n_t_smooth.obj", &sphereS);
    generateAndLoadBuffers(true, &sphereS);
	Object obj = {};
	obj.mesh = sphereS;
	obj.material = MaterialType::NO_MATERIAL;
	obj.shading = ShadingType::PASS_THROUGH; 
	obj.name = "light";
	obj.M = glm::scale(glm::translate(glm::mat4(1), light.position), light.scale);
	objects.push_back(obj);
}

void initTexturedPlane() {
    //Textured plane (2 triangles) with a procedural texture but no material, use a texture-only shader
	Mesh surface = {};
	surface.vertices = { {-2,0,-2}, {-2,0,2}, {2,0,2}, {2,0,-2}, {-2,0,-2}, {2,0,2} };
	surface.normals = { {0,1,0}, {0,1,0}, {0,1,0}, {0,1,0}, {0,1,0}, {0,1,0} };
	//Tex coords are out of bound to show the GL_REPEAT effect
	//surface.texCoords = { {-4,-4}, {-4,+4}, {4,4}, {4,-4}, {-4,-4}, {4,4} }; // out of bound UVs
	surface.texCoords = { {0,0}, {0,1}, {1,1}, {1,0}, {0,0}, {1,1} }; // standard UVs
    generateAndLoadBuffers(true, &surface);
	Object obj0 = {};
	obj0.mesh = surface;
	obj0.material = MaterialType::NO_MATERIAL;
	obj0.shading = ShadingType::TEXTURE_ONLY;

	/////////////////////////////////////////////////////////////////////////
	//  Compute checkboard procedural_texture image of dimension width x width x 3 (RGB)
	/////////////////////////////////////////////////////////////////////////
	GLubyte image[64][64][3];
	int i, j, c;
	for (i = 0; i < 64; i++) {
		for (j = 0; j < 64; j++) {
			c = ((((i & 0x8) == 0) ^ (((j & 0x8)) == 0))) * 255;
			image[i][j][0] = (GLubyte)c;
			image[i][j][1] = (GLubyte)c;
			image[i][j][2] = (GLubyte)c;
		}
	}
	/////////////////////////////////////////
	glGenTextures(1, &obj0.textureID);
	glBindTexture(GL_TEXTURE_2D, obj0.textureID);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexImage2D(GL_TEXTURE_2D,  //the target
		0, // the mip map level we want to generate
		GL_RGB, // the format of the texture
		64, //texture_size, width
		64, //texture_size, heigth
		0,  // border, leave 0
		GL_RGB, // we assume is a RGB color image with 24 bit depth per pixel
		GL_UNSIGNED_BYTE, // the data type
		image);
	obj0.name = "WavingPlane";
	obj0.M = glm::translate(glm::mat4(1), glm::vec3(objectsPos[0][0], objectsPos[0][1], objectsPos[0][2]));
	objects.push_back(obj0);
}

void initCube() {
	Mesh cube = {};
	loadObjFile(MeshDir + "cube_n_t_flat.obj", &cube);
    generateAndLoadBuffers(true, &cube);
	Object obj1 = {};
	obj1.mesh = cube;
	obj1.material = MaterialType::NO_MATERIAL;
	obj1.shading = ShadingType::TEXTURE_ONLY;
	obj1.textureID = loadTexture(TextureDir + "cube_tex.jpg");
	obj1.name = "Textured Cube";
	obj1.M = glm::translate(glm::mat4(1), glm::vec3(objectsPos[1][0], objectsPos[1][1], objectsPos[1][2]));
	objects.push_back(obj1);
}

void initTorus() {
	Mesh torus = {};
    computeTorus(&torus);
    generateAndLoadBuffers(true, &torus);
	// Object Setup with NO texture, will use the light shader and a material for color 
	Object obj2 = {};
	obj2.mesh = torus;
	obj2.material = MaterialType::BRASS;
	obj2.shading = ShadingType::GOURAUD;
	obj2.name = "Torus";
	obj2.M = glm::translate(glm::mat4(1), glm::vec3(objectsPos[2][0], objectsPos[2][1], objectsPos[2][2]));
	objects.push_back(obj2);
	TorusSetup.torus_index = objects.size() - 1;
}

void initSphereFlat() {
	Mesh sphereF = {};
	loadObjFile(MeshDir + "sphere_n_t_flat.obj", &sphereF);
    generateAndLoadBuffers(true, &sphereF);
	// Object Setup with NO texture, will use the light shader and a material for color and light behavior
	Object obj3 = {};
	obj3.mesh = sphereF;
	obj3.material = MaterialType::EMERALD;
	obj3.shading = ShadingType::GOURAUD;
	obj3.name = "Sphere FLAT";
	obj3.M = glm::translate(glm::mat4(1), glm::vec3(objectsPos[3][0], objectsPos[3][1], objectsPos[3][2]));
	objects.push_back(obj3);
}

void initSphereSmooth() {
	Mesh sphereS = {};
	loadObjFile(MeshDir + "sphere_n_t_smooth.obj", &sphereS);
    generateAndLoadBuffers(true, &sphereS);
	// Object Setup with NO texture, will use the light shader and a material for color and light behavior
	Object obj4 = {};
	obj4.mesh = sphereS;
	obj4.material = MaterialType::RED_PLASTIC;
	obj4.shading = ShadingType::BLINN;
	obj4.name = "Sphere SMOOTH";
	obj4.M = glm::translate(glm::mat4(1), glm::vec3(objectsPos[4][0], objectsPos[4][1], objectsPos[4][2]));
	objects.push_back(obj4);
}

void initAxis() {
	Mesh _grid = {};
	loadObjFile(MeshDir + "axis.obj", &_grid);
    generateAndLoadBuffers(true, &_grid);
	Object obj1 = {};
	obj1.mesh = _grid;
	obj1.material = MaterialType::NO_MATERIAL;
	obj1.shading = ShadingType::TEXTURE_ONLY;
	obj1.textureID = loadTexture(TextureDir + "AXIS_TEX.png");
	obj1.name = "axis";
	obj1.M = glm::scale(glm::mat4(1),glm::vec3(2.f,2.f,2.f));
	Axis = obj1;
}

void initGrid() {
	Mesh _grid = {};
	loadObjFile(MeshDir + "reference_grid.obj", &_grid);
    generateAndLoadBuffers(true, &_grid);
	Object obj1 = {};
	obj1.mesh = _grid;
	obj1.material = MaterialType::NO_MATERIAL;
	obj1.shading = ShadingType::PASS_THROUGH;
	obj1.name = "grid";
	obj1.M = glm::scale(glm::mat4(1), glm::vec3(2.f, 2.f, 2.f));
	Grid = obj1;
}

void initWavingPlane() {
	Mesh plane = {};
	loadObjFile(MeshDir + "GridPlane.obj", &plane);
    generateAndLoadBuffers(true, &plane);
	Object obj = {};
	obj.mesh = plane;
	obj.material = MaterialType::SLATE;
	obj.shading = ShadingType::WAVE;
	obj.name = "Waving plane";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(0.f, -4.f, 0.f)), glm::vec3(12.f, 12.f, 12.f));
    objects.push_back(obj);
}

void init() {
    // Set current working directory
    char cwd[PATH_MAX];
    getcwd(cwd, sizeof(cwd));
    strcat(cwd, "/../");
    string CWD(cwd);
    MeshDir = CWD + "Mesh/";
	TextureDir = CWD + "Textures/";
	ShaderDir = CWD + "Shaders/";

    // Default render settings
	OperationMode = NAVIGATION;
	glEnable(GL_DEPTH_TEST);	// Hidden surface removal
	glCullFace(GL_BACK);	// remove faces facing the background
	glEnable(GL_LINE_SMOOTH);

	//Light initialization
	light.position = {5.0, 5.0, -5.0};
	light.scale = {0.2, 0.2, 0.2};
	light.color = {1.0, 1.0, 1.0};
	light.power = 1.f;

	// Materials setup
	materials.resize(5);
	materials[MaterialType::RED_PLASTIC].name = "Red Plastic";
	materials[MaterialType::RED_PLASTIC].ambient = red_plastic_ambient;
	materials[MaterialType::RED_PLASTIC].diffuse = red_plastic_diffuse;
	materials[MaterialType::RED_PLASTIC].specular = red_plastic_specular;
	materials[MaterialType::RED_PLASTIC].shininess = red_plastic_shininess;

	materials[MaterialType::EMERALD].name = "Emerald";
	materials[MaterialType::EMERALD].ambient = emerald_ambient;
	materials[MaterialType::EMERALD].diffuse = emerald_diffuse;
	materials[MaterialType::EMERALD].specular = emerald_specular;
	materials[MaterialType::EMERALD].shininess = emerald_shininess;

	materials[MaterialType::BRASS].name = "Brass";
	materials[MaterialType::BRASS].ambient = brass_ambient;
	materials[MaterialType::BRASS].diffuse = brass_diffuse;
	materials[MaterialType::BRASS].specular = brass_specular;
	materials[MaterialType::BRASS].shininess = brass_shininess;

	materials[MaterialType::SLATE].name = "Slate";
	materials[MaterialType::SLATE].ambient = slate_ambient;
	materials[MaterialType::SLATE].diffuse = slate_diffuse;
	materials[MaterialType::SLATE].specular = slate_specular;
	materials[MaterialType::SLATE].shininess = slate_shininess;

	materials[MaterialType::NO_MATERIAL].name = "NO_MATERIAL";
	materials[MaterialType::NO_MATERIAL].ambient = glm::vec3(1,1,1);
	materials[MaterialType::NO_MATERIAL].diffuse = glm::vec3(0, 0, 0);
	materials[MaterialType::NO_MATERIAL].specular = glm::vec3(0, 0, 0);
	materials[MaterialType::NO_MATERIAL].shininess = 1.f;

    // SHADERS configuration section
    shadersIDs.resize(NUM_SHADERS);
    lightUniforms.resize(NUM_SHADERS); // allocate space for uniforms of PHONG, BLINN, GOURAND, TEXTURE_ONLY, TEXTURE_PHONG and WAVE
    baseUniforms.resize(NUM_SHADERS); // allocate space for uniforms of PHONG, BLINN, GOURAND, TOON, WAVE, TEXTURE_ONLY, TEXTURE_PHONG

    //Gourand Shader loading
    shadersIDs[GOURAUD] = initShader(ShaderDir + "v_gouraud.glsl", ShaderDir + "f_gouraud.glsl");
    BaseShaderUniform base_unif = {};
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "M");
    baseUniforms[ShadingType::GOURAUD] = base_unif;
    LightShaderUniform light_unif = {};
    light_unif.material_ambient = glGetUniformLocation(shadersIDs[GOURAUD], "material.ambient");
    light_unif.material_diffuse = glGetUniformLocation(shadersIDs[GOURAUD], "material.diffuse");
    light_unif.material_specular = glGetUniformLocation(shadersIDs[GOURAUD], "material.specular");
    light_unif.material_shininess = glGetUniformLocation(shadersIDs[GOURAUD], "material.shininess");
    light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "light.position");
    light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "light.color");
    light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[GOURAUD], "light.power");
    lightUniforms[ShadingType::GOURAUD] = light_unif;
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[GOURAUD]);
    //Shader uniforms initialization
    glUniform3f(lightUniforms[GOURAUD].light_position_pointer, light.position.x, light.position.y, light.position.z);
    glUniform3f(lightUniforms[GOURAUD].light_color_pointer, light.color.r, light.color.g, light.color.b);
    glUniform1f(lightUniforms[GOURAUD].light_power_pointer, light.power);

    //Phong Shader loading
    shadersIDs[PHONG] = initShader(ShaderDir + "v_phong.glsl", ShaderDir + "f_phong.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[PHONG], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[PHONG], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[PHONG], "M");
    baseUniforms[ShadingType::PHONG] = base_unif;
    light_unif.material_ambient = glGetUniformLocation(shadersIDs[PHONG], "material.ambient");
    light_unif.material_diffuse = glGetUniformLocation(shadersIDs[PHONG], "material.diffuse");
    light_unif.material_specular = glGetUniformLocation(shadersIDs[PHONG], "material.specular");
    light_unif.material_shininess = glGetUniformLocation(shadersIDs[PHONG], "material.shininess");
    light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[PHONG], "light.position");
    light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[PHONG], "light.color");
    light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[PHONG], "light.power");
    lightUniforms[ShadingType::PHONG] = light_unif;
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[PHONG]);
    //Shader uniforms initialization
    glUniform3f(lightUniforms[PHONG].light_position_pointer, light.position.x, light.position.y, light.position.z);
    glUniform3f(lightUniforms[PHONG].light_color_pointer, light.color.r, light.color.g, light.color.b);
    glUniform1f(lightUniforms[PHONG].light_power_pointer, light.power);

	//Toon Shader loading
	shadersIDs[TOON] = initShader(ShaderDir + "v_toon.glsl", ShaderDir + "f_toon.glsl");
	//Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
	base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[TOON], "P");
	base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[TOON], "V");
	base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[TOON], "M");
	baseUniforms[ShadingType::TOON] = base_unif;
	light_unif.material_ambient = glGetUniformLocation(shadersIDs[TOON], "material.ambient");
	light_unif.material_diffuse = glGetUniformLocation(shadersIDs[TOON], "material.diffuse");
	light_unif.material_specular = glGetUniformLocation(shadersIDs[TOON], "material.specular");
	light_unif.material_shininess = glGetUniformLocation(shadersIDs[TOON], "material.shininess");
	light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[TOON], "light.position");
	light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[TOON], "light.color");
	light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[TOON], "light.power");
	lightUniforms[ShadingType::TOON] = light_unif;
	//Rendiamo attivo lo shader
	glUseProgram(shadersIDs[TOON]);
	//Shader uniforms initialization
	glUniform3f(lightUniforms[TOON].light_position_pointer, light.position.x, light.position.y, light.position.z);
	glUniform3f(lightUniforms[TOON].light_color_pointer, light.color.r, light.color.g, light.color.b);
	glUniform1f(lightUniforms[TOON].light_power_pointer, light.power);

    //Texture_Phong Shader loading
	shadersIDs[TEXTURE_PHONG] = initShader(ShaderDir + "v_texture_phong.glsl", ShaderDir + "f_texture_phong.glsl");
	//Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
	base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "P");
	base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "V");
	base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "M");
	baseUniforms[ShadingType::TEXTURE_PHONG] = base_unif;
	light_unif.material_ambient = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "material.ambient");
	light_unif.material_diffuse = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "material.diffuse");
	light_unif.material_specular = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "material.specular");
	light_unif.material_shininess = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "material.shininess");
	light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "light.position");
	light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "light.color");
	light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "light.power");
	lightUniforms[ShadingType::TEXTURE_PHONG] = light_unif;
	//Rendiamo attivo lo shader
	glUseProgram(shadersIDs[TEXTURE_PHONG]);
	//Shader uniforms initialization
	glUniform1i(glGetUniformLocation(shadersIDs[TEXTURE_PHONG], "textureBuffer"), 0);
	glUniform3f(lightUniforms[TEXTURE_PHONG].light_position_pointer, light.position.x, light.position.y, light.position.z);
	glUniform3f(lightUniforms[TEXTURE_PHONG].light_color_pointer, light.color.r, light.color.g, light.color.b);
	glUniform1f(lightUniforms[TEXTURE_PHONG].light_power_pointer, light.power);

    //Blinn Shader loading
    shadersIDs[BLINN] = initShader(ShaderDir + "v_blinn.glsl", ShaderDir + "f_blinn.glsl");
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[BLINN], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[BLINN], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[BLINN], "M");
    baseUniforms[ShadingType::BLINN] = base_unif;
    light_unif.material_ambient = glGetUniformLocation(shadersIDs[BLINN], "material.ambient");
    light_unif.material_diffuse = glGetUniformLocation(shadersIDs[BLINN], "material.diffuse");
    light_unif.material_specular = glGetUniformLocation(shadersIDs[BLINN], "material.specular");
    light_unif.material_shininess = glGetUniformLocation(shadersIDs[BLINN], "material.shininess");
    light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[BLINN], "light.position");
    light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[BLINN], "light.color");
    light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[BLINN], "light.power");
    lightUniforms[ShadingType::BLINN] = light_unif;
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[BLINN]);
    //Shader uniforms initialization
    glUniform3f(lightUniforms[BLINN].light_position_pointer, light.position.x, light.position.y, light.position.z);
    glUniform3f(lightUniforms[BLINN].light_color_pointer, light.color.r, light.color.g, light.color.b);
    glUniform1f(lightUniforms[BLINN].light_power_pointer, light.power);

    //Texture Shader loading
    shadersIDs[TEXTURE_ONLY] = initShader(ShaderDir + "v_texture.glsl", ShaderDir + "f_texture.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "M");
    baseUniforms[ShadingType::TEXTURE_ONLY] = base_unif;
    // Rendiamo attivo lo shader
    glUseProgram(shadersIDs[TEXTURE_ONLY]);
    // Shader uniforms initialization
    glUniform1i(glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "textureBuffer"), 0);

    //Pass-Through Shader loading
    shadersIDs[PASS_THROUGH] = initShader(ShaderDir + "v_passthrough.glsl", ShaderDir + "f_passthrough.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "M");
    baseUniforms[ShadingType::PASS_THROUGH] = base_unif;
    glUseProgram(shadersIDs[PASS_THROUGH]);
    glUniform4fv(glGetUniformLocation(shadersIDs[PASS_THROUGH], "Color"), 1, value_ptr(glm::vec4(1.0, 1.0, 1.0, 1.0)));

    //Wave Shader loading
    shadersIDs[WAVE] = initShader(ShaderDir + "v_wave.glsl", ShaderDir + "f_wave.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[WAVE], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[WAVE], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[WAVE], "M");
    baseUniforms[ShadingType::WAVE] = base_unif;
    light_unif.material_ambient = glGetUniformLocation(shadersIDs[WAVE], "material.ambient");
    light_unif.material_diffuse = glGetUniformLocation(shadersIDs[WAVE], "material.diffuse");
    light_unif.material_specular = glGetUniformLocation(shadersIDs[WAVE], "material.specular");
    light_unif.material_shininess = glGetUniformLocation(shadersIDs[WAVE], "material.shininess");
    light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[WAVE], "light.position");
    light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[WAVE], "light.color");
    light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[WAVE], "light.power");
    lightUniforms[ShadingType::WAVE] = light_unif;
	currentTime = glGetUniformLocation(shadersIDs[WAVE], "time");
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[WAVE]);
    //Shader uniforms initialization
    glUniform3f(lightUniforms[WAVE].light_position_pointer, light.position.x, light.position.y, light.position.z);
    glUniform3f(lightUniforms[WAVE].light_color_pointer, light.color.r, light.color.g, light.color.b);
    glUniform1f(lightUniforms[WAVE].light_power_pointer, light.power);

	// Camera Setup
	ViewSetup = {};
	ViewSetup.position = glm::vec4(10.0, 10.0, 10.0, 0.0);
	ViewSetup.target = currentFocus;
	ViewSetup.upVector = glm::vec4(0.0, 1.0, 0.0, 0.0);
	PerspectiveSetup = {};
	PerspectiveSetup.aspect = (GLfloat)WindowWidth / (GLfloat)WindowHeight;
	PerspectiveSetup.fovY = 45.0f;
	PerspectiveSetup.far_plane = 2000.0f;
	PerspectiveSetup.near_plane = 1.0f;

	//////////////////////////////////////////////////////////////////////
	//				OBJECTS IN SCENE
	//////////////////////////////////////////////////////////////////////
	
	// FLAT SPHERE (face normals) no texture with material, uses a shader with lighting
    initSphereFlat();

	// SMOOTH SPHERE (vertex normals) no texture with material, uses a shader with lighting
    initSphereSmooth();

	//PLANE with a procedural texture but no material, uses a texture-only shader
    initTexturedPlane();

	//CUBE with a texture image but no material, uses a texture-only shader
    initCube();

	//Reference point of the position of the light
    initLightObject();

	// White Axis
    initAxis();

	// White Grid for reference
    initGrid();

	// PARAMETRIC TORUS no texture with material, uses a shader with lighting
    initTorus();

    // Waving plane
    initWavingPlane();
}

void display() {
	glClearColor(0.4, 0.4, 0.4, 1);
	glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	
	// Draw the central Axis point of reference and the grid
	drawAxisAndGrid();

	for(int i = 0; i < objects.size(); i++) {
		//Shader selection
		switch (objects[i].shading) {
            case ShadingType::GOURAUD:
                glUseProgram(shadersIDs[GOURAUD]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[GOURAUD].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                //Material loading
                glUniform3f(lightUniforms[GOURAUD].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
                glUniform3fv(lightUniforms[GOURAUD].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[GOURAUD].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[GOURAUD].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[GOURAUD].material_shininess, materials[objects[i].material].shininess);
                break;
            case ShadingType::PHONG:
                glUseProgram(shadersIDs[PHONG]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[PHONG].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                //Material loading
                glUniform3f(lightUniforms[PHONG].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
                glUniform3fv(lightUniforms[PHONG].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[PHONG].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[PHONG].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[PHONG].material_shininess, materials[objects[i].material].shininess);
                break;
            case ShadingType::BLINN:
                glUseProgram(shadersIDs[BLINN]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[BLINN].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                //Material loading
                glUniform3f(lightUniforms[BLINN].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
                glUniform3fv(lightUniforms[BLINN].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[BLINN].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[BLINN].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[BLINN].material_shininess, materials[objects[i].material].shininess);
                break;
            case ShadingType::TEXTURE_ONLY:
                glUseProgram(shadersIDs[TEXTURE_ONLY]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[TEXTURE_ONLY].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                glActiveTexture(GL_TEXTURE0); // this addresses the first sampler2D uniform in the shader
                glBindTexture(GL_TEXTURE_2D, objects[i].textureID);
                break;
            case ShadingType::TEXTURE_PHONG:
				glUseProgram(shadersIDs[TEXTURE_PHONG]);
				// Caricamento matrice trasformazione del modello
				glUniformMatrix4fv(baseUniforms[TEXTURE_PHONG].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
				glActiveTexture(GL_TEXTURE0); // this addresses the first sampler2D uniform in the shader
				glBindTexture(GL_TEXTURE_2D, objects[i].textureID);
				//Material loading
				glUniform3f(lightUniforms[TEXTURE_PHONG].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
				glUniform3fv(lightUniforms[TEXTURE_PHONG].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
				glUniform3fv(lightUniforms[TEXTURE_PHONG].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
				glUniform3fv(lightUniforms[TEXTURE_PHONG].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
				glUniform1f(lightUniforms[TEXTURE_PHONG].material_shininess, materials[objects[i].material].shininess);
                break;
            case ShadingType::PASS_THROUGH:
                glUseProgram(shadersIDs[PASS_THROUGH]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[PASS_THROUGH].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                break;
            case ShadingType::TOON:
                glUseProgram(shadersIDs[TOON]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[TOON].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                //Material loading
                glUniform3f(lightUniforms[TOON].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
                glUniform3fv(lightUniforms[TOON].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[TOON].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[TOON].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[TOON].material_shininess, materials[objects[i].material].shininess);
                break;
            case ShadingType::WAVE:
                glUseProgram(shadersIDs[WAVE]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[WAVE].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                // Material loading
                glUniform3f(lightUniforms[WAVE].light_position_pointer, light.position.x, light.position.y, light.position.z); // Added light update
                glUniform3fv(lightUniforms[WAVE].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[WAVE].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[WAVE].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[WAVE].material_shininess, materials[objects[i].material].shininess);
				glUniform1f(currentTime, (float) glutGet(GLUT_ELAPSED_TIME));
				break;
            default:
                break;
		}
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		glBindVertexArray(objects[i].mesh.vertexArrayObjID);
		glDrawArrays(GL_TRIANGLES, 0, objects[i].mesh.vertices.size());

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}

	cameraLookAt = currentFocus;

    glUseProgram(0);
    printToScreen();
    glutSwapBuffers();
    glutPostRedisplay();
}

void resize(int w, int h) {
	if (h == 0) return;	// Window is minimized

	int width = h * aspectRatio;           // width is adjusted for aspect ratio
	int left = (w - width) / 2;
	// Set Viewport to window dimensions
	glViewport(left, 0, width, h);
	WindowWidth = w;
	WindowHeight = h;

	// Fixed Pipeline matrices for retro compatibility
	glUseProgram(0); // Embedded openGL shader
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(PerspectiveSetup.fovY, PerspectiveSetup.aspect, PerspectiveSetup.near_plane, PerspectiveSetup.far_plane);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	gluLookAt(ViewSetup.position.x, ViewSetup.position.y, ViewSetup.position.z,
              cameraLookAt.x, cameraLookAt.y, cameraLookAt.z,
		      ViewSetup.upVector.x, ViewSetup.upVector.y, ViewSetup.upVector.z);

	// Programmable Pipeline matrices for object rendering
	glm::mat4 P = glm::perspective(PerspectiveSetup.fovY, PerspectiveSetup.aspect, PerspectiveSetup.near_plane, PerspectiveSetup.far_plane);
	glm::mat4 V = glm::lookAt(glm::vec3(ViewSetup.position), glm::vec3(cameraLookAt), glm::vec3(ViewSetup.upVector));

	for (int i = 0; i < shadersIDs.size();i++) {
		glUseProgram(shadersIDs[i]);
		glUniformMatrix4fv(baseUniforms[i].P_Matrix_pointer, 1, GL_FALSE, value_ptr(P));
		glUniformMatrix4fv(baseUniforms[i].V_Matrix_pointer, 1, GL_FALSE, value_ptr(V));
	}
}

void mouseListener(int button, int state, int x, int y) {
	glutPostRedisplay();

	int modifiers = glutGetModifiers();
	if (modifiers == GLUT_ACTIVE_SHIFT) {
		switch (button) {
            case WHEEL_UP:
            	verticalPan(0.5);
            	break;
            case WHEEL_DOWN:
            	verticalPan(-0.5);
            	break;
		}
		return;
	} else if (modifiers == GLUT_ACTIVE_CTRL) {
		switch (button) {
            case WHEEL_UP:
            	horizontalPan(0.5);
            	break;
            case WHEEL_DOWN:
            	horizontalPan(-0.5);
            	break;
		}
		return;
	}

	glm::vec4 axis;
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
            if (state == GLUT_DOWN) { movingTrackball = true; }
            if (state == GLUT_UP) { movingTrackball = false; }
            OperationMode = NAVIGATION;
            lastMousePosX = x;
            lastMousePosY = y;
            break;
        default:
            break;
	}

	// Selezione dell'asse per le trasformazioni
	switch (WorkingAxis) {
        case X:
            axis = glm::vec4(1.0, 0.0, 0.0, 0.0);
            break;
        case Y:
            axis = glm::vec4(0.0, 1.0, 0.0, 0.0);
            break;
        case Z:
            axis = glm::vec4(0.0, 0.0, 1.0, 0.0);
            break;
        default:
            break;
	}

	switch (OperationMode) {
        case TRASLATING:
            modifyModelMatrix(axis * amount, axis, 0.0f, 1.0f);
            break;
        case ROTATING:
            modifyModelMatrix(glm::vec4(0), axis, amount * 20.0f, 1.0f);
            break;
        case SCALING:
            modifyModelMatrix(glm::vec4(0), axis, 0.0f, 1.0f + amount);
            break;
        case NAVIGATION:
            if (button == WHEEL_UP)
                zoom(0.5);
            else if (button == WHEEL_DOWN)
                zoom(-0.5);
            break;
        default:
            break;
	}
}

void mouseMotionListener(int x, int y) {
	// Spostamento su trackball del vettore posizione Camera 
	if (!movingTrackball) return;

	glm::vec3 destination = getTrackBallPoint(x, y);
	glm::vec3 origin = getTrackBallPoint(lastMousePosX, lastMousePosY);
	float dx, dy, dz;
	dx = destination.x - origin.x;
	dy = destination.y - origin.y;
	dz = destination.z - origin.z;
	if (dx || dy || dz) {
		// rotation angle = acos( (v dot w) / (len(v) * len(w)) ) o approssimato da ||dest-orig||;
		float pi = glm::pi<float>();
		float angle = sqrt(dx * dx + dy * dy + dz * dz) * (180.0 / pi);
		// rotation axis = (dest vec orig) / (len(dest vec orig))
		glm::vec3 rotation_vec = glm::cross(origin, destination);
		// calcolo del vettore direzione w = C - A
		glm::vec4 direction = ViewSetup.position - ViewSetup.target;
		// rotazione del vettore direzione w 
		// determinazione della nuova posizione della camera 
		ViewSetup.position = ViewSetup.target + glm::rotate(glm::mat4(1.0f), glm::radians(-angle), rotation_vec) * direction;
	}
	lastMousePosX = x; lastMousePosY = y;
	glutPostRedisplay();
}

void keyboardListener(unsigned char key, int x, int y) {
	switch (key) {
		// Selezione della modalità di trasformazione
        case 'g':
            OperationMode = TRASLATING;
            break;
        case 'r':
            OperationMode = ROTATING;
            break;
        case 's':
            OperationMode = SCALING;
            break;
        case 27:
            glutLeaveMainLoop();
            break;
        // Selezione dell'asse
        case 'x':
            WorkingAxis = X;
            break;
        case 'y':
            WorkingAxis = Y;
            break;
        case 'z':
            WorkingAxis = Z;
            break;
        case 'W':
            TorusSetup.NumWraps++;
            computeTorus(&(objects[TorusSetup.torus_index].mesh));
            generateAndLoadBuffers(false, &(objects[TorusSetup.torus_index].mesh));
            break;
        case 'w':
            if (TorusSetup.NumWraps > 4) {
                TorusSetup.NumWraps--;
                computeTorus(&(objects[TorusSetup.torus_index].mesh));
                generateAndLoadBuffers(false, &(objects[TorusSetup.torus_index].mesh));
            }
            break;
        case 'N':
            TorusSetup.NumPerWrap++;
            computeTorus(&(objects[TorusSetup.torus_index].mesh));
            generateAndLoadBuffers(false, &(objects[TorusSetup.torus_index].mesh));
            break;
        case 'n':
            if (TorusSetup.NumPerWrap > 4) {
                TorusSetup.NumPerWrap--;
                computeTorus(&(objects[TorusSetup.torus_index].mesh));
                generateAndLoadBuffers(false, &(objects[TorusSetup.torus_index].mesh));
            }
            break;
        default:
            break;
	}
	glutPostRedisplay();
}

void special(int key, int x, int y) {
	switch (key) {
        case GLUT_KEY_LEFT:
            selectedObj = selectedObj > 0 ? selectedObj - 1 : objects.size() - 1;
            break;
        case GLUT_KEY_RIGHT:
            selectedObj = (selectedObj + 1) < objects.size() ? selectedObj + 1 : 0;
            break;
        default:
            break;
	}
	glutPostRedisplay();
}

void mainMenuFunction(int option) {
	switch (option) {
        case MenuOption::WIRE_FRAME:
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
            objects[selectedObj].material = MaterialType::RED_PLASTIC;
            break;
        case MaterialType::EMERALD:
            objects[selectedObj].material = MaterialType::EMERALD;
            break;
        case MaterialType::BRASS:
            objects[selectedObj].material = MaterialType::BRASS;
            break;
        case MaterialType::SLATE:
            objects[selectedObj].material = MaterialType::SLATE;
            break;
        default:
            break;
    }
    glutPostRedisplay();
}

void torusMenuFunction(int option) {
    switch (option) {
        case TorusStyle::SHADING_GOURAUD:
            objects[TorusSetup.torus_index].shading = ShadingType::GOURAUD;
            break;
        case TorusStyle::SHADING_PHONG:
			objects[TorusSetup.torus_index].shading = ShadingType::PHONG;
            break;
        case TorusStyle::SHADING_BLINN:
			objects[TorusSetup.torus_index].shading = ShadingType::BLINN;
            break;
        case TorusStyle::SHADING_TOON:
            objects[TorusSetup.torus_index].shading = ShadingType::TOON;
            break;
        case TorusStyle::ONLY_TEXTURE:
			objects[TorusSetup.torus_index].textureID = loadTexture(TextureDir + "bombolone.jpg");
            objects[TorusSetup.torus_index].shading = ShadingType::TEXTURE_ONLY;
            break;
		case TorusStyle::PHONG_TEXTURE:
			objects[TorusSetup.torus_index].textureID = loadTexture(TextureDir + "bombolone.jpg");
			objects[TorusSetup.torus_index].shading = ShadingType::TEXTURE_PHONG;
            break;
        case TorusStyle::PROCEDURAL_MAPPING:
			torusProceduralMapping(&objects[TorusSetup.torus_index]);
			objects[TorusSetup.torus_index].shading = ShadingType::TEXTURE_ONLY;
            break;
        default:
            break;
    }
    glutPostRedisplay();
}

void buildOpenGLMenu() {
	// Materials sub menu
	int materialSubMenu = glutCreateMenu(materialMenuFunction);
	glutAddMenuEntry(materials[MaterialType::RED_PLASTIC].name.c_str(), MaterialType::RED_PLASTIC);
	glutAddMenuEntry(materials[MaterialType::EMERALD].name.c_str(), MaterialType::EMERALD);
	glutAddMenuEntry(materials[MaterialType::BRASS].name.c_str(), MaterialType::BRASS);
	glutAddMenuEntry(materials[MaterialType::SLATE].name.c_str(), MaterialType::SLATE);

	// Torus operations sub menu
	int torusSubMenu = glutCreateMenu(torusMenuFunction);
	glutAddMenuEntry("Gouraud", TorusStyle::SHADING_GOURAUD);
	glutAddMenuEntry("Phong", TorusStyle::SHADING_PHONG);
	glutAddMenuEntry("Blinn", TorusStyle::SHADING_BLINN);
	glutAddMenuEntry("Toon", TorusStyle::SHADING_TOON);
	glutAddMenuEntry("Texture", TorusStyle::ONLY_TEXTURE);
	glutAddMenuEntry("Phong Texture", TorusStyle::PHONG_TEXTURE);
	glutAddMenuEntry("Procedural Mapping", TorusStyle::PROCEDURAL_MAPPING);

	// Main menu
    glutCreateMenu(mainMenuFunction); // richiama mainMenuFunction() alla selezione di una voce menu
	glutAddMenuEntry("Opzioni", -1); // -1 significa che non si vuole gestire questa riga
	glutAddMenuEntry("", -1);
	glutAddMenuEntry("Wireframe", MenuOption::WIRE_FRAME);
	glutAddMenuEntry("Face fill", MenuOption::FACE_FILL);
	glutAddMenuEntry("Culling: ON", MenuOption::CULLING_ON);
	glutAddMenuEntry("Culling: OFF", MenuOption::CULLING_OFF);
	glutAddSubMenu("Material", materialSubMenu);
	glutAddMenuEntry("World coordinate system", MenuOption::CHANGE_TO_WCS);
	glutAddMenuEntry("Object coordinate system", MenuOption::CHANGE_TO_OCS);
    glutAddSubMenu("Torus options", torusSubMenu);
	glutAttachMenu(GLUT_RIGHT_BUTTON);
}

glm::vec3 getTrackBallPoint(float x, float y) {
	float zTemp;
	glm::vec3 point;
	//map to [-1;1]
	point.x = (2.0f * x - WindowWidth) / WindowWidth;
	point.y = (WindowHeight - 2.0f * y) / WindowHeight;

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
    glm::vec3 lookAt(currentFocus), position(ViewSetup.position), upVector(ViewSetup.upVector);

    glm::vec3 direction = lookAt - position;
    glm::vec3 normalToPlane = normalize(cross(direction, upVector));
    glm::vec4 increment(normalToPlane.x * dir, normalToPlane.y * dir, normalToPlane.z * dir, 1.0);

    currentFocus += increment;
    ViewSetup.position += increment;
}

void verticalPan(float dir) {
    glm::vec3 lookAt(currentFocus), position(ViewSetup.position), upVector(ViewSetup.upVector);

    glm::vec3 direction = lookAt - position;
    glm::vec3 normalToPlane = normalize(cross(direction, upVector));
    glm::vec3 normalToNormalToPlane = -normalize(cross(direction, normalToPlane));
    glm::vec4 increment(normalToNormalToPlane.x * dir, normalToNormalToPlane.y * dir, normalToNormalToPlane.z * dir, 1.0);

    currentFocus += increment;
    ViewSetup.position += increment;
}

void modifyModelMatrix(glm::vec4 translation_vector, glm::vec4 rotation_vector, GLfloat angle, GLfloat scale_factor) {
    glPushMatrix();
    	glLoadIdentity();

		switch (TransformMode) {
			case WCS:
                glRotatef(angle, rotation_vector.x, rotation_vector.y, rotation_vector.z);
                glScalef(scale_factor, scale_factor, scale_factor);
                glTranslatef(translation_vector.x, translation_vector.y, translation_vector.z);

				glMultMatrixf(value_ptr(objects[selectedObj].M));

				break;
			case OCS:
				glMultMatrixf(value_ptr(objects[selectedObj].M));

				glRotatef(angle, rotation_vector.x, rotation_vector.y, rotation_vector.z);
				glScalef(scale_factor, scale_factor, scale_factor);
				glTranslatef(translation_vector.x, translation_vector.y, translation_vector.z);

				break;
		}

		if(selectedObj == 4) { // Update light position multiply ModelView matrix by light position vector
            GLfloat matrix[16];
            glGetFloatv(GL_MODELVIEW_MATRIX, matrix);

            // newLightPosition = ModelViewMatrix * lightPosition
            light.position = glm::vec3(matrix[0]*light.position[0] + matrix[4]*light.position[1] + matrix[8]*light.position[2] + matrix[12],
                                       matrix[1]*light.position[0] + matrix[5]*light.position[1] + matrix[9]*light.position[2] + matrix[13],
                                       matrix[2]*light.position[0] + matrix[6]*light.position[1] + matrix[10]*light.position[2] + matrix[14]);
		}

    	glGetFloatv(GL_MODELVIEW_MATRIX, value_ptr(objects[selectedObj].M));
    glPopMatrix();
}

void generateAndLoadBuffers(bool generate, Mesh *mesh) {
	if (generate) {
		// Genero 1 Vertex Array Object
		glGenVertexArrays(1, &mesh->vertexArrayObjID);

		// Genero 1 Vertex Buffer Object per i vertici
		glGenBuffers(1, &mesh->vertexBufferObjID);
		// Genero 1 Buffer Object per le normali
		glGenBuffers(1, &mesh->normalBufferObjID);
		// Genero 1 Buffer Object per le coordinate texture
		glGenBuffers(1, &mesh->uvBufferObjID);
	}

	glBindVertexArray(mesh->vertexArrayObjID);
	
	glBindBuffer(GL_ARRAY_BUFFER, mesh->vertexBufferObjID);
	glBufferData(GL_ARRAY_BUFFER, mesh->vertices.size() * sizeof(glm::vec3), &mesh->vertices[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(
		0,					// attribute index in the shader
		3,                  // size
		GL_FLOAT,           // type
		false,              // normalized 
		0,					// stride
		(void*)0            // array buffer offset
	);

	glBindBuffer(GL_ARRAY_BUFFER, mesh->normalBufferObjID);
	glBufferData(GL_ARRAY_BUFFER, mesh->normals.size() * sizeof(glm::vec3), mesh->normals.data(), GL_STATIC_DRAW);
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(
		1,					// attribute index in the shader
		3,                  // size
		GL_FLOAT,           // type
		false,              // normalized 
		0,					// stride
		(void*)0            // array buffer offset
	);
	
	glBindBuffer(GL_ARRAY_BUFFER, mesh->uvBufferObjID);
	glBufferData(GL_ARRAY_BUFFER, mesh->texCoords.size() * sizeof(glm::vec2), mesh->texCoords.data(), GL_STATIC_DRAW);
	glEnableVertexAttribArray(2);
	glVertexAttribPointer(
		2,					// attribute index in the shader
		2,                  // size
		GL_FLOAT,           // type
		false,              // normalized 
		0,					// stride
		(void*)0            // array buffer offset
	);

	glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);
	glDisableVertexAttribArray(2);
}

void loadObjFile(string file_path, Mesh* mesh) {
	FILE * file = fopen(file_path.c_str(), "r");
	if (file == NULL) {
		std::cerr << "\nFailed to open obj file! --> " << file_path << std::endl;
		std::getchar();
		exit(EXIT_FAILURE);
	}
	// tmp data structures
	vector<GLuint> vertexIndices, normalIndices, uvIndices;
	vector<glm::vec3> tmp_vertices,  tmp_normals;
	vector<glm::vec2> tmp_uvs;

	char lineHeader[128];
	while (fscanf(file, "%s", lineHeader) != EOF) {
		if (strcmp(lineHeader, "v") == 0) {
			glm::vec3 vertex;
			fscanf(file, " %f %f %f\n", &vertex.x, &vertex.y, &vertex.z);
			tmp_vertices.push_back(vertex);
		} else if (strcmp(lineHeader, "vn") == 0) {
			glm::vec3 normal;
			fscanf(file, " %f %f %f\n", &normal.x, &normal.y, &normal.z);
			tmp_normals.push_back(normal);
		} else if (strcmp(lineHeader, "vt") == 0) {
			glm::vec2 uv;
			fscanf(file, " %f %f\n", &uv.x, &uv.y);
			uv.y = 1 - uv.y;
			tmp_uvs.push_back(uv);
		} else if (strcmp(lineHeader, "f") == 0) {
			GLuint v_a, v_b, v_c; // index in position array
			GLuint n_a, n_b, n_c; // index in normal array
			GLuint t_a, t_b, t_c; // index in UV array

			fscanf(file, "%s", lineHeader);
			if (strstr(lineHeader, "//")) { // case: v//n v//n v//n
				sscanf(lineHeader, "%d//%d",&v_a, &n_a);
				fscanf(file,"%d//%d %d//%d\n", &v_b, &n_b, &v_c, &n_c);
				n_a--, n_b--, n_c--;
				normalIndices.push_back(n_a); normalIndices.push_back(n_b); normalIndices.push_back(n_c);
			} else if (strstr(lineHeader, "/")) {// case: v/t/n v/t/n v/t/n
				sscanf(lineHeader, "%d/%d/%d", &v_a, &t_a, &n_a);
				fscanf(file, "%d/%d/%d %d/%d/%d\n", &v_b, &t_b, &n_b, &v_c, &t_c, &n_c);
				n_a--, n_b--, n_c--;
				t_a--, t_b--, t_c--;
				normalIndices.push_back(n_a); normalIndices.push_back(n_b); normalIndices.push_back(n_c);
				uvIndices.push_back(t_a); uvIndices.push_back(t_b); uvIndices.push_back(t_c);
			} else {// case: v v v
				sscanf(lineHeader, "%d", &v_a);
				fscanf(file,"%d %d\n", &v_b, &v_c);
			}
			v_a--; v_b--; v_c--;
			vertexIndices.push_back(v_a); vertexIndices.push_back(v_b); vertexIndices.push_back(v_c);
		}
	}
	fclose(file);

	// If normals and uvs are not loaded, we calculate them for a default smooth shading effect
	if (tmp_normals.size() == 0) {
		tmp_normals.resize(vertexIndices.size() / 3, glm::vec3(0.0, 0.0, 0.0));
		// normal of each face saved 1 time PER FACE!
		for (int i = 0; i < vertexIndices.size(); i += 3) {
			GLushort ia = vertexIndices[i];
			GLushort ib = vertexIndices[i + 1];
			GLushort ic = vertexIndices[i + 2];
			glm::vec3 normal = glm::normalize(glm::cross(
				glm::vec3(tmp_vertices[ib]) - glm::vec3(tmp_vertices[ia]),
				glm::vec3(tmp_vertices[ic]) - glm::vec3(tmp_vertices[ia])));
			tmp_normals[ i / 3 ] = normal;
			//Put an index to the normal for all 3 vertex of the face
			normalIndices.push_back(i/3);
			normalIndices.push_back(i/3);
			normalIndices.push_back(i/3);
		}
	}
	//if texture coordinates were not included we fake them
	if (tmp_uvs.size() == 0) {
		tmp_uvs.push_back(glm::vec2(0)); //dummy uv
		for (int i = 0; i < vertexIndices.size(); i += 3) {
			// The UV is dummy
			uvIndices.push_back(0);
			uvIndices.push_back(0);
			uvIndices.push_back(0);
		}
	}
	// The data for loaded for the glDrawArrays call, this is a simple but non optimal way of storing mesh data.
	// However, you could optimize the mesh data using a index array for both vertex positions, 
	// normals and textures and later use glDrawElements

	// Following the index arrays, the final arrays that will contain the data for glDrawArray are built.
	for (int i = 0; i < vertexIndices.size(); i++) {
		mesh->vertices.push_back(tmp_vertices[vertexIndices[i]]);
		mesh->normals.push_back(tmp_normals[normalIndices[i]]);
		mesh->texCoords.push_back(tmp_uvs[uvIndices[i]]);
	}
}

GLuint loadTexture(string path) {
	int width, height, texChannels;
	GLuint textureID;
	stbi_uc* pixels = stbi_load(path.data(), &width, &height, &texChannels, STBI_rgb_alpha);
	if (!pixels) {
		std::cerr << "\nFailed to load texture image! --> " << path << std::endl;
		std::getchar();
		exit(EXIT_FAILURE);
	}
	glGenTextures(1, &textureID);
	glBindTexture(GL_TEXTURE_2D, textureID);

	//Texture displacement logic
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	//Texture sampling logic
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	// data loading in memory
	glTexImage2D(GL_TEXTURE_2D,  //the target
		0, // the mip map level we want to generate
		GL_RGBA, 
		width, 
		height, 
		0, // border, leave 0
		GL_RGBA, // we assume is a RGB color image with 24 bit depth per pixel
		GL_UNSIGNED_BYTE, // the data type
		pixels);
	glGenerateMipmap(GL_TEXTURE_2D);// automatic mip maps generation

	stbi_image_free(pixels);
	return textureID;
}

// Computes the Vertex attributes data for segment number j of wrap number i.
void computeTorusVertex(int i, int j, Mesh* mesh) {
	float theta = 2.f * glm::pi<float>() *(float)i / (float)TorusSetup.NumWraps;
	float phi = 2.f * glm::pi<float>()*(float)j / (float)TorusSetup.NumPerWrap;
	float sinphi = sin(phi);
	float cosphi = cos(phi);
	float sintheta = sin(theta);
	float costheta = cos(theta);
	
	float tmp = TorusSetup.MajorRadius + TorusSetup.MinorRadius * cosphi;
	float x = sintheta * tmp;
	float y = TorusSetup.MinorRadius * sinphi; 
	float z = costheta * tmp;

	mesh->vertices.push_back(glm::vec3(x, y, z));
	mesh->normals.push_back(glm::vec3(sintheta*cosphi, sinphi, costheta*cosphi));
	mesh->texCoords.push_back(glm::vec2(j / (float)TorusSetup.NumPerWrap, i / (float)TorusSetup.NumWraps)); // Added texture coordinates
}

void torusProceduralMapping(Object * obj) {
	GLubyte image[512][1024][3];
	int i, j;
	double val  ;
	for (i = 0; i < 512; i++) {
        val = abs(tan(i * 8) * 64);
		for (j = 0; j < 1024; j++) {
			double cX, cY, cZ;

			if(j < val || j > 1024 - val || (j < 512 + val && j > 512 - val)) {
                cX = 20 + val * 20 / 1024;
                cY = 20 + val * 10 / 1024;
                cZ = 5;
			} else {
                cX = 62;
                cY = 107;
                cZ = 35;
            }

			image[i][j][0] = (GLubyte) cX;
			image[i][j][1] = (GLubyte) cY;
			image[i][j][2] = (GLubyte) cZ;
		}
	}

	glGenTextures(1, &obj->textureID);
	glBindTexture(GL_TEXTURE_2D, obj->textureID);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexImage2D(GL_TEXTURE_2D,  //the target
				 0, // the mip map level we want to generate
				 GL_RGB, // the format of the texture
				 512, //texture_size, width
				 1024, //texture_size, heigth
				 0,  // border, leave 0
				 GL_RGB, // we assume is a RGB color image with 24 bit depth per pixel
				 GL_UNSIGNED_BYTE, // the data type
				 image);
}

void computeTorus(Mesh *mesh) {
	mesh->vertices.clear();
	mesh->normals.clear();
	mesh->texCoords.clear();
	// draw the torus as NumWraps strips one next to the other
	for (int i = 0; i < TorusSetup.NumWraps; i++) {
		for (int j = 0; j <= TorusSetup.NumPerWrap; j++) {
			// first face   3
			//				| \
			//				1--2
			computeTorusVertex(i, j, mesh);
			computeTorusVertex(i + 1, j,mesh);
			computeTorusVertex(i , j + 1, mesh);
			// second face  1--3
			//				 \ |
			//				   2
			computeTorusVertex(i, j + 1, mesh);
			computeTorusVertex(i + 1, j, mesh);
			computeTorusVertex(i + 1, j + 1, mesh);
		}
	}
}

void drawAxisAndGrid() {
	glUseProgram(shadersIDs[Grid.shading]);
	glUniformMatrix4fv(baseUniforms[Grid.shading].M_Matrix_pointer, 1, GL_FALSE, value_ptr(Grid.M));
	glEnableVertexAttribArray(0);
	glEnableVertexAttribArray(1);
	glEnableVertexAttribArray(2);
	glBindVertexArray(Grid.mesh.vertexArrayObjID);
	glDrawArrays(GL_TRIANGLES, 0, Grid.mesh.vertices.size());
	glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);
	glDisableVertexAttribArray(2);

	glUseProgram(shadersIDs[Axis.shading]);
	// Caricamento matrice trasformazione del modello
	glUniformMatrix4fv(baseUniforms[Axis.shading].M_Matrix_pointer, 1, GL_FALSE, value_ptr(Axis.M));
	glActiveTexture(GL_TEXTURE0); // this addresses the first sampler2D uniform in the shader
	glBindTexture(GL_TEXTURE_2D, Axis.textureID);
	glEnableVertexAttribArray(0);
	glEnableVertexAttribArray(1);
	glEnableVertexAttribArray(2);
	glBindVertexArray(Axis.mesh.vertexArrayObjID);
	glDrawArrays(GL_TRIANGLES, 0, Axis.mesh.vertices.size());
	glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);
	glDisableVertexAttribArray(2);
}

void printToScreen() {
	string axis = "Axis: ";
	string mode = "Navigate/Modify: ";
	string obj = "Object: " + objects[selectedObj].name;
	string ref = "WCS/OCS: ";
	string mat = "Material: " + materials[objects[selectedObj].material].name;

	switch (WorkingAxis) {
        case X: axis += "X"; break;
        case Y: axis += "Y"; break;
        case Z: axis += "Z"; break;
	}

	switch (OperationMode) {
        case TRASLATING: mode += "Translate"; break;
        case ROTATING: mode += "Rotate"; break;
        case SCALING: mode += "Scale"; break;
        case NAVIGATION: mode += "Navigate"; break;
	}

	switch (TransformMode) {
        case OCS: ref += "OCS"; break;
        case WCS: ref += "WCS"; break;
	}
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	gluOrtho2D(0, WindowHeight * aspectRatio, 0, WindowHeight);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
		
	vector<string> lines;
	lines.push_back(mat);
	lines.push_back(obj);
	lines.push_back(axis);
	lines.push_back(mode);
	lines.push_back(ref);
	glDisable(GL_DEPTH_TEST);
	HUD_Logger::get()->printInfo(lines);
	glEnable(GL_DEPTH_TEST);

	resize(WindowWidth, WindowHeight);
}
