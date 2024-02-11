package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.audio.synthesis.Synthesizer;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

public class SawOscillatorTest {
	private static final int SECONDS_2 = 2000;
	private MovingCamera camera;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private float calcualteSpeed(final float frequency) {
		return Trader.MIN_ENGINE_SPEED + (((frequency - 220) / (500 - 220)) * (Trader.MAX_ENGINE_SPEED - Trader.MIN_ENGINE_SPEED));
	}

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
	public void renderPerformanceTest() throws Exception {
		final SawAudioEngine audioEngine = new SawAudioEngine();
		audioEngine.create();
		createCamera();
		final int numberOfSources = audioEngine.getMaxMonoSources();
		final List<Synthesizer> synths = new ArrayList<>();
		//create synths
		for (int i = 0; i < numberOfSources; i++) {
			synths.add(audioEngine.createAudioProducer(SawSynthesizer.class));
		}

		audioEngine.begin(camera);
		final long time1 = System.currentTimeMillis();
		for (final Synthesizer synth : synths) {
			synth.renderBuffer();
		}
		final long time2 = System.currentTimeMillis();
		final long delta = time2 - time1;
		audioEngine.end();
		audioEngine.dispose();
		logger.info(String.format("Rendered %d buffers each %d samples in %dms", numberOfSources, SawAudioEngine.samplerate, delta));
		assertThat(String.format("expected to render %d buffers each %d samples in less than 1s", numberOfSources, SawAudioEngine.samples), delta, is(lessThan(1000L)));
	}

	@Test
	public void renderSpeedTest() throws Exception {
		final SawAudioEngine audioEngine = new SawAudioEngine();
		audioEngine.create();
		createCamera();
		{
			final SawSynthesizer synth = audioEngine.createAudioProducer(SawSynthesizer.class);
			synth.setGain(5);

			float frequency = 220.0f;
			float speed = calcualteSpeed(frequency);
			synth.saw1.setFrequency(frequency);

			synth.setPositionAndVelocity(new float[] { frequency, 0, 0 }, new float[] { speed, 0, 0 });
			synth.play();
			audioEngine.begin(camera);
			final long time1 = System.currentTimeMillis();
			long lastTime = 0;
			long time2;
			float delta = 2;
			synth.saw1.setOscillator(frequency);
			synth.setGain(20.0f);
			//						synth.setKeepCopy(true);
			do {
				time2 = System.currentTimeMillis();
				if (time2 - lastTime > 20) {
					frequency += delta;
					lastTime = time2;
					if (frequency > 500 || frequency < 220) {
						delta *= -1f;
					}

				} else {
					Thread.sleep(10);
				}
				speed = calcualteSpeed(frequency);
				//				logger.info(String.format("speed = %f", speed));
				synth.setPositionAndVelocity(new float[] { frequency, 0, 0 }, new float[] { speed, 0, 0 });
			} while (time2 - time1 < 6000);
			synth.writeWav("target/sinSpeed");
			synth.setKeepCopy(false);
			audioEngine.end();
		}
		audioEngine.dispose();
	}

	@Test
	public void renderTest() throws Exception {
		final SawAudioEngine audioEngine = new SawAudioEngine();
		audioEngine.create();
		createCamera();
		{
			final Synthesizer synth = audioEngine.createAudioProducer(SawSynthesizer.class);
			synth.setGain(5);
			synth.play();
			audioEngine.begin(camera);
			final long time1 = System.currentTimeMillis();
			do {
			} while (System.currentTimeMillis() - time1 < SECONDS_2);
			synth.renderBuffer();
			synth.writeWav("target/saw");
			audioEngine.end();
		}
		audioEngine.dispose();
	}

}