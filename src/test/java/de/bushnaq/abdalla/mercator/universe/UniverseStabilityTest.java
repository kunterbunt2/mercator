package de.bushnaq.abdalla.mercator.universe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.util.Event;
import de.bushnaq.abdalla.mercator.util.EventLevel;
import de.bushnaq.abdalla.mercator.util.EventManager;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 *         36,00 export has stopped for one year
 */
public class UniverseStabilityTest {
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 5;
	private static final int UNIVERSE_SIZE = 10;
	EventManager eventManager2 = new EventManager(EventLevel.all, null);
	MercatorRandomGenerator g2 = new MercatorRandomGenerator(UNIVERSE_GENERATION_RANDOM_SEED, eventManager2);

	@Test
	public void testTimeCompression() throws Exception {
		final boolean enableEventLog = false;
		final Universe[] universeList = new Universe[2];
		// ---create 2 different universes with different speed
		for (int i = 0; i < universeList.length; i++) {
			universeList[i] = new Universe("U-" + i, GraphicsDimentions.D2, EventLevel.none, null);
			universeList[i].create(UNIVERSE_GENERATION_RANDOM_SEED, UNIVERSE_SIZE, 0L);
		}
		universeList[0].timeDelta = 10L;
		universeList[1].timeDelta = 100L;
		for (int i = 0; i < 10; i++) {
			for (int universeIndex = 0; universeIndex < universeList.length; universeIndex++) {
				final Universe universe = universeList[universeIndex];
				universe.advanceInTime(100 * TimeUnit.TICKS_PER_DAY);
			}
			if (enableEventLog) {
				final List<Event> eventList0 = universeList[0].eventManager.eventList;
				final List<Event> eventList1 = universeList[1].eventManager.eventList;
				for (int e = 0; e < Math.min(eventList0.size(), eventList1.size()); e++) {
					final Event event0 = eventList0.get(e);
					final Event event1 = eventList1.get(e);
					if (event0.when != event1.when || !event0.what.equals(event1.what)) {
						System.out.printf("%s %s %s %s\n", universeList[0].getName(), TimeUnit.toString(event0.when), event0.getWhosName(), event0.what);
						for (final StackTraceElement trace : event0.stackTrace)
							System.out.println("\t" + trace);
						System.out.printf("%s %s %s %s\n", universeList[1].getName(), TimeUnit.toString(event1.when), event1.getWhosName(), event1.what);
						for (final StackTraceElement trace : event1.stackTrace)
							System.out.println("\t" + trace);
						System.out.printf("-\n");
					}
				}
				// compareToStandard( eventList0, eventList1 );
				// ---Print out the rest if the two lists are nto of the same size
				for (int e = Math.min(eventList0.size(), eventList1.size()); e < Math.max(eventList0.size(), eventList1.size()); e++) {
					Event event;
					Universe universe;
					if (eventList0.size() > eventList1.size()) {
						event = eventList0.get(e);
						universe = universeList[0];
					} else {
						event = eventList1.get(e);
						universe = universeList[1];
					}
					System.out.printf("%s %s %s %s\n", universe.getName(), TimeUnit.toString(event.when), event.getWhosName(), event.what);
					for (final StackTraceElement trace : event.stackTrace)
						System.out.println("\t" + trace);
					// compareToStandard( eventList0, eventList1 );
					System.out.printf("-\n");
				}
				assertEquals(eventList0.size(), eventList1.size(), String.format("in year %.2f in universe [size %d] event size is different", universeList[0].currentTime / 100f, UNIVERSE_SIZE));
				for (final Universe universe : universeList) {
					universe.eventManager.eventList.clear();
				}
			}
		}
	}
	// private void compareToStandard( List< Event > eventList0, List< Event >
	// eventList1 )
	// {
	// for ( int e = 0; e < eventList0.size(); e++ )
	// {
	// Event event0 = eventList0.get( e );
	// Event event1 = eventList1.get( e );
	// if ( event0.who.getClass() == event1.who.getClass() )
	// {
	// if ( RandomBound.class.isInstance( event0.who ) )
	// {
	// RandomBound rb0 = (RandomBound)event0.who;
	// RandomBound rb1 = (RandomBound)event1.who;
	// int result = g2.nextInt( 0, this, rb0.bound );
	// if ( result != rb0.nextInt || result != rb1.nextInt )
	// {
	// System.out.println( "index " + g2.index + " original " + result + " g0 " +
	// rb0.bound + ", " + rb0.nextInt + " g1 " + rb1.bound + ", " + rb1.nextInt );
	// break;
	// }
	// }
	// }
	// else
	// {
	// System.out.println( "EventManager mismatch " + e );
	// break;
	// }
	// }
	// }
}
