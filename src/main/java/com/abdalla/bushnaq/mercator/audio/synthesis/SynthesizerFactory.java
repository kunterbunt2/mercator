package com.abdalla.bushnaq.mercator.audio.synthesis;

public interface SynthesizerFactory<T> {
	void cacheSynth(T synth);

	T createSynth() throws OpenAlException;

	Class<T> handles();

	T uncacheSynth() throws OpenAlException;
}
