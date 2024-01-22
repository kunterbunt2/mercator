package de.bushnaq.abdalla.mercator.audio.synthesis;

public interface Oscilator {

	public void dispose();

	public double gen(long i);

	public double getFrequency();

	/**
	 * Vibrato control of the Oscillator
	 * @param lfoFreq in Hz
	 * @param lfoDepth in cent
	 */
	void setLfo(float lfoFreq, float lfoDepth);

	/**
	 * set the base oscillator frequency
	 * @param oscFreq
	 */
	void setOscillator(float oscFreq);

	public void setSampleRate(int samplerate);

	void setFrequency(float frequency);
}