package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import de.bushnaq.abdalla.mercator.audio.synthesis.AbstractSynthesizerFactory;
import de.bushnaq.abdalla.mercator.audio.synthesis.OpenAlException;

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
