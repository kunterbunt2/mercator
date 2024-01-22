package de.bushnaq.abdalla.mercator;

import de.bushnaq.abdalla.mercator.audio.synthesis.util.LiniarTranslation;
import de.bushnaq.abdalla.mercator.audio.synthesis.util.TranslationUtil;
import de.bushnaq.abdalla.mercator.renderer.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.math.Vector3;

public class MercatorFboTest extends TranslationUtil<LiniarTranslation> {
	private static final float CUBE_SIZE = 64;
	private static final int MAX_LINIAR_TRANSLATION = 500;
	protected static final int NUMBER_OF_SOURCES = 1;
	private float factor = 2f;
	private float factorIncrement = 1;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	Vector3 minSpeed = new Vector3(Trader.MIN_ENGINE_SPEED, 0, 0);
	private int switchedDirection;

	@Override
	public void create() {
		//		simulateBassBoost = true;
		super.create(NUMBER_OF_SOURCES);
		createCamera();
		try {
			for (int l = 0; l < NUMBER_OF_SOURCES; l++) {
				final float x = 0;
				final float y = CUBE_SIZE / 2;
				final float z = 0;
				final LiniarTranslation t = new LiniarTranslation();
				t.origin.set(x, y, z);
				t.position.set(x - MAX_LINIAR_TRANSLATION, y, z);
				//				final float speed = Trader.MIN_ENGINE_SPEED;
				t.velocity.set(minSpeed);
				translation.add(t);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}

		sceneManager.setInfoVisible(false);
	}

	private void createCamera() {
		Gdx.files = new Lwjgl3Files();
		Lwjgl3NativesLoader.load();
		final MovingCamera camera = sceneManager.getCamera();
		camera.position.set(0f, 200f, 200f);
		camera.up.set(0f, 1f, 0f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 8000f;
		camera.update();
	}

	@Test
	public void liniarTranslatingSources() throws Exception {
		runFor = 20000;
		startLwjgl();
	}

	@Override
	protected void updateTranslation() {
		for (int i = 0; i < gameObjects.size(); i++) {
			final LiniarTranslation t = translation.get(i);
			if (universe.isEnableTime()) {
				t.position.x += t.velocity.x;
				t.position.z += t.velocity.z;
				if (Math.abs(t.position.x - t.origin.x) > MAX_LINIAR_TRANSLATION) {
					t.velocity.x = -t.velocity.x;
					switchedDirection++;
				}
				if (Math.abs(t.position.z - t.origin.z) > MAX_LINIAR_TRANSLATION) {
					t.velocity.z = -t.velocity.z;
					switchedDirection++;
				}
				if (switchedDirection > 1) {
					final Vector3 newSpeed = t.velocity.cpy().nor().scl(minSpeed).scl(factor);
					//					logger.info("x=" + newSpeed.x + " y=" + newSpeed.y + " z=" + newSpeed.z);
					t.velocity.set(newSpeed);
					switchedDirection = 0;
					if (t.velocity.len() >= Trader.MAX_ENGINE_SPEED || t.velocity.len() <= Trader.MIN_ENGINE_SPEED) {
						factorIncrement = -factorIncrement;
						factor += factorIncrement;
					} else {
						factor += factorIncrement;
					}
				}
			}
		}
	}
}