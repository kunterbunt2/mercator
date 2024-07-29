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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    private final GameEngine3D            gameEngine;
    //    public        int                     index           = 0;
    private final LaunchMode              launchMode;
    private final Logger                  logger          = LoggerFactory.getLogger(this.getClass());
    public        MercatorRandomGenerator randomGenerator = new MercatorRandomGenerator(1, null);
    public        long                    startTime;//start time of demo
    private final List<ScheduledTask>     tasks           = new ArrayList<>();
    private final List<DemoString>        text            = new ArrayList<>();
    private final float                   textX           = 100;
    private       float                   textY           = 0;

    public Demo(GameEngine3D gameEngine, LaunchMode launchMode) throws OpenAlException {
        this.gameEngine = gameEngine;
        this.launchMode = launchMode;
        if (launchMode == LaunchMode.demo) {
            startDemoMode();
        }
    }

    private void executeTasks(float deltaTime) throws OpenAlException {
        if (tasks.isEmpty()) startDemoMode();
        if (tasks.get(0).execute(deltaTime)) {
            tasks.remove(0);
        }
    }

    private void export(final String fileName, final List<DemoString> Strings) throws IOException {
        final FileWriter  fileWriter  = new FileWriter(fileName);
        final PrintWriter printWriter = new PrintWriter(fileWriter);
        for (final DemoString demoString : Strings) {
            printWriter.println(demoString.text);
        }
        printWriter.close();
    }

    public void renderDemo(float deltaTime) throws IOException, OpenAlException {

        if (launchMode == LaunchMode.demo) {
            executeTasks(deltaTime);
            final float lineHeightFactor = 2f;
            if (text.isEmpty()) {
                text.add(new DemoString("Mercator", gameEngine.getAtlasManager().bold128Font));
                text.add(new DemoString("A computer game implementation of a closed economical simulation.", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString(String.format("The current world is generated proceduraly and includes %d cities, %d factories, %d traders and %d sims.", gameEngine.universe.planetList.size(), gameEngine.universe.planetList.size() * 2, gameEngine.universe.traderList.size(), gameEngine.universe.simList.size()), gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString(String.format("There exist %d static models, %d dynamic models, %d audio producers.", gameEngine.renderEngine.staticGameObjects.size, gameEngine.renderEngine.dynamicGameObjects.size, gameEngine.audioEngine.getNumberOfAudioProducers()), gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("The amount of wealth in the system, including products and money is constant at all times. ", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("Factories pay wages to sims to produce goods that are sold on a free market.", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("Some sims are traders that buy products in one city and sell them with profit in another city.", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("All sims have needs that they need to fulfill else they die.", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("All sims have cravings that they need to fulfill to keep their satisfaction level up.", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("All sounds are generated by a openal based audio render engine for libgdx supporting procedurally generated audio using HRTF.", gameEngine.getAtlasManager().demoMidFont));
                //				demoText.add(new DemoString("Demo song is 'abyss' by Abdalla Bushnaq.", render2DMaster.atlasManager.demoMidFont));
                text.add(new DemoString("Work in progress...", gameEngine.getAtlasManager().demoMidFont));
                text.add(new DemoString("Developed using libgdx and gdx-gltf open source frameworks.", gameEngine.getAtlasManager().demoMidFont));
                export("target/demo.txt", text);
            }

            Color demoTextColor;
//            if (renderEngine.isNight())
            demoTextColor = new Color(1f, 1f, 1f, 0.2f);
//            else demoTextColor = new Color(0f, 0f, 0f, 0.6f);
            float deltaY = 0;

            final GlyphLayout layout = new GlyphLayout();
            layout.setText(text.get(0).font, text.get(0).text);
            final float width = layout.width;// contains the width of the current set text
            //		final float height = layout.height; // contains the height of the current set text

            final float topMargin    = 50f;
            final float bottomMargin = 200f;
            for (final DemoString ds : text) {
                ds.font.setColor(demoTextColor);
                final float y = textY - deltaY;
                if (y < bottomMargin) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = 0f;
                } else if (y < bottomMargin * 2) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = demoTextColor.a * (y - bottomMargin) / bottomMargin;
                } else if (y > gameEngine.renderEngine.renderEngine2D.height - topMargin) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = 0;
                } else if (y > gameEngine.renderEngine.renderEngine2D.height - topMargin * 2) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = demoTextColor.a * (1 - (y - gameEngine.renderEngine.renderEngine2D.height + topMargin * 2) / topMargin);
                } else {
                    ds.font.setColor(demoTextColor);
                }
                final GlyphLayout lastLayout = ds.font.draw(gameEngine.renderEngine.renderEngine2D.batch, ds.text, textX, y, width, Align.left, true);
                deltaY += lastLayout.height * lineHeightFactor;
            }
            textY += 100 * deltaTime;
            if (textY - deltaY > gameEngine.renderEngine.renderEngine2D.height * lineHeightFactor) textY = 0;
        }
    }

    public void startDemoMode() throws OpenAlException {
        int                        secondsDelta      = 18;
        int                        firstSecondsDelta = 19;
        Map<Integer, List<Planet>> planetNeighbors   = new HashMap<>();

        for (Planet planet : gameEngine.universe.planetList) {
            int          gateCount  = planet.pathList.size();
            List<Planet> planetList = planetNeighbors.get(gateCount);
            if (planetList == null)
                planetList = new ArrayList<>();
            planetList.add(planet);
            planetNeighbors.put(gateCount, planetList);
        }
        boolean firstPlanet = true;
        for (Integer neighbors : planetNeighbors.keySet()) {
            if (neighbors > 3) {
                List<Planet> planetList = planetNeighbors.get(neighbors);
                for (Planet planet : planetList) {
                    int zoomIndex;
                    if (randomGenerator.nextInt(2) == 1)
                        zoomIndex = 2;
                    else
                        zoomIndex = randomGenerator.nextInt(7);
                    float angle = (float) randomGenerator.nextInt(360);
                    if (firstPlanet) {
                        firstPlanet = false;
                        tasks.add(new PositionCamera(gameEngine, zoomIndex, planet.name));
                        tasks.add(new RotateCamera(gameEngine, angle));
                        tasks.add(new PauseTask(gameEngine, firstSecondsDelta));
                    } else {
                        tasks.add(new PositionCamera(gameEngine, zoomIndex, planet.name));
                        tasks.add(new RotateCamera(gameEngine, angle));
                        tasks.add(new FadeInTask(gameEngine));
                        tasks.add(new PauseTask(gameEngine, secondsDelta));
//                        if (randomGenerator.nextInt(10) == 0)
//                        {
//                            for (int zoomIndex = 0; zoomIndex < gameEngine.getCamController().zoomFactors.length; zoomIndex++) {
//                                tasks.add(new ZoomCamera(gameEngine, zoomIndex, planet.name));
//                                tasks.add(new RotateingCamera(gameEngine, 1, 0));
//                            }
//                        } else
                        {
                        }
                    }
                    tasks.add(new FadeOutTask(gameEngine));
                }
            }
        }

//        renderEngine.getDepthOfFieldEffect().setEnabled(true);
//        updateDepthOfFieldFocusDistance();
        gameEngine.renderEngine.setAlwaysDay(false);
        gameEngine.oggPlayer = gameEngine.audioEngine.createAudioProducer(OggPlayer.class);
        gameEngine.oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/06-abyss(m).ogg"));
        gameEngine.oggPlayer.setGain(.1f);
        gameEngine.oggPlayer.play();
        AudioEngine.checkAlError("Failed to set listener orientation with error #");
//        index     = 0;
        textY     = 0;
        startTime = System.currentTimeMillis();
    }

}
