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
package com.abdalla.bushnaq.mercator.universe.good;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.abdalla.bushnaq.mercator.universe.planet.Planet;

/**
 * @author bushnaq Created 13.02.2005
 */
public class GoodList extends Vector<Good> {
	private static final long serialVersionUID = -1781731576270326943L;
	private final Map<GoodType, Good> goodTypeFinder = new HashMap<GoodType, Good>();

	private void addGood(final Good good) {
		add(good);
		goodTypeFinder.put(good.type, good);
	}

	public void calculatePrice(final long currentTime) {
		for (final Good good : this) {
			good.calculatePrice(currentTime);
		}
	}

	public void consume(final GoodType type, final int i) {
		final Good good = getByType(type);
		good.consume(1);
	}

	public void createEmptyGoodList() {
		removeAllElements();
		createGoodList(50, 0, null);
	}

	/*
	 * good technology level 0 costs s
	 *
	 */
	private void createGoodList(final int averageAmount, final int amount, final Planet planet) {
		addGood(new Good(GoodType.FOOD, 5, averageAmount, amount, planet));
		addGood(new Good(GoodType.G02, 5, averageAmount, amount, planet));
		addGood(new Good(GoodType.G03, 5, averageAmount, amount, planet));
		addGood(new Good(GoodType.G04, 5, averageAmount, amount, planet));
	}

	public void createGoodList(final Planet planetOfOrigin) {
		removeAllElements();
		createGoodList(100, 100, planetOfOrigin);
	}

	public Good getByType(final GoodType type) {
		return goodTypeFinder.get(type);
		// for ( Good good : this )
		// {
		// if ( good.type == type )
		// {
		// return good;
		// }
		// else
		// {
		// }
		// }
		// return null;
	}

	public int queryAmount() {
		int amount = 0;
		for (final Good good : this) {
			amount += good.getAmount();
		}
		return amount;
	}

	public Good queryFirstGood() {
		for (final Good good : this) {
			if (good.getAmount() > 0)
				return good;
		}
		return null;
	}

	public String queryFirstGoodName() {
		final Good good = queryFirstGood();
		if (good != null)
			return good.type.getName();
		return "";
	}
}
