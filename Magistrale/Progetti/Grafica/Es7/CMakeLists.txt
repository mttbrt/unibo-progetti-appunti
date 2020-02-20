cmake_minimum_required(VERSION 3.13)
project(OpenGLWorkbench)
set(CMAKE_CXX_STANDARD 14)

find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)
find_package(GLEW REQUIRED)
find_package(glm REQUIRED)
include_directories( ${OPENGL_INCLUDE_DIRS} ${GLUT_INCLUDE_DIRS} ${GLEW_INCLUDE_DIRS} ${GLM_INCLUDE_DIRS})

add_executable(lab07 common.cpp common.h HUD_Logger.cpp HUD_Logger.h main.cpp main.h stb_image.h)
target_link_libraries(lab07 ${OPENGL_LIBRARIES} ${GLUT_LIBRARIES} ${GLEW_LIBRARY} ${GLM_LIBRARY})