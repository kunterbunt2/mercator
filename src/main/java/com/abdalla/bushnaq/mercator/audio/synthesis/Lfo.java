package com.abdalla.bushnaq.mercator.audio.synthesis;

public interface Lfo extends Cloneable {
	public void dispose();

	public double gen(long i);

	float getFactor();

	void setFrequency(float freq, float factor);

	void setSampleRate(int samplerate);
}
