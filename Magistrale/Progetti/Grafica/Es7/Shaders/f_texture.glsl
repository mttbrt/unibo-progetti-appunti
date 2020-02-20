#version 320 es

precision highp float;

in vec2 _TexCoord;
// Ouput data
out vec4 fragColor;

// Values that stay constant for the whole mesh.
uniform sampler2D textureBuffer;

void main(){

	fragColor = texture(textureBuffer,_TexCoord);
}
