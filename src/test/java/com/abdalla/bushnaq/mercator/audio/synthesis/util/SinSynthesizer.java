package com.abdalla.bushnaq.mercator.audio.synthesis.util;

import com.abdalla.bushnaq.mercator.audio.synthesis.OpenAlException;
import com.abdalla.bushnaq.mercator.audio.synthesis.SinOscillator;
import com.abdalla.bushnaq.mercator.audio.synthesis.Synthesizer;

public class SinSynthesizer extends Synthesizer {
	SinOscillator sin1;

	public SinSynthesizer() throws OpenAlException {
		super(44100);
		sin1 = new SinOscillator();
		add(sin1);
	}

	@Override
	public void adaptToVelocity(float speed) throws OpenAlException {
		speed = Math.min(50, speed);
		speed = Math.max(5, speed);

		final float speedFactor = speed / 5;
		final float frequency = 440f * speedFactor;
		sin1.setOscillator(frequency);
		setGain(2.0f);
		//		add(new SawGen(29f * speedFactor));
		//		add(new Lfo1(2f * speedFactor, 1.0f));
		//		add(new SinGen(frequency * 2));
		//		final float bassGain = 1 - (speed - 10) / 40;
		//		setGain(5f + bassGain * 10);
		//		setFilterGain(bassGain, (speed - 10) / 40);
		//				createFilter();
		//		createBassBoost();
		//		setBassBoostGain(frequency, bassGain * 48);
		//				createBassBoost();
		//		begin();

	}

}
