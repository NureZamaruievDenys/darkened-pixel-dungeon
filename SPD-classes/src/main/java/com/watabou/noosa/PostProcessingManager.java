package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PostProcessingManager {

    public static PostProcessingManager instance = new PostProcessingManager();

    private ShaderProgram fisheyeShader;
    private ShaderProgram universalShader;

    public float poisonIntensity = 0f;
    public float poisonDistortion = 0f;

    public float saturation = 1f;
    public float vignetteIntensity = 0f;
    public float vignetteRed = 0f;
    public float tintAmount = 0f;
    public float tintR = 1f, tintG = 1f, tintB = 1f;

    public float starvingFlash = 0f;
    public float teleportFlash = 0f;

    public float chromaticDistortion = 0f;
    public float fisheyeAmount = 0f;
    public float corrosionNoise = 0f;

    public boolean testMode = true;


    public static void triggerStarvingFlash() {
        instance.starvingFlash = 0.85f;
    }

    public static void triggerTeleport() {
        instance.teleportFlash = 0.16f;
    }


    public void update(float delta) {
        if (starvingFlash > 0f) {
            starvingFlash -= delta * 2.0f;
            if (starvingFlash < 0f) starvingFlash = 0f;
        }

        if (teleportFlash > 0f) {
            teleportFlash -= delta * 0.18f;
            if (teleportFlash < 0f) teleportFlash = 0f;
        }
    }

    public void init(int width, int height) {
        universalShader = loadShader("shaders/blur.vert", "shaders/universal.frag");
        fisheyeShader = loadShader("shaders/blur.vert", "shaders/fisheye.frag");
    }

    private ShaderProgram loadShader(String vertPath, String fragPath) {
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertPath), Gdx.files.internal(fragPath));
        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderError", shader.getLog());
        }
        return shader;
    }

    public ShaderProgram getActiveShader() {
        float activeChromatic = chromaticDistortion + teleportFlash;

        if (Math.abs(saturation - 1.0f) > 0.001f || tintAmount > 0.001f ||
                corrosionNoise > 0.001f || activeChromatic > 0.0001f ||
                poisonDistortion > 0.0001f || vignetteIntensity > 0.001f || starvingFlash > 0.001f) {
            return universalShader;
        }
        if (fisheyeAmount > 0.001f) return fisheyeShader;
        return null;
    }

    public void bindUniforms(ShaderProgram shader) {
        if (shader == universalShader) {
            setUniformIfHas(shader, "u_fisheyeAmount", fisheyeAmount); // Передаем изгиб
            setUniformIfHas(shader, "u_saturation", saturation);

            float currentVignette = Math.max(vignetteIntensity, starvingFlash);
            setUniformIfHas(shader, "u_vignetteIntensity", currentVignette);

            if (shader.hasUniform("u_vignetteColor")) {
                float r = (vignetteIntensity > 0f) ? vignetteRed : 0f;
                shader.setUniformf("u_vignetteColor", r, 0f, 0f);
            }

            if (shader.hasUniform("u_tintColor")) {
                shader.setUniformf("u_tintColor", tintR, tintG, tintB);
            }
            setUniformIfHas(shader, "u_tintAmount", tintAmount);

            setUniformIfHas(shader, "u_noiseAmount", corrosionNoise);
            setUniformIfHas(shader, "u_seed", Game.timeTotal);

            setUniformIfHas(shader, "u_chromaticAmount", chromaticDistortion + teleportFlash);
            setUniformIfHas(shader, "u_distortionAmount", poisonDistortion);
            setUniformIfHas(shader, "u_time", Game.timeTotal);
        }
    }

    private void setUniformIfHas(ShaderProgram shader, String name, float value) {
        if (shader.hasUniform(name)) {
            shader.setUniformf(name, value);
        }
    }
}