package de.bushnaq.abdalla.mercator.audio.synthesis;

public class Lfo2 implements Lfo {
	private float factor;
	private float freq = 44f;
	private final float freq1Max = 70f;
	private final float freq1Min = 50f;
	private float incr = 0.00f;
	private float max = Float.MIN_VALUE;
	private float min = Float.MAX_VALUE;
	private int samplerate;

	public Lfo2() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public double gen(final long i) {
		final double fraction = 1.0f * factor - 2.0 * factor * ((freq * i) % samplerate) / samplerate;
		//		prepareNextSample(i);
		final double amplitude = Math.signum(fraction);
		min = (float) Math.min(min, amplitude);
		max = (float) Math.max(max, amplitude);

		return amplitude;
	}

	@Override
	public float getFactor() {
		return factor;
	}

	private void prepareNextSample(final long i) {
		freq += incr;
		if (freq1Min > freq || freq > freq1Max) {
			incr *= -1.0f;
		}
	}

	@Override
	public void setFrequency(final float freq, final float factor) {
		this.freq = freq;
		this.factor = factor;
	}

	@Override
	public void setSampleRate(final int samplerate) {
		this.samplerate = samplerate;
	}

}
