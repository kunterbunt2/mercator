/* ---------------------------------------------------------------------------
 * BEGIN_PROJECT_HEADER
 *
 *       RRRR  RRR    IIIII    CCCCC      OOOO    HHH  HHH
 *       RRRR  RRRR   IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR  RRRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR RRRR    IIIII  CCCC       OOO  OOO  HHHHHHHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR   RRRR  IIIII    CCCCC      OOOO    HHH  HHH
 *
 *       Copyright 2005 by Ricoh Europe B.V.
 *
 *       This material contains, and is part of a computer software program
 *       which is, proprietary and confidential information owned by Ricoh
 *       Europe B.V.
 *       The program, including this material, may not be duplicated, disclosed
 *       or reproduced in whole or in part for any purpose without the express
 *       written authorization of Ricoh Europe B.V.
 *       All authorized reproductions must be marked with this legend.
 *
 *       Department : European Development and Support Center
 *       Group      : Printing & Fax Solution Group
 *       Author(s)  : bushnaq
 *       Created    : 13.02.2005
 *
 *       Project    : com.abdalla.bushnaq.mercator
 *       Product Id : <Product Key Index>
 *       Component  : <Project Component Name>
 *       Compiler   : Java/Eclipse
 *
 * END_PROJECT_HEADER
 * -------------------------------------------------------------------------*/
package com.abdalla.bushnaq.mercator.universe.sim;

import com.abdalla.bushnaq.mercator.universe.good.GoodType;

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
