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

package de.bushnaq.abdalla.mercator.universe;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RadioTTS {
    public static final String   REQUESTING_APPROVAL_TO_DOCK_01 = "REQUESTING_APPROVAL_TO_DOCK_01";
    private final       Voice    helloVoice;
    private final       Logger   logger                         = LoggerFactory.getLogger(this.getClass());
    private final       Universe universe;
    Set<String> audioFiles;
    private List<String> radioMessages = new ArrayList<>();
    private String[]     tokens        = {"name", "pause"};

    public RadioTTS(Universe universe) {
        this.universe = universe;


        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        listAllVoices();
        VoiceManager voiceManager = VoiceManager.getInstance();
        helloVoice = voiceManager.getVoice("kevin16");
        helloVoice.allocate();
//        renderAllStrings();
//        loadAudio();
        logger.info("end");
    }

    public static void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice[]      voices       = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
        }
    }

    public void dispose() {
        helloVoice.deallocate();
    }

    private void handleTokenEnd(String token, String value) {
        logger.info(value);
        switch (token) {
            case "name":
                break;
            case "pause":
                break;
        }
    }

    private void handleTokenEnd(String value) {
        renderTTSString(value);

    }

    private boolean handleTokenStart(String token) {
        switch (token) {
            case "pause":
                return true;
        }
        return false;
    }

    private String insertPause(String name) {
        return name.replace("-", ". ");
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    private void loadAudio() {
        audioFiles = listFilesUsingJavaIO("audio");
    }

    private void renderAllStrings() {
        renderRadio();
        renderNames();
    }

    private void renderNames() {
        for (Trader trader : universe.traderList) {
            renderTTSString(insertPause(trader.getName()));
        }
        for (Planet planet : universe.planetList) {
            renderTTSString(insertPause(planet.getName()));
        }
    }

    private void renderRadio() {
        Properties radioProperties = new Properties();
        try {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/radio.properties");
            radioProperties.load(resourceAsStream);
            for (Object key : radioProperties.stringPropertyNames()) {
                radioMessages.add((String) radioProperties.get(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String msg : radioMessages) {
            logger.info(msg);
            int start = -1;
            int end   = -1;
            int head  = 0;
            int i     = 0;
            for (i = 0; i < msg.length(); i++) {
                String substring = msg.substring(i);
                for (String token : tokens) {
                    String st = String.format("{%s}", token);
                    if (substring.startsWith(st)) {
                        if (head != i) {
                            String value = msg.substring(head, i);
                            handleTokenEnd(value);
                        }
                        if (handleTokenStart(token)) {
                            head = i + st.length();
                        }
                        start = i + st.length();
                    }
                    String et = String.format("{/%s}", token);
                    if (substring.startsWith(et)) {
                        end = i;
                        String value = msg.substring(start, end);
                        handleTokenEnd(token, value);
                        head = i + et.length();
                    }
                }
            }
            if (head != msg.length()) {
                String value = msg.substring(head, i);
                handleTokenEnd(value);
            }
            logger.info("end");
        }
    }

    private void renderTTSString(String value) {
        SingleFileAudioPlayer audioPlayer = new SingleFileAudioPlayer("audio/" + value, AudioFileFormat.Type.WAVE);
        helloVoice.setAudioPlayer(audioPlayer);
        helloVoice.speak(value);
        audioPlayer.close();
    }

    public void speak(String message) {
        helloVoice.speak(message);
    }
}
