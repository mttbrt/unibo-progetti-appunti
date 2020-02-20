cmake_minimum_required(VERSION 3.13)
project(OpenGLWorkbench)
set(CMAKE_CXX_STANDARD 14)

find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)
find_package(GLEW REQUIRED)
find_package(glm REQUIRED)
include_directories( ${OPENGL_INCLUDE_DIRS} ${GLUT_INCLUDE_DIRS} ${GLEW_INCLUDE_DIRS} ${GLM_INCLUDE_DIRS})

add_executable(lab05 argparser.h bag.h boundingbox.cpp boundingbox.h camera.cpp camera.h edge.cpp edge.h face.cpp face.h freeglut.h
        freeglut_ext.h freeglut_std.h glCanvas.cpp glCanvas.h glut.h hit.h main.cpp material.cpp material.h matrix.cpp matrix.h
        mesh.cpp mesh.h radiosity.cpp radiosity.h ray.h raytracer.h raytracer.cpp raytracer.h raytree.cpp raytree.h sphere.cpp sphere.h utils.h vectors.h vertex.h vertex_parent.h)
target_link_libraries(lab05 ${OPENGL_LIBRARIES} ${GLUT_LIBRARIES} ${GLEW_LIBRARY} ${GLM_LIBRARY})