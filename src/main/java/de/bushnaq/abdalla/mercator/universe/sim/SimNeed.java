package de.bushnaq.abdalla.mercator.universe.sim;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SimNeed implements Cloneable, Comparable<SimNeed> {
	public long consumeEvery; // ---Needs to consume before this time to keep satisfied
	public float creditLimit; // sim has to own at least this much of credits to think of buying this good
	public long dieIfNotConsumedWithin;
	public long lastConsumed; // ---Last time the sim has consumed this good
	public int totalConsumed;
	public GoodType type;

	public SimNeed(final GoodType food, final long consumeEvery, final long dieIfNotConsumedWithin, final float creditLimit) {
		this.type = food;
		this.consumeEvery = consumeEvery;
		this.dieIfNotConsumedWithin = dieIfNotConsumedWithin;
		this.creditLimit = creditLimit;
	}

	@Override
	public SimNeed clone() throws CloneNotSupportedException {
		final SimNeed simNeeds = (SimNeed) super.clone();
		return simNeeds;
	}

	@Override
	public int compareTo(final SimNeed compareObject) {
		if (lastConsumed < compareObject.lastConsumed)
			return -1;
		else if (lastConsumed == compareObject.lastConsumed)
			return 0;
		else
			return 1;
	}

	public void consume(final long currentTime) {
		lastConsumed = currentTime;
		totalConsumed++;
	}
}
