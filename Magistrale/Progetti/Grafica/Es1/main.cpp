/*
 * Lab-01-1819_students.c
 *
 *     This program draws straight lines connecting dots placed with mouse clicks.
 *
 * Usage:
 *   Left click to place a control point.
 *		Maximum number of control points allowed is currently set at 64.
 *	 Press "f" to remove the first control point
 *	 Press "l" to remove the last control point.
 *	 Press "o" to draw the curve with OpenGL standard function
 *	 Press "d" to draw the curve with deCasteljau algorithm
 *	 Press "i" to enter interactive mode, in this mode press:
 *	 - "0" to get a C0 continuity between the last control point and the next one
 *	 - "1" to get a G1 continuity between the last control point and the next one
 *	 - "2" to get a C1 continuity between the last control point and the next one
 *	 Press escape to exit.
 */

#include <GL/glew.h>
#include <GL/freeglut.h>
#include <math.h>
#include <string>
#include <vector>

#define MAX_CURVES 100
#define PI 3.14159265359
using namespace std;

struct Point {
    double x;
    double y;
};

double pointsArray[3 * MAX_CURVES][3];
int numPts = 0;

bool drawMode = false; // 0 OpenGL - 1 deCasteljau
bool interactive; // 0 not interactive - 1 interactive
int pointsContinuity[MAX_CURVES]; // 0 C0 - 1 G1 - 2 C1
int contPts = 0;
int clickedPoint;

typedef enum { C0, G1, C1 } Continuity;
Continuity CONTINUITY = C0;

// Window size in pixels
int windowHeight;
int windowWidth;



void printToScreen(float x, float y, string str) {
    glColor3f(0.3f, 0.1f, 0.3f);
    glRasterPos2f(x, y);
    for (int i = 0; i < str.length(); i++)
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, str[i]);
}

// dist: distanza euclidea tra l'ultimo punto di controllo e il punto in cui si trova il puntatore
Point getPointInLine(Point P0, Point P1, double dist, bool direction) {
    double m, q, xd, yd;

    if(P0.x == P1.x) { // Vertical line
        xd = P1.x;
        if(direction) {
            if(P1.y > P0.y)
                yd = P1.y + dist;
            else
                yd = P1.y - dist;
        } else {
            if(P1.y > P0.y)
                yd = P1.y - dist;
            else
                yd = P1.y + dist;
        }
    } else {
        if(P0.y == P1.y) // Horizontal line
            m = 0;
        else // Diagonal line
            m = (P1.y - P0.y)/(P1.x - P0.x);

        q = P0.x * (P0.y - P1.y) / (P1.x - P0.x) + P0.y;

        if(direction) { // La direzione del punto segue la direzione dei punti sulla retta
            if(P1.x > P0.x) // Seguo la direzione dei punti
                xd = P1.x + dist / sqrt(1 + pow(m, 2.0)); // Formula derivata dall'intersezione della retta con una circonferenza di raggio dist.
            else
                xd = P1.x - dist / sqrt(1 + pow(m, 2.0));
        } else { // La direzione del punto è contraria a quella dei punti
            if(P1.x > P0.x)
                xd = P1.x - dist / sqrt(1 + pow(m, 2.0));
            else
                xd = P1.x + dist / sqrt(1 + pow(m, 2.0));
        }
        yd = m * xd + q;
    }

    return {xd, yd};
}

double euclideanDistance(Point A, Point B) {
    return sqrt(pow(A.x - B.x, 2.0) + pow(A.y - B.y, 2.0));
}

void removeFirstPoint() {
    if (numPts > 0) { // Remove the first point, slide the rest down
        numPts--;
        for (int i = 0; i < numPts; i++) {
            pointsArray[i][0] = pointsArray[i+1][0];
            pointsArray[i][1] = pointsArray[i+1][1];
            pointsArray[i][2] = 0;

            pointsContinuity[i] = pointsContinuity[i+1];
        }
    }
}

void removeLastPoint() {
    if(numPts > 0)
        numPts--;

    if(numPts % 3 == 0)
        contPts--;
}

void addNewPoint(double x, double y) {

    if(numPts >= 3 * MAX_CURVES) { // Remove first curve
        for (int i = 0; i < 3; i++)
            removeFirstPoint();
        contPts--;
    }

    // Control points in curve
    pointsArray[numPts][0] = x;
    pointsArray[numPts][1] = y;
    pointsArray[numPts][2] = 0;

    if(numPts % 3 == 0) { // Set junction point continuity
        if(numPts > 1)
            pointsContinuity[contPts] = (CONTINUITY == C0 ? 0 : (CONTINUITY == G1 ? 1 : 2)); // Current continuity
        else
            pointsContinuity[contPts] = 0;

        contPts++;
    }

    // First control point (next to the junction point) in the curve
    if(numPts % 3 == 1) {
        if(pointsContinuity[contPts-1] == G1) {
            if(numPts < 2) {
                pointsArray[numPts][0] = x;
                pointsArray[numPts][1] = y;
                pointsArray[numPts][2] = 0;
            } else {
                double dist = euclideanDistance({x, y}, {pointsArray[numPts-1][0], pointsArray[numPts-1][1]});
                Point result = getPointInLine({pointsArray[numPts-2][0], pointsArray[numPts-2][1]}, {pointsArray[numPts-1][0], pointsArray[numPts-1][1]}, dist, true);

                pointsArray[numPts][0] = result.x;
                pointsArray[numPts][1] = result.y;
                pointsArray[numPts][2] = 0;
            }
        } else if(pointsContinuity[contPts-1] == C1) {
            if(numPts < 2) {
                pointsArray[numPts][0] = x;
                pointsArray[numPts][1] = y;
                pointsArray[numPts][2] = 0;
            } else {
                double dist = euclideanDistance({pointsArray[numPts-2][0], pointsArray[numPts-2][1]}, {pointsArray[numPts-1][0], pointsArray[numPts-1][1]});
                Point result = getPointInLine({pointsArray[numPts-2][0], pointsArray[numPts-2][1]}, {pointsArray[numPts-1][0], pointsArray[numPts-1][1]}, dist, true);

                pointsArray[numPts][0] = result.x;
                pointsArray[numPts][1] = result.y;
                pointsArray[numPts][2] = 0;
            }
        }
    }
    numPts++;
}

void keyboardListener(unsigned char key, int x, int y) {
	switch (key) {
		case 'f':
			removeFirstPoint();
			break;
		case 'l':
			removeLastPoint();
			break;
        case 'o':
            drawMode = false;
            break;
        case 'd':
            drawMode = true;
            break;
        case 'i':
            interactive = !interactive;
            break;
        case 48: // Key "0"
            if(interactive)
                CONTINUITY = C0;
            break;
        case 49: // Key "1"
            if(interactive)
                CONTINUITY = G1;
            break;
        case 50: // Key "2"
            if(interactive)
                CONTINUITY = C1;
            break;
		case 27: // Escape key
			exit(0);
	}
    glutPostRedisplay();
}

// Controlla che un punto sia all'interno del segmento di retta che passa tra due punti
bool isPointInRange(Point A, Point B, Point check) {
    double angle = atan2(B.y - A.y, B.x - A.x) * 180 / PI; // Angle of the line from x-asis
    return ((angle > 0 && angle < 90 && check.x >= A.x) ||
            (angle > 90 && angle < 180 && check.x <= A.x) ||
            (angle > -180 && angle < -90 && check.x <= A.x) ||
            (angle > -90 && angle < 0 && check.x >= A.x) ||
            (angle == 90 && check.y >= A.y) ||
            (angle == -90 && check.y <= A.y) ||
            (angle == 0 && check.x >= A.x) ||
            (angle == 180 && check.x <= A.x));
}

void movePoint(int clicked, int x, int y) {
    double xPos = ((double) x) / ((double) (windowWidth - 1));
    double yPos = 1.0f - ((double) y) / ((double) (windowHeight - 1)); // Flip value since y position is from top row.

    // Clicked a junction point
    if(clicked % 3 == 0) {
        if(pointsContinuity[clicked / 3] == 0) { // Clicked point with continuity C0
            pointsArray[clicked][0] = xPos;
            pointsArray[clicked][1] = yPos;
        } else if(pointsContinuity[clicked / 3] == 1) { // Clicked point with continuity G1
            double dist = euclideanDistance({xPos, yPos}, {pointsArray[clicked + 1][0], pointsArray[clicked + 1][1]}); // Distance from the clicked point in the window and the next control point
            Point result = getPointInLine({pointsArray[clicked - 1][0], pointsArray[clicked - 1][1]}, {pointsArray[clicked + 1][0], pointsArray[clicked + 1][1]}, dist, false);
            if(isPointInRange({pointsArray[clicked - 1][0], pointsArray[clicked - 1][1]}, {pointsArray[clicked + 1][0], pointsArray[clicked + 1][1]}, result)) {
                pointsArray[clicked][0] = result.x;
                pointsArray[clicked][1] = result.y;
            }
        } else if(pointsContinuity[clicked / 3] == 2) { // Clicked point with continuity C1
            pointsArray[clicked - 1][0] += xPos - pointsArray[clicked][0];
            pointsArray[clicked - 1][1] += yPos - pointsArray[clicked][1];

            pointsArray[clicked + 1][0] += xPos - pointsArray[clicked][0];
            pointsArray[clicked + 1][1] += yPos - pointsArray[clicked][1];

            pointsArray[clicked][0] = xPos;
            pointsArray[clicked][1] = yPos;
        }
    } else {
        pointsArray[clicked][0] = xPos;
        pointsArray[clicked][1] = yPos;

        // Adattare i control points vicini
        if((clicked + 1) % 3 == 0) { // Se il successivo è un junction point
            if(pointsContinuity[(clicked + 1) / 3] == 1) { // Ed ha continuità G1
                double dist = euclideanDistance({pointsArray[clicked + 1][0], pointsArray[clicked + 1][1]}, {pointsArray[clicked + 2][0], pointsArray[clicked + 2][1]});
                Point result = getPointInLine({pointsArray[clicked][0], pointsArray[clicked][1]}, {pointsArray[clicked + 2][0], pointsArray[clicked + 2][1]}, dist, false);

                pointsArray[clicked + 1][0] = result.x;
                pointsArray[clicked + 1][1] = result.y;
            } else if(pointsContinuity[(clicked + 1) / 3] == 2) { // Ed ha continuità C1
                double dist = euclideanDistance({pointsArray[clicked][0], pointsArray[clicked][1]}, {pointsArray[clicked + 2][0], pointsArray[clicked + 2][1]}) / 2;
                Point result = getPointInLine({pointsArray[clicked][0], pointsArray[clicked][1]}, {pointsArray[clicked + 2][0], pointsArray[clicked + 2][1]}, dist, false);

                pointsArray[clicked + 1][0] = result.x;
                pointsArray[clicked + 1][1] = result.y;
            }
        }

        if((clicked - 1) % 3 == 0) { // Se il precedente è un junction point
            if(pointsContinuity[(clicked - 1) / 3] == 1) {
                double dist = euclideanDistance({pointsArray[clicked - 1][0], pointsArray[clicked - 1][1]}, {pointsArray[clicked][0], pointsArray[clicked][1]});
                Point result = getPointInLine({pointsArray[clicked - 2][0], pointsArray[clicked - 2][1]}, {pointsArray[clicked][0], pointsArray[clicked][1]}, dist, false);

                pointsArray[clicked - 1][0] = result.x;
                pointsArray[clicked - 1][1] = result.y;
            } else if(pointsContinuity[(clicked + 1) / 3] == 2) {
                double dist = euclideanDistance({pointsArray[clicked - 2][0], pointsArray[clicked - 2][1]}, {pointsArray[clicked][0], pointsArray[clicked][1]}) / 2;
                Point result = getPointInLine({pointsArray[clicked - 2][0], pointsArray[clicked - 2][1]}, {pointsArray[clicked][0], pointsArray[clicked][1]}, dist, false);

                pointsArray[clicked - 1][0] = result.x;
                pointsArray[clicked - 1][1] = result.y;
            }
        }
    }

	glutPostRedisplay();
}

void mouseMotionListener(int x, int y) {
    movePoint(clickedPoint, x, y);
}

int getClosestPoint(float x, float y) {
	int near = -1;
    double lowerDistance = sqrt(pow(windowWidth, 2.0) + pow(windowHeight, 2.0)), tolerance = 0.04, distance;

    for (int i = 0; i < numPts; i++) {
        distance = sqrt(pow(x - pointsArray[i][0], 2.0) + pow(y - pointsArray[i][1], 2.0)); // Distanza di ogni punto dalla posizione cliccata
        if (distance <= tolerance && distance <= lowerDistance) { // Salvo il punto meno distante
            near = i;
            lowerDistance = distance;
        }
    }

	return near;
}

void mouseListener(int button, int state, int x, int y) {
	float xPos, yPos;

    // Conversione da coordinate pixel a coordinate mondo
	yPos = ((float) y) / ((float) (windowHeight - 1));
	xPos = ((float) x) / ((float) (windowWidth - 1));
	yPos = 1.0f - yPos;			   // Flip value since y position is from top row.

	if(button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
		clickedPoint = getClosestPoint(xPos , yPos);
	} else if (button == GLUT_LEFT_BUTTON && state == GLUT_UP) {
		if (clickedPoint >= 0) {
			movePoint(clickedPoint, x, y);
			clickedPoint = -1;
		} else {
			addNewPoint(xPos, yPos);
			glutPostRedisplay();
		}
	}
}

void deCasteljau(float t, int startIndex) {
    int pts = numPts - startIndex < 4 ? numPts - startIndex : 4;
    int i;
    double temp[pts][3];

	for (i = 0; i < pts; i++) { // Copio i punti per non sovrascriverli
		temp[i][0] = pointsArray[startIndex + i][0];
		temp[i][1] = pointsArray[startIndex + i][1];
		temp[i][2] = pointsArray[startIndex + i][2];
	}

	for (i = 1; i < pts; i++)
		for (int j = 0; j < pts - i; j++) { // Interpolazione lineare (lerp)
			temp[j][0] = t * temp[j+1][0] + (1-t) * temp[j][0];
			temp[j][1] = t * temp[j+1][1] + (1-t) * temp[j][1];
			temp[j][2] = t * temp[j+1][2] + (1-t) * temp[j][2];
		}

	glVertex3f(temp[0][0], temp[0][1], temp[0][2]);
}

void display(void) {
	int i;

	glClear(GL_COLOR_BUFFER_BIT);
	glLineWidth(2);

	string mode = "Mode: ";
	mode.append(drawMode ? "deCasteljau" : "OpenGL");
    printToScreen(0.015, 0.95, mode);

    string cont = "Continuity: ";
    cont.append(interactive ? "ON " : "OFF ");
    cont.append(CONTINUITY == C0 ? "[C0]" : (CONTINUITY == G1 ? "[G1]" : "[C1]"));
    printToScreen(0.015, 0.90, cont);

	// Draw line segments
	if (numPts > 1) {
		glColor3f(0.4f, 0.2f, 0.4f);		// Edit: color
		glBegin( GL_LINE_STRIP );
		for (i = 0; i < numPts; i++)
			glVertex2f(pointsArray[i][0], pointsArray[i][1]);
		glEnd();
	}

	// Draw control points
	glBegin( GL_POINTS );
	for (i = 0; i < numPts; i++) {
        if(i % 3 == 0)
            glColor3f(0.68f, 0.37f, 0.66f);    // Edit: color
        else
            glColor3f(0.3f, 0.1f, 0.3f);
        glVertex2f(pointsArray[i][0], pointsArray[i][1]);
	}
	glEnd();

	// Write continuities in control points
    for (i = 0; i < numPts; i += 3)
        printToScreen(pointsArray[i][0] + 0.01, pointsArray[i][1] + 0.01, pointsContinuity[i/3] == 0 ? "C0" : (pointsContinuity[i/3] == 1 ? "G1" : "C1"));

	// Draw curves
	if(numPts > 1) {
        glColor3f(0.9f, 0.2f, 0.1f);       // Curves color
        // Draw completed curves
	    for(i = 0; i < numPts - 3; i += 3) {
            glMap1d(GL_MAP1_VERTEX_3, 0.0, 1.0, 3, 4, &pointsArray[i][0]);
            glBegin(GL_LINE_STRIP);
            for (int j = 0; j <= 100; j++)
                if(drawMode)                    // Draw: DeCasteljau
                    deCasteljau((float) (j / 100.0), i);
                else                            // Draw: OpenGL
                    glEvalCoord1d((GLdouble) (j / 100.0));
            glEnd();
	    }
	    // Draw the last curve of degree <= 3
        int orphanControlPoints = numPts - (((numPts - 1) / 3) * 3 + 1);
        glMap1d(GL_MAP1_VERTEX_3, 0.0, 1.0, 3, orphanControlPoints + 1, &pointsArray[numPts - orphanControlPoints - 1][0]);
        glBegin(GL_LINE_STRIP);
        for (int j = 0; j <= 100; j++)
            if(drawMode)                    // Draw: DeCasteljau
                deCasteljau((float) (j / 100.0), i);
            else                            // Draw: OpenGL
                glEvalCoord1d((GLdouble) (j / 100.0));
        glEnd();
	}

    glFlush();
}

void initRendering() {
	glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glPointSize(8);

	glEnable(GL_POINT_SMOOTH);
	glEnable(GL_LINE_SMOOTH);
	glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);	// Make round points, not square points
	glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);		// Antialias the lines
	glEnable(GL_MAP1_VERTEX_3);
}

void resizeWindow(int w, int h) {
	windowHeight = (h>1) ? h : 2;
	windowWidth = (w>1) ? w : 2;
	glViewport(0, 0, (GLsizei) w, (GLsizei) h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(0.0f, 1.0f, 0.0f, 1.0f);  // Always view [0,1]x[0,1].
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

int main(int argc, char** argv) {
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutInitWindowSize(1000, 500);
	glutInitWindowPosition(100, 100);
	glutCreateWindow("Bezier Curves");


	glutDisplayFunc(display);
	glutReshapeFunc(resizeWindow);
    glutKeyboardFunc(keyboardListener);
    glutMouseFunc(mouseListener);
    glutMotionFunc(mouseMotionListener); // Rileva il movimento del mouse

    initRendering();

	glutMainLoop();

	return 0; // This line is never reached
}