/*
   Author: Berti Matteo, 2018
*/

#define L_MOTOR_PIN1 8 // Pin 10 on L293D IC
#define L_MOTOR_PIN2 9 // Pin 15 on L293D IC
#define R_MOTOR_PIN1 3 // Pin 2 on L293D IC
#define R_MOTOR_PIN2 4 // Pin 7 on L293D IC
#define ENABLE_PIN1 6 // Pin 1 on L293D IC
#define ENABLE_PIN2 10 // Pin 9 on L293D IC
#define TRIG_PIN 11
#define ECHO_PIN 12

int command = 'S';
int lastCommand = command;
bool forward = true;
long distance;

void setup() { 
  // Set pins as outputs
  pinMode(L_MOTOR_PIN1, OUTPUT);
  pinMode(L_MOTOR_PIN2, OUTPUT);
  pinMode(ENABLE_PIN1, OUTPUT);
  pinMode(R_MOTOR_PIN1, OUTPUT);
  pinMode(R_MOTOR_PIN2, OUTPUT);
  pinMode(ENABLE_PIN2, OUTPUT);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, OUTPUT);

  // Set enable pins high so each motor can turn on
  digitalWrite(ENABLE_PIN1, HIGH);
  digitalWrite(ENABLE_PIN2, HIGH);

  // Initialize serial communication at 9600 bits per second
  Serial.begin(9600);
}

void loop() {
  readDistanceInFront();
  
  // Save data if received
  if (Serial.available() > 0) {
    command = Serial.read();
    lastCommand = command;
  }

  // Go back if an obstacle is in front of the robocar
  if(distance < 10) {
    command = 'B';
  } else {
    command = lastCommand;
  }
  
  if (command == 'F') { // Forward command
    forward = true;
    moveRobocar();
  } else if (command == 'L') { // Left command
    turnLeft();
  } else if (command == 'S') { // Stop command
    stopRobocar();
  } else if (command == 'R') { // Right command
    turnRight();
  } else if (command == 'B') { // Backward command
    forward = false;
    moveRobocar();
  }
}

void moveRobocar() {
  if (forward) {
    if(distance > 10) {
      digitalWrite(L_MOTOR_PIN1, HIGH);
      digitalWrite(L_MOTOR_PIN2, LOW);
      digitalWrite(R_MOTOR_PIN1, LOW);
      digitalWrite(R_MOTOR_PIN2, HIGH);
    }
  } else {
    digitalWrite(L_MOTOR_PIN1, LOW);
    digitalWrite(L_MOTOR_PIN2, HIGH);
    digitalWrite(R_MOTOR_PIN1, HIGH);
    digitalWrite(R_MOTOR_PIN2, LOW);
  }
}

void stopRobocar() {
  digitalWrite(L_MOTOR_PIN1, LOW);
  digitalWrite(L_MOTOR_PIN2, LOW);
  digitalWrite(R_MOTOR_PIN1, LOW);
  digitalWrite(R_MOTOR_PIN2, LOW);
}

void turnRight() {
  if (forward) {
    if(distance > 10) {
      digitalWrite(L_MOTOR_PIN1, LOW);
      digitalWrite(L_MOTOR_PIN2, HIGH);
      digitalWrite(R_MOTOR_PIN1, LOW);
      digitalWrite(R_MOTOR_PIN2, HIGH);
    }
  } else {
    digitalWrite(L_MOTOR_PIN1, HIGH);
    digitalWrite(L_MOTOR_PIN2, LOW);
    digitalWrite(R_MOTOR_PIN1, HIGH);
    digitalWrite(R_MOTOR_PIN2, LOW);
  }
}

void turnLeft() {
  if (forward) {
    if(distance > 10) {
      digitalWrite(L_MOTOR_PIN1, HIGH);
      digitalWrite(L_MOTOR_PIN2, LOW);
      digitalWrite(R_MOTOR_PIN1, HIGH);
      digitalWrite(R_MOTOR_PIN2, LOW);
    }
  } else {
    digitalWrite(L_MOTOR_PIN1, LOW);
    digitalWrite(L_MOTOR_PIN2, HIGH);
    digitalWrite(R_MOTOR_PIN1, LOW);
    digitalWrite(R_MOTOR_PIN2, HIGH);
  }
}

void readDistanceInFront() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  pinMode(ECHO_PIN, INPUT);
  distance = 2 + (pulseIn(ECHO_PIN, HIGH) / 2) * 0.0343;
  Serial.print(distance);
  Serial.print('#');
}


