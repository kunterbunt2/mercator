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

package de.bushnaq.abdalla.mercator.universe.planet;

import de.bushnaq.abdalla.engine.audio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PlanetCommunicationPartner implements CommunicationPartner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Planet planet;
    List<RadioMessage> radioMessages = new ArrayList<>();
    private AudioEngine audioEngine;
    private TTSPlayer   ttsSynth;

    public PlanetCommunicationPartner(Planet planet) {
        this.planet = planet;
    }

    @Override
    public String getName() {
        return planet.getName();
    }

    @Override
    public boolean isSelected() {
        return false;
    }


    @Override
    public void radio(RadioMessage message) {
        radioMessages.add(message);
        say(message.message);
    }

    void handleRadioMessage() {
        boolean changed;
        do {
            changed = false;
            ListIterator<RadioMessage> crunchifyIterator = radioMessages.listIterator();
            // hasNext(): Returns true if this list iterator has more elements when traversing the list in the forward direction.
            // (In other words, returns true if next would return an element rather than throwing an exception.)
            while (crunchifyIterator.hasNext()) {
                RadioMessage rm = crunchifyIterator.next();

                if (planet.currentTime - rm.time > RADIO_ANSWER_DELAY) {
                    switch (rm.id) {
                        case REQUEST_TO_DOCK -> {
                            String string = String.format(RadioTTS.REQUEST_TO_DOCK_APPROVED_01, getName(), rm.from.getName());
                            rm.from.radio(new RadioMessage(planet.currentTime, this, rm.from, RadioMessageId.APPROVE_TO_DOCK, string));
//                            radioMessages.remove(rm);
                            crunchifyIterator.remove();
                            changed = true;
                            break;
                        }
                    }
                }

            }
//            for (RadioMessage rm : radioMessages) {
//            }
        }
        while (changed);
    }

    public void say(String msg) {
        if (isSelected()) {
            ttsSynth.speak(msg);
            logger.info(msg);
        }
    }


}
