// Vertex shader: Gouraud shading
// ================
#version 320 es

// Input vertex data, different for all executions of this shader.
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

out vec3 LightingColor; // resulting color from lighting calculations

// Values that stay constant for the whole mesh.
uniform mat4 P;
uniform mat4 V;
uniform mat4 M; // position*rotation*scaling

struct PointLight{
	vec3 position;
	vec3 color;
	float power;
 };
uniform PointLight light;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
}; 
uniform Material material;

void main()
{
    gl_Position = P * V * M * vec4(aPos, 1.0);

    // Position in VCS
	vec4 eyePosition = V * M * vec4(aPos, 1.0);
	// LightPos in VCS
	vec4 eyeLightPos = V * vec4(light.position, 1.0);

	// Compute vectors E,L,N in VCS
	vec3 E = -eyePosition.xyz;
	vec3 L = (eyeLightPos - eyePosition).xyz;
	vec3 N = transpose(inverse(mat3(V * M))) * aNormal;

    // ambient
    vec3 ambient = light.power * material.ambient;
  	
    // diffuse 
    vec3 norm = normalize(N);
    vec3 lightDir = normalize(L);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.power * light.color * (diff * material.diffuse);

    // specular
    vec3 viewDir = normalize(E);
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular =  light.power * light.color * (spec * material.specular);  

    LightingColor = ambient + diffuse + specular;
}