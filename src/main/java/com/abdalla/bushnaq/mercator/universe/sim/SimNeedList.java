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

import java.util.Vector;

import com.abdalla.bushnaq.mercator.universe.good.GoodType;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SimNeedList extends Vector<SimNeed> implements Cloneable {
	private static final float G01_CREDIT_LIMIT = 0;
	private static final float G02_CREDIT_LIMIT = 0;
	private static final float G03_CREDIT_LIMIT = 0;
	private static final float G04_CREDIT_LIMIT = 0;

	@Override
	public SimNeedList clone() {
		final SimNeedList simNeedsList = (SimNeedList) super.clone();
		return simNeedsList;
	}

	public void createGoodList() {
		removeAllElements();
		createGoodList(Sim.BUYS_GOODS_EVERY, Sim.DIES_IF_NOT_CONSUMED_WITHIN);
	}

	/*
	 * good technology level 0 costs s
	 *
	 */
	private void createGoodList(final long consumeEvery, final long dieIfNotConsumedWithin) {
		add(new SimNeed(GoodType.FOOD, consumeEvery, dieIfNotConsumedWithin, G01_CREDIT_LIMIT));
		add(new SimNeed(GoodType.G02, consumeEvery, dieIfNotConsumedWithin, G02_CREDIT_LIMIT));
		add(new SimNeed(GoodType.G03, consumeEvery, dieIfNotConsumedWithin, G03_CREDIT_LIMIT));
		add(new SimNeed(GoodType.G04, consumeEvery, dieIfNotConsumedWithin, G04_CREDIT_LIMIT));
	}

	public SimNeed getByType(final GoodType type) {
		for (final SimNeed good : this) {
			if (good.type == type) {
				return good;
			} else {
			}
		}
		return null;
	}
	// int queryAmount()
	// {
	// int amount = 0;
	// for ( SimNeeds good : this )
	// {
	// amount += good.amount;
	// }
	// return amount;
	// }
	// public String queryFirstGoodName()
	// {
	// Good good = queryFirstGood();
	// if ( good != null )
	// return good.Name;
	// return "";
	// }
	//
	// public Good queryFirstGood()
	// {
	// for ( Good good : this )
	// {
	// if ( good.amount > 0 )
	// return good;
	// }
	// return null;
	// }
}
