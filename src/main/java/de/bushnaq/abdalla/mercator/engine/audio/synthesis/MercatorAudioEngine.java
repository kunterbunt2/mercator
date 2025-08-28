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

package de.bushnaq.abdalla.mercator.engine.audio.synthesis;


import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OggPlayerFactory;
import de.bushnaq.abdalla.engine.audio.radio.TTSPlayerFactory;

public class MercatorAudioEngine extends AudioEngine {

    public MercatorAudioEngine() {
        super(4410, 44100, 16);
        add(new MercatorSynthesizerFactory());
        add(new OggPlayerFactory());
        add(new TTSPlayerFactory());
    }

    public MercatorAudioEngine(final int samples) {
        super(samples, 44100, 16);
        add(new MercatorSynthesizerFactory());
        add(new OggPlayerFactory());
        add(new TTSPlayerFactory());
    }

}
