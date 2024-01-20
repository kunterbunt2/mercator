package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import de.bushnaq.abdalla.mercator.audio.synthesis.AudioEngine;

public class SinAudioEngine extends AudioEngine {
	public static final int bits = 16;
	//	public static final int channels = 1;
	public static final int samplerate = 44100;
	public static final int samples = 44100;

	public SinAudioEngine() {
		super(samples, samplerate, bits);
		add(new SinSynthesizerFactory());
	}

}
