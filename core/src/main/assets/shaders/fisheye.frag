#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
precision PRECISION int;
#else
    #define PRECISION
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_distortion;

void main() {
    vec2 uv = v_texCoords;
    vec2 xy = 2.0 * uv - 1.0;
    float d = length(xy);

    if (u_distortion > 0.001) {
        xy *= 1.0 + u_distortion * (d * d);
        uv = (xy + 1.0) * 0.5;
    }

    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        gl_FragColor = texture2D(u_texture, uv);
    }
}