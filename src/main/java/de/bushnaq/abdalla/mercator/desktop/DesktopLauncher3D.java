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

package de.bushnaq.abdalla.mercator.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import de.bushnaq.abdalla.engine.util.logger.Logger;
import de.bushnaq.abdalla.engine.util.logger.LoggerFactory;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.MavenPropertiesProvider;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

/**
 * @author kunterbunt
 */
public class DesktopLauncher3D implements ApplicationListener {
    private static final int UNIVERSE_GENERATION_RANDOM_SEED = 1;
    private static final int UNIVERSE_SIZE                   = 10;
    //    private final Universe universe;
    DesktopContextFactory contextFactory = new DesktopContextFactory();
    LaunchMode            launchMode     = LaunchMode.normal;
    boolean               useOGL3        = true;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * called by GameEngine to create Lwjgl3Application
     *
     * @throws Exception
     */
    public DesktopLauncher3D() throws Exception {
//        this.universe = universe;
//        if (launchMode != LaunchMode.demo && launchMode != LaunchMode.development) {
//            final MercatorFrame frame = new MercatorFrame(universe);
//            frame.setVisible(true);
//        }
//		final Lwjgl3ApplicationConfiguration config = createConfig();
//		new Lwjgl3Application(screen, config);
//		System.out.println("DesktopLauncher constructed");
//		System.exit(0);
    }

    public static void main(final String[] args) throws Exception {
        DesktopLauncher3D desktopLauncher3D = new DesktopLauncher3D();
        desktopLauncher3D.start();
    }

    @Override
    public void create() {
        contextFactory.create();
        Gdx.app.exit();// exit the gdx environment that we created just to read the options
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    private Lwjgl3ApplicationConfiguration createConfig(Context context) {
        int                            foregroundFPS = context.getForegroundFPSProperty();
        Lwjgl3ApplicationConfiguration config;
        config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(context.getVsyncProperty());
        config.setForegroundFPS(foregroundFPS);
        config.setResizable(true);
//		config.useOpenGL3(true, 3, 2);
        config.setOpenGLEmulation(GLEmulation.GL30, 3, 2);

//		if (Context.getOeratingSystemType() == OperatingSystem.osx)
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

        config.setBackBufferConfig(8, 8, 8, 8, 24, 0, context.getMSAASamples());
        config.setTitle("Pluvia");
        config.setAutoIconify(true);
        final Monitor[]   monitors       = Lwjgl3ApplicationConfiguration.getMonitors();
        int               monitor        = context.getMonitorProperty();
        final DisplayMode primaryMode    = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[monitor]);
        boolean           fullScreenMode = context.getFullscreenModeProperty();
        if (fullScreenMode)
            config.setFullscreenMode(primaryMode);
//		config.setWindowPosition(0, 0);
//		config.setWindowedMode(primaryMode.width, primaryMode.height);
//		config.setMaximized(true);
        return config;
    }

    private void loop() throws Exception, InterruptedException {
        boolean restart = false;
        do {
            if (restart)
                logger.info(String.format("Restarting pluvia v%s.", MavenPropertiesProvider.getProperty("module.version")));
            {
                // initialize a gdx environment, so that we can access files
                Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
                new Lwjgl3Application(this, config);
            }

            final GraphicsDimentions gd       = GraphicsDimentions.D3;
            final Universe           universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
            universe.create(UNIVERSE_GENERATION_RANDOM_SEED, UNIVERSE_SIZE, 10L * TimeUnit.TICKS_PER_DAY);

            final GameEngine3D                   gameEngine = new GameEngine3D(contextFactory, universe, launchMode);
            final Lwjgl3ApplicationConfiguration config     = createConfig(contextFactory.getContext());
            try {
                contextFactory.getContext().restart = false;
                new Lwjgl3Application(gameEngine, config);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
            contextFactory.getContext().dispose();
            restart = contextFactory.getContext().restart;
            universe.dispose();
        } while (restart);
    }

    private void start() throws InterruptedException, Exception {
        logger.info("------------------------------------------------------------------------------");
        logger.info(String.format("Starting pluvia v%s.", MavenPropertiesProvider.getProperty("project.version")));
        logger.info("------------------------------------------------------------------------------");
        String property = System.getProperty("user.home");
        logger.info("user.home = " + property);
        loop();
        logger.info("------------------------------------------------------------------------------");
        logger.info(String.format("Shutting down pluvia v%s.", MavenPropertiesProvider.getProperty("project.version")));
        logger.info("------------------------------------------------------------------------------");
    }

}
