/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bushnaq.abdalla.mercator.universe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

/**
 *
 * @author bushnaq
 */
public class UniversePerformance {
	private static final int MAX_CREATE_TIME = 1000;
	private static final int MAX_MILLINIUM_TIME = 1000;
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 5;

	public static void main(final String[] args) throws Exception {
		final UniversePerformance testObject = new UniversePerformance();
		testObject.advanceInTime();
	}

	@Test
	public void advanceInTime() throws Exception {
		System.out.println("-----------------------------------------------------------------------");
		final long time = System.currentTimeMillis();
		final Universe universe = new Universe("U-fast", GraphicsDimentions.D2, EventLevel.none, null);
		universe.timeDelta = 100L;
		universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 100000L * TimeUnit.TICKS_PER_DAY);
		System.out.printf("%d planets\n", universe.planetList.size());
		System.out.printf("%d traders\n", universe.traderList.size());
		final long duration = System.currentTimeMillis() - time;
		assertTrue(duration < MAX_MILLINIUM_TIME, "Advancing time is taking too long");
	}

	@Test
	public void advanceSlowInTime() throws Exception {
		System.in.read();
		System.out.println("-----------------------------------------------------------------------");
		final long time = System.currentTimeMillis();
		final Universe universe = new Universe("U-slow", GraphicsDimentions.D2, EventLevel.none, null);
		universe.timeDelta = 10L;
		universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 100000L * TimeUnit.TICKS_PER_DAY);
		System.out.printf("%d planets\n", universe.planetList.size());
		System.out.printf("%d traders\n", universe.traderList.size());
		final long duration = System.currentTimeMillis() - time;
		assertTrue(duration < MAX_MILLINIUM_TIME, "Advancing time is taking too long");
	}

	@Test
	public void create() throws Exception {
		// ---create 10 different universes with 10 different random seeds
		for (int universeIndex = 0; universeIndex < 10; universeIndex++) {
			for (int i = 1; i < 11; i++) {
				System.out.println("-----------------------------------------------------------------------");
				final long time = System.currentTimeMillis();
				System.out.printf("Universe seed = %d\n", universeIndex);
				final Universe universe = new Universe("U-create", GraphicsDimentions.D2, EventLevel.none, null);
				universe.create(universeIndex, i, 100L * TimeUnit.TICKS_PER_DAY);
				System.out.printf("%d planets, %d traders\n", universe.planetList.size(), universe.traderList.size());
				final long duration = System.currentTimeMillis() - time;
				assertTrue(duration < MAX_CREATE_TIME, String.format("Creading universe [seed %d and size %d] is taking too long (> 1000ms) %dms", universeIndex, i, duration));
			}
		}
	}
}
