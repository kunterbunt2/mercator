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

package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.engine.audio.*;
import de.bushnaq.abdalla.mercator.engine.ai.LLMTTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static de.bushnaq.abdalla.engine.audio.RadioTTS.SHIP_TAG;
import static de.bushnaq.abdalla.engine.audio.RadioTTS.STATION_TAG;

public class TraderCommunicationPartner implements CommunicationPartner {
    private final AudioEngine audioEngine;
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    List<RadioMessage> radioMessages = new ArrayList<>();
    private final Trader trader;
    TTSPlayer ttsPlayer;

    public TraderCommunicationPartner(AudioEngine audioEngine, Trader trader) throws OpenAlException {
        this.trader      = trader;
        this.audioEngine = audioEngine;
        ttsPlayer        = audioEngine.createAudioProducer(TTSPlayer.class);
        ttsPlayer.setGain(1f);
    }

    @Override
    public String getName() {
        return trader.getName();
    }

    private void handleRadioMessage() {
        for (RadioMessage rm : radioMessages) {
            if (trader.currentTime - rm.time > CommunicationPartner.RADIO_ANSWER_DELAY) {

            }
        }
    }

    @Override
    public boolean isSelected() {
        return trader.selected;
    }

    @Override
    public void radio(RadioMessage message) {
        radioMessages.add(message);
//        say(message.message);
    }

    public void requestDocking() {
        if (audioEngine.radioTTS != null && trader.destinationPlanet.isSelected()) {
            String       string = RadioMessage.createMessage(audioEngine.radioTTS.resolveString(LLMTTS.REQUEST_DOCKING), SHIP_TAG, getName(), STATION_TAG, trader.destinationPlanet.getName());
            RadioMessage rm     = new RadioMessage(trader.currentTime, this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_DOCK, string);
            say(rm);
            trader.destinationPlanet.communicationPartner.radio(rm);// send to partner

        }
    }

    public void requestUndocking() {
        if (audioEngine.radioTTS != null && trader.sourcePlanet.isSelected()) {
            String       string = RadioMessage.createMessage(audioEngine.radioTTS.resolveString(LLMTTS.REQUEST_UNDOCKING), SHIP_TAG, getName(), STATION_TAG, trader.sourcePlanet.getName());
            RadioMessage rm     = new RadioMessage(trader.currentTime, this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_UNDOCK, string);
            say(rm);
            trader.sourcePlanet.communicationPartner.radio(rm);// send to partner

        }
    }

    public void say(RadioMessage msg) {
        if (isSelected()) {
            ttsPlayer.speak(msg);
            logger.info(msg.message);
        }
    }

    /**
     * selecting this partner should enable its ttsPlayer
     */
    @Override
    public void select() {
        ttsPlayer.setOptIn(true);
    }

    @Override
    public void unselect() {
        ttsPlayer.setOptIn(false);
    }

}
