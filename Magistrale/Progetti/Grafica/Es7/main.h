#define _CRT_SECURE_NO_WARNINGS // for fscanf

#include <stdio.h>
#include <unistd.h>
#include <math.h>
#include <vector>
#include <string>

#include <GL/glew.h>
#include <GL/freeglut.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <algorithm>
#include "HUD_Logger.h"
#include "common.h"

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define WHEEL_UP 3
#define WHEEL_DOWN 4

#define NUM_SHADERS 7
#define NUM_LIGHT_SHADERS 2

using namespace std;

static int WindowWidth = 1120;
static int WindowHeight = 630;
GLfloat aspectRatio = 16.0f / 9.0f;

typedef struct {
    std::vector<glm::vec3> vertices;
    std::vector<glm::vec3> normals;
    std::vector<glm::vec2> texCoords;
    std::vector<glm::vec3> tangents;
    GLuint vertexArrayObjID;
    GLuint vertexBufferObjID;
    GLuint normalBufferObjID;
    GLuint uvBufferObjID;
    GLuint tgBufferObjID;
} Mesh;

typedef enum {
    RED_PLASTIC,
    EMERALD,
    BRASS,
    SLATE,
    NO_MATERIAL
} MaterialType;

typedef struct {
    std::string name;
    glm::vec3 ambient;
    glm::vec3 diffuse;
    glm::vec3 specular;
    GLfloat shininess;
} Material;

typedef enum { // used also as index, don't modify order
    NORMAL_MAPPING,
    TEXTURE_PHONG,
    SKYBOX,
    REFLECTION,
    REFRACTION,
    TEXTURE_ONLY,
    PASS_THROUGH
} ShadingType;

typedef struct {
    Mesh mesh;
    MaterialType material;
    ShadingType shading;
    GLuint diffuseTexID;
    GLuint normalTexID;
    glm::mat4 M;
    string name;
} Object;

typedef struct {
    GLuint light_position_pointer;
    GLuint light_color_pointer;
    GLuint light_power_pointer;
    GLuint material_diffuse;
    GLuint material_ambient;
    GLuint material_specular;
    GLuint material_shininess;
    GLuint diffuse_sampler;
    GLuint normal_sampler;
} LightShaderUniform;

typedef struct {
    GLuint P_Matrix_pointer;
    GLuint V_Matrix_pointer;
    GLuint M_Matrix_pointer;
    GLuint camera_position_pointer;
} BaseShaderUniform;

static string MeshDir, TextureDir, ShaderDir;
static GLuint cubeTexture;
static vector<Object> objects; // All 3D stuff
static vector<Material> materials;
static int selectedObj = 0;
GLfloat objectsPos[7][3] = { {-5., 5., 0.}, {-5., 0., 0.}, {5., 0.75, 5.}, {6., 2., -3.}, {-5., 2.5, 0.}, {0., 0., -10.}, {0., 0., -8.} }; // Sphere, Cube, Rock, Brick, Skybox, Windows, Light

// Materiali disponibili
glm::vec3 red_plastic_ambient = { 0.1, 0.0, 0.0 }, red_plastic_diffuse = { 0.6, 0.1, 0.1 }, red_plastic_specular = { 0.6, 0.6, 0.6 }; GLfloat red_plastic_shininess = 32.0f;
glm::vec3 brass_ambient = { 0.1, 0.06, 0.015 }, brass_diffuse = { 0.78, 0.57, 0.11 }, brass_specular = { 0.99, 0.91, 0.91 }; GLfloat brass_shininess = 27.8f;
glm::vec3 emerald_ambient = { 0.0215, 0.04745, 0.0215 }, emerald_diffuse = { 0.07568, 0.71424, 0.07568 }, emerald_specular = { 0.633,0.633, 0.633 }; GLfloat emerald_shininess = 78.8f;
glm::vec3 slate_ambient = { 0.02, 0.02, 0.02 }, slate_diffuse = { 0.1, 0.1, 0.1 }, slate_specular{ 0.3, 0.3, 0.3 }; GLfloat slate_shininess = 20.78125f;

typedef struct {
    glm::vec3 position;
    glm::vec3 color;
    GLfloat power;
} PointLight;

static PointLight light;

/*camera structures*/
constexpr float CAMERA_ZOOM_SPEED = 0.5f;
constexpr float CAMERA_TRASLATION_SPEED = 0.5f;

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
    CHANGE_TO_OCS
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

// Main initialization funtion
void init();
// Display Funtion
void display();
// Reshape Function
void resize(int w, int h);
// Calls glutPostRedisplay each millis milliseconds
void refreshMonitor(int millis);
// Mouse Function
void mouseListener(int button, int state, int x, int y);
// Keyboard:  g traslate r rotate s scale x,y,z axis esc
void keyboardListener(unsigned char key, int x, int y);
// Special key arrow: select active object (arrows left,right)
void special(int key, int x, int y);
// gestione delle voci principali del menu
void mainMenuFunc(int option);
// gestione delle voci principali del sub menu per i matriali
void materialMenuFunction(int option);
// costruisce i menu openGL
void buildOpenGLMenu();
// Trackball: Converte un punto 2D sullo schermo in un punto 3D sulla trackball
glm::vec3 getTrackBallPoint(float x, float y);
// Trackball: Effettua la rotazione del vettore posizione sulla trackball
void mouseMotionListener(int x, int y);
void zoom(float dir);
void horizontalPan(float dir);
void verticalPan(float dir);
//	Crea ed applica la matrice di trasformazione alla matrice dell'oggeto discriminando tra WCS e OCS.
//	La funzione � gia invocata con un input corretto, � sufficiente concludere la sua implementazione.
void modifyModelMatrix(glm::vec3 translation_vector, glm::vec3 rotation_vector, GLfloat angle, GLfloat scale_factor);
/* Mesh Functions*/
// Genera i buffer per la mesh in input e ne salva i puntatori di openGL
void generateAndLoadBuffers(bool generate, Mesh *mesh);
// legge un file obj ed inizializza i vector della mesh in input
void loadObjFile(string file_path, Mesh* mesh);
//Uses stb_image.h to read an image, then loads it.
GLuint loadTexture(string path);
//Loads 6 images to build a cube map
GLuint loadCubeMapTexture(string *face_textures);
//Surface Tangents calculations based on edges distances and uv distances, Bitangents are calculated in the vertex shader
void calculateTangents(Mesh *mesh);
// 2D fixed pipeline Font rendering on screen
void printToScreen();
