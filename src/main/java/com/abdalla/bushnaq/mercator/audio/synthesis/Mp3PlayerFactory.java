package com.abdalla.bushnaq.mercator.audio.synthesis;

public class Mp3PlayerFactory extends AbstractSynthesizerFactory<Mp3Player> {

	@Override
	public Class<Mp3Player> handles() {
		return Mp3Player.class;
	}

	@Override
	public Mp3Player uncacheSynth() throws OpenAlException {
		return new Mp3Player();
	}

}
