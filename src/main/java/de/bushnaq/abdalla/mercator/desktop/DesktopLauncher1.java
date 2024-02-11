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

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.bushnaq.abdalla.mercator.gui.frame.MercatorFrame;
import de.bushnaq.abdalla.mercator.renderer.GameEngine2D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;

public class DesktopLauncher1 {
    boolean useOGL3 = true;

    public DesktopLauncher1(final Universe universe, final GameEngine2D screen, final boolean demoMode) throws Exception {
        if (!demoMode) {
            final MercatorFrame frame = new MercatorFrame(universe);
            frame.setVisible(true);
        }
        final Lwjgl3ApplicationConfiguration config = createConfig();
        new Lwjgl3Application(screen, config);
        System.out.println("DesktopLauncher constructed");
        System.exit(0);
    }

    public DesktopLauncher1(final Universe universe, final GameEngine3D screen) throws Exception {
        if (screen.launchMode != LaunchMode.demo && screen.launchMode != LaunchMode.development) {
            final MercatorFrame frame = new MercatorFrame(universe);
            frame.setVisible(true);
        }
        final Lwjgl3ApplicationConfiguration config = createConfig();
        new Lwjgl3Application(screen, config);
        System.out.println("DesktopLauncher constructed");
        System.exit(0);
    }

    private Lwjgl3ApplicationConfiguration createConfig() {
        Lwjgl3ApplicationConfiguration config;
        config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setForegroundFPS(0);
        config.setResizable(false);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2); // use GL 3.0 (emulated by OpenGL 3.2)
//		config.useOpenGL3(true, 3, 2);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        config.setTitle("Mercator");
        final Monitor[]   monitors    = Lwjgl3ApplicationConfiguration.getMonitors();
        final DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[1]);
        config.setFullscreenMode(primaryMode);
        return config;
    }

}
