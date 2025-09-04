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
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.audio.radio.RadioChannel;
import de.bushnaq.abdalla.engine.audio.radio.RadioMessage;
import de.bushnaq.abdalla.engine.audio.radio.RadioWave;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import de.bushnaq.abdalla.mercator.engine.audio.radio.RadioMessageId;
import de.bushnaq.abdalla.mercator.universe.event.EventManager;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.LOWERING;

public class TraderRadioChannel implements RadioChannel {
    private final AudioEngine audioEngine;
    private final IGameEngine gameEngine;
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private final Trader      trader;

    public TraderRadioChannel(IGameEngine gameEngine, Trader trader) throws OpenAlException {
        this.gameEngine  = gameEngine;
        this.trader      = trader;
        this.audioEngine = gameEngine.getAudioEngine();
    }

    /**
     * Adds a subtitle to the game engine's subtitle list.
     * This method removes any post tags in the string.
     *
     * @param string The subtitle text to be added.
     */
    private void addSubtitle(String string) {
        gameEngine.getSubtitles().add(string);
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
        switch (RadioMessageId.valueOf(rm.getMessageId())) {
            case APPROVE_DOCKING -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("docking request was approved by  '%s'", rm.getFrom().getName()));
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
            }
            case APPROVE_UNDOCKING -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("undocking request was approved by  '%s'", rm.getFrom().getName()));
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC);
                trader.planet.dockingDoors.setDockingDoorStatus(LOWERING);
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("departing '%s' to reach '%s' %s", trader.planet.getName(), trader.navigator.destinationPlanet.city.getName(), trader.navigator.WaypointPortsAsString()));
            }
            case APPROVE_TRANSITION -> {
                trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("transition request was approved by  '%s'", rm.getFrom().getName()));
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
//        System.out.println("TraderRadioChannel.notifyFinishedTalking");
        handleRadioMessages(rm);
    }

    @Override
    public void notifyStartedTalking(RadioWave rw) {
        RadioMessage rm = rw.radioMessage();
        if (!rm.isSilent()) {
            String postText = rm.getTags().removeAllPostTags(rm.getMessages().get(rw.index()));
            addSubtitle(trader.getName() + ": " + postText);
            trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, postText);
        }
    }

    @Override
    public void processRadioMessage(RadioMessage rm) {
        long time = System.currentTimeMillis();
        if (rm.getOriginalRequest() == null)
            rm.addMessage(audioEngine.radio.generateLlmAnswer(rm.getMessageId(), null, rm.getTags(), rm.isSilent()));
        else
            rm.addMessage(audioEngine.radio.generateLlmAnswer(rm.getMessageId(), rm.getOriginalRequest().getAggregatedMessages(), rm.getTags(), rm.isSilent()));
        rm.setTime(trader.currentTime);
//        if (!rm.isSilent()) {
//            addSubtitle(string, rm.getTags());
//            trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, rm.getTags().removeAllPostTags(string));
//        }
//        RadioMessage rm = new RadioMessage(trader.currentTime, rm.getFrom(), rm.getTo(), rm.getMessageId(), rm.getTags().replaceAllPostTags(string), rm.isSilent(), rm.getTags());
        gameEngine.getRadio().radio(rm);// speak
    }

    public void requestDocking(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting docking approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
//        if (Debug.isFilterTrader(trader.getName()))
//            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_DOCKING.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, null, planet.communicationPartner, RadioMessageId.REQUEST_DOCKING.name(), tags));
    }

    public void requestTransition(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting transition approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
//        if (Debug.isFilterTrader(trader.getName()))
//            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_TRANSITION.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, null, planet.communicationPartner, RadioMessageId.REQUEST_TRANSITION.name(), tags));
    }

    public void requestUndocking(Planet planet) {
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("requesting undocking approval to '%s'", planet.getName()));
        PromptTags tags = new MerkatorPromptTags(trader, planet);
//        if (Debug.isFilterTrader(trader.getName()))
//            System.out.printf("%s requesting %s to %s\n", trader.getName(), RadioMessageId.REQUEST_UNDOCKING.name(), planet.getName());
        audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, null, planet.communicationPartner, RadioMessageId.REQUEST_UNDOCKING.name(), tags));
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
