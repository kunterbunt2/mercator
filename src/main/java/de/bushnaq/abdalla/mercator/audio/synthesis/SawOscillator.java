package de.bushnaq.abdalla.mercator.audio.synthesis;

public class SawOscillator implements Oscilator {
	private float lfoDepth = 0;//cent
	private float lfoFreq = 1;//Hz
	private float oscFreq = 440f;//Hz
	private int samplerate;//1/s

	public SawOscillator() {
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

//	@Override
//    protected void finalize() {
		//		System.out.println(String.format("min=%f, max=%f", min, max));
//	}

	@Override
	public double gen(final long i) {
		final float oscFreqDetune = (float) (lfoDepth / 20 * Math.sin((2 * Math.PI * lfoFreq / samplerate) * i));
		final float oscFreqWithVibrato = (float) (oscFreq * Math.pow(2, oscFreqDetune / 1200));

		final double fraction = samplerate / oscFreqWithVibrato;
		final double value = -1 + 2 * (i % fraction) / fraction;
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