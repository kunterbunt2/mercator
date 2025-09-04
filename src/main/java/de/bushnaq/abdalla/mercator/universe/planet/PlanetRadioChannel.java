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

import de.bushnaq.abdalla.engine.IGameEngine;
import de.bushnaq.abdalla.engine.ai.PromptTags;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.audio.radio.RadioChannel;
import de.bushnaq.abdalla.engine.audio.radio.RadioMessage;
import de.bushnaq.abdalla.engine.audio.radio.RadioWave;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.engine.event.IEventManager;
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import de.bushnaq.abdalla.mercator.engine.audio.radio.RadioMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlanetRadioChannel implements RadioChannel {
    private final AudioEngine        audioEngine;
    private final IGameEngine        gameEngine;
    private final Logger             logger            = LoggerFactory.getLogger(this.getClass());
    private final Planet             planet;
    private final List<RadioMessage> radioMessageQueue = new ArrayList<>();

    public PlanetRadioChannel(IGameEngine gameEngine, Planet planet) throws OpenAlException {
        this.gameEngine  = gameEngine;
        this.audioEngine = gameEngine.getAudioEngine();
        this.planet      = planet;
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
    public IEventManager getEventManager() {
        return planet.eventManager;
    }

    @Override
    public int getId() {
        return planet.getId();
    }

    @Override
    public String getName() {
        return planet.getName();
    }

    /**
     * Handle incoming RadioMessage or the message queue when a trader leaves the dock.
     *
     * @param rm
     */
    void handleRadioMessage(RadioMessage rm) {
        if (planet.inDock != null) {
            if (rm != null) {
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, String.format("Queuing request from '%s' because dock is occupied by '%s'.", rm.getFrom().getName(), planet.inDock.getName()));
                radioMessageQueue.add(rm);
            }
            return;
        }
        if (rm == null) {
            //our dock just got free
            if (!radioMessageQueue.isEmpty()) {
                rm = radioMessageQueue.removeFirst();
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, String.format("Handling queued request from '%s' because dock got freed.", rm.getFrom().getName()));
            }
        }
        if (rm != null) {
            PromptTags tags = new MerkatorPromptTags(planet, rm.getFrom());
            switch (RadioMessageId.valueOf(rm.getMessageId())) {
                case REQUEST_DOCKING -> {
                    planet.occupyDock(rm.getFrom());//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, rm, rm.getFrom(), RadioMessageId.APPROVE_DOCKING.name(), tags));
                }
                case REQUEST_UNDOCKING -> {
                    // we got an undock request
                    planet.occupyDock(rm.getFrom());//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, rm, rm.getFrom(), RadioMessageId.APPROVE_UNDOCKING.name(), tags));
                }
                case REQUEST_TRANSITION -> {
                    planet.occupyDock(rm.getFrom());//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioMessage(!planet.isSelected(), this, rm, rm.getFrom(), RadioMessageId.APPROVE_TRANSITION.name(), tags));
                }
            }
        }
    }

    @Override
    public boolean isSelected() {
        return planet.selected;
    }

    @Override
    public void notifyFinishedTalking(RadioMessage rm) {
        if (!rm.isSilent())
            logger.info("{} received notifyFinishedTalking messageId: {}, from: {}, to: {} ", getName(), rm.getMessageId(), rm.getFrom().getName(), rm.getTo().getName());
        handleRadioMessage(rm);
    }

    @Override
    public void notifyStartedTalking(RadioWave rw) {
        RadioMessage rm = rw.radioMessage();
        if (!rm.isSilent()) {
            String postText = rm.getTags().removeAllPostTags(rm.getMessages().get(rw.index()));
            addSubtitle(planet.getName() + ": " + postText);
            planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, postText);
        }
    }

    @Override
    public void processRadioMessage(RadioMessage rm) {
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
        String message = audioEngine.radio.generateLlmAnswer(rm.getMessageId(), rm.getOriginalRequest().getAggregatedMessages(), rm.getTags(), rm.isSilent());
        rm.addMessage(message);
        planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, message);
        rm.setTime(planet.currentTime);
//        if (!rm.isSilent()) {
//            addSubtitle(string, rm.getTags());
//            planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, rm.getTags().removeAllPostTags(string));
//        }
//        RadioMessage rm = new RadioMessage(planet.currentTime, rm.getFrom(), rm.getTo(), rm.getMessageId(), rm.getTags().replaceAllPostTags(string), rm.isSilent(), rm.getTags());
        gameEngine.getRadio().radio(rm);// send to partner
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
