// Fragment shader: Phong shading
// ================
#version 320 es

precision highp float;

// Input data
in vec3 E;
in vec3 N;
in vec3 L;

// Ouput data
out vec3 FragColor;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct PointLight {
	vec3 position;
	vec3 color;
	float power;
 };

uniform Material material;
uniform PointLight light;

void main() {
    vec3 normal = normalize(N);
    vec3 lightSourceDirection = normalize(L);
    vec3 eyePosition = normalize(E);

    // Emissive component: 0

    // Ambient component: I_a * k_a
    vec3 ambient = light.power * material.ambient;

    // Diffuse component: k_d * (l * n) * I_l
    vec3 diffuse = material.diffuse * (max(dot(normal, lightSourceDirection), 0.0)) * light.power * light.color;

    // Specular component: k_s * I_l * (v * r)^(n_s)
    vec3 specular =  material.specular * light.power * light.color * (pow(max(dot(eyePosition, reflect(-lightSourceDirection, normal)), 0.0), material.shininess));

    FragColor = ambient + diffuse + specular;
}