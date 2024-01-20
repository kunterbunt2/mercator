#ifdef GLSL3
#define attribute in
#define varying out
#endif

attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_clippingPlane; //clipping plane for water refraction and reflections
uniform vec4 u_cameraPosition;
uniform float u_tiling;
varying vec2 v_texCoord0;
varying vec4 v_clipSpace;
varying vec3 v_toCamera;

void main() {
	//clipping
	vec4 worldPosition = u_worldTrans * vec4(a_position, 1.0);
	gl_ClipDistance[0] = dot(worldPosition, u_clippingPlane);
	//coords
	v_clipSpace = u_projViewTrans * worldPosition;
	gl_Position = v_clipSpace;
	//DUDV
	v_texCoord0 = a_texCoord0 * u_tiling;
	v_toCamera = u_cameraPosition.xyz - worldPosition.xyz;
}
