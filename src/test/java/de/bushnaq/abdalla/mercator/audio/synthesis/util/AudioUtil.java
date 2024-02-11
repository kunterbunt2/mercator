package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.mercator.audio.synthesis.OpenAlException;
import de.bushnaq.abdalla.mercator.desktop.DesktopContextFactory;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class AudioUtil implements ApplicationListener, InputProcessor {
	private BitmapFont font;
	private boolean hrtfEnabled = true;
	private final Matrix4 identityMatrix = new Matrix4();
	private final List<Label> labels = new ArrayList<>();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected MercatorRandomGenerator rg = new MercatorRandomGenerator(1, null);
	protected Screen3D sceneManager;
	protected boolean simulateBassBoost = true;
	private Stage stage;
	private StringBuilder stringBuilder;
	private boolean takeScreenShot = false;
	protected Universe universe;
	DesktopContextFactory contextFactory = new DesktopContextFactory();
	@Override
	public void create() {
		try {
			final GraphicsDimentions gd = GraphicsDimentions.D3;
			contextFactory.create();
			universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
			createStage();
			sceneManager = new Screen3D(contextFactory,universe, LaunchMode.development);
			sceneManager.renderEngine.setAlwaysDay(true);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Lwjgl3ApplicationConfiguration createConfig() {
		Lwjgl3ApplicationConfiguration config;
		config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(true);
		config.setForegroundFPS(0);
		config.setResizable(false);
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2); // use GL 3.0 (emulated by OpenGL 3.2)
//		config.useOpenGL3(true, 3, 2);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
		config.setTitle("Mercator");
		final Monitor[] monitors = Lwjgl3ApplicationConfiguration.getMonitors();
		final DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[1]);
		config.setFullscreenMode(primaryMode);
		return config;
	}

	private void createStage() throws Exception {
		final int height = 12;
		stage = new Stage();
		font = new BitmapFont();
		for (int i = 0; i < 8; i++) {
			final Label label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
			label.setPosition(0, i * height);
			stage.addActor(label);
			labels.add(label);
		}
		stringBuilder = new StringBuilder();
	}

	@Override
	public void dispose() {
		try {
			sceneManager.dispose();
			font.dispose();
			Gdx.app.exit();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean keyDown(final int keycode) {
		switch (keycode) {
		case Input.Keys.Q:
			Gdx.app.exit();
			return true;
		case Input.Keys.P:
			universe.setEnableTime(!universe.isEnableTime());
			return true;
		case Input.Keys.PRINT_SCREEN:
			takeScreenShot = true;
			return true;
		case Input.Keys.NUM_2:
			simulateBassBoost = !simulateBassBoost;
			if (simulateBassBoost)
				logger.info("bassBoost on");
			else
				logger.info("bassBoost off");
			return true;
		case Input.Keys.H:
			try {
				if (hrtfEnabled) {
					sceneManager.audioEngine.disableHrtf(0);
					hrtfEnabled = false;
				} else {
					sceneManager.audioEngine.enableHrtf(0);
					hrtfEnabled = true;
				}
			} catch (final OpenAlException e) {
				logger.error(e.getMessage(), e);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void render() {
		try {
			universe.advanceInTime();
			update();
			sceneManager.renderEngine.render(universe.currentTime, Gdx.graphics.getDeltaTime(), takeScreenShot);
			sceneManager.renderEngine.postProcessRender();

			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			//			Gdx.gl.glEnable(GL20.GL_BLEND);
			sceneManager.renderEngine.batch2D.enableBlending();
			sceneManager.renderEngine.batch2D.begin();
			//			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			sceneManager.renderEngine.batch2D.setProjectionMatrix(sceneManager.renderEngine.getCamera().combined);
			renderText();
			sceneManager.renderEngine.batch2D.end();
			sceneManager.renderEngine.batch2D.setTransformMatrix(identityMatrix);//fix transformMatrix
			//			renderStage();
			takeScreenShot = false;
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void renderStage() throws Exception {
		int labelIndex = 0;
		// fps
		{
			stringBuilder.setLength(0);
			stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
			labels.get(labelIndex++).setText(stringBuilder);
		}
		//audio sources
		{
			stringBuilder.setLength(0);
			stringBuilder.append(" audio sources: ").append(sceneManager.audioEngine.getEnabledAudioSourceCount() + " / " + sceneManager.audioEngine.getDisabledAudioSourceCount());
			labels.get(labelIndex++).setText(stringBuilder);
		}
		stage.draw();
	}

	protected abstract void renderText();

	@Override
	public void resize(final int width, final int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		return false;
	}

	protected void startLwjgl() {
		final Lwjgl3ApplicationConfiguration config = createConfig();
		new Lwjgl3Application(this, config);
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	protected abstract void update() throws Exception;

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}
}
