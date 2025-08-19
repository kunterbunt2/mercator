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

import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.LOWERING;

public class TraderCommunicationPartner implements CommunicationPartner {
    private final AudioEngine audioEngine;
    private final IGameEngine gameEngine;
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    //    List<RadioMessage> radioMessages = new ArrayList<>();
    private final Trader      trader;

    public TraderCommunicationPartner(IGameEngine gameEngine, Trader trader) throws OpenAlException {
        this.gameEngine  = gameEngine;
        this.trader      = trader;
        this.audioEngine = gameEngine.getAudioEngine();
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

    @Override
    public int getId() {
        return trader.getId();
    }

    @Override
    public String getName() {
        return trader.getName();
    }


    private void handleRadioMessages(RadioMessage rm) {
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
    public void handleRadioRequest(RadioRequest rr) {
        String string = RadioMessage.createMessage(audioEngine.radio.resolveString(rr.getMessageId(), rr.getTags(), rr.isSilent()), rr.getTags());
        if (!rr.isSilent())
            addSubtitle(string, rr.getTags());
        RadioMessage rm = new RadioMessage(trader.currentTime, rr.getFrom(), rr.getTo(), rr.getRadioMessageId(), rr.getTags().replaceAllPostTags(string), rr.isSilent());
        gameEngine.getRadio().radio(rm);// send to partner
    }

    @Override
    public boolean isSelected() {
        return trader.selected;
    }

    @Override
    public void notifyFinishedTalking(RadioMessage rm) {
//        System.out.println("TraderCommunicationPartner.notifyFinishedTalking");
        handleRadioMessages(rm);
    }

    @Override
    public void radio(RadioMessage message) {
//        radioMessages.add(message);
//        say(message.message);
    }

    public void requestDocking() {
        PromptTags tags = new MerkatorPromptTags(trader, trader.destinationPlanet);
        audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!trader.destinationPlanet.isSelected(), this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_DOCK, LLMTTS.REQUEST_DOCKING, tags));
//        boolean    silent = !trader.destinationPlanet.isSelected();
//        String     string = RadioMessage.createMessage(audioEngine.radio.resolveString(LLMTTS.REQUEST_DOCKING, tags, silent), tags);
//        if (!silent)
//            addSubtitle(string, tags);
//        RadioMessage rm = new RadioMessage(trader.currentTime, this, trader.destinationPlanet.communicationPartner, RadioMessageId.REQUEST_TO_DOCK, tags.replaceAllPostTags(string), silent);
//        gameEngine.getRadio().radio(rm);// send to partner
    }

    public void requestUndocking() {
        PromptTags tags = new MerkatorPromptTags(trader, trader.sourcePlanet);
        audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!trader.sourcePlanet.isSelected(), this, trader.sourcePlanet.communicationPartner, RadioMessageId.REQUEST_TO_UNDOCK, LLMTTS.REQUEST_UNDOCKING, tags));
//        String string = RadioMessage.createMessage(audioEngine.radio.resolveString(LLMTTS.REQUEST_UNDOCKING, tags, isSilent()), tags);
//        if (!isSilent())
//            addSubtitle(string, tags);
//        RadioMessage rm = new RadioMessage(trader.currentTime, this, trader.sourcePlanet.communicationPartner, RadioMessageId.REQUEST_TO_UNDOCK, tags.replaceAllPostTags(string), isSilent());
//        gameEngine.getRadio().radio(rm);// send to partner
    }

    /**
     * selecting this partner should enable its ttsPlayer
     */
    @Override
    public void select() {
//        ttsPlayer.setOptIn(true);
    }

    @Override
    public void unselect() {
//        ttsPlayer.setOptIn(false);
    }

}
