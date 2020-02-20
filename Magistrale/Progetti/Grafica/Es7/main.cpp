/******************************************************************************************
LAB 07
Gestione interattiva di una scena 3D mediante controllo da mouse e da tastiera.
I modelli geometrici in scena sono mesh poligonali in formato *.obj

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

// Camera structures
glm::vec4 cameraLookAt = {0.0, 0.0, 0.0, 1.0}, currentFocus = {0.0, 0.0, 0.0, 1.0};

int main(int argc, char** argv) {
    GLboolean GlewInitResult;
    glutInit(&argc, argv);
    glutSetOption(GLUT_MULTISAMPLE, 4);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH | GLUT_MULTISAMPLE);
    glutInitWindowSize(WindowWidth, WindowHeight);
    glutInitWindowPosition(100, 100);
    glutCreateWindow("Model Viewer with Shaders");

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

    refreshMonitor(32);

    glutMainLoop();

    return 1;
}

void initReflectiveSphere() {
    Mesh surface = {};
    loadObjFile(MeshDir + "sphere_n_t_smooth.obj", &surface);
    calculateTangents(&surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = cubeTexture;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::REFLECTION;
    obj.name = "Mirror Ball";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[0][0], objectsPos[0][1], objectsPos[0][2])), glm::vec3(2.0));
    objects.push_back(obj);
}

void initRefractiveObj() {
    Mesh surface = {};
    loadObjFile(MeshDir + "cube_n_t_flat.obj", &surface);
    calculateTangents(&surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = cubeTexture;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::REFRACTION;
    obj.name = "Glass Cube";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[1][0], objectsPos[1][1], objectsPos[1][2])), glm::vec3(2.0));
    objects.push_back(obj);
}

void initRock() {
    Mesh surface = {};
    loadObjFile(MeshDir + "sharprockfree.obj", &surface);
    calculateTangents(&surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = loadTexture(TextureDir + "sharprockfree_default_color.png");
    obj.normalTexID = loadTexture(TextureDir + "sharprockfree_default_nmap-dx.png");
    obj.material = MaterialType::SLATE;
    obj.shading = ShadingType::NORMAL_MAPPING;
    obj.name = "Sharpy Rock";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[2][0], objectsPos[2][1], objectsPos[2][2])), glm::vec3(0.05));
    objects.push_back(obj);
}

void initBrickColumn() {
    Mesh surface = {};
    loadObjFile(MeshDir + "column.obj", &surface);
    calculateTangents(&surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = loadTexture(TextureDir + "brickwall.jpg");
    obj.normalTexID = loadTexture(TextureDir + "brickwall_normal.jpg");
    obj.material = MaterialType::SLATE;
    obj.shading = ShadingType::NORMAL_MAPPING;
    obj.name = "Brick Wall Normal Mapping";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[3][0], objectsPos[3][1], objectsPos[3][2])), glm::vec3(10.));
    objects.push_back(obj);
}

void initSkybox() {
    Mesh surface = {};
    loadObjFile(MeshDir + "cube_n_t_flat.obj", &surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = cubeTexture;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::SKYBOX;
    obj.name = "Skybox";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[4][0], objectsPos[4][1], objectsPos[4][2])), glm::vec3(500.));
    objects.push_back(obj);
}

void initWindows() {
    Mesh surface = {};
    loadObjFile(MeshDir + "window.obj", &surface);
    generateAndLoadBuffers(true, &surface);
    Object obj = {};
    obj.mesh = surface;
    obj.diffuseTexID = loadTexture(TextureDir + "winTex2.png");;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::TEXTURE_ONLY;
    obj.name = "Little Window";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[5][0], objectsPos[5][1], objectsPos[5][2])), glm::vec3(0.5, 0.5, 0.2));
    objects.push_back(obj);
    obj.name = "Big Window";
    obj.M = glm::scale(glm::translate(glm::mat4(1), glm::vec3(objectsPos[6][0], objectsPos[6][1], objectsPos[6][2])), glm::vec3(1., 1., 0.2));
    objects.push_back(obj);
}

void initLightObject() {
    Mesh sphereS = {};
    loadObjFile(MeshDir + "sphere_n_t_smooth.obj", &sphereS);
    generateAndLoadBuffers(true, &sphereS);
    Object obj = {};
    obj.mesh = sphereS;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::PASS_THROUGH;
    obj.name = "Light";
    obj.M = glm::scale(glm::translate(glm::mat4(1), light.position), glm::vec3(0.2));
    objects.push_back(obj);
}

void initAxis() {
    Mesh _grid = {};
    loadObjFile(MeshDir + "axis.obj", &_grid);
    generateAndLoadBuffers(true, &_grid);
    Object obj = {};
    obj.mesh = _grid;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::TEXTURE_ONLY;
    obj.diffuseTexID = loadTexture(TextureDir + "AXIS_TEX.png");
    obj.name = "Axis";
    obj.M = glm::scale(glm::mat4(1), glm::vec3(2.f));
    objects.push_back(obj);
}

void initGrid() {
    Mesh _grid = {};
    loadObjFile(MeshDir + "reference_grid.obj", &_grid);
    generateAndLoadBuffers(true, &_grid);
    Object obj = {};
    obj.mesh = _grid;
    obj.material = MaterialType::NO_MATERIAL;
    obj.shading = ShadingType::PASS_THROUGH;
    obj.name = "grid_";
    obj.M = glm::scale(glm::mat4(1), glm::vec3(2.f, 2.f, 2.f));
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
    // Blending set up
    glEnable(GL_BLEND);
    // The blending function tells the pipeline how to mix the color of a transparent object with the background.
    // The factor of the source color is the source color alpha,
    // The factor of the destination color is calculated as 1 - alpha of the source color
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // Camera Setup
    ViewSetup = {};
    ViewSetup.position = glm::vec4(10.0, 10.0, 10.0, 0.0);
    ViewSetup.target = currentFocus;
    ViewSetup.upVector = glm::vec4(0.0, 1.0, 0.0, 0.0);
    PerspectiveSetup = {};
    PerspectiveSetup.aspect = (GLfloat)WindowWidth / (GLfloat)WindowHeight;
    PerspectiveSetup.fovY = 45.0f;
    PerspectiveSetup.far_plane = 2000.0f;
    PerspectiveSetup.near_plane = 0.1f;

    //Light initialization
    light.position = { 0.0, 10.0, 0.0 };
    light.color = { 1.0, 1.0, 1.0 };
    light.power = 2.f;

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
    materials[MaterialType::NO_MATERIAL].ambient = glm::vec3(1, 1, 1);
    materials[MaterialType::NO_MATERIAL].diffuse = glm::vec3(0, 0, 0);
    materials[MaterialType::NO_MATERIAL].specular = glm::vec3(0, 0, 0);
    materials[MaterialType::NO_MATERIAL].shininess = 1.f;

    // SHADERS configuration section
    shadersIDs.resize(NUM_SHADERS);
    lightUniforms.resize(NUM_LIGHT_SHADERS); // allocate space for uniforms with light
    baseUniforms.resize(NUM_SHADERS); // allocate space for uniforms of all shaders

    //NORMAL_MAPPING Shader loading
    shadersIDs[NORMAL_MAPPING] = initShader(ShaderDir + "v_normal_map.glsl", ShaderDir + "f_normal_map.glsl");
    BaseShaderUniform base_unif = {};
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "camera_position");
    baseUniforms[ShadingType::NORMAL_MAPPING] = base_unif;
    LightShaderUniform light_unif = {};
    light_unif.material_ambient = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "material.ambient");
    light_unif.material_diffuse = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "material.diffuse");
    light_unif.material_specular = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "material.specular");
    light_unif.material_shininess = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "material.shininess");
    light_unif.light_position_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "light.position");
    light_unif.light_color_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "light.color");
    light_unif.light_power_pointer = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "light.power");
    light_unif.diffuse_sampler = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "diffuseMap");
    light_unif.normal_sampler = glGetUniformLocation(shadersIDs[NORMAL_MAPPING], "normalMap");
    lightUniforms[ShadingType::NORMAL_MAPPING] = light_unif;
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[NORMAL_MAPPING]);
    //Shader uniforms initialization
    glUniform3f(lightUniforms[NORMAL_MAPPING].light_position_pointer, light.position.x, light.position.y, light.position.z);
    glUniform3f(lightUniforms[NORMAL_MAPPING].light_color_pointer, light.color.r, light.color.g, light.color.b);
    glUniform1f(lightUniforms[NORMAL_MAPPING].light_power_pointer, light.power);
    glUniform1i(lightUniforms[NORMAL_MAPPING].diffuse_sampler, 0); // index for GL_TEXTURE0
    glUniform1i(lightUniforms[NORMAL_MAPPING].normal_sampler, 1); // index for GL_TEXTURE1

    //SKYBOX Shader loading
    shadersIDs[SKYBOX] = initShader(ShaderDir + "v_skybox.glsl", ShaderDir + "f_skybox.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[SKYBOX], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[SKYBOX], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[SKYBOX], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[SKYBOX], "camera_position");
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[SKYBOX]);
    glUniform1i(glGetUniformLocation(shadersIDs[SKYBOX], "skybox"), 0);
    baseUniforms[ShadingType::SKYBOX] = base_unif;

    //REFLECTION Shader loading
    shadersIDs[REFLECTION] = initShader(ShaderDir + "v_reflection.glsl", ShaderDir + "f_reflection.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[REFLECTION], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[REFLECTION], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[REFLECTION], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[REFLECTION], "camera_position");
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[REFLECTION]);
    glUniform1i(glGetUniformLocation(shadersIDs[REFLECTION], "reflection"), 0);
    baseUniforms[ShadingType::REFLECTION] = base_unif;

    //REFRACTION Shader loading
    shadersIDs[REFRACTION] = initShader(ShaderDir + "v_refraction.glsl", ShaderDir + "f_refraction.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[REFRACTION], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[REFRACTION], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[REFRACTION], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[REFRACTION], "camera_position");
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[REFRACTION]);
    glUniform1i(glGetUniformLocation(shadersIDs[REFRACTION], "refraction"), 0);
    baseUniforms[ShadingType::REFRACTION] = base_unif;

    //Texture Shader loading
    shadersIDs[TEXTURE_ONLY] = initShader(ShaderDir + "v_texture.glsl", ShaderDir + "f_texture.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[TEXTURE_ONLY], "camera_position");
    baseUniforms[ShadingType::TEXTURE_ONLY] = base_unif;

    //Pass-Through Shader loading
    shadersIDs[PASS_THROUGH] = initShader(ShaderDir + "v_passthrough.glsl", ShaderDir + "f_passthrough.glsl");
    //Otteniamo i puntatori alle variabili uniform per poterle utilizzare in seguito
    base_unif.P_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "P");
    base_unif.V_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "V");
    base_unif.M_Matrix_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "M");
    base_unif.camera_position_pointer = glGetUniformLocation(shadersIDs[PASS_THROUGH], "camera_position");
    baseUniforms[ShadingType::PASS_THROUGH] = base_unif;
    //Rendiamo attivo lo shader
    glUseProgram(shadersIDs[PASS_THROUGH]);
    glUniform4fv(glGetUniformLocation(shadersIDs[PASS_THROUGH], "Color"), 1, value_ptr(glm::vec4(1.0, 1.0, 1.0, 1.0)));

    // Cube Texture
    string skybox_textures[6] = {
            TextureDir + "cape_cubemap/" + "posx2.jpg",//GL_TEXTURE_CUBE_MAP_POSITIVE_X 	Right
            TextureDir + "cape_cubemap/" + "negx2.jpg",//GL_TEXTURE_CUBE_MAP_NEGATIVE_X 	Left
            TextureDir + "cape_cubemap/" + "posy2.jpg",//GL_TEXTURE_CUBE_MAP_POSITIVE_Y 	Top
            TextureDir + "cape_cubemap/" + "negy2.jpg",//GL_TEXTURE_CUBE_MAP_NEGATIVE_Y 	Bottom
            TextureDir + "cape_cubemap/" + "negz2.jpg",//GL_TEXTURE_CUBE_MAP_POSITIVE_Z 	Back
            TextureDir + "cape_cubemap/" + "posz2.jpg" //GL_TEXTURE_CUBE_MAP_NEGATIVE_Z 	Front
    };
    cubeTexture = loadCubeMapTexture(skybox_textures);

    //////////////////////////////////////////////////////////////////////
    //				OBJECTS IN SCENE
    //////////////////////////////////////////////////////////////////////

    // a cool rock
    initRock();

    //Brick wall with normal mapping
    initBrickColumn();

    //Full reflective sphere
    initReflectiveSphere();

    //Full refractive sphere
    initRefractiveObj();

    //Reference point of the position of the light
    initLightObject();

    // White Axis
    initAxis();

    // White Grid for reference
    initGrid();

    //Skybox cube
    initSkybox();

    // white window
    initWindows();
}

void display() {
    glClearColor(0.4, 0.4, 0.4, 1);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    // Reorder windows according to distance from camera
    Object window1 = objects[8];
    glm::vec4 w1pos = window1.M[3];
    Object window2 = objects[9];
    glm::vec4 w2pos = window2.M[3];

    if(glm::length(ViewSetup.position - w2pos) > glm::length(ViewSetup.position - w1pos)) {
        objects[8] = window2;
        objects[9] = window1;
    }

    for (int i = 0; i < objects.size(); i++) {
        //Shader selection
        switch (objects[i].shading) {
            case ShadingType::NORMAL_MAPPING:
                glUseProgram(shadersIDs[NORMAL_MAPPING]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[NORMAL_MAPPING].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                //Material loading
                glUniform3f(lightUniforms[NORMAL_MAPPING].light_position_pointer, light.position.x, light.position.y, light.position.z);
                glUniform3fv(lightUniforms[NORMAL_MAPPING].material_ambient, 1, glm::value_ptr(materials[objects[i].material].ambient));
                glUniform3fv(lightUniforms[NORMAL_MAPPING].material_diffuse, 1, glm::value_ptr(materials[objects[i].material].diffuse));
                glUniform3fv(lightUniforms[NORMAL_MAPPING].material_specular, 1, glm::value_ptr(materials[objects[i].material].specular));
                glUniform1f(lightUniforms[NORMAL_MAPPING].material_shininess, materials[objects[i].material].shininess);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, objects[i].diffuseTexID);
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, objects[i].normalTexID);
                break;
            case ShadingType::SKYBOX:
                glUseProgram(shadersIDs[SKYBOX]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[SKYBOX].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_CUBE_MAP, objects[i].diffuseTexID);
                // We draw the skybox first, so if we disable writes on the Z-BUFFER,
                // all later draw calls will overwrite the skybox pixels for sure,
                // no matter how distant is the skybox.
                // glDepthMask(GL_FALSE);
                break;
            case ShadingType::REFLECTION:
                glUseProgram(shadersIDs[REFLECTION]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[REFLECTION].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_CUBE_MAP, objects[i].diffuseTexID);
                break;
            case ShadingType::REFRACTION:
                glUseProgram(shadersIDs[REFRACTION]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[REFRACTION].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_CUBE_MAP, objects[i].diffuseTexID);
                break;
            case ShadingType::TEXTURE_ONLY:
                glUseProgram(shadersIDs[TEXTURE_ONLY]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[TEXTURE_ONLY].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, objects[i].diffuseTexID);
                break;
            case ShadingType::PASS_THROUGH:
                glUseProgram(shadersIDs[PASS_THROUGH]);
                // Caricamento matrice trasformazione del modello
                glUniformMatrix4fv(baseUniforms[PASS_THROUGH].M_Matrix_pointer, 1, GL_FALSE, value_ptr(objects[i].M));
                break;
            default:
                break;
        }
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glBindVertexArray(objects[i].mesh.vertexArrayObjID);
        glDrawArrays(GL_TRIANGLES, 0, objects[i].mesh.vertices.size());

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDepthMask(GL_TRUE);
    }

    cameraLookAt = currentFocus;

    // OLD fixed pipeline for simple graphics and symbols
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

    for (int i = 0; i < shadersIDs.size(); i++) {
        glUseProgram(shadersIDs[i]);
        glUniformMatrix4fv(baseUniforms[i].P_Matrix_pointer, 1, GL_FALSE, value_ptr(P));
        glUniformMatrix4fv(baseUniforms[i].V_Matrix_pointer, 1, GL_FALSE, value_ptr(V));
        glUniform3fv(baseUniforms[i].camera_position_pointer, 1, glm::value_ptr(glm::vec3(ViewSetup.position)));
    }
}

void refreshMonitor(int millis) {
    glutPostRedisplay();
    glutTimerFunc(millis, refreshMonitor, millis);
}

void mouseListener(int button, int state, int x, int y) {
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
            if(selectedObj != 2 && selectedObj != 3) modifyModelMatrix(axis * amount, axis, 0.0f, 1.0f);
            break;
        case ROTATING:
            modifyModelMatrix(glm::vec4(0), axis, amount * 20.0f, 1.0f);
            break;
        case SCALING:
            modifyModelMatrix(glm::vec4(0), axis, 0.0f, 1.0f + amount);
            break;
        case NAVIGATION:
            if (button == WHEEL_UP)
                zoom(CAMERA_ZOOM_SPEED);
            else if (button == WHEEL_DOWN)
                zoom(-CAMERA_ZOOM_SPEED);
            break;
        default:
            break;
    }
}

void mouseMotionListener(int x, int y) {
    // Spostamento su trackball del vettore posizione Camera
    if (!movingTrackball) {
        return;
    }
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

void mainMenuFunc(int option) {
    switch (option) {
        case MenuOption::WIRE_FRAME: glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            break;
        case MenuOption::FACE_FILL: glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            break;
        case MenuOption::CULLING_ON: glEnable(GL_CULL_FACE);
            break;
        case MenuOption::CULLING_OFF: glDisable(GL_CULL_FACE);
            break;
        case MenuOption::CHANGE_TO_OCS: TransformMode = OCS;
            break;
        case MenuOption::CHANGE_TO_WCS: TransformMode = WCS;
            break;
        default:
            break;
    }
}

void materialMenuFunction(int option) {
    objects[selectedObj].material = (MaterialType)option;
}

void buildOpenGLMenu() {
    int materialSubMenu = glutCreateMenu(materialMenuFunction);

    glutAddMenuEntry(materials[MaterialType::RED_PLASTIC].name.c_str(), MaterialType::RED_PLASTIC);
    glutAddMenuEntry(materials[MaterialType::EMERALD].name.c_str(), MaterialType::EMERALD);
    glutAddMenuEntry(materials[MaterialType::BRASS].name.c_str(), MaterialType::BRASS);
    glutAddMenuEntry(materials[MaterialType::SLATE].name.c_str(), MaterialType::SLATE);

    glutCreateMenu(mainMenuFunc); // richiama mainMenuFunc() alla selezione di una voce menu
    glutAddMenuEntry("Opzioni", -1); //-1 significa che non si vuole gestire questa riga
    glutAddMenuEntry("", -1);
    glutAddMenuEntry("Wireframe", MenuOption::WIRE_FRAME);
    glutAddMenuEntry("Face fill", MenuOption::FACE_FILL);
    glutAddMenuEntry("Culling: ON", MenuOption::CULLING_ON);
    glutAddMenuEntry("Culling: OFF", MenuOption::CULLING_OFF);
    glutAddSubMenu("Material", materialSubMenu);
    glutAddMenuEntry("World coordinate system", MenuOption::CHANGE_TO_WCS);
    glutAddMenuEntry("Object coordinate system", MenuOption::CHANGE_TO_OCS);
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

void modifyModelMatrix(glm::vec3 translation_vector, glm::vec3 rotation_vector, GLfloat angle, GLfloat scale_factor) {
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
        // Genero 1 Buffer Object per le tangenti ai vertici (normal Maps)
        glGenBuffers(1, &mesh->tgBufferObjID);
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

    glBindBuffer(GL_ARRAY_BUFFER, mesh->tgBufferObjID);
    glBufferData(GL_ARRAY_BUFFER, mesh->tangents.size() * sizeof(glm::vec3), mesh->tangents.data(), GL_STATIC_DRAW);
    glEnableVertexAttribArray(3);
    glVertexAttribPointer(
            3,					// attribute index in the shader
            3,                  // size
            GL_FLOAT,           // type
            false,              // normalized
            0,					// stride
            (void*)0            // array buffer offset
    );

    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glDisableVertexAttribArray(2);
    glDisableVertexAttribArray(3);
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
    vector<glm::vec3> tmp_vertices, tmp_normals;
    vector<glm::vec2> tmp_uvs;

    char lineHeader[128];
    while (fscanf(file, "%s", lineHeader) != EOF) {
        if (strcmp(lineHeader, "v") == 0) {
            glm::vec3 vertex;
            fscanf(file, " %f %f %f\n", &vertex.x, &vertex.y, &vertex.z);
            tmp_vertices.push_back(vertex);
        }
        else if (strcmp(lineHeader, "vn") == 0) {
            glm::vec3 normal;
            fscanf(file, " %f %f %f\n", &normal.x, &normal.y, &normal.z);
            tmp_normals.push_back(normal);
        }
        else if (strcmp(lineHeader, "vt") == 0) {
            glm::vec2 uv;
            fscanf(file, " %f %f\n", &uv.x, &uv.y);
            uv.y = 1 - uv.y;
            tmp_uvs.push_back(uv);
        }
        else if (strcmp(lineHeader, "f") == 0) {
            GLuint v_a, v_b, v_c; // index in position array
            GLuint n_a, n_b, n_c; // index in normal array
            GLuint t_a, t_b, t_c; // index in UV array

            fscanf(file, "%s", lineHeader);
            if (strstr(lineHeader, "//")) { // case: v//n v//n v//n
                sscanf(lineHeader, "%d//%d", &v_a, &n_a);
                fscanf(file, "%d//%d %d//%d\n", &v_b, &n_b, &v_c, &n_c);
                n_a--, n_b--, n_c--;
                normalIndices.push_back(n_a); normalIndices.push_back(n_b); normalIndices.push_back(n_c);
            }
            else if (strstr(lineHeader, "/")) {// case: v/t/n v/t/n v/t/n
                sscanf(lineHeader, "%d/%d/%d", &v_a, &t_a, &n_a);
                fscanf(file, "%d/%d/%d %d/%d/%d\n", &v_b, &t_b, &n_b, &v_c, &t_c, &n_c);
                n_a--, n_b--, n_c--;
                t_a--, t_b--, t_c--;
                normalIndices.push_back(n_a); normalIndices.push_back(n_b); normalIndices.push_back(n_c);
                uvIndices.push_back(t_a); uvIndices.push_back(t_b); uvIndices.push_back(t_c);
            }
            else {// case: v v v
                sscanf(lineHeader, "%d", &v_a);
                fscanf(file, "%d %d\n", &v_b, &v_c);
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
        for (int i = 0; i < vertexIndices.size(); i += 3)
        {
            GLushort ia = vertexIndices[i];
            GLushort ib = vertexIndices[i + 1];
            GLushort ic = vertexIndices[i + 2];
            glm::vec3 normal = glm::normalize(glm::cross(
                    glm::vec3(tmp_vertices[ib]) - glm::vec3(tmp_vertices[ia]),
                    glm::vec3(tmp_vertices[ic]) - glm::vec3(tmp_vertices[ia])));
            tmp_normals[i / 3] = normal;
            //Put an index to the normal for all 3 vertex of the face
            normalIndices.push_back(i / 3);
            normalIndices.push_back(i / 3);
            normalIndices.push_back(i / 3);
        }
    }
    //if texture coordinates were not included we fake them
    if (tmp_uvs.size() == 0) {
        tmp_uvs.push_back(glm::vec2(0)); //dummy uv
        for (int i = 0; i < vertexIndices.size(); i += 3)
        {
            // The UV is dummy
            uvIndices.push_back(0);
            uvIndices.push_back(0);
            uvIndices.push_back(0);
        }
    }
    // We prepare the data for glDrawArrays calls, this is a simple but non optimal way of storing mesh data.
    // However, you could optimize the mesh data using a index array for both vertex positions,
    // normals and textures and later use glDrawElements
    int i = 0;
    // Now following the index arrays, we build the final arrays that will contain the data for glDrawArray...
    for (int i = 0; i < vertexIndices.size(); i++) {

        mesh->vertices.push_back(tmp_vertices[vertexIndices[i]]);
        mesh->normals.push_back(tmp_normals[normalIndices[i]]);
        mesh->texCoords.push_back(tmp_uvs[uvIndices[i]]);
    }
}

GLuint loadTexture(string path) {
    GLint width, height, texChannels, format;
    GLuint textureID;
    stbi_uc* pixels = stbi_load(path.data(), &width, &height, &texChannels, 0);
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

    switch (texChannels) {
        case 1:	format = GL_RED;
            break;
        case 3: format = GL_RGB;
            break;
        case 4: format = GL_RGBA;
            break;
        default: format = GL_RGB;
            break;
    }

    // data loading in memory
    glTexImage2D(GL_TEXTURE_2D,  //the target
                 0, // the mip map level we want to generate
                 format,
                 width,
                 height,
                 0, // border, leave 0
                 format, // we assume is a RGB color image with 24 bit depth per pixel
                 GL_UNSIGNED_BYTE, // the data type
                 pixels);
    glGenerateMipmap(GL_TEXTURE_2D);// automatic mip maps generation

    stbi_image_free(pixels);
    return textureID;
}

GLuint loadCubeMapTexture(string *face_textures) {
    GLuint textureID;
    glGenTextures(1, &textureID);
    glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

    int width, height, channels;
    for (unsigned int i = 0; i < 6; i++)
    {
        unsigned char *data = stbi_load(face_textures[i].c_str(), &width, &height, &channels, STBI_rgb_alpha);
        if (data)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                         0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);// automatic mip maps generation
            stbi_image_free(data);
        }
        else
        {
            std::cerr << "\nFailed to load texture image! --> " << face_textures[i] << std::endl;
            std::getchar();
            exit(EXIT_FAILURE);
        }
    }
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

    return textureID;
}

void calculateTangents(Mesh *mesh) {
    for (int i = 0; i < mesh->vertices.size(); i += 3) {
        glm::vec3 tangent;
        glm::vec3 edge1 = mesh->vertices[i + 1] - mesh->vertices[i];
        glm::vec3 edge2 = mesh->vertices[i + 2] - mesh->vertices[i];
        glm::vec2 deltaUV1 = mesh->texCoords[i + 1] - mesh->texCoords[i];
        glm::vec2 deltaUV2 = mesh->texCoords[i + 2] - mesh->texCoords[i];
        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        tangent = f * (deltaUV2.y * edge1 - deltaUV1.y * edge2);
        tangent = glm::normalize(tangent);
        mesh->tangents.push_back(tangent);
        mesh->tangents.push_back(tangent);
        mesh->tangents.push_back(tangent);
    }
}

void printToScreen() {
    string axis = "Axis: ";
    string mode = "Navigate/Modify: ";
    string obj = "Object: " + objects[selectedObj].name;
    string ref = "WCS/OCS: ";
    string shader = "Shader: ";
    switch (objects[selectedObj].shading) {
        case ShadingType::NORMAL_MAPPING:shader += "NORMAL_MAPPING";
            break;
        case ShadingType::TEXTURE_PHONG:shader += "TEXTURE_PHONG";
            break;
        case ShadingType::TEXTURE_ONLY:shader += "TEXTURE_ONLY";
            break;
        case ShadingType::REFLECTION:shader += "REFLECTION";
            break;
        case ShadingType::REFRACTION:shader += "REFRACTION";
            break;
        case ShadingType::PASS_THROUGH:shader += "PASS_THROUGH";
            break;
        case ShadingType::SKYBOX:shader += "SKYBOX";
            break;
        default:
            break;
    }
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

    // Handle output info layout
    gluOrtho2D(0, WindowHeight * aspectRatio, 0, WindowHeight);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    vector<string> lines;
    lines.push_back(shader);
    lines.push_back(obj);
    lines.push_back(axis);
    lines.push_back(mode);
    lines.push_back(ref);
    glDisable(GL_DEPTH_TEST);
    HUD_Logger::get()->printInfo(lines);
    glEnable(GL_DEPTH_TEST);

    resize(WindowWidth, WindowHeight);
}