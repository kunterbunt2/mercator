package com.abdalla.bushnaq.mercator.audio.synthesis.util;

import com.abdalla.bushnaq.mercator.audio.synthesis.AbstractSynthesizerFactory;
import com.abdalla.bushnaq.mercator.audio.synthesis.OpenAlException;

public class SinSynthesizerFactory extends AbstractSynthesizerFactory<SinSynthesizer> {

	@Override
	public Class<SinSynthesizer> handles() {
		return SinSynthesizer.class;
	}

	@Override
	public SinSynthesizer uncacheSynth() throws OpenAlException {
		return new SinSynthesizer();
	}

}
