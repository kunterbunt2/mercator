package com.abdalla.bushnaq.mercator.audio.synthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abdalla.bushnaq.mercator.audio.synthesis.util.SawAudioEngine;
import com.abdalla.bushnaq.mercator.renderer.camera.MovingCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

public class MercatorOscillatorTest {
	private static final int SECONDS_2 = 2000;
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
	public void renderPerformanceTest() throws Exception {
		final MercatorAudioEngine audioEngine = new MercatorAudioEngine();
		audioEngine.create();
		createCamera();
		final int numberOfSources = audioEngine.getMaxMonoSources();
		final List<Synthesizer> synths = new ArrayList<>();
		//create synths
		for (int i = 0; i < numberOfSources; i++) {
			synths.add(audioEngine.createAudioProducer(MercatorSynthesizer.class));
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
	public void renderTest() throws Exception {
		final MercatorAudioEngine audioEngine = new MercatorAudioEngine(44100 * 5);
		audioEngine.create();
		createCamera();
		{
			final Synthesizer synth = audioEngine.createAudioProducer(MercatorSynthesizer.class);
			synth.setGain(5);
			synth.play();
			audioEngine.begin(camera);
			final long time1 = System.currentTimeMillis();
			do {
			} while (System.currentTimeMillis() - time1 < SECONDS_2);
			synth.renderBuffer();
			synth.writeWav("target/mercator.wav");
			audioEngine.end();
		}
		audioEngine.dispose();
	}

}