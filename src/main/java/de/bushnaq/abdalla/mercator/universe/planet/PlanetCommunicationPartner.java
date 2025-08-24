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
import de.bushnaq.abdalla.engine.audio.*;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.engine.event.IEventManager;
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import de.bushnaq.abdalla.mercator.engine.ai.RadioMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlanetCommunicationPartner implements CommunicationPartner {
    private final AudioEngine        audioEngine;
    private final IGameEngine        gameEngine;
    private final Logger             logger            = LoggerFactory.getLogger(this.getClass());
    private final Planet             planet;
    private final List<RadioMessage> radioMessageQueue = new ArrayList<>();

    public PlanetCommunicationPartner(IGameEngine gameEngine, Planet planet) throws OpenAlException {
        this.gameEngine  = gameEngine;
        this.audioEngine = gameEngine.getAudioEngine();
        this.planet      = planet;
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
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, String.format("Queuing request from '%s' because dock is occupied by '%s'.", rm.from.getName(), planet.inDock.getName()));
                radioMessageQueue.add(rm);
            }
            return;
        }
        if (rm == null) {
            //dock just got free
            if (!radioMessageQueue.isEmpty()) {
                rm = radioMessageQueue.removeFirst();
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, String.format("Handling queued request from '%s' because dock got freed.", rm.from.getName()));
            }
        }
        if (rm != null) {
            boolean silent = !isSelected();
//        if (!silent)
//            logger.info("handleRadioMessage " + planet.getName() + " selected=" + isSelected() + " id=" + rm.id.name());
            PromptTags tags = new MerkatorPromptTags(planet, rm.from);
            switch (RadioMessageId.valueOf(rm.radioMessageId)) {
                case REQUEST_DOCKING -> {
                    planet.occupyDock(rm.from);//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, rm.from, RadioMessageId.APPROVE_DOCKING.name(), tags));
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
//                String string = RadioMessage.createMessage(audioEngine.radio.resolveString(MercatorSystemPrompts.APPROVE_DOCKING, tags, silent), tags);
//                if (!silent)
//                    addSubtitle(string, tags);
//                RadioMessage replyMessage = new RadioMessage(planet.currentTime, this, rm.from, RadioMessageId.APPROVE_TO_DOCK, tags.replaceAllPostTags(string), silent);
////                        logger.info("replyMessage" + replyMessage.message);
//                gameEngine.getRadio().radio(replyMessage);// send to partner
                }
                case REQUEST_UNDOCKING -> {
                    planet.occupyDock(rm.from);//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, rm.from, RadioMessageId.APPROVE_UNDOCKING.name(), tags));
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
//                String string = RadioMessage.createMessage(audioEngine.radio.resolveString(MercatorSystemPrompts.APPROVE_UNDOCKING, tags, silent), tags);
//                if (!silent)
//                    addSubtitle(string, tags);
//                RadioMessage replyMessage = new RadioMessage(planet.currentTime, this, rm.from, RadioMessageId.APPROVE_TO_UNDOCK, tags.replaceAllPostTags(string), silent);
////                        logger.info("replyMessage" + replyMessage.message);
//                gameEngine.getRadio().radio(replyMessage);// send to partner
                }
                case REQUEST_TRANSITION -> {
                    planet.occupyDock(rm.from);//occupy dock before informing the trader
                    audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, rm.from, RadioMessageId.APPROVE_TRANSITION.name(), tags));
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
//        System.out.println("PlanetCommunicationPartner.notifyFinishedTalking");
        handleRadioMessage(rm);
    }

//    @Override
//    public boolean isSilent() {
//        return !planet.isSelected();
//    }

    @Override
    public void notifyStartedTalking(RadioMessage message) {
    }

    @Override
    public void processRadioMessage(RadioRequest rr) {
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
        String string = RadioMessage.createMessage(audioEngine.radio.resolveString(rr.getMessageId(), rr.getTags(), rr.isSilent()), rr.getTags());
        if (!rr.isSilent()) {
            addSubtitle(string, rr.getTags());
            planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, rr.getTags().removeAllPostTags(string));
        }
        RadioMessage rm = new RadioMessage(planet.currentTime, rr.getFrom(), rr.getTo(), rr.getMessageId(), rr.getTags().replaceAllPostTags(string), rr.isSilent());
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
