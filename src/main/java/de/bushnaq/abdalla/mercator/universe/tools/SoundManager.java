/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.universe.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SoundManager {
    static int MAX_PLAYLIST_CHANNELS  = 64;
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
