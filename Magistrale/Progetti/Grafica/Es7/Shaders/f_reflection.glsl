#version 320 es

precision highp float;

out vec4 FragColor;

in vec3 Normal;
in vec3 Position;

uniform vec3 camera_position;
uniform samplerCube skybox;

void main() {
    vec3 I = normalize(Position - camera_position);
    vec3 R = reflect(I, normalize(Normal));
    FragColor = vec4(texture(skybox, R).rgb, 1.0);
}
