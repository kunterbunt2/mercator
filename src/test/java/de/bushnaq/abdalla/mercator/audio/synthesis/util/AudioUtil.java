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

package de.bushnaq.abdalla.mercator.audio.synthesis.util;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.desktop.DesktopContextFactory;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AudioUtil implements ApplicationListener, InputProcessor {
    private final Matrix4                 identityMatrix    = new Matrix4();
    private final List<Label>             labels            = new ArrayList<>();
    private final Logger                  logger            = LoggerFactory.getLogger(this.getClass());
    protected     GameEngine3D            gameEngine;
    protected     MercatorRandomGenerator rg                = new MercatorRandomGenerator(1, null);
    protected     boolean                 simulateBassBoost = true;
    protected     Universe                universe;
    DesktopContextFactory contextFactory = new DesktopContextFactory();
    private BitmapFont    font;
    private boolean       hrtfEnabled    = true;
    private Stage         stage;
    private StringBuilder stringBuilder;
    private boolean       takeScreenShot = false;

    @Override
    public void create() {
        try {
            final GraphicsDimentions gd = GraphicsDimentions.D3;
            contextFactory.create();
            universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
            createStage();
            gameEngine = new GameEngine3D(contextFactory, universe, LaunchMode.development);
            gameEngine.create();
            gameEngine.renderEngine.setAlwaysDay(true);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void render() {
        try {
            universe.advanceInTime();
            update();
            gameEngine.renderEngine.render(universe.currentTime, Gdx.graphics.getDeltaTime(), takeScreenShot);
            gameEngine.renderEngine.postProcessRender();

            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            //			Gdx.gl.glEnable(GL20.GL_BLEND);
            gameEngine.renderEngine.batch2D.enableBlending();
            gameEngine.renderEngine.batch2D.begin();
            //			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            gameEngine.renderEngine.batch2D.setProjectionMatrix(gameEngine.renderEngine.getCamera().combined);
            renderText();
            gameEngine.renderEngine.batch2D.end();
            gameEngine.renderEngine.batch2D.setTransformMatrix(identityMatrix);//fix transformMatrix
            //			renderStage();
            takeScreenShot = false;
            gameEngine.audioEngine.begin(gameEngine.renderEngine.getCamera());
            gameEngine.audioEngine.end();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        try {
            gameEngine.dispose();
            font.dispose();
            Gdx.app.exit();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Lwjgl3ApplicationConfiguration createConfig() {
        Lwjgl3ApplicationConfiguration config;
        config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setForegroundFPS(0);
        config.setResizable(false);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2); // use GL 3.0 (emulated by OpenGL 3.2)
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        config.setTitle("Mercator");
        {
            ShaderProgram.prependVertexCode   = "#version 150\n"//
                    + "#define GLSL3\n"//
                    + "#ifdef GLSL3\n"//
                    + "#define attribute in\n"//
                    + "#define varying out\n"//
                    + "#endif\n";//
            ShaderProgram.prependFragmentCode = "#version 150\n"//
                    + "#define GLSL3\n"//
                    + "#ifdef GLSL3\n"//
                    + "#define textureCube texture\n"//
                    + "#define texture2D texture\n"//
                    + "#define varying in\n"//
                    + "#endif\n";//
        }
        final Monitor[]   monitors    = Lwjgl3ApplicationConfiguration.getMonitors();
        final DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[1]);
        config.setFullscreenMode(primaryMode);
        return config;
    }

    private void createStage() throws Exception {
        final int height = 12;
        stage = new Stage();
        font  = new BitmapFont();
        for (int i = 0; i < 8; i++) {
            final Label label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
            label.setPosition(0, i * height);
            stage.addActor(label);
            labels.add(label);
        }
        stringBuilder = new StringBuilder();
    }

    @Override
    public boolean keyDown(final int keycode) {
        switch (keycode) {
            case Input.Keys.Q:
                Gdx.app.exit();
                return true;
            case Input.Keys.P:
                universe.setEnableTime(!universe.isEnableTime());
                return true;
            case Input.Keys.PRINT_SCREEN:
                takeScreenShot = true;
                return true;
            case Input.Keys.NUM_2:
                simulateBassBoost = !simulateBassBoost;
                if (simulateBassBoost)
                    logger.info("bassBoost on");
                else
                    logger.info("bassBoost off");
                return true;
            case Input.Keys.H:
                try {
                    if (hrtfEnabled) {
                        gameEngine.audioEngine.disableHrtf(0);
                        hrtfEnabled = false;
                    } else {
                        gameEngine.audioEngine.enableHrtf(0);
                        hrtfEnabled = true;
                    }
                } catch (final OpenAlException e) {
                    logger.error(e.getMessage(), e);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final float amountX, final float amountY) {
        return false;
    }


    private void renderStage() throws Exception {
        int labelIndex = 0;
        // fps
        {
            stringBuilder.setLength(0);
            stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //audio sources
        {
            stringBuilder.setLength(0);
            stringBuilder.append(" audio sources: ").append(gameEngine.audioEngine.getEnabledAudioSourceCount() + " / " + gameEngine.audioEngine.getDisabledAudioSourceCount());
            labels.get(labelIndex++).setText(stringBuilder);
        }
        stage.draw();
    }

    protected abstract void renderText();

    protected void startLwjgl() {
        final Lwjgl3ApplicationConfiguration config = createConfig();
        new Lwjgl3Application(this, config);
    }

    protected abstract void update() throws Exception;
}
