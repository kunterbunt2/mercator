package com.abdalla.bushnaq.mercator.audio.synthesis;

import java.util.List;

import com.scottlogic.util.UnsortedList;

public abstract class AbstractSynthesizerFactory<T> implements SynthesizerFactory<T> {
	private final List<T> synthsCache = new UnsortedList<>();

	@Override
	public void cacheSynth(final T synth) {
		synthsCache.add(synth);
	}

	@Override
	public T createSynth() throws OpenAlException {
		if (synthsCache.size() != 0) {
			final T synth = synthsCache.remove(0);
			return synth;
		} else {
			final T synth = uncacheSynth();
			return synth;
		}
	}

}
