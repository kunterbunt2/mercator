package com.abdalla.bushnaq.mercator.universe.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SoundManager {
	static int MAX_PLAYLIST_CHANNELS = 64;
	static int MAX_SPEAKLIST_CHANNELS = 1;
	Collection<WavePlayer> playList = Collections.synchronizedCollection(new ArrayList<WavePlayer>());
	//	Collection<SpeechSyntheziser> speakList = Collections.synchronizedCollection(new ArrayList<SpeechSyntheziser>());

	public boolean isBusy() {
		return playList.size() != 0;
	}

	public void play(final String string) {
		if (playList.size() < MAX_PLAYLIST_CHANNELS) {
			final WavePlayer makeSound = new WavePlayer(this, "ping.wav");
			playList.add(makeSound);
			makeSound.start();
		}
	}

	public void remove(final WavePlayer makeSound) {
		playList.remove(makeSound);
	}

	// public void remove( SpeechSyntheziser makeSound )
	// {
	// speakList.remove( makeSound );
	// }
	public void speak(final String text) {
		//		if (speakList.size() < MAX_SPEAKLIST_CHANNELS) {
		//			SpeechSyntheziser speechSyntheziser = new SpeechSyntheziser(this, text);
		//			speakList.add(speechSyntheziser);
		//			speechSyntheziser.start();
		//		}
	}
}
