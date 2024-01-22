package de.bushnaq.abdalla.mercator.universe;

import static org.junit.jupiter.api.Assertions.fail;

import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 *         36,00 export has stopped for one year
 */
public class TraderDeathPerformanceTest {
	private static final int UNIVERSE_SIZE = 10;

	@Test
	public void testTrading() throws Exception {
		final Universe[] universeList = new Universe[1];
		// ---create 10 different universes with 10 different random seeds
		for (int i = 0; i < universeList.length; i++) {
			final Universe universe = new Universe("U-" + i, GraphicsDimentions.D2, EventLevel.none, null);
			universeList[i] = universe;
			universe.timeDelta = 20L;
			universe.useFixedDelta = true;
			universe.create(i, UNIVERSE_SIZE, 0);
			// universe.traderCreditBuffer = ( Trader.TRADER_START_CREDITS / (
			// universeList.length + 1 ) ) * i;
			universe.traderCreditBuffer = 50;
		}
		Universe bestUniverse = null;
		long bestTime = 0;
		final int limit = 0/* universeList[0].traderList.size() / 10 */;
		for (float i = 1; i < 15f; i++) {
			for (final Universe universe : universeList) {
				universe.advanceInTime(100 * TimeUnit.TICKS_PER_DAY);
				if (universe.deadTraderList.size() <= limit) {
					bestTime = universe.currentTime;
					bestUniverse = universe;
				}
			}
		}
		//		bestUniverse.deadTraderList.get(0).eventManager.print(System.out);
		for (final Universe universe : universeList) {
			System.out.printf("%s\n", TimeUnit.toString(universe.deadTraderList.get(0).lastTimeAdvancement));
			// System.out.printf( "%s\n", TimeUnit.toString( universe.deadTraderList.get(
			// universe.deadTraderList.size() - 1 ).lastTimeAdvancement ) );
		}
		fail(String.format("Best result was credit buffer %f in %s", bestUniverse.traderCreditBuffer, TimeUnit.toString(bestTime)));
	}
}
