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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TraderCommunicationPartner implements CommunicationPartner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Trader trader;
    List<RadioMessage> radioMessages = new ArrayList<>();
    TTSPlayer          ttsPlayer;
    private AudioEngine audioEngine;

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

    @Override
    public boolean isSelected() {
        return trader.selected;
    }

    @Override
    public void radio(RadioMessage message) {
        radioMessages.add(message);
//        say(message.message);
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

    private void handleRadioMessage() {
        for (RadioMessage rm : radioMessages) {
            if (trader.currentTime - rm.time > CommunicationPartner.RADIO_ANSWER_DELAY) {

            }
        }
    }

    public void informControlTower() {
        if (audioEngine.radioTTS != null) {
            String string = String.format(audioEngine.radioTTS.resolveString(RadioTTS.REQUESTING_APPROVAL_TO_DOCK_01), getName(), trader.destinationPlanet.getName());
            say(string);
            RadioMessage rm = new RadioMessage(trader.currentTime, this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_DOCK, string);
            trader.destinationPlanet.communicationPartner.radio(rm);// send to partner

        }
    }

    public void say(String msg) {
        if (isSelected()) {
            ttsPlayer.speak(msg);
            logger.info(msg);
        }
    }


}
