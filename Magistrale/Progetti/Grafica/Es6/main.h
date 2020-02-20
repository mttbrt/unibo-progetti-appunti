
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
#include "HUD_Logger.h"
#include "common.h"

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define WHEEL_UP 3
#define WHEEL_DOWN 4

#define NUM_SHADERS 8

using namespace std;

static int WindowWidth = 1120;
static int WindowHeight = 630;
GLfloat aspectRatio = 16.0f / 9.0f;

typedef struct {
    std::vector<glm::vec3> vertices;
    std::vector<glm::vec3> normals;
    std::vector<glm::vec2> texCoords;
    GLuint vertexArrayObjID;
    GLuint vertexBufferObjID;
    GLuint normalBufferObjID;
    GLuint uvBufferObjID;
} Mesh;

typedef enum {
    RED_PLASTIC,
    EMERALD,
    BRASS,
    SLATE,
    NO_MATERIAL
} MaterialType;

typedef enum {
    SHADING_GOURAUD,
    SHADING_PHONG,
    SHADING_BLINN,
    SHADING_TOON,
    ONLY_TEXTURE,
    PHONG_TEXTURE,
    PROCEDURAL_MAPPING,
} TorusStyle;

typedef struct {
    std::string name;
    glm::vec3 ambient;
    glm::vec3 diffuse;
    glm::vec3 specular;
    GLfloat shininess;
} Material;

typedef enum { // used also as index, don't modify order
    GOURAUD,
    PHONG,
    BLINN,
    TOON,
    TEXTURE_ONLY,
    TEXTURE_PHONG,
    PASS_THROUGH,
    WAVE
} ShadingType;

typedef struct {
    Mesh mesh;
    MaterialType material;
    ShadingType shading;
    GLuint textureID;
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
} LightShaderUniform;

typedef struct {
    GLuint P_Matrix_pointer;
    GLuint V_Matrix_pointer;
    GLuint M_Matrix_pointer;
} BaseShaderUniform;

// Main initialization funtion
void init();
// Display Funtion
void display();
// Reshape Function
void resize(int w, int h);
// Mouse Function
void mouseListener(int button, int state, int x, int y);
// Keyboard:  g traslate r rotate s scale x,y,z axis esc
void keyboardListener(unsigned char key, int x, int y);
// Special key arrow: select active object (arrows left,right)
void special(int key, int x, int y);
// Main Menu
void mainMenuFunction(int option);
// Sub menu for materials
void materialMenuFunction(int option);
// Build openGL menus
void buildOpenGLMenu();
// Trackball: Convert a 2D screen point into a 3D point on the semi-sphere trackball
glm::vec3 getTrackBallPoint(float x, float y);
// Trackball: rotate the position vector
void mouseMotionListener(int x, int y);
void zoom(float dir);
void horizontalPan(float dir);
void verticalPan(float dir);
void modifyModelMatrix(glm::vec4 translation_vector, glm::vec4 rotation_vector, GLfloat angle, GLfloat scale_factor);
// Mesh Functions
void computeTorus(Mesh *mesh);
// Procedural texture
void torusProceduralMapping(Object* obj);
// Generate the buffers VAO and VBO for a mesh
void generateAndLoadBuffers(bool generate, Mesh *mesh);
// Read an .obj polygon mesh
void loadObjFile(string file_path, Mesh* mesh);
//Uses stb_image.h to read an image, then loads it.
GLuint loadTexture(string path);
// Draw Axis and grid
void drawAxisAndGrid();
// 2D fixed pipeline Font rendering on screen
void printToScreen();