import RPi.GPIO as GPIO
from time import sleep
import matplotlib.pyplot as plt
from multiprocessing import Process, Array
from threading import Thread
import serial, os, http.server, socketserver, subprocess


# ----- INITIALIZATION -----

# > GPIO AND MOTORS

# refer to the pins by the "Bradcom SOC channel" and mute warnings
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# using 2xL293D (Motor1: front, Motor2: back)
MotorL1 = {'ENABLE': 25, 'INPUT1': 24, 'INPUT2': 23}
MororR1 = {'ENABLE': 17, 'INPUT1': 27, 'INPUT2': 22}
MotorL2 = {'ENABLE': 6, 'INPUT1': 5, 'INPUT2': 26}
MororR2 = {'ENABLE': 16, 'INPUT1': 12, 'INPUT2': 1}

# number in range [0, 100] which specifies motors speed
SPEED = 100

# set all the pins to send OUT current
for pin in ['ENABLE', 'INPUT1', 'INPUT2']:
    GPIO.setup(MotorL1[pin], GPIO.OUT)
    GPIO.setup(MororR1[pin], GPIO.OUT)
    GPIO.setup(MotorL2[pin], GPIO.OUT)
    GPIO.setup(MororR2[pin], GPIO.OUT)

# create 4 PWM instances on the enable channels with frequency 100Hz
ENABLE_L1 = GPIO.PWM(MotorL1['ENABLE'], 100)
ENABLE_R1 = GPIO.PWM(MororR1['ENABLE'], 100)
ENABLE_L2 = GPIO.PWM(MotorL2['ENABLE'], 100)
ENABLE_R2 = GPIO.PWM(MororR2['ENABLE'], 100)

# start PWM (Pulse-Width Modulation) with duty cycle 0
ENABLE_L1.start(0)
ENABLE_R1.start(0)
ENABLE_L2.start(0)
ENABLE_R2.start(0)

# > ROBOT ORIENTATION AND MAP

# robot coordinates on the world space
WRLD_POS = (0, 0)
# robot coordinates on the object space
OBJ_POS = (1, 0)

CELL_NUM = 5 # total area (cm^2) = (CELL_NUM * ~20cm) * (CELL_NUM * ~20cm)
GROUND_MAP = Array('f', [1.0] * (CELL_NUM * CELL_NUM))
SCANNED_CELLS = 0
FREQ_VALUES = []

# > ARDUINO NANO SERIAL PORT

SERIAL_PORT = serial.Serial('/dev/ttyUSB0', 115200)


def move_forward(seconds=1):
    print (f'Forward motion for {seconds}s')

    # speed up duty cycle on enable pins
    ENABLE_R1.ChangeDutyCycle(SPEED)
    ENABLE_L1.ChangeDutyCycle(SPEED)
    ENABLE_R2.ChangeDutyCycle(SPEED)
    ENABLE_L2.ChangeDutyCycle(SPEED)

    # in order to move forward send current to one input and block the other
    GPIO.output(MotorL1['INPUT1'], GPIO.HIGH)
    GPIO.output(MotorL1['INPUT2'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT1'], GPIO.HIGH)
    GPIO.output(MotorL2['INPUT2'], GPIO.LOW)

    GPIO.output(MororR1['INPUT1'], GPIO.LOW)
    GPIO.output(MororR1['INPUT2'], GPIO.HIGH)
    GPIO.output(MororR2['INPUT1'], GPIO.LOW)
    GPIO.output(MororR2['INPUT2'], GPIO.HIGH)

    sleep(seconds)

def move_backward(seconds=1):
    print (f'Backward motion for {seconds}s')

    # speed up duty cycle on enable pins
    ENABLE_R1.ChangeDutyCycle(SPEED)
    ENABLE_L1.ChangeDutyCycle(SPEED)
    ENABLE_R2.ChangeDutyCycle(SPEED)
    ENABLE_L2.ChangeDutyCycle(SPEED)

    # in order to move backward send current to one input and block the other
    GPIO.output(MotorL1['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL1['INPUT2'], GPIO.HIGH)
    GPIO.output(MotorL2['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT2'], GPIO.HIGH)

    GPIO.output(MororR1['INPUT1'], GPIO.HIGH)
    GPIO.output(MororR1['INPUT2'], GPIO.LOW)
    GPIO.output(MororR2['INPUT1'], GPIO.HIGH)
    GPIO.output(MororR2['INPUT2'], GPIO.LOW)

    sleep(seconds)

def move_right(angle=90.0):
    # translate angle to seconds of rotation (2s is equal to 90°)
    seconds = angle * 2.0 / 90.0
    print (f'Right motion for {seconds}s ({angle}°)')

    # speed up duty cycle on enable pins
    ENABLE_R1.ChangeDutyCycle(SPEED)
    ENABLE_L1.ChangeDutyCycle(SPEED)
    ENABLE_R2.ChangeDutyCycle(SPEED)
    ENABLE_L2.ChangeDutyCycle(SPEED)

    # to move right move forward left motors and backward the right back motor
    GPIO.output(MotorL1['INPUT1'], GPIO.HIGH)
    GPIO.output(MotorL1['INPUT2'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT1'], GPIO.HIGH)
    GPIO.output(MotorL2['INPUT2'], GPIO.LOW)

    GPIO.output(MororR1['INPUT1'], GPIO.LOW)
    GPIO.output(MororR1['INPUT2'], GPIO.LOW)
    GPIO.output(MororR2['INPUT1'], GPIO.HIGH)
    GPIO.output(MororR2['INPUT2'], GPIO.LOW)

    sleep(seconds)

def move_left(angle=90.0):
    # translate angle to seconds of rotation (2s is equal to 90°)
    seconds = angle * 2.0 / 90.0
    print (f'Left motion for {seconds}s (~{angle}°)')

    # speed up duty cycle on enable pins
    ENABLE_R1.ChangeDutyCycle(SPEED)
    ENABLE_L1.ChangeDutyCycle(SPEED)
    ENABLE_R2.ChangeDutyCycle(SPEED)
    ENABLE_L2.ChangeDutyCycle(SPEED)

    # to move left move forward right motors and backward the left back motor
    GPIO.output(MotorL1['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL1['INPUT2'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT2'], GPIO.HIGH)

    GPIO.output(MororR1['INPUT1'], GPIO.LOW)
    GPIO.output(MororR1['INPUT2'], GPIO.HIGH)
    GPIO.output(MororR2['INPUT1'], GPIO.LOW)
    GPIO.output(MororR2['INPUT2'], GPIO.HIGH)

    sleep(seconds)

def stop(seconds=1):
    print(f'Stop for {seconds}s')

    # stop sending current to any motor
    GPIO.output(MotorL1['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL1['INPUT2'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT1'], GPIO.LOW)
    GPIO.output(MotorL2['INPUT2'], GPIO.LOW)

    GPIO.output(MororR1['INPUT1'], GPIO.LOW)
    GPIO.output(MororR1['INPUT2'], GPIO.LOW)
    GPIO.output(MororR2['INPUT1'], GPIO.LOW)
    GPIO.output(MororR2['INPUT2'], GPIO.LOW)

    sleep(seconds)

def square_cos(alpha):
    if (0 <= alpha <= 45) or (315 <= alpha <= 360):
        return 1.0
    elif 45 < alpha < 135:
        return -(((alpha - 45) * 2.0) / 90.0 - 1.0)
    elif 135 <= alpha <= 225:
        return -1.0
    elif 225 < alpha < 315:
        return ((alpha - 225) * 2.0) / 90.0 - 1.0

def square_sin(alpha):
    if 0 <= alpha < 45:
        return alpha / 45.0
    elif 45 <= alpha <= 135:
        return 1.0
    elif 135 < alpha < 225:
        return -(((alpha - 135) * 2.0) / 90.0 - 1.0)
    elif 225 <= alpha <= 315:
        return -1.0
    elif 315 < alpha <= 360:
        return (alpha - 315) / 45.0 - 1.0

# vector rotation in a square
def rotate_vector_square(v, alpha):
    mult, alpha = 1 if alpha >= 0 else -1, alpha if alpha >= 0 else -alpha

    cos = square_cos(alpha)
    sin = square_sin(alpha)

    x, y = v[0]*mult*cos - v[1]*mult*sin, v[0]*mult*sin + v[1]*mult*cos
    return (1 if x > 1.0 else x, 1 if y > 1.0 else y)

def heatmap_process(GROUND_MAP):
    fig, ax = plt.subplots(1, 1)
    ax.xaxis.tick_top()
    ax.set_ylabel('x')
    ax.set_xlabel('y')
    ax.xaxis.set_label_position('top')

    # remove all images from the last execution
    for img in os.listdir('img'):
        if 'map_' in img:
            os.remove(os.path.join('img', img))
    image_idx = 0

    while True:
        gmap = [GROUND_MAP[i:i+CELL_NUM] for i in range(0, CELL_NUM*CELL_NUM, CELL_NUM)]
        ax.imshow(gmap, cmap='gray')
        fig.canvas.draw()
        fig.savefig(f'./img/map_{image_idx}.png')
        plt.pause(0.05)

        image_idx += 1

def webserver_process():
    class ReqHandler(http.server.SimpleHTTPRequestHandler):
        def do_GET(self):
            if self.path == '/':
                self.path = 'index.html'
            return http.server.SimpleHTTPRequestHandler.do_GET(self)

    socketserver.TCPServer.allow_reuse_address = True
    socketserver.TCPServer(('192.168.1.173', 8082), ReqHandler).serve_forever()

# algorithm to find an empty cell near the robot position.
# it scans all the cells in the square around the cell where the robot is.
# if no empty cell is found the algorithm scans all cells in the square around the robot with distance increased by 1.
def find_empty_cell():
    radar = 1.0

    while radar < CELL_NUM:
        angle_increment = 45.0/radar

        for scan_pos in range(int(8*radar)):
            next_pos = rotate_vector_square(OBJ_POS, angle_increment*scan_pos)
            wrld_next_pos = (round(WRLD_POS[0] + next_pos[0]*radar), round(WRLD_POS[1] + next_pos[1]*radar))

            # check if this cell is empty
            if wrld_next_pos[1] >= 0 and wrld_next_pos[1] < CELL_NUM and \
               wrld_next_pos[0] >= 0 and wrld_next_pos[0] < CELL_NUM and \
               GROUND_MAP[wrld_next_pos[0]*CELL_NUM + wrld_next_pos[1]] == 1:
                return ((next_pos[0], next_pos[1]), radar, angle_increment*scan_pos)

        radar += 1.0

    return None

# listens to metal detector frequency values
def metal_detector_listener():
    global FREQ_VALUES

    while True:
        freq = int(SERIAL_PORT.readline().decode('utf-8')[:-2])
        if len(FREQ_VALUES) < 8:
            FREQ_VALUES.append(freq)
        else:
            FREQ_VALUES = FREQ_VALUES[1:]
            FREQ_VALUES.append(freq)

# ----- SETUP -----

# thread from main process which listens to metal detector values
metal_detector_freq = Thread(target=metal_detector_listener, args=(), daemon=True)
metal_detector_freq.start()

# background process to handle data plotting
plot_process = Process(None, heatmap_process, args=(GROUND_MAP,))
plot_process.start()

# background process for the web server
server_process = Process(None, webserver_process, args=())
server_process.start()

# open webcam streaming on port 8081
subprocess.run(["sudo", "service", "motion", "start"])

# ----- EXECUTION -----

# this stop is to let the plot process to setup and load the figure
stop(10)

while True:
    # if all cells in the map have been visited then quit
    if SCANNED_CELLS == CELL_NUM*CELL_NUM:
        break

    # check nearest not scanned cells
    cell = find_empty_cell()

    # rotate car of cell[2] degrees, move forward of cell[1] steps
    if 0 < cell[2] < 180:
        move_left(cell[2])
    elif 180 <= cell[2] < 360:
        move_right(360-cell[2])

    # object position now points to the empty cell
    OBJ_POS = (round(cell[0][0]), round(cell[0][1]))

    # move towards empty cell and update robot position in world coordinates
    move_forward(cell[1])
    WRLD_POS = (round(WRLD_POS[0] + OBJ_POS[0]*cell[1]), round(WRLD_POS[1] + OBJ_POS[1]*cell[1]))

    # set map cell value as the mean of the last 8 frequencies recorded
    idx = WRLD_POS[0]*CELL_NUM + WRLD_POS[1]
    if idx >= 0 and idx < CELL_NUM*CELL_NUM and GROUND_MAP[idx] == 1:
        GROUND_MAP[idx] = sum(FREQ_VALUES) / len(FREQ_VALUES)
        SCANNED_CELLS += 1

    # adjust robot orientation to nearest right angle
    if 0 < cell[2] <= 45:
        move_right(cell[2])
        OBJ_POS = rotate_vector_square(OBJ_POS, 360 - cell[2])
    elif 45 < cell[2] <= 90:
        move_left(90 - cell[2])
        OBJ_POS = rotate_vector_square(OBJ_POS, 90 - cell[2])
    elif 90 < cell[2] <= 135:
        move_right(cell[2] - 90)
        OBJ_POS = rotate_vector_square(OBJ_POS, 360 - (cell[2] - 90))
    elif 135 < cell[2] <= 180:
        move_left(180 - cell[2])
        OBJ_POS = rotate_vector_square(OBJ_POS, 180 - cell[2])
    elif 180 < cell[2] <= 225:
        move_right(cell[2] - 180)
        OBJ_POS = rotate_vector_square(OBJ_POS, 360 - (cell[2] - 180))
    elif 225 < cell[2] <= 270:
        move_left(270 - cell[2])
        OBJ_POS = rotate_vector_square(OBJ_POS, 170 - cell[2])
    elif 270 < cell[2] <= 315:
        move_right(cell[2] - 270)
        OBJ_POS = rotate_vector_square(OBJ_POS, 360 - (cell[2] - 270))
    elif 315 < cell[2] < 360:
        move_left(360 - cell[2])
        OBJ_POS = rotate_vector_square(OBJ_POS, 360 - cell[2])

    stop(2)
    print('---------------------')

stop(16)

# close webcam streaming
subprocess.run(["sudo", "service", "motion", "stop"])

# terminate plotting and webserver processes
plot_process.terminate()
server_process.terminate()

# sets all pins used in the program back to input mode
GPIO.cleanup()
