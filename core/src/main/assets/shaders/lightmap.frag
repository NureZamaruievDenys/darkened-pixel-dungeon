#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;  // fboWorld
uniform sampler2D u_lightmap; // fboLight

void main() {
    vec4 worldColor = texture2D(u_texture, v_texCoords);
    vec4 lightColor = texture2D(u_lightmap, v_texCoords);

    vec3 clampedLight = max(lightColor.rgb, vec3(0.30, 0.32, 0.42));

    vec3 finalRGB = worldColor.rgb * clampedLight;

    gl_FragColor = vec4(finalRGB, worldColor.a);
}