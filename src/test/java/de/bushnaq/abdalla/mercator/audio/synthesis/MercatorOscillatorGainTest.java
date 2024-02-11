package de.bushnaq.abdalla.mercator.audio.synthesis;

import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

public class MercatorOscillatorGainTest {
	private static final float HIGHEST_FREQUENCY = 2 * 261.6256f;//C5
	private static final float LOWEST_FREQUENCY = 32.70320f;//C1
	private MovingCamera camera;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private void createCamera() throws Exception {
		Gdx.files = new Lwjgl3Files();
		Lwjgl3NativesLoader.load();
		camera = new MovingCamera(67f, 640, 480);
		camera.position.set(300f, 500f, 400f);
		camera.lookAt(camera.lookat);
		camera.near = 1f;
		camera.far = 8000f;
		camera.update();
	}

	@Test
	public void gainTest() throws Exception {
		final MercatorAudioEngine audioEngine = new MercatorAudioEngine(44100 * 5);
		createCamera();
		audioEngine.create();
		{
			final Synthesizer synth = audioEngine.createAudioProducer(MercatorSynthesizer.class);
			audioEngine.begin(camera);
			for (int i = 2; i <= 25; i++) {
				final float speed = i;
				synth.adaptToVelocity(i);
				final float gain = synth.getGain();
				final float frequency = LOWEST_FREQUENCY + (HIGHEST_FREQUENCY - LOWEST_FREQUENCY) * (speed - Trader.MIN_ENGINE_SPEED) / (Trader.MAX_ENGINE_SPEED - Trader.MIN_ENGINE_SPEED);
				final float bassGain = 1 - (speed - Trader.MIN_ENGINE_SPEED) / (Trader.MAX_ENGINE_SPEED - Trader.MIN_ENGINE_SPEED);
				logger.info(String.format("speed = %d bassGain = %f gain = %f freq = %f", i, bassGain, gain, frequency));
			}
			audioEngine.end();
		}
		audioEngine.dispose();
	}

}