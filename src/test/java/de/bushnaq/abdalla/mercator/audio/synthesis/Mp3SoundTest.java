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

package de.bushnaq.abdalla.mercator.audio.synthesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.audio.synthesis.util.LiniarTranslation;
import de.bushnaq.abdalla.mercator.audio.synthesis.util.TranslationUtil;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mp3SoundTest extends TranslationUtil<LiniarTranslation> {
    protected static final int    NUMBER_OF_SOURCES = 0;
    private static final   long   SECONDS_2         = 2000;
    private final          Logger logger            = LoggerFactory.getLogger(this.getClass());
    Vector3   minSpeed = new Vector3(Trader.MIN_ENGINE_SPEED, 0, 0);
    Mp3Player mp3Player;

    @Override
    public void create() {
        super.create(NUMBER_OF_SOURCES);
        createCamera();
        try {
            mp3Player = sceneManager.audioEngine.createAudioProducer(Mp3Player.class);
            mp3Player.setFile(Gdx.files.internal("06-abyss(m).ogg"));
            mp3Player.setGain(5.0f);
            mp3Player.play();
            sceneManager.audioEngine.begin(sceneManager.renderEngine.getCamera());
            final long time1 = System.currentTimeMillis();
            do {
            } while (System.currentTimeMillis() - time1 < SECONDS_2);
            mp3Player.pause();
            mp3Player.renderBuffer();
            mp3Player.writeWav("target/mp3.wav");
            sceneManager.audioEngine.end();
            Gdx.app.exit();

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void createCamera() {
        Gdx.files = new Lwjgl3Files();
        Lwjgl3NativesLoader.load();
        final MovingCamera camera = sceneManager.renderEngine.getCamera();
        camera.position.set(0f, 200f, 200f);
        camera.up.set(0f, 1f, 0f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far  = 8000f;
        camera.update();
    }

    @Test
    public void liniarTranslatingSources() throws Exception {
        runFor = 2000000;
        startLwjgl();
    }

    @Override
    protected void updateTranslation() {
    }
}