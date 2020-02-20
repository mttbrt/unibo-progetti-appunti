// Fragment shader: Blinn shading
// ================
#version 320 es

precision highp float;

// Input data
in vec3 N;
in vec3 L;

// Ouput data
out vec4 FragColor;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform Material material;

void main() {
    vec3 normal = normalize(N);
    vec3 lightSourceDirection = normalize(L);
    float intensity = dot(lightSourceDirection, normal);

    vec4 color;
    if (intensity > 0.9)
        color = vec4(material.diffuse, 1.0) * 1.5;
    else if (intensity > 0.5)
        color = vec4(material.diffuse, 1.0) * 1.0;
    else if (intensity > 0.25)
        color = vec4(material.diffuse, 1.0) * 0.75;
    else if (intensity > 0.05)
        color = vec4(material.diffuse, 1.0) * 0.5;
    else
        color = vec4(material.diffuse, 1.0) * 0.25;

    FragColor = color;
}
