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
import de.bushnaq.abdalla.mercator.engine.ai.LLMTTS;
import de.bushnaq.abdalla.mercator.engine.ai.MerkatorPromptTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanetCommunicationPartner implements CommunicationPartner {
    private final AudioEngine audioEngine;
    private final IGameEngine gameEngine;
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private final Planet      planet;
//    private final List<RadioMessage> radioMessages = new ArrayList<>();

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
    public int getId() {
        return planet.getId();
    }

    @Override
    public String getName() {
        return planet.getName();
    }

    void handleRadioMessage(RadioMessage rm) {
        boolean silent = !isSelected();
//        if (!silent)
//            logger.info("handleRadioMessage " + planet.getName() + " selected=" + isSelected() + " id=" + rm.id.name());
        PromptTags tags = new MerkatorPromptTags(planet, rm.from);
        switch (rm.id) {
            case REQUEST_TO_DOCK -> {
                audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, rm.from, RadioMessageId.APPROVE_TO_DOCK, LLMTTS.APPROVE_DOCKING, tags));
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
//                String string = RadioMessage.createMessage(audioEngine.radio.resolveString(LLMTTS.APPROVE_DOCKING, tags, silent), tags);
//                if (!silent)
//                    addSubtitle(string, tags);
//                RadioMessage replyMessage = new RadioMessage(planet.currentTime, this, rm.from, RadioMessageId.APPROVE_TO_DOCK, tags.replaceAllPostTags(string), silent);
////                        logger.info("replyMessage" + replyMessage.message);
//                gameEngine.getRadio().radio(replyMessage);// send to partner
            }
            case REQUEST_TO_UNDOCK -> {
                audioEngine.radio.queueRadioMessageGeneration(new RadioRequest(!planet.isSelected(), this, rm.from, RadioMessageId.APPROVE_TO_UNDOCK, LLMTTS.APPROVE_UNDOCKING, tags));
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
//                String string = RadioMessage.createMessage(audioEngine.radio.resolveString(LLMTTS.APPROVE_UNDOCKING, tags, silent), tags);
//                if (!silent)
//                    addSubtitle(string, tags);
//                RadioMessage replyMessage = new RadioMessage(planet.currentTime, this, rm.from, RadioMessageId.APPROVE_TO_UNDOCK, tags.replaceAllPostTags(string), silent);
////                        logger.info("replyMessage" + replyMessage.message);
//                gameEngine.getRadio().radio(replyMessage);// send to partner
            }
        }
    }

    @Override
    public void handleRadioRequest(RadioRequest rr) {
//                            if (Debug.isFilterPlanet(planet.getName()))
//                                logger.info(String.format("answering %s message", rm.id.name()));
        String string = RadioMessage.createMessage(audioEngine.radio.resolveString(rr.getMessageId(), rr.getTags(), rr.isSilent()), rr.getTags());
        if (!rr.isSilent())
            addSubtitle(string, rr.getTags());
        RadioMessage rm = new RadioMessage(planet.currentTime, rr.getFrom(), rr.getTo(), rr.getRadioMessageId(), rr.getTags().replaceAllPostTags(string), rr.isSilent());
        gameEngine.getRadio().radio(rm);// send to partner
    }

    @Override
    public boolean isSelected() {
        return planet.selected;
    }

//    @Override
//    public boolean isSilent() {
//        return !planet.isSelected();
//    }

    @Override
    public void notifyFinishedTalking(RadioMessage rm) {
//        System.out.println("PlanetCommunicationPartner.notifyFinishedTalking");
        handleRadioMessage(rm);
    }

    @Override
    public void radio(RadioMessage message) {
//        radioMessages.add(message);
//        say(message);
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
