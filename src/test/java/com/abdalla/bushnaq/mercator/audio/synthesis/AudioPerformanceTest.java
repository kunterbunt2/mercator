package com.abdalla.bushnaq.mercator.audio.synthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abdalla.bushnaq.mercator.renderer.camera.MovingCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

public class AudioPerformanceTest {
	MovingCamera camera;
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
		final List<Synthesizer> synths = new ArrayList<>();

		final MercatorAudioEngine audioEngine = new MercatorAudioEngine(44100);
		audioEngine.create();
		createCamera();
		final int numberOfSources = audioEngine.getMaxMonoSources();

		//create synths
		for (int i = 0; i < numberOfSources; i++) {
			final MercatorSynthesizer synth = audioEngine.createAudioProducer(MercatorSynthesizer.class);
			synth.pause();
			synths.add(synth);
		}

		final long time1 = System.currentTimeMillis();
		audioEngine.begin(camera);
		for (final Synthesizer synth : synths) {
			synth.renderBuffer();
		}
		final long time2 = System.currentTimeMillis();
		final long delta = time2 - time1;

		audioEngine.end();
		audioEngine.dispose();
		logger.info(String.format("Rendered %d buffers each %d samples in %dms", numberOfSources, audioEngine.getSamples(), delta));
		assertThat(String.format("expected to render %d buffers each %d samples in less than 1s", numberOfSources, audioEngine.getSamples()), delta, is(lessThan(1000L)));
	}
}