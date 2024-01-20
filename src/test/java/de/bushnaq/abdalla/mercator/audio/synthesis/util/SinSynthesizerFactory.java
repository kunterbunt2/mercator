package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import de.bushnaq.abdalla.mercator.audio.synthesis.AbstractSynthesizerFactory;
import de.bushnaq.abdalla.mercator.audio.synthesis.OpenAlException;

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
