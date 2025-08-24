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
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import de.bushnaq.abdalla.mercator.engine.ai.RadioMessageId;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.EventManager;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.Debug;
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
    public EventManager getEventManager() {
        return trader.eventManager;
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
        switch (RadioMessageId.valueOf(rm.radioMessageId)) {
            case APPROVE_DOCKING -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("docking request was approved by  '%s'", rm.from.getName()));
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
            }
            case APPROVE_UNDOCKING -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("undocking request was approved by  '%s'", rm.from.getName()));
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC);
                trader.planet.dockingDoors.setDockingDoorStatus(LOWERING);
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("departing '%s' to reach '%s' %s", trader.planet.getName(), trader.navigator.destinationPlanet.city.getName(), trader.navigator.WaypointPortsAsString()));
            }
            case APPROVE_TRANSITION -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("transition request was approved by  '%s'", rm.from.getName()));
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
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
        handleRadioMessages(rm);
    }

    @Override
    public void notifyStartedTalking(RadioMessage message) {

    }

    @Override
    public void processRadioMessage(RadioRequest rr) {
        String string = RadioMessage.createMessage(audioEngine.radio.resolveString(rr.getMessageId(), rr.getTags(), rr.isSilent()), rr.getTags());
        if (!rr.isSilent()) {
            addSubtitle(string, rr.getTags());
            trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, rr.getTags().removeAllPostTags(string));
        }
        RadioMessage rm = new RadioMessage(trader.currentTime, rr.getFrom(), rr.getTo(), rr.getMessageId(), rr.getTags().replaceAllPostTags(string), rr.isSilent());
        gameEngine.getRadio().radio(rm);// speak
    }

    public void requestDocking(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting docking approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
        if (Debug.isFilterTrader(trader.getName()))
            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_DOCKING.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, planet.communicationPartner, RadioMessageId.REQUEST_DOCKING.name(), tags));
    }

    public void requestTransition(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting transition approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
        if (Debug.isFilterTrader(trader.getName()))
            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_TRANSITION.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, planet.communicationPartner, RadioMessageId.REQUEST_TRANSITION.name(), tags));
    }

    public void requestUndocking(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting undocking approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
        if (Debug.isFilterTrader(trader.getName()))
            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_UNDOCKING.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, planet.communicationPartner, RadioMessageId.REQUEST_UNDOCKING.name(), tags));
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
