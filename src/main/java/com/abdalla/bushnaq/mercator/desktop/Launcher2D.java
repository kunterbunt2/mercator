package com.abdalla.bushnaq.mercator.desktop;

import com.abdalla.bushnaq.mercator.renderer.Screen2D;
import com.abdalla.bushnaq.mercator.universe.Universe;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.util.EventLevel;
import com.abdalla.bushnaq.mercator.util.TimeUnit;

public class Launcher2D {
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 1;

	public static void main(final String[] args) throws Exception {
		final GraphicsDimentions gd = GraphicsDimentions.D2;
		final Universe universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
		universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 10L * TimeUnit.TICKS_PER_DAY);
		final Screen2D screen = new Screen2D(universe);
		final DesktopLauncher launcher = new DesktopLauncher(universe, screen, false);
		synchronized (launcher) {
			launcher.wait();
		}
		System.out.println("DesktopLauncher exiting");
	}

}
