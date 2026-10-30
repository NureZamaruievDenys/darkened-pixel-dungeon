#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
precision PRECISION int;
#else
	#define PRECISION
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_texture1;

uniform float u_time;
uniform float u_amount;
uniform float u_speed;
uniform vec2 u_screenSize;

void main () {
    vec4 waterTile = texture2D(u_texture, v_texCoords) * v_color;

    vec2 reflectUV = gl_FragCoord.xy / u_screenSize;
    reflectUV.y += 0.03;

    reflectUV.y += (cos((reflectUV.y + (u_time * 0.04 * u_speed)) * 45.0) * 0.0019 * u_amount)
    + (cos((reflectUV.y + (u_time * 0.1 * u_speed)) * 10.0) * 0.002 * u_amount);

    reflectUV.x += (sin((reflectUV.y + (u_time * 0.07 * u_speed)) * 15.0) * 0.0029 * u_amount)
    + (sin((reflectUV.y + (u_time * 0.1 * u_speed)) * 15.0) * 0.002 * u_amount);

    vec4 reflectColor = texture2D(u_texture1, reflectUV);

    reflectColor.rgb *= vec3(0.5, 0.65, 0.85);

    gl_FragColor = mix(waterTile, reflectColor, 0.55 * waterTile.a);
}