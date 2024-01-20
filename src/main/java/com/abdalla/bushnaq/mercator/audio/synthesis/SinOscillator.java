package com.abdalla.bushnaq.mercator.audio.synthesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinOscillator implements Oscilator {
	private float lfoDepth = 0;//cent
	private float lfoFreq = 1;//Hz
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public float maxFrequency = 0f;
	public float minFrequency = 1000000f;
	private float oscFreq = 440f;
	private int samplerate;

	public SinOscillator() {
	}

	@Override
	public void dispose() {
		logger.info(String.format("minFrequency=%f oscFreq=%f maxFrequency=%f", minFrequency, oscFreq, maxFrequency));
	}

	@Override
	public double gen(final long i) {
		final float oscFreqDetune = (float) (lfoDepth / 10 * Math.sin((2 * Math.PI * lfoFreq / samplerate) * i));
		final float oscFreqWithVibrato = (float) (oscFreq * Math.pow(2, oscFreqDetune / 1200));
		minFrequency = Math.min(minFrequency, oscFreqWithVibrato);
		maxFrequency = Math.max(maxFrequency, oscFreqWithVibrato);

		final double value = Math.sin(((2 * Math.PI * oscFreqWithVibrato) / samplerate) * i);
		return value;
	}

	/**
	 * Vibrato control of the Oscillator
	 * @param lfoFreq in Hz
	 * @param lfoDepth in cent
	 */
	@Override
	public void setLfo(final float lfoFreq, final float lfoDepth) {
		this.lfoFreq = lfoFreq;
		this.lfoDepth = lfoDepth;
	}

	@Override
	public void setOscillator(final float oscFreq) {
		this.oscFreq = oscFreq;
	}

	@Override
	public void setSampleRate(final int samplerate) {
		this.samplerate = samplerate;
	}
}