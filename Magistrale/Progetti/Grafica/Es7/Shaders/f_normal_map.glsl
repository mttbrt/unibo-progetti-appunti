// Fragment shader: Normal mapping shading
// ================
#version 320 es

precision highp float;

in vec2 _TexCoord;
in vec3 TangentLightDir;
in vec3 TangentViewPos;
in vec3 TangentFragPos;

// Ouput data
out vec4 FragColor;

// uniforms
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
}; 

struct PointLight{
    vec3 position;
    vec3 color;
    float power;
};

uniform Material material;
uniform PointLight light;

void main() {
    // compute light direction
    vec3 lightSourceDirection = normalize(TangentLightDir);//normalize(TangentLightPos - TangentFragPos);

    // obtain normal from normal map in range [0,1]
    vec3 normal = texture(normalMap, _TexCoord).rgb;
    // transform normal vector to range [-1,1]
    normal = normalize(normal * 2.0 - 1.0); // Local normal in tangent space

    // emissive: 0

    // ambient
    vec3 ambient = 0.75 * light.power * material.ambient;

    // diffuse
    vec3 diffuse = 5.0 * material.diffuse * max(dot(lightSourceDirection, normal), 0.0) * light.power * light.color;

    // specular
    vec3 specular = 2.5 * (pow(max(dot(normalize(TangentViewPos - TangentFragPos), reflect(-lightSourceDirection, normal)), 0.0), material.shininess)) * material.specular * light.power * light.color;

    vec3 result = texture(diffuseMap, _TexCoord).rgb * (specular + diffuse + ambient);
    FragColor = vec4(result, 1.0);
}