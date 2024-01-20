package com.abdalla.bushnaq.mercator.audio.synthesis.util;

import com.abdalla.bushnaq.mercator.audio.synthesis.AbstractSynthesizerFactory;
import com.abdalla.bushnaq.mercator.audio.synthesis.OpenAlException;

public class SawSynthesizerFactory extends AbstractSynthesizerFactory<SawSynthesizer> {

	@Override
	public Class<SawSynthesizer> handles() {
		return SawSynthesizer.class;
	}

	@Override
	public SawSynthesizer uncacheSynth() throws OpenAlException {
		return new SawSynthesizer();
	}

}
