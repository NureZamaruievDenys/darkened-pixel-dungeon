package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Uniform;

public class WaterScript extends NoosaScriptNoLighting {

    public Uniform uTexture1;
    public Uniform uTime;
    public Uniform uAmount;
    public Uniform uSpeed;
    public Uniform uScreenSize;
    public Uniform uTileUV;

    public static WaterScript get() {
        return Script.use( WaterScript.class );
    }

    public WaterScript() {
        super();

        uTexture1   = uniform( "u_texture1" );
        uTime       = uniform( "u_time" );
        uAmount     = uniform( "u_amount" );
        uSpeed      = uniform( "u_speed" );
        uScreenSize = uniform( "u_screenSize" );
        uTileUV     = uniform( "u_tileUV" );
    }

    @Override
    public void use() {
        super.use();

        if (Game.instance != null && Game.instance.fboReflection != null) {
            com.badlogic.gdx.graphics.Texture fboTexture = Game.instance.fboReflection.getColorBufferTexture();

            if (fboTexture != null && fboTexture.getTextureObjectHandle() != 0) {
                fboTexture.bind( 1 );
                Gdx.gl.glActiveTexture( GL20.GL_TEXTURE0 );

                if (uTexture1 != null) uTexture1.value1i( 1 );

                Camera camera = Camera.main;
                float zoom = (camera != null) ? camera.zoom : 1.0f;
                float singleTileUV = (16f * zoom) / (float) Gdx.graphics.getHeight();

                if (uTileUV != null) uTileUV.value1f( singleTileUV );
                if (uTime != null) uTime.value1f( Game.timeTotal );
                if (uAmount != null) uAmount.value1f( 1.0f );
                if (uSpeed != null) uSpeed.value1f( 1.0f );
                if (uScreenSize != null) uScreenSize.value2f( (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight() );
            }
        }
    }

    @Override
    protected String shader() {
        return SHADER;
    }

    private static final String SHADER =
            "uniform mat4 uCamera;\n" +
                    "uniform mat4 uModel;\n" +
                    "attribute vec4 aXYZW;\n" +
                    "attribute vec2 aUV;\n" +
                    "varying vec2 vUV;\n" +
                    "void main() {\n" +
                    "  gl_Position = uCamera * uModel * aXYZW;\n" +
                    "  vUV = aUV;\n" +
                    "}\n" +

                    "//\n" +

                    "#ifdef GL_ES\n" +
                    "  precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec2 vUV;\n" +
                    "uniform sampler2D uTex;\n" +
                    "uniform sampler2D u_texture1;\n" +
                    "uniform float u_time;\n" +
                    "uniform float u_amount;\n" +
                    "uniform float u_speed;\n" +
                    "uniform vec2 u_screenSize;\n" +
                    "uniform float u_tileUV;\n" +

                    "void main() {\n" +
                    "  vec4 waterTile = texture2D( uTex, vUV );\n" +
                    "  vec2 screenUV = gl_FragCoord.xy / u_screenSize;\n" +

                    "  vec2 tileCoord = screenUV / max(u_tileUV, 0.0001);\n" +

                    "  vec2 reflectUV = screenUV;\n" +
                    "  reflectUV.x += sin(u_time * 2.0 + tileCoord.y * 2.5) * (u_tileUV * 0.03) * u_amount;\n" +
                    "  reflectUV.y += cos(u_time * 2.5 + tileCoord.x * 2.5) * (u_tileUV * 0.05) * u_amount;\n" +

                    "  vec4 reflectColor = texture2D( u_texture1, reflectUV );\n" +
                    "  vec3 tintedReflect = reflectColor.rgb * vec3( 0.65, 0.8, 1.1 );\n" +

                    "  vec3 finalRGB = mix(waterTile.rgb, tintedReflect, 0.6 * reflectColor.a * waterTile.a);\n" +
                    "  gl_FragColor = vec4(finalRGB, waterTile.a);\n" +
                    "}\n";
}