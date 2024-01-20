package com.abdalla.bushnaq.mercator.desktop;

import com.abdalla.bushnaq.mercator.renderer.Screen3D;
import com.abdalla.bushnaq.mercator.universe.Universe;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.util.EventLevel;
import com.abdalla.bushnaq.mercator.util.TimeUnit;

public class Launcher3DDemo {
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 4;
	private static final int UNIVERSE_SIZE = 5;

	public static void main(final String[] args) throws Exception {
		final GraphicsDimentions gd = GraphicsDimentions.D3;
		final Universe universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
		universe.create(UNIVERSE_GENERATION_RANDOM_SEED, UNIVERSE_SIZE, 10L * TimeUnit.TICKS_PER_DAY);
		final Screen3D screen = new Screen3D(universe, true);
		new DesktopLauncher(universe, screen);
	}

}
