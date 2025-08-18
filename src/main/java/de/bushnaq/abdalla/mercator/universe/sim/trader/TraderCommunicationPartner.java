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

import de.bushnaq.abdalla.engine.IGameEngine;
import de.bushnaq.abdalla.engine.ai.PromptTags;
import de.bushnaq.abdalla.engine.audio.*;
import de.bushnaq.abdalla.mercator.engine.ai.LLMTTS;
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.LOWERING;

public class TraderCommunicationPartner implements CommunicationPartner {
    private final AudioEngine audioEngine;
    private final IGameEngine gameEngine;
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    List<RadioMessage> radioMessages = new ArrayList<>();
    private final Trader trader;
    TTSPlayer ttsPlayer;

    public TraderCommunicationPartner(IGameEngine gameEngine, Trader trader) throws OpenAlException {
        this.gameEngine  = gameEngine;
        this.trader      = trader;
        this.audioEngine = gameEngine.getAudioEngine();
        ttsPlayer        = audioEngine.createAudioProducer(TTSPlayer.class);
        ttsPlayer.setGain(1f);
    }

    /**
     * Adds a subtitle to the game engine's subtitle list.
     * This method removes any post tags in the string.
     *
     * @param string The subtitle text to be added.
     * @param tags   The PromptTags containing post tags to be replaced in the string.
     */
    private void addSubtitle(String string, PromptTags tags) {
        gameEngine.getSubtitles().add(tags.removeAllPostTags(string));
    }

//    private void handleRadioMessage() {
//        for (RadioMessage rm : radioMessages) {
//            if (trader.currentTime - rm.time > CommunicationPartner.RADIO_ANSWER_DELAY) {
//
//            }
//        }
//    }

    @Override
    public int getId() {
        return trader.getId();
    }

    @Override
    public String getName() {
        return trader.getName();
    }

    private void handleRadioReplies(RadioMessage rm) {
        switch (rm.id) {
            case APPROVE_TO_DOCK -> {
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
            }
            case APPROVE_TO_UNDOCK -> {
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC);
                trader.sourcePlanet.dockingDoors.setDockingDoorStatus(LOWERING);
                trader.planet.universe.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("departing %s to reach %s", trader.planet.getName(), trader.destinationPlanet.city.getName()));
            }
        }
    }

    @Override
    public boolean isSelected() {
        return trader.selected;
    }

    @Override
    public void notifyFinishedTalking(RadioMessage rm) {
//        System.out.println("TraderCommunicationPartner.notifyFinishedTalking");
        handleRadioReplies(rm);
    }

    @Override
    public void radio(RadioMessage message) {
//        radioMessages.add(message);
//        say(message.message);
    }

    public void requestDocking() {
        boolean    silent = !trader.destinationPlanet.isSelected();
        PromptTags tags   = new MerkatorPromptTags(trader, trader.destinationPlanet);
        String     string = RadioMessage.createMessage(audioEngine.radioTTS.resolveString(LLMTTS.REQUEST_DOCKING, tags, silent), tags);
        if (!silent)
            addSubtitle(string, tags);
        RadioMessage rm = new RadioMessage(trader.currentTime, this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_DOCK, tags.replaceAllPostTags(string), silent);
//            say(rm);
        trader.destinationPlanet.communicationPartner.radio(rm);// send to partner
    }

    public void requestUndocking() {
        boolean    silent = !trader.sourcePlanet.isSelected();
        PromptTags tags   = new MerkatorPromptTags(trader, trader.sourcePlanet);
        String     string = RadioMessage.createMessage(audioEngine.radioTTS.resolveString(LLMTTS.REQUEST_UNDOCKING, tags, silent), tags);
        if (!silent)
            addSubtitle(string, tags);
        RadioMessage rm = new RadioMessage(trader.currentTime, this, trader.sourcePlanet.communicationPartner, RadioMessageId.REQUEST_TO_UNDOCK, tags.replaceAllPostTags(string), silent);
//            say(rm);
        trader.sourcePlanet.communicationPartner.radio(rm);// send to partner
    }

//    public void say(RadioMessage msg) {
//        if (isSelected()) {
//            ttsPlayer.speak(msg);
//            logger.info(msg.message);
//        }
//    }

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
