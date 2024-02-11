package de.bushnaq.abdalla.mercator.desktop;

import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

public class Launcher3DDeveMode {
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 3;
	private static final int UNIVERSE_SIZE = 3;

	public static void main(final String[] args) throws Exception {
		final GraphicsDimentions gd = GraphicsDimentions.D3;
		final Universe universe = new Universe("U-0", gd, EventLevel.info, Sim.class);
		universe.create(UNIVERSE_GENERATION_RANDOM_SEED, UNIVERSE_SIZE, 10L * TimeUnit.TICKS_PER_DAY);
		final Sim trader = universe.traderList.seekTraderByName("T-49");
		universe.eventManager.setObjectFilter(trader);
		universe.eventManager.setEnablePrintEvent(true);
		universe.setSelected(trader, true);
//		final Screen3D screen = new Screen3D(universe, LaunchMode.development);
//		new DesktopLauncher(universe);
	}

}
