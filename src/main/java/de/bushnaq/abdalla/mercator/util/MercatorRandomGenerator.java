package de.bushnaq.abdalla.mercator.util;

import java.util.Random;

import de.bushnaq.abdalla.mercator.universe.event.EventManager;

public class MercatorRandomGenerator {
	EventManager eventManager;
	public int index;
	private final Random random;

	public MercatorRandomGenerator(final int seed, final EventManager eventManager) {
		random = new Random(seed);
		this.eventManager = eventManager;
	}

	public float nextFloat() {
		return random.nextFloat();
	}

	public int nextInt(final float bound) {
		return random.nextInt((int) bound);
	}

	public int nextInt(final int bound) {
		return random.nextInt(bound);
	}

	public int nextInt(final long currentTime, final Object who, final int bound) {
		if (bound == 0)
			return 0;
		final int nextInt = random.nextInt(bound);
		//		eventManager.add(currentTime, who, String.format("generated %d random number %d out of [0 - [%d", index++, nextInt, bound));
		return nextInt;
	}
}
