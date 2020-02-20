// Spider Game

#include <iostream>
#include <sstream>
#include <GL/glew.h>
#include <GL/freeglut.h>
#include <tgmath.h>
#include <vector>

#define PI 3.14159265358979323846
#define nvertices 24
using namespace std;

typedef struct { double r, g, b, a; } ColorRGBA;
typedef struct { float x, y, z; } Point;
typedef struct { Point min, max; } AABB;
typedef struct {
    float drag;
    Point coord;
    Point factors;
    ColorRGBA color;
} Particle;

// Window
int width = 1280;
int height = 720;

// CLOUDS
float xCloud1 = 2 * width / 5, yCloud1 = 4 * height / 5;
float xCloud2 = 3 * width / 5, yCloud2 = height / 5;
int cloudVertices = 216, cloudExpansion = 50;
ColorRGBA *cloudColor;
Point *cloud;
GLuint vboCloud, vboCloudColor; // VBO

// SPIDER
float xSpider = 0.3 * width - 15, ySpider = 0.3 * height;
int spiderVertices = 198, spiderExpansion = 20;
float spiderAngle = 0;
bool increaseAngle = true, spiderJumping = false;
ColorRGBA *spiderColor;
Point *spider;
AABB spiderAABB, spiderAABB_WCS;
GLuint vboSpider, vboSpiderColor; // VBO

// FLIES
float xLFly1 = 0.3 * width + 30, yLFly1 = -20;
float xLFly2 = 0.3 * width + 30, yLFly2 = -20;
float xRFly1 = 0.7 * width - 30, yRFly1 = 4 * height / 5;
float xRFly2 = 0.7 * width - 30, yRFly2 = -20;
int flyVertices = 216, flyExpansion = 10;
ColorRGBA *flyColor;
Point *fly;
AABB flyAABB;
AABB lFly1AABB_WCS, lFly2AABB_WCS, rFly1AABB_WCS, rFly2AABB_WCS;
GLuint vboFly, vboFlyColor; // VBO

// TREES
int treeVertices = 6;

// Left tree
float xLTree = 0, yLTree = height;
ColorRGBA *lTreeColor;
Point *lTree;
GLuint vboLTree, vboLTreeColor; // VBO

// Right tree
float xRTree = width, yRTree = height;
ColorRGBA *rTreeColor;
Point *rTree;
GLuint vboRTree, vboRTreeColor; // VBO

// BRANCHES
int branchVertices = 78, branchExpansion = 80;

// Left branch
float xLBranch1 = 0.3 * width, yLBranch1 = 4 * height / 5;
float xLBranch2 = 0.3 * width, yLBranch2 = -30;
ColorRGBA *lBranchColor;
Point *lBranch;
AABB lBranchAABB;
AABB lBranch1AABB_WCS, lBranch2AABB_WCS;
GLuint vboLBranch, vboLBranchColor; // VBO

// Right branch
float xRBranch1 = 0.7 * width, yRBranch1 = 1 * height / 5;
float xRBranch2 = 0.7 * width, yRBranch2 = -30;
ColorRGBA *rBranchColor;
Point *rBranch;
AABB rBranchAABB;
AABB rBranch1AABB_WCS, rBranch2AABB_WCS;
GLuint vboRBranch, vboRBranchColor; // VBO

// Colors
ColorRGBA darkGray = {0.1, 0.1, 0.1, 1.0};
ColorRGBA white = {1.0, 1.0, 1.0, 1.0};
ColorRGBA whiteTrans = {1.0, 1.0, 1.0, 0.75};
ColorRGBA brown = {0.7, 0.45, 0.22, 1.0};
ColorRGBA darkBrown = {0.4, 0.26, 0.13, 1.0};
ColorRGBA green = {0.29, 0.7, 0.22, 1.0};
ColorRGBA red = {0.86, 0.16, 0.12, 1.0};

// Particles
vector<Particle> particles;

// Utils
int fliesEaten = 0;
int bestScore = 0;

float objectsSpeed = 3;

float jumpVelocityX, jumpVelocityY;
float jumpGravity = 0.54f;
bool jumpToRight = true;

float jumpTime = 0;

bool endGame = false;



double randomInRange(double min, double max) {
    return min + static_cast <double> (rand()) / (static_cast <double> (RAND_MAX / (max - min)));
}

string floatToString(float value) {
    stringstream ss;
    ss << value;
    return ss.str();
}

void bitmapOutput(int x, int y, int z, string s, void *font) {
    glRasterPos3f(x, y, 0);
    for (int i = 0; i < s.length(); i++)
        glutBitmapCharacter(font, s[i]);
}

void buildCircle(float cx, float cy, float radiusx, float radiusy, Point *circle) {
    float full = (2 * PI) / nvertices;
    int vert = 0;

    for(int i = 0; i < nvertices; i++) {
        circle[vert].x = cx + cos((double)i * full) * radiusx;
        circle[vert].y = cy + sin((double)i * full) * radiusy;
        circle[vert].z = 0;

        circle[vert + 1].x = cx + cos((double)(i + 1) * full) * radiusx;
        circle[vert + 1].y = cy + sin((double)(i + 1) * full) * radiusy;
        circle[vert + 1].z = 0;

        circle[vert + 2].x = cx;
        circle[vert + 2].y = cy;
        circle[vert + 2].z = 0;

        vert += 3;
    }
}

void buildSemicircle(float cx, float cy, float radiusx, float radiusy, Point *semicircle) {
    float half = PI / nvertices;
    int vert = 0;

    for(int i = 0; i < nvertices; i++) {
        semicircle[vert].x = cx + cos((double)i * half) * radiusx;
        semicircle[vert].y = cy + sin((double)i * half) * radiusy;
        semicircle[vert].z = 0;

        semicircle[vert + 1].x = cx + cos((double)(i + 1) * half) * radiusx;
        semicircle[vert + 1].y = cy + sin((double)(i + 1) * half) * radiusy;
        semicircle[vert + 1].z = 0;

        semicircle[vert + 2].x = cx;
        semicircle[vert + 2].y = cy;
        semicircle[vert + 2].z = 0;

        vert += 3;
    }
}

void buildSpider(Point* spider, ColorRGBA* spiderColor) {
    Point *head, *body;
    head = new Point[72];
    body = new Point[72];

    // Leg 1 L
    int cont = 0;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = 0; spider[cont + 1].z = 0;
    spider[cont + 2].x = -1.4; spider[cont + 2].y = -0.25; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 3;
    spider[cont].x = -1.4; spider[cont].y = -0.25; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = 0; spider[cont + 1].z = 0;
    spider[cont + 2].x = -2; spider[cont + 2].y = -1; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 2 L
    cont = 6;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -0.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = -1.4; spider[cont + 2].y = -0.75; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 9;
    spider[cont].x = -1.4; spider[cont].y = -0.75; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -0.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = -2; spider[cont + 2].y = -1.5; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 3 L
    cont = 12;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -1; spider[cont + 1].z = 0;
    spider[cont + 2].x = -1.4; spider[cont + 2].y = -1.25; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 15;
    spider[cont].x = -1.4; spider[cont].y = -1.25; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -1; spider[cont + 1].z = 0;
    spider[cont + 2].x = -2; spider[cont + 2].y = -2; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 4 L
    cont = 18;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -1.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = -1.4; spider[cont + 2].y = -1.75; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 21;
    spider[cont].x = -1.4; spider[cont].y = -1.75; spider[cont].z = 0;
    spider[cont + 1].x = -1.4; spider[cont + 1].y = -1.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = -2; spider[cont + 2].y = -2.5; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 1 R
    cont = 24;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = 0; spider[cont + 1].z = 0;
    spider[cont + 2].x = 1.4; spider[cont + 2].y = -0.25; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 27;
    spider[cont].x = 1.4; spider[cont].y = -0.25; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = 0; spider[cont + 1].z = 0;
    spider[cont + 2].x = 2; spider[cont + 2].y = -1; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 2 R
    cont = 30;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -0.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = 1.4; spider[cont + 2].y = -0.75; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 33;
    spider[cont].x = 1.4; spider[cont].y = -0.75; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -0.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = 2; spider[cont + 2].y = -1.5; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 3 R
    cont = 36;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -1; spider[cont + 1].z = 0;
    spider[cont + 2].x = 1.4; spider[cont + 2].y = -1.25; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 39;
    spider[cont].x = 1.4; spider[cont].y = -1.25; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -1; spider[cont + 1].z = 0;
    spider[cont + 2].x = 2; spider[cont + 2].y = -2; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Leg 4 R
    cont = 42;
    spider[cont].x = 0; spider[cont].y = -1.4; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -1.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = 1.4; spider[cont + 2].y = -1.75; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    cont = 45;
    spider[cont].x = 1.4; spider[cont].y = -1.75; spider[cont].z = 0;
    spider[cont + 1].x = 1.4; spider[cont + 1].y = -1.5; spider[cont + 1].z = 0;
    spider[cont + 2].x = 2; spider[cont + 2].y = -2.5; spider[cont + 2].z = 0;

    spiderColor[cont] = darkGray; spiderColor[cont + 1] = darkGray; spiderColor[cont + 2] = darkGray;

    // Head
    buildCircle(0, -0.2, 0.5, 0.5, head);
    cont = 48;
    for(int i = 0; i < 72; i++) spider[cont + i] = head[i];
    for(int i = 0; i < nvertices; i++) {
        spiderColor[cont + i * 3] = darkGray;
        spiderColor[cont + i * 3 + 1] = darkGray;
        spiderColor[cont + i * 3 + 2] = darkGray;
    }

    // Eye 1
    cont = 120;
    spider[cont].x = -0.1; spider[cont].y = -0.2; spider[cont].z = 0;
    spider[cont + 1].x = -0.3; spider[cont + 1].y = -0.2; spider[cont + 1].z = 0;
    spider[cont + 2].x = -0.3; spider[cont + 2].y = 0; spider[cont + 2].z = 0;

    spiderColor[cont] = white; spiderColor[cont + 1] = white; spiderColor[cont + 2] = white;

    // Eye 2
    cont = 123;
    spider[cont].x = 0.1; spider[cont].y = -0.2; spider[cont].z = 0;
    spider[cont + 1].x = 0.3; spider[cont + 1].y = -0.2; spider[cont + 1].z = 0;
    spider[cont + 2].x = 0.3; spider[cont + 2].y = 0; spider[cont + 2].z = 0;

    spiderColor[cont] = white; spiderColor[cont + 1] = white; spiderColor[cont + 2] = white;

    // Body
    buildCircle(0, -1.4, 1, 1, body);
    cont = 126;
    for(int i = 0; i < 72; i++) spider[cont + i] = body[i];
    for(int i = 0; i < nvertices; i++) {
        spiderColor[cont + i * 3] = darkGray;
        spiderColor[cont + i * 3 + 1] = darkGray;
        spiderColor[cont + i * 3 + 2] = red;
    }
}

void buildCloud(Point* cloud, ColorRGBA* cloudColor) {
    Point *left, *center, *right;
    left = new Point[72];
    center = new Point[72];
    right = new Point[72];

    buildSemicircle(0, 0, 1, 1, center);
    int cont = 0;
    for(int i = 0; i < 72; i++) cloud[cont + i] = center[i];
    for(int i = 0; i < nvertices; i++) {
        cloudColor[i * 3] = white;
        cloudColor[i * 3 + 1] = white;
        cloudColor[i * 3 + 2] = white;
    }

    buildSemicircle(-1.25, 0, 0.4, 0.4, left);
    cont = 72;
    for(int i = 0; i < 72; i++) cloud[cont + i] = left[i];
    for(int i = 0; i < nvertices; i++) {
        cloudColor[cont + i * 3] = white;
        cloudColor[cont + i * 3 + 1] = white;
        cloudColor[cont + i * 3 + 2] = white;
    }

    buildSemicircle(1.25, 0, 0.65, 0.65, right);
    cont = 144;
    for(int i = 0; i < 72; i++) cloud[cont + i] = right[i];
    for(int i = 0; i < nvertices; i++) {
        cloudColor[cont + i * 3] = white;
        cloudColor[cont + i * 3 + 1] = white;
        cloudColor[cont + i * 3 + 2] = white;
    }
}

void buildFly(Point* fly, ColorRGBA* flyColor) {
    Point *leftWing, *body, *rightWing;
    leftWing = new Point[72];
    body = new Point[72];
    rightWing = new Point[72];

    buildCircle(0, 0, 1, 1.5, body);
    int cont = 0;
    for(int i = 0; i < 72; i++) fly[cont + i] = body[i];
    for(int i = 0; i < nvertices; i++) {
        flyColor[i * 3] = darkGray;
        flyColor[i * 3 + 1] = darkGray;
        flyColor[i * 3 + 2] = darkGray;
    }

    buildCircle(-0.8, -1, 0.7, 1.2, leftWing);
    cont = 72;
    for(int i = 0; i < 72; i++) fly[cont + i] = leftWing[i];
    for(int i = 0; i < nvertices; i++) {
        flyColor[cont + i * 3] = whiteTrans;
        flyColor[cont + i * 3 + 1] = whiteTrans;
        flyColor[cont + i * 3 + 2] = whiteTrans;
    }

    buildCircle(0.8, -1, 0.7, 1.2, rightWing);
    cont = 144;
    for(int i = 0; i < 72; i++) fly[cont + i] = rightWing[i];
    for(int i = 0; i < nvertices; i++) {
        flyColor[cont + i * 3] = whiteTrans;
        flyColor[cont + i * 3 + 1] = whiteTrans;
        flyColor[cont + i * 3 + 2] = whiteTrans;
    }
}

void buildLTree(Point* lTree, ColorRGBA* lTreeColor) {
    int cont = 0;
    lTree[cont].x = 0.1; lTree[cont].y = 0; lTree[cont].z = 0;
    lTree[cont + 1].x = 0.1; lTree[cont + 1].y = -width/height; lTree[cont + 1].z = 0;
    lTree[cont + 2].x = 0.3; lTree[cont + 2].y = 0; lTree[cont + 2].z = 0;

    lTreeColor[cont] = brown; lTreeColor[cont + 1] = brown; lTreeColor[cont + 2] = darkBrown;

    cont = 3;
    lTree[cont].x = 0.3; lTree[cont].y = 0; lTree[cont].z = 0;
    lTree[cont + 1].x = 0.1; lTree[cont + 1].y = -width/height; lTree[cont + 1].z = 0;
    lTree[cont + 2].x = 0.3; lTree[cont + 2].y = -width/height; lTree[cont + 2].z = 0;

    lTreeColor[cont] = darkBrown; lTreeColor[cont + 1] = brown; lTreeColor[cont + 2] = darkBrown;
}

void buildRTree(Point* rTree, ColorRGBA* rTreeColor) {
    int cont = 0;
    rTree[cont].x = -0.1; rTree[cont].y = 0; rTree[cont].z = 0;
    rTree[cont + 1].x = -0.1; rTree[cont + 1].y = -width/height; rTree[cont + 1].z = 0;
    rTree[cont + 2].x = -0.3; rTree[cont + 2].y = 0; rTree[cont + 2].z = 0;

    rTreeColor[cont] = brown; rTreeColor[cont + 1] = brown; rTreeColor[cont + 2] = darkBrown;

    cont = 3;
    rTree[cont].x = -0.3; rTree[cont].y = 0; rTree[cont].z = 0;
    rTree[cont + 1].x = -0.1; rTree[cont + 1].y = -width/height; rTree[cont + 1].z = 0;
    rTree[cont + 2].x = -0.3; rTree[cont + 2].y = -width/height; rTree[cont + 2].z = 0;

    rTreeColor[cont] = darkBrown; rTreeColor[cont + 1] = brown; rTreeColor[cont + 2] = darkBrown;
}

void buildLBranch(Point* lBranch, ColorRGBA* lBranchColor) {
    Point *leaf = new Point[72];

    int cont = 0;
    lBranch[cont].x = 0; lBranch[cont].y = 0.2; lBranch[cont].z = 0;
    lBranch[cont + 1].x = 0; lBranch[cont + 1].y = -0.2; lBranch[cont + 1].z = 0;
    lBranch[cont + 2].x = 1.6; lBranch[cont + 2].y = 0; lBranch[cont + 2].z = 0;

    lBranchColor[cont] = darkBrown; lBranchColor[cont + 1] = darkBrown; lBranchColor[cont + 2] = brown;

    cont = 3;
    lBranch[cont].x = 1.6; lBranch[cont].y = 0; lBranch[cont].z = 0;
    lBranch[cont + 1].x = 0.8; lBranch[cont + 1].y = -0.1; lBranch[cont + 1].z = 0;
    lBranch[cont + 2].x = 2.4; lBranch[cont + 2].y = -0.3; lBranch[cont + 2].z = 0;

    lBranchColor[cont] = brown; lBranchColor[cont + 1] = brown; lBranchColor[cont + 2] = darkBrown;

    buildCircle(1.5, 0.16, 0.08, 0.16, leaf);
    cont = 6;
    for(int i = 0; i < 72; i++) lBranch[cont + i] = leaf[i];
    for(int i = 0; i < nvertices; i++) {
        lBranchColor[cont + i * 3] = green;
        lBranchColor[cont + i * 3 + 1] = green;
        lBranchColor[cont + i * 3 + 2] = green;
    }
}

void buildRBranch(Point* lBranch, ColorRGBA* lBranchColor) {
    Point *leaf = new Point[72];

    int cont = 0;
    lBranch[cont].x = 0; lBranch[cont].y = 0.2; lBranch[cont].z = 0;
    lBranch[cont + 1].x = 0; lBranch[cont + 1].y = -0.2; lBranch[cont + 1].z = 0;
    lBranch[cont + 2].x = -1.6; lBranch[cont + 2].y = 0; lBranch[cont + 2].z = 0;

    lBranchColor[cont] = darkBrown; lBranchColor[cont + 1] = darkBrown; lBranchColor[cont + 2] = brown;

    cont = 3;
    lBranch[cont].x = -1.6; lBranch[cont].y = 0; lBranch[cont].z = 0;
    lBranch[cont + 1].x = -0.8; lBranch[cont + 1].y = -0.1; lBranch[cont + 1].z = 0;
    lBranch[cont + 2].x = -2.4; lBranch[cont + 2].y = -0.3; lBranch[cont + 2].z = 0;

    lBranchColor[cont] = brown; lBranchColor[cont + 1] = brown; lBranchColor[cont + 2] = darkBrown;

    buildCircle(-1.5, 0.16, 0.08, 0.16, leaf);
    cont = 6;
    for(int i = 0; i < 72; i++) lBranch[cont + i] = leaf[i];
    for(int i = 0; i < nvertices; i++) {
        lBranchColor[cont + i * 3] = green;
        lBranchColor[cont + i * 3 + 1] = green;
        lBranchColor[cont + i * 3 + 2] = green;
    }
}

void keyboardListener(unsigned char key, int x, int y) {
    switch (key) {
        case ' ':
            if(endGame) {
                xCloud1 = 2 * width / 5; yCloud1 = 4 * height / 5;
                xCloud2 = 3 * width / 5; yCloud2 = height / 5;

                xSpider = 0.3 * width - 15; ySpider = 0.3 * height;

                xLFly1 = 0.3 * width + 30; yLFly1 = -20;
                xLFly2 = 0.3 * width + 30; yLFly2 = -20;
                xRFly1 = 0.7 * width - 30; yRFly1 = 4 * height / 5;
                xRFly2 = 0.7 * width - 30; yRFly2 = -20;

                xLBranch1 = 0.3 * width; yLBranch1 = 4 * height / 5;
                xLBranch2 = 0.3 * width; yLBranch2 = -30;

                xRBranch1 = 0.7 * width; yRBranch1 = 1 * height / 5;
                xRBranch2 = 0.7 * width; yRBranch2 = -30;

                objectsSpeed = 3;
                fliesEaten = 0;
                jumpTime = 0;
                spiderJumping = false;
                jumpToRight = true;
                endGame = false;
            } else {
                if(!spiderJumping) {
                    jumpVelocityX = 12.0f, jumpVelocityY = -12.0f;
                    jumpTime = 0;
                    spiderJumping = true;
                }
            }
            break;
        case 27:
            exit(0);
        default:
            break;
    }
}

// Build AABB in OCS
void buildAABB(AABB &aabb, Point* vertices, int vertNum) {
    aabb.min = vertices[0];
    aabb.max = vertices[0];
    for(int i = 1; i < vertNum; ++i) {
        if(vertices[i].x < aabb.min.x ) aabb.min.x = vertices[i].x;
        if(vertices[i].y < aabb.min.y ) aabb.min.y = vertices[i].y;
        if(vertices[i].x > aabb.max.x ) aabb.max.x = vertices[i].x;
        if(vertices[i].y > aabb.max.y ) aabb.max.y = vertices[i].y;
    }
}

// convert AABB in WCS
void AABBWCS(AABB &aabbwcs, AABB aabb, float x, float y, int expansion) {
    aabbwcs.min.x = x + aabb.min.x * expansion;
    aabbwcs.min.y = y + aabb.min.y * expansion;
    aabbwcs.max.x = x + aabb.max.x * expansion;
    aabbwcs.max.y = y + aabb.max.y * expansion;
}

// Qui vengono costruiti i VBO di tutti gli oggetti della scena e dei loro colori, al termine verranno spostate le geometrice i dati del colore sulla GPU
void InitVBO() {
    // Cloud VBO
    glGenBuffers(1, &vboCloud); // Si genera il VBO e gli viene assegnato l'hanfr vboCloud (variabile globale di tipo GLuint)
    glBindBuffer(GL_ARRAY_BUFFER, vboCloud); // Si attiva il VBO
    glBufferData(GL_ARRAY_BUFFER, cloudVertices * sizeof(Point), cloud, GL_STATIC_DRAW); // Si alloca lo spazio sulla GPU e si copiano i dati a partire da cloud
    glBindBuffer(GL_ARRAY_BUFFER, 0); // Si rilascia il VBO appena creato
    glGenBuffers(1, &vboCloudColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboCloudColor);
    glBufferData(GL_ARRAY_BUFFER, cloudVertices * sizeof(ColorRGBA), cloudColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Spider VBO
    glGenBuffers(1, &vboSpider);
    glBindBuffer(GL_ARRAY_BUFFER, vboSpider);
    glBufferData(GL_ARRAY_BUFFER, spiderVertices * sizeof(Point), spider, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboSpiderColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboSpiderColor);
    glBufferData(GL_ARRAY_BUFFER, spiderVertices * sizeof(ColorRGBA), spiderColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Fly VBO
    glGenBuffers(1, &vboFly);
    glBindBuffer(GL_ARRAY_BUFFER, vboFly);
    glBufferData(GL_ARRAY_BUFFER, flyVertices * sizeof(Point), fly, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboFlyColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboFlyColor);
    glBufferData(GL_ARRAY_BUFFER, flyVertices * sizeof(ColorRGBA), flyColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Left Tree VBO
    glGenBuffers(1, &vboLTree);
    glBindBuffer(GL_ARRAY_BUFFER, vboLTree);
    glBufferData(GL_ARRAY_BUFFER, treeVertices * sizeof(Point), lTree, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboLTreeColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboLTreeColor);
    glBufferData(GL_ARRAY_BUFFER, treeVertices * sizeof(ColorRGBA), lTreeColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Right Tree VBO
    glGenBuffers(1, &vboRTree);
    glBindBuffer(GL_ARRAY_BUFFER, vboRTree);
    glBufferData(GL_ARRAY_BUFFER, treeVertices * sizeof(Point), rTree, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboRTreeColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboRTreeColor);
    glBufferData(GL_ARRAY_BUFFER, treeVertices * sizeof(ColorRGBA), rTreeColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Left Branch VBO
    glGenBuffers(1, &vboLBranch);
    glBindBuffer(GL_ARRAY_BUFFER, vboLBranch);
    glBufferData(GL_ARRAY_BUFFER, branchVertices * sizeof(Point), lBranch, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboLBranchColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboLBranchColor);
    glBufferData(GL_ARRAY_BUFFER, branchVertices * sizeof(ColorRGBA), lBranchColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Right Branch VBO
    glGenBuffers(1, &vboRBranch);
    glBindBuffer(GL_ARRAY_BUFFER, vboRBranch);
    glBufferData(GL_ARRAY_BUFFER, branchVertices * sizeof(Point), rBranch, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &vboRBranchColor);
    glBindBuffer(GL_ARRAY_BUFFER, vboRBranchColor);
    glBufferData(GL_ARRAY_BUFFER, branchVertices * sizeof(ColorRGBA), rBranchColor, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}

// Costruzione della scena iniziale, viene chiamata una sola volta e serve a generare tutte le geometrie necessarie
void buildScene(void) {
    // Draw clouds
    cloud = new Point[cloudVertices];
    cloudColor = new ColorRGBA[cloudVertices];
    buildCloud(cloud, cloudColor);
    buildCloud(cloud, cloudColor);

    // Draw left tree
    lTree = new Point[treeVertices];
    lTreeColor = new ColorRGBA[treeVertices];
    buildLTree(lTree, lTreeColor);

    // Draw left branches
    lBranch = new Point[branchVertices];
    lBranchColor = new ColorRGBA[branchVertices];
    buildLBranch(lBranch, lBranchColor);
    buildLBranch(lBranch, lBranchColor);
    buildAABB(lBranchAABB, lBranch, branchVertices);
    AABBWCS(lBranch1AABB_WCS, lBranchAABB, xLBranch1, yLBranch1, branchExpansion);
    AABBWCS(lBranch2AABB_WCS, lBranchAABB, xLBranch2, yLBranch2, branchExpansion);

    // Draw right tree
    rTree = new Point[treeVertices];
    rTreeColor = new ColorRGBA[treeVertices];
    buildRTree(rTree, rTreeColor);

    // Draw right branches
    rBranch = new Point[branchVertices];
    rBranchColor = new ColorRGBA[branchVertices];
    buildRBranch(rBranch, rBranchColor);
    buildRBranch(rBranch, rBranchColor);
    buildAABB(rBranchAABB, rBranch, branchVertices);
    AABBWCS(rBranch1AABB_WCS, rBranchAABB, xRBranch1, yRBranch1, branchExpansion);
    AABBWCS(rBranch2AABB_WCS, rBranchAABB, xRBranch2, yRBranch2, branchExpansion);

    // Draw spider
    spider = new Point[spiderVertices];
    spiderColor = new ColorRGBA[spiderVertices];
    buildSpider(spider, spiderColor);
    buildAABB(spiderAABB, spider, spiderVertices);
    AABBWCS(spiderAABB_WCS, spiderAABB, xSpider, ySpider, spiderExpansion);

    // Draw flies
    fly = new Point[flyVertices];
    flyColor = new ColorRGBA[flyVertices];
    buildFly(fly, flyColor);
    buildFly(fly, flyColor);
    buildFly(fly, flyColor);
    buildFly(fly, flyColor);
    buildAABB(flyAABB, fly, flyVertices);
    AABBWCS(lFly1AABB_WCS, flyAABB, xLFly1, yLFly1, flyExpansion);
    AABBWCS(lFly2AABB_WCS, flyAABB, xLFly2, yLFly2, flyExpansion);
    AABBWCS(rFly1AABB_WCS, flyAABB, xRFly1, yRFly1, flyExpansion);
    AABBWCS(rFly2AABB_WCS, flyAABB, xRFly2, yRFly2, flyExpansion);
}

void drawCloud(float x, float y) {
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(cloudExpansion, cloudExpansion, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboCloud); // Si attiva il VBO con i vertici della nuvola
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboCloudColor); // Si attiva il VBO con i colori della nuvola
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, cloudVertices);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glPopMatrix();
}

void drawSpider(float x, float y, float angle) {
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(spiderExpansion, spiderExpansion, 1);
    if(!spiderJumping && !endGame)
        glRotatef(angle, 0, 0, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboSpider);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboSpiderColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, spiderVertices);
    glPopMatrix();
}

void drawFly(float x, float y) {
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(flyExpansion, flyExpansion, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboFly);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboFlyColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, flyVertices);
    glPopMatrix();
}

void drawLTree() {
    glPushMatrix();
    glTranslatef(xLTree, yLTree, 0);
    glScalef(width, height, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboLTree);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboLTreeColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, treeVertices);
    glPopMatrix();
}

void drawRTree() {
    glPushMatrix();
    glTranslatef(xRTree, yRTree, 0);
    glScalef(width, height, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboRTree);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboRTreeColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, treeVertices);
    glPopMatrix();
}

void drawLBranch(float x, float y) {
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(branchExpansion, branchExpansion, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboLBranch);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboLBranchColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, branchVertices);
    glPopMatrix();
}

void drawRBranch(float x, float y) {
    glPushMatrix();
    glTranslatef(x, y, 0);
    glScalef(branchExpansion, branchExpansion, 1);
    glBindBuffer(GL_ARRAY_BUFFER, vboRBranch);
    glVertexPointer(3, GL_FLOAT, 0, (char *)NULL);
    glBindBuffer(GL_ARRAY_BUFFER, vboRBranchColor);
    glColorPointer(4, GL_DOUBLE, 0, (char *)NULL);
    glDrawArrays(GL_TRIANGLES, 0, branchVertices);
    glPopMatrix();
}

bool checkCollision(AABB a, AABB b) {
    return (a.max.x > b.min.x &&
            a.min.x < b.max.x &&
            a.max.y > b.min.y &&
            a.min.y < b.max.y);
}

void drawParticles() {
    glBegin(GL_POINTS);
    for (int i = 0; i < particles.size(); i++) {
        particles.at(i).factors.x /= particles.at(i).drag;
        particles.at(i).factors.y /= particles.at(i).drag;

        particles.at(i).coord.x += particles.at(i).factors.x;
        particles.at(i).coord.y += particles.at(i).factors.y;

        particles.at(i).color.a -= 0.05;

        glColor4f(particles.at(i).color.r, particles.at(i).color.g, particles.at(i).color.b, particles.at(i).color.a);
        glVertex2f(particles.at(i).coord.x, particles.at(i).coord.y);

        if (particles.at(i).color.a <= 0.0)
            particles.erase(particles.begin() + i);
    }
    glEnd();
    glFlush();
}

void updateSpider(int value) {
    if(!endGame && spiderJumping) { // Spider jump
        if(xSpider > 0.7 * width + 15) {
            spiderJumping = false;
            jumpToRight = false;
            xSpider = 0.7 * width + 15;
            ySpider = 0.3 * height;
        } else if(xSpider < 0.3 * width - 15) {
            spiderJumping = false;
            jumpToRight = true;
            xSpider = 0.3 * width - 15;
            ySpider = 0.3 * height;
        } else {
            // Incremento la x con la funzione y = -x^2 + 1 per dare un effetto scatto iniziale e poi rallentamento e aumento insieme alla velocità degli oggetti
            jumpTime += (-pow(jumpTime, 2.0) + objectsSpeed/3) / (sqrt(objectsSpeed/3)); // Isolo la y e ricavo la x quando y = 0 (questo è quello per cui divido tutto)
            ySpider -= jumpVelocityY * jumpTime;

            if(jumpToRight)
                xSpider += jumpVelocityX * jumpTime;
            else
                xSpider -= jumpVelocityX * jumpTime;

            jumpVelocityY += jumpGravity * jumpTime;
        }

        // Compute web particles
        for (int i = 0; i < 75; i++) {
            Particle p;
            p.drag = 1.05;
            p.coord = {xSpider, ySpider - 45, 0};
            p.factors.x = (rand() % 1000 + 1) / 300 * (rand() % 2 == 0 ? -0.75 : 0.75);
            p.factors.y = (rand() % 1000 + 1) / 300 * (rand() % 2 == 0 ? -0.75 : 0.75);
            p.color = {0.35, 0.35, 0.35, 1.5};
            particles.push_back(p);
        }

        glutPostRedisplay();
    }
    glutTimerFunc(5, updateSpider, 0);
}

void drawScene(void) {
    // Background color
    glClearColor(0.74, 0.84, 1.0, 1.0);
    glClear(GL_COLOR_BUFFER_BIT);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);

    // DRAW GEOMETRIES with VBOs

    objectsSpeed += 0.001;

    // Move clouds
    if(!endGame) {
        yCloud2 -= objectsSpeed / 2;
        if(yCloud2 < -10) { // Restart from top
            xCloud2 = randomInRange(0, 200) + width / 2;
            yCloud2 = height + 100;
        }
        yCloud1 -= objectsSpeed / 2;
        if(yCloud1 < -10) {
            xCloud1 = randomInRange(-200, 0) + width / 2;
            yCloud1 = height + 100;
        }
    }
    drawCloud(xCloud1, yCloud1);
    drawCloud(xCloud2, yCloud2);

    // Static left tree
    drawLTree();

    // Static right tree
    drawRTree();

    // Move left branch
    if(!endGame) {
        if(lBranch1AABB_WCS.max.y > 0) {
            yLBranch1 -= objectsSpeed;
            AABBWCS(lBranch1AABB_WCS, lBranchAABB, xLBranch1, yLBranch1, branchExpansion);
        } else { // Branch not visible, redraw it on scene
            float start = height + randomInRange(120, height);
            AABBWCS(lBranch1AABB_WCS, lBranchAABB, xLBranch1, start, branchExpansion);  // Recompute AABBWCS so it can be decided where to draw other branches
            if( lBranch1AABB_WCS.max.y > lBranch2AABB_WCS.max.y + (height / 3) + 120 && // Draw branches far enough from the other branch on the same side
                lBranch1AABB_WCS.max.y > rBranch1AABB_WCS.max.y + (height / 3) + 120 && // Far enough from the branches on the other tree
                lBranch1AABB_WCS.max.y > rBranch2AABB_WCS.max.y + (height / 3) + 120 &&
                lBranch1AABB_WCS.max.y > lFly1AABB_WCS.max.y + 120 &&                   // And far enough from the flies on this tree
                lBranch1AABB_WCS.max.y > lFly2AABB_WCS.max.y + 120)
                yLBranch1 = start;
        }

        if(lBranch2AABB_WCS.max.y > 0) {
            yLBranch2 -= objectsSpeed;
            AABBWCS(lBranch2AABB_WCS, lBranchAABB, xLBranch2, yLBranch2, branchExpansion);
        } else {
            float start = height + randomInRange(120, height);
            AABBWCS(lBranch2AABB_WCS, lBranchAABB, xLBranch2, start, branchExpansion);
            if( lBranch2AABB_WCS.max.y > lBranch1AABB_WCS.max.y + (height / 3) + 120 &&
                lBranch2AABB_WCS.max.y > rBranch1AABB_WCS.max.y + (height / 3) + 120 &&
                lBranch2AABB_WCS.max.y > rBranch2AABB_WCS.max.y + (height / 3) + 120 &&
                lBranch2AABB_WCS.max.y > lFly1AABB_WCS.max.y + 120 &&
                lBranch2AABB_WCS.max.y > lFly2AABB_WCS.max.y + 120)
                yLBranch2 = start;
        }
    }
    drawLBranch(xLBranch1, yLBranch1);
    drawLBranch(xLBranch2, yLBranch2);

    // Move right branch
    if(!endGame) {
        if(rBranch1AABB_WCS.max.y > 0) {
            yRBranch1 -= objectsSpeed;
            AABBWCS(rBranch1AABB_WCS, rBranchAABB, xRBranch1, yRBranch1, branchExpansion);
        } else {
            float start = height + randomInRange(120, height);
            AABBWCS(rBranch1AABB_WCS, rBranchAABB, xRBranch1, start, branchExpansion);
            if( rBranch1AABB_WCS.max.y > rBranch2AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch1AABB_WCS.max.y > lBranch1AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch1AABB_WCS.max.y > lBranch2AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch1AABB_WCS.max.y > rFly1AABB_WCS.max.y + 120 &&
                rBranch1AABB_WCS.max.y > rFly2AABB_WCS.max.y + 120)
                yRBranch1 = start;
        }

        if(rBranch2AABB_WCS.max.y > 0) {
            yRBranch2 -= objectsSpeed;
            AABBWCS(rBranch2AABB_WCS, rBranchAABB, xRBranch2, yRBranch2, branchExpansion);
        } else {
            float start = height + randomInRange(120, height);
            AABBWCS(rBranch2AABB_WCS, rBranchAABB, xRBranch2, start, branchExpansion);
            if( rBranch2AABB_WCS.max.y > rBranch1AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch2AABB_WCS.max.y > lBranch1AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch2AABB_WCS.max.y > lBranch2AABB_WCS.max.y + (height / 3) + 120 &&
                rBranch2AABB_WCS.max.y > rFly1AABB_WCS.max.y + 120 &&
                rBranch2AABB_WCS.max.y > rFly2AABB_WCS.max.y + 120)
                yRBranch2 = start;
        }
    }
    drawRBranch(xRBranch1, yRBranch1);
    drawRBranch(xRBranch2, yRBranch2);

    // Mode Spider
    if(!endGame) {
        if( checkCollision(spiderAABB_WCS, lBranch1AABB_WCS) || // Check collisions with branches
            checkCollision(spiderAABB_WCS, lBranch2AABB_WCS) ||
            checkCollision(spiderAABB_WCS, rBranch1AABB_WCS) ||
            checkCollision(spiderAABB_WCS, rBranch2AABB_WCS)) {
            endGame = true;
        } else {
            if(!spiderJumping) { // Spider swing
                increaseAngle = (spiderAngle > 8) ? false : (spiderAngle < -8 ? true : increaseAngle);
                spiderAngle += increaseAngle ? 0.8 : -0.8;

                for (int i = 0; i < 10; i++) {
                    Particle p;
                    p.drag = 1.05;
                    p.coord = {xSpider, ySpider - 45, 0};
                    p.factors.x = (rand() % 1000 + 1) / 100 * (rand() % 2 == 0 ? -0.25 : 0.25);
                    p.factors.y = (rand() % 1000 + 1) / 100 * (rand() % 2 == 0 ? -0.25 : -0.25);
                    p.color = {0.35, 0.35, 0.35, 1.5};
                    particles.push_back(p);
                }
            }
        }
    }
    AABBWCS(spiderAABB_WCS, spiderAABB, xSpider, ySpider, spiderExpansion);

    drawParticles();
    drawSpider(xSpider, ySpider, spiderAngle);

    // Move fly
    if(!endGame) {
        if(checkCollision(lFly1AABB_WCS, spiderAABB_WCS)) {
            fliesEaten += (objectsSpeed - 3) * 10;
            yLFly1 = -20;
            AABBWCS(lFly1AABB_WCS, flyAABB, xLFly1, yLFly1, flyExpansion);
        } else {
            if(lFly1AABB_WCS.max.y > 0) {
                yLFly1 -= objectsSpeed;
                AABBWCS(lFly1AABB_WCS, flyAABB, xLFly1, yLFly1, flyExpansion);
            } else {
                float start = height + randomInRange(120, height);
                AABBWCS(lFly1AABB_WCS, flyAABB, xLFly1, start, flyExpansion);
                if( lFly1AABB_WCS.max.y > lFly2AABB_WCS.max.y + (height / 3) && // Distance from the other fly on the same tree side
                    lFly1AABB_WCS.max.y > lBranch1AABB_WCS.max.y + 120 && // Distance from the first branch
                    lFly1AABB_WCS.max.y > lBranch2AABB_WCS.max.y + 120) // Distance from the second branch
                    yLFly1 = start;
            }
        }

        if(checkCollision(lFly2AABB_WCS, spiderAABB_WCS)) {
            fliesEaten += (objectsSpeed - 3) * 10;
            yLFly2 = -20;
            AABBWCS(lFly2AABB_WCS, flyAABB, xLFly2, yLFly2, flyExpansion);
        } else {
            if(lFly2AABB_WCS.max.y > 0) {
                yLFly2 -= objectsSpeed;
                AABBWCS(lFly2AABB_WCS, flyAABB, xLFly2, yLFly2, flyExpansion);
            } else {
                float start = height + randomInRange(120, height);
                AABBWCS(lFly2AABB_WCS, flyAABB, xLFly2, start, flyExpansion);
                if( lFly2AABB_WCS.max.y > lFly1AABB_WCS.max.y + (height / 3) &&
                    lFly2AABB_WCS.max.y > lBranch1AABB_WCS.max.y + 120 &&
                    lFly2AABB_WCS.max.y > lBranch2AABB_WCS.max.y + 120)
                    yLFly2 = start;
            }
        }

        if(checkCollision(rFly1AABB_WCS, spiderAABB_WCS)) {
            fliesEaten += (objectsSpeed - 3) * 10;
            yRFly1 = -20;
            AABBWCS(rFly1AABB_WCS, flyAABB, xRFly1, yRFly1, flyExpansion);
        } else {
            if(rFly1AABB_WCS.max.y > 0) {
                yRFly1 -= objectsSpeed;
                AABBWCS(rFly1AABB_WCS, flyAABB, xRFly1, yRFly1, flyExpansion);
            } else {
                float start = height + randomInRange(120, height);
                AABBWCS(rFly1AABB_WCS, flyAABB, xRFly1, start, flyExpansion);
                if( rFly1AABB_WCS.max.y > rFly2AABB_WCS.max.y + (height / 3) &&
                    rFly1AABB_WCS.max.y > rBranch1AABB_WCS.max.y + 120 &&
                    rFly1AABB_WCS.max.y > rBranch2AABB_WCS.max.y + 120)
                    yRFly1 = start;
            }
        }

        if(checkCollision(rFly2AABB_WCS, spiderAABB_WCS)) {
            fliesEaten += (objectsSpeed - 3) * 10;
            yRFly2 = -20;
            AABBWCS(rFly2AABB_WCS, flyAABB, xRFly2, yRFly2, flyExpansion);
        } else {
            if(rFly2AABB_WCS.max.y > 0) {
                yRFly2 -= objectsSpeed;
                AABBWCS(rFly2AABB_WCS, flyAABB, xRFly2, yRFly2, flyExpansion);
            } else {
                float start = height + randomInRange(120, height);
                AABBWCS(rFly2AABB_WCS, flyAABB, xRFly2, start, flyExpansion);
                if( rFly2AABB_WCS.max.y > rFly1AABB_WCS.max.y + (height / 3) &&
                    rFly2AABB_WCS.max.y > rBranch1AABB_WCS.max.y + 120 &&
                    rFly2AABB_WCS.max.y > rBranch2AABB_WCS.max.y + 120)
                    yRFly2 = start;
            }
        }
    }
    drawFly(xLFly1, yLFly1);
    drawFly(xLFly2, yLFly2);
    drawFly(xRFly1, yRFly1);
    drawFly(xRFly2, yRFly2);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);

    if(fliesEaten > bestScore) bestScore = fliesEaten;

    if(endGame) { // Game Over Text
        glColor3f(0.96, 0.09, 0.09);
        bitmapOutput(width / 2 - 55, 3 * height / 5 - 10, 0, "Game Over", GLUT_BITMAP_TIMES_ROMAN_24);

        glColor3f(0.88, 0.24, 0.24);
        bitmapOutput(width / 2 - 110, 3 * height / 5 - 50, 0, "Press SPACE to restart", GLUT_BITMAP_TIMES_ROMAN_24);
    }

    glColor3f(0.0, 0.0, 0.0);
    string text = "Score: ";
    text.append(floatToString(fliesEaten).substr(0, 4));
    bitmapOutput(width / 2 - 40, height - 40, 0, text, GLUT_BITMAP_TIMES_ROMAN_24);

    glColor3f(0.5, 0.5, 0.5);
    string best = "Best: ";
    best.append(floatToString(bestScore).substr(0, 4));
    bitmapOutput(width / 2 - 35, height - 70, 0, best, GLUT_BITMAP_TIMES_ROMAN_24);

    glutSwapBuffers();
    glutPostRedisplay();
}

void resize(int w, int h) {
    glViewport(0, 0, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0.0, width, 0.0, height, -1.0, 1.0);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}

void Init() {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glPointSize(2.0);

    buildScene(); // Scena iniziale
    InitVBO();
}

int main(int argc, char **argv) {
    glutInit(&argc, argv);

    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA);
    glutInitWindowSize(width, height);
    glutInitWindowPosition(100, 100);
    glutCreateWindow("SpiderGame");
    glutDisplayFunc(drawScene);
    glutReshapeFunc(resize);
    glutKeyboardFunc(keyboardListener);

    glutTimerFunc(5, updateSpider, 0);

    glewInit();
    Init();
    glutMainLoop();

    return 0;
}
