package com.abdalla.bushnaq.mercator.audio.synthesis;

public class MercatorSynthesizerFactory extends AbstractSynthesizerFactory<MercatorSynthesizer> {

	@Override
	public Class<MercatorSynthesizer> handles() {
		return MercatorSynthesizer.class;
	}

	@Override
	public MercatorSynthesizer uncacheSynth() throws OpenAlException {
		return new MercatorSynthesizer();
	}

}
