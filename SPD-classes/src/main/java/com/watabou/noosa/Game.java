/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.watabou.glscripts.Script;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.input.ControllerHandler;
import com.watabou.input.InputHandler;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Reflection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.IntBuffer;

public class Game implements ApplicationListener {

	public static Game instance;

	public FrameBuffer fboLight, fboWorld, fboReflection, fboBase, fboBloomMask, fboPing, fboPong;
	private TextureRegion regLight, regWorld, regReflection, regBase, regBloomMask, regPing, regPong;
	public ShaderProgram lightmapShader, blurShader, combineShader, thresholdShader, waterShader;
	private SpriteBatch postBatch;
	private final IntBuffer glStateBuf = BufferUtils.newIntBuffer(16);
	private int savedVbo = -1;
	private int savedEbo = -1;
	private boolean wasInGameplay = false;
	
	// Size of the EGL surface view
	public static int width;
	public static int height;

	// Density: mdpi=1, hdpi=1.5, xhdpi=2...
	public static float density = 1;
	
	public static String version;
	public static int versionCode;
	
	// Current scene
	protected Scene scene;
	// New scene we are going to switch to
	protected Scene requestedScene;
	// true if scene switch is requested
	protected boolean requestedReset = true;
	// callback to perform logic during scene change
	protected SceneChangeCallback onChange;
	// New scene class
	protected static Class<? extends Scene> sceneClass;
	
	public static float timeScale = 1f;
	public static float elapsed = 0f;
	public static float timeTotal = 0f;
	public static long realTime = 0;

	public static InputHandler inputHandler;
	
	public static PlatformSupport platform;
	
	public Game(Class<? extends Scene> c, PlatformSupport platform) {
		sceneClass = c;
		
		instance = this;
		this.platform = platform;
	}
	
	@Override
	public void create() {
		density = Gdx.graphics.getDensity();
		if (density == Float.POSITIVE_INFINITY){
			density = 100f / 160f; //assume 100PPI if density can't be found
		} else if (DeviceCompat.isDesktop()) {
			int dispWidth = Gdx.graphics.getDisplayMode().width;
			int dispHeight = Gdx.graphics.getDisplayMode().height;
			float reportedWidth = dispWidth / Gdx.graphics.getPpiX();
			float reportedHeight = dispHeight / Gdx.graphics.getPpiY();

			//this exists because Steam deck reports its display size as 4"x6.3" for some reason
			// as if in portrait, instead of 6.3"x4". This results in incorrect PPI measurements.
			// So we check that the orientation of the resolution and the display dimensions match.
			// If they don't, re-calculate density assuming reported dimensions are flipped.
			if (dispWidth > dispHeight != reportedWidth > reportedHeight){
				float realPpiX = dispWidth / reportedHeight;
				density = realPpiX / 160f;
			}
		}

		inputHandler = new InputHandler( Gdx.input );
		if (ControllerHandler.controllersSupported()){
			Controllers.addListener(new ControllerHandler());
		}

		//refreshes texture and vertex data stored on the gpu
		versionContextRef = Gdx.graphics.getGLVersion();
		Blending.useDefault();
		TextureCache.reload();
		Vertexbuffer.reload();

		blurShader = new ShaderProgram(Gdx.files.internal("shaders/blur.vert"), Gdx.files.internal("shaders/convolve-1d.frag"));
		combineShader = new ShaderProgram(Gdx.files.internal("shaders/blur.vert"), Gdx.files.internal("shaders/combine.frag"));
		thresholdShader = new ShaderProgram(Gdx.files.internal("shaders/blur.vert"), Gdx.files.internal("shaders/gamma-threshold.frag"));
		waterShader = new ShaderProgram(Gdx.files.internal("shaders/water_reflection.vert"), Gdx.files.internal("shaders/water_reflection.frag"));
		lightmapShader = new ShaderProgram(Gdx.files.internal("shaders/blur.vert"), Gdx.files.internal("shaders/lightmap.frag"));
	}

	private GLVersion versionContextRef;
	
	@Override
	public void resize(int width, int height) {
		if (width == 0 || height == 0){
			return;
		}

		//If the EGL context was destroyed, we need to refresh some data stored on the GPU.
		// This checks that by seeing if GLVersion has a new object reference
		if (versionContextRef != Gdx.graphics.getGLVersion()) {
			versionContextRef = Gdx.graphics.getGLVersion();
			Blending.useDefault();
			TextureCache.reload();
			Vertexbuffer.reload();
		}

		if (height != Game.height || width != Game.width) {

			Game.width = width;
			Game.height = height;
			
			resetScene();
		}

		if (fboBase != null) {
			if (fboWorld != null) fboWorld.dispose();
			if (fboReflection != null) fboReflection.dispose();
			fboBase.dispose();
			fboBloomMask.dispose();
			fboPing.dispose();
			fboPong.dispose();
		}
		if (postBatch == null) postBatch = new SpriteBatch();
		if (fboLight != null) fboLight.dispose();
		fboLight = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		regLight = new TextureRegion(fboLight.getColorBufferTexture());
		regLight.flip(false, true);

		fboWorld = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		fboReflection = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		fboBase = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		fboBloomMask = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		fboPing = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		fboPong = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

		regWorld = new TextureRegion(fboWorld.getColorBufferTexture()); regWorld.flip(false, true);
		regReflection = new TextureRegion(fboReflection.getColorBufferTexture()); regReflection.flip(false, true);
		regBase = new TextureRegion(fboBase.getColorBufferTexture()); regBase.flip(false, true);
		regBloomMask = new TextureRegion(fboBloomMask.getColorBufferTexture()); regBloomMask.flip(false, true);
		regPing = new TextureRegion(fboPing.getColorBufferTexture()); regPing.flip(false, true);
		regPong = new TextureRegion(fboPong.getColorBufferTexture()); regPong.flip(false, true);
	}

	//justResumed is a bit of a hack to improve start time metrics on Android,
	// as texture refreshing leads to slow warm starts. TODO would be nice to fix this properly
	private boolean justResumed = true;

	@Override
	public void render() {
		if (instance != this){ finish(); return; }
		if (justResumed){ justResumed = false; if (DeviceCompat.isAndroid()) return; }

		boolean isGamePlay = scene != null && scene.getClass().getName().endsWith("GameScene");
		boolean isSwitching = switchingScene();

		if (isSwitching || !isGamePlay || fboBase == null) {
			if (wasInGameplay) {
				wasInGameplay = false;
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
			NoosaScript.get().resetCamera();
			NoosaScriptNoLighting.get().resetCamera();
			Gdx.gl.glDisable(Gdx.gl.GL_SCISSOR_TEST);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			draw();
			Gdx.gl.glDisable( Gdx.gl.GL_SCISSOR_TEST );
			step();
			return;
		}

		wasInGameplay = true;
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// === raw world render ===
		fboWorld.begin();
		NoosaScript.get().resetCamera();
		NoosaScriptNoLighting.get().resetCamera();
		WaterScript.get().resetCamera();
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scene.drawWorld();
		fboWorld.end();

		boolean useAdvancedLighting = true; //TODO SPD settings

		// === dynamic light ===
		if (useAdvancedLighting) {
			fboLight.begin();
			NoosaScript.get().resetCamera();
			NoosaScriptNoLighting.get().resetCamera();
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
			Gdx.gl.glClearColor(0.05f, 0.07f, 0.12f, 1.0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);

			scene.drawLightSources();

			Blending.useDefault();
			fboLight.end();
		}

		// === reflections ===
		fboReflection.begin();
		NoosaScript.get().resetCamera();
		NoosaScriptNoLighting.get().resetCamera();
		WaterScript.get().resetCamera();
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scene.drawReflections();
		fboReflection.end();

		// === fboBase (light + reflection) ===
		fboBase.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		postBatch.begin();
		if (useAdvancedLighting) {
			postBatch.setShader(lightmapShader);
			regLight.getTexture().bind(1);
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			lightmapShader.setUniformi("u_lightmap", 1);
		} else {
			postBatch.setShader(null);
		}
		postBatch.draw(regWorld, 0, 0, w, h);
		postBatch.end();

		postBatch.setShader(null);
		Script.reset();
		Blending.useDefault();
		NoosaScript.get().resetCamera();
		NoosaScriptNoLighting.get().resetCamera();
		WaterScript.get().resetCamera();

		scene.drawWater();
		fboBase.end();

		// === bloom ===
		fboBloomMask.begin();
		NoosaScript.get().resetCamera();
		NoosaScriptNoLighting.get().resetCamera();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scene.drawBloomObjects();
		fboBloomMask.end();

		glStateBuf.clear();
		Gdx.gl.glGetIntegerv(GL20.GL_ELEMENT_ARRAY_BUFFER_BINDING, glStateBuf);
		savedEbo = glStateBuf.get(0);
		glStateBuf.clear();
		Gdx.gl.glGetIntegerv(GL20.GL_ARRAY_BUFFER_BINDING, glStateBuf);
		savedVbo = glStateBuf.get(0);

		// === threshold mask  ===
		fboPing.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		postBatch.begin();
		postBatch.setShader(thresholdShader);
		// 0.55f..0.7f — light pixels
		thresholdShader.setUniformf("u_threshold", 0.8f);
		thresholdShader.setUniformf("u_thresholdInv", 2.5f);
		postBatch.draw(regBloomMask, 0, 0, w, h);
		postBatch.end();
		fboPing.end();

		// === blur horizontal ===
		fboPong.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		postBatch.begin();
		postBatch.setShader(blurShader);
		setBlurUniforms(blurShader, true, w, h);
		postBatch.draw(regPing, 0, 0, w, h);
		postBatch.end();
		fboPong.end();

		// === blur vertical ===
		fboPing.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		postBatch.begin();
		postBatch.setShader(blurShader);
		setBlurUniforms(blurShader, false, w, h);
		postBatch.draw(regPong, 0, 0, w, h);
		postBatch.end();
		fboPing.end();

		// === blending ===
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		ShaderProgram postShader = PostProcessingManager.instance.getActiveShader();

		postBatch.begin();
		if (postShader != null) {
			postBatch.setShader(postShader);
			PostProcessingManager.instance.bindUniforms(postShader);
		} else {
			postBatch.setShader(combineShader);
			regPing.getTexture().bind(1);
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			combineShader.setUniformi("u_texture1", 1);
			combineShader.setUniformf("u_src0Intensity", 1.0f);
			combineShader.setUniformf("u_src1Intensity", 1.5f);
			combineShader.setUniformf("u_src0Saturation", 1.0f);
			combineShader.setUniformf("u_src1Saturation", 1.0f);
		}

		postBatch.draw(regBase, 0, 0, w, h);
		postBatch.end();
		postBatch.setShader(null);

		// === ui and state recovery ===
		if (Gdx.gl30 != null) Gdx.gl30.glBindVertexArray(0);
		if (savedVbo != -1) Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, savedVbo);
		if (savedEbo != -1) Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, savedEbo);

		Script.reset();
		Blending.useDefault();
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		scene.drawUI();
		Gdx.gl.glDisable(Gdx.gl.GL_SCISSOR_TEST);
		step();
	}
	
	@Override
	public void pause() {
		if (scene != null) {
			scene.onPause();
		}
		
		Script.reset();
	}
	
	@Override
	public void resume() {
		justResumed = true;
	}
	
	public void finish(){
		Gdx.app.exit();
		
	}
	
	public void destroy(){
		if (scene != null) {
			scene.destroy();
			scene = null;
		}
		
		sceneClass = null;
		Music.INSTANCE.reset();
		Sample.INSTANCE.reset();
	}
	
	@Override
	public void dispose() {
		destroy();
	}
	
	public static void resetScene() {
		switchScene( instance.sceneClass );
	}

	public static void switchScene(Class<? extends Scene> c) {
		switchScene(c, null);
	}
	
	public static void switchScene(Class<? extends Scene> c, SceneChangeCallback callback) {
		instance.sceneClass = c;
		instance.requestedReset = true;
		instance.onChange = callback;
	}
	
	public static Scene scene() {
		return instance.scene;
	}

	public static boolean switchingScene() {
		return instance.requestedReset;
	}
	
	protected void step() {
		
		if (requestedReset) {
			requestedReset = false;
			
			requestedScene = Reflection.newInstance(sceneClass);
			if (requestedScene != null){
				switchScene();
			}

		}
		
		update();
	}
	
	protected void draw() {
		if (scene != null) scene.draw();
	}
	
	protected void switchScene() {

		Camera.reset();
		
		if (scene != null) {
			scene.destroy();
		}
		//clear any leftover vertex buffers
		Vertexbuffer.clear();
		scene = requestedScene;
		if (onChange != null) onChange.beforeCreate();
		scene.create();
		if (onChange != null) onChange.afterCreate();
		onChange = null;
		
		Game.elapsed = 0f;
		Game.timeScale = 1f;
		Game.timeTotal = 0f;
	}

	protected void update() {
		//game will not process more than 200ms of graphics time per frame
		float frameDelta = Math.min(0.2f, Gdx.graphics.getDeltaTime());
		Game.elapsed = Game.timeScale * frameDelta;
		Game.timeTotal += Game.elapsed;
		
		Game.realTime = TimeUtils.millis();

		inputHandler.processAllEvents();

		Music.INSTANCE.update();
		Sample.INSTANCE.update();
		scene.update();
		Camera.updateAll();
	}
	
	public static void reportException( Throwable tr ) {
		if (instance != null && Gdx.app != null) {
			instance.logException(tr);
		} else {
			//fallback if error happened in initialization
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			tr.printStackTrace(pw);
			pw.flush();
			System.err.println(sw.toString());
		}
	}
	
	protected void logException( Throwable tr ){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		Gdx.app.error("GAME", sw.toString());
	}
	
	public static void runOnRenderThread(Callback c){
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				c.call();
			}
		});
	}
	
	public static void vibrate( int milliseconds ) {
		if (platform.supportsVibration()) {
			platform.vibrate(milliseconds);
		}
	}

	public interface SceneChangeCallback{
		void beforeCreate();
		void afterCreate();
	}

	private void setBlurUniforms(com.badlogic.gdx.graphics.glutils.ShaderProgram shader, boolean horizontal, int w, int h) {
		float[] weights = {0.05f, 0.09f, 0.12f, 0.15f, 0.18f, 0.15f, 0.12f, 0.09f, 0.05f};
		float[] offsets = new float[18];
		float texelSize = (1.0f / (horizontal ? w : h)) * 2.5f;

		for (int i = 0; i < 9; i++) {
			int offset = i - 4;
			if (horizontal) {
				offsets[i*2] = offset * texelSize;
				offsets[i*2+1] = 0;
			} else {
				offsets[i*2] = 0;
				offsets[i*2+1] = offset * texelSize;
			}
		}
		shader.setUniform1fv("u_sampleWeights", weights, 0, 9);
		shader.setUniform2fv("u_sampleOffsets", offsets, 0, 18);
	}
	
}
