package com.abdalla.bushnaq.mercator.audio.synthesis.util;

import com.abdalla.bushnaq.mercator.audio.synthesis.OpenAlException;
import com.abdalla.bushnaq.mercator.audio.synthesis.SawOscillator;
import com.abdalla.bushnaq.mercator.audio.synthesis.Synthesizer;

public class SawSynthesizer extends Synthesizer {
	SawOscillator saw1;

	public SawSynthesizer() throws OpenAlException {
		super(44100);
		saw1 = new SawOscillator();
		add(saw1);
	}

	@Override
	public void adaptToVelocity(float speed) throws OpenAlException {
		speed = Math.min(50, speed);
		speed = Math.max(5, speed);

		final float speedFactor = speed / 5;
		final float frequency = 440f * speedFactor;
		saw1.setOscillator(frequency);
		setFilter(false);
	}

}
