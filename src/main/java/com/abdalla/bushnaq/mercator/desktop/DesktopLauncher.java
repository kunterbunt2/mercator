package com.abdalla.bushnaq.mercator.desktop;

import com.abdalla.bushnaq.mercator.gui.frame.MercatorFrame;
import com.abdalla.bushnaq.mercator.renderer.Screen2D;
import com.abdalla.bushnaq.mercator.renderer.Screen3D;
import com.abdalla.bushnaq.mercator.universe.Universe;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	boolean useOGL3 = true;

	public DesktopLauncher(final Universe universe, final Screen2D screen, final boolean demoMode) throws Exception {
		final MercatorFrame frame = new MercatorFrame(universe);
		if (!demoMode)
			frame.setVisible(true);
		final Lwjgl3ApplicationConfiguration config = createConfig();
		new Lwjgl3Application(screen, config);
		System.out.println("DesktopLauncher constructed");
		System.exit(0);
	}

	public DesktopLauncher(final Universe universe, final Screen3D screen) throws Exception {
		final MercatorFrame frame = new MercatorFrame(universe);
		if (!screen.isDemoMode())
			frame.setVisible(true);
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
		config.useOpenGL3(true, 3, 2);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
		config.setTitle("Mercator");
		final Monitor[] monitors = Lwjgl3ApplicationConfiguration.getMonitors();
		final DisplayMode primaryMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[1]);
		config.setFullscreenMode(primaryMode);
		return config;
	}

}
