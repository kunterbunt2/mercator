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

package de.bushnaq.abdalla.mercator.engine.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.chronos.*;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo2 {
    public        String[]     files = {"01-morning.ogg", "02-methodica.ogg", "05-massive.ogg", "06-abyss.ogg"};
    private final GameEngine3D gameEngine;
    public        int          index = 0;
    private final LaunchMode   launchMode;
    public        long         startTime;//start time of demo

    public Demo2(GameEngine3D gameEngine, LaunchMode launchMode) throws OpenAlException {
        this.gameEngine = gameEngine;
        this.launchMode = launchMode;
        if (launchMode == LaunchMode.demo2) {
            startDemoMode();
        }
    }

    private void export(final String fileName, final List<TextData> Strings) throws IOException {
        final FileWriter  fileWriter  = new FileWriter(fileName);
        final PrintWriter printWriter = new PrintWriter(fileWriter);
        for (final TextData demoString : Strings) {
            printWriter.println(demoString.text);
        }
        printWriter.close();
    }

    private void playNext() throws OpenAlException {
        index++;
        index = index % 5;
        gameEngine.oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/" + files[index]));
        gameEngine.oggPlayer.setGain(.05f);
        gameEngine.oggPlayer.play();
    }

    private void renderAmbientMusic() throws OpenAlException {
        if (!gameEngine.oggPlayer.isPlaying()) {
            playNext();
        }
    }

    public void renderDemo(float deltaTime) throws IOException, OpenAlException {
    }

    private void startAmbientMusic() throws OpenAlException {
        gameEngine.oggPlayer = gameEngine.audioEngine.createAudioProducer(OggPlayer.class, "main-ambient");
        playNext();
    }

    public void startDemoMode() throws OpenAlException {
        gameEngine.setShowAudioSources(false);
        gameEngine.setShowCameraInfo(false);
        gameEngine.setShowDemo2Info(false);
        gameEngine.setShowDepthOfFieldInfo(false);
        gameEngine.setShowFps(false);
        gameEngine.setShowInfo(false);
        gameEngine.setShowTime(false);
        gameEngine.setShowUniverseTime(false);

        Map<Integer, List<Planet>> planetNeighbors = new HashMap<>();
        gameEngine.renderEngine.getFadeEffect().setEnabled(true);
        gameEngine.renderEngine.setAlwaysDay(false);
//        Planet planet = gameEngine.universe.planetList.findByName(Debug.getFilterPlanet());
        Planet planet = gameEngine.universe.planetList.findBusyCenterPlanet();

        //fade out
        gameEngine.getRenderEngine().getFadeEffect().setIntensity(0f);
        gameEngine.getRenderEngine().getFadeEffect().setBackgroundColor(Color.BLACK);
        float angle           = 0;
        float durationSeconds = 15;
        gameEngine.getRenderEngine().getScheduledEffectEngine().add((Task) new MercatorPositionCamera<>(gameEngine, 2, planet.name));
        gameEngine.getRenderEngine().getScheduledEffectEngine().add((Task) new RotateCamera<>(gameEngine, angle));
        gameEngine.getRenderEngine().getScheduledEffectEngine().add((Task) new Pause<>(gameEngine, durationSeconds));
        gameEngine.getRenderEngine().getScheduledEffectEngine().add((Task) new FadeIn<>(gameEngine, Color.BLACK, 5f));

//        startAmbientMusic();
//        index     = 0;
//        textY     = 0;
        startTime = System.currentTimeMillis();
    }

}
