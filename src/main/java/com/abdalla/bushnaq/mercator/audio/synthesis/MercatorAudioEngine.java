package com.abdalla.bushnaq.mercator.audio.synthesis;

public class MercatorAudioEngine extends AudioEngine {

	public MercatorAudioEngine() {
		super(4410, 44100, 16);
		add(new MercatorSynthesizerFactory());
		add(new Mp3PlayerFactory());
	}

	public MercatorAudioEngine(final int samples) {
		super(samples, 44100, 16);
		add(new MercatorSynthesizerFactory());
		add(new Mp3PlayerFactory());
	}

}
