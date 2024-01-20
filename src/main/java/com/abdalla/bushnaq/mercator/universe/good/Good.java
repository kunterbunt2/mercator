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

import com.abdalla.bushnaq.mercator.renderer.Renderable;
import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.util.TimeUnit;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Good extends Renderable implements Cloneable {
	private static final long MAX_BUY_INTEREST_INTERVAL = 250 * TimeUnit.TICKS_PER_DAY;
	private int amount = 0;
	public int averageAmount = 0;
	public float averagePrice = 0;
	public long lastBuyInterest = Long.MIN_VALUE;
	public float price = getAveragePrice();
	public GoodStatistic statistic = new GoodStatistic();
	public GoodType type;
	//	public List<Volume> volume = new LinkedList<Volume>();

	public Good(final GoodType type, final float averagePrice, final int averageAmount, final int amount, final Planet planetOfOrigin) {
		this.type = type;
		// this.Name = type.getName();
		this.averagePrice = averagePrice;
		this.averageAmount = averageAmount;
		set2DRenderer(new Good2DRenderer(this));
		set3DRenderer(new Good3DRenderer(this));
		produce(amount, planetOfOrigin);
	}

	public void buy(final int volume) {
		this.amount += volume;
		statistic.bought += volume;
	}

	public void calculatePrice(final long currentTime) {
		if (isTraded(currentTime)) {
			// ---The price will tend to move to meet the criteria of the market
			// ---A trader tends to pay less for it if he does not own the money
			//max price is 250 times the average price
			//min price is 1
			if (amount >= getAverageAmount()) {
				price = (int) (getAveragePrice() - (((float) amount - getAverageAmount()) / getAverageAmount()) * (getAveragePrice() - 1));
			} else {
				price = (int) (getAveragePrice() * 5 - (((float) amount) / getAverageAmount()) * (getAveragePrice() * 4)) * 10;
			}
			// if ( price < 0 )
			// {
			// System.out.println( price );
			// }
			// float volumeInfluence = ( getMaxPriceDelta() * ( amount - getAverageAmount()
			// ) ) / getMaxAmountDelta();
			// ---Food 10 - -6 = 16
			// price = (float)( getAveragePrice() / Math.log10( ((amount/getAverageAmount())
			// ) );
			// Price = GetAveragePrice() - ( GetMaxPriceDelta()*(Amount -
			// GetAverageAmount()) )/GetMaxAmountDelta();
		} else {
			price = getAveragePrice();
		}
	}

	@Override
	public Good clone() throws CloneNotSupportedException {
		final Good good = (Good) super.clone();
		return good;
	}

	public void consume(final int volume) {
		this.amount -= volume;
		statistic.consumed += volume;
	}

	public int getAmount() {
		return amount;
	}

	public int getAverageAmount() {
		return averageAmount;
	}

	public float getAveragePrice() {
		return averagePrice;
	}

	public int getMaxAmount() {
		return getAverageAmount() + getMaxAmountDelta();
	}

	// public String getName()
	// {
	// return type.getName();
	// }
	public int getMaxAmountDelta() {
		return getAverageAmount();
	}

	public float getMaxPrice() {
		return /* (int)( getAveragePrice() + getMaxPriceDelta() ) */getAveragePrice() * 5;
	}

	public float getMaxPriceDelta() {
		return /* 5.0f + */(getAveragePrice() * 0.99f);
	}

	public int getMinAmount() {
		return getAverageAmount() - getMaxAmountDelta();
	}

	public float getMinPrice() {
		return 1/* (int)( getAveragePrice() - getMaxPriceDelta() ) */;
	}

	public void indicateBuyInterest(final long currentTime) {
		lastBuyInterest = currentTime;
	}

	public boolean isTraded(final long currentTime) {
		return currentTime - lastBuyInterest < MAX_BUY_INTEREST_INTERVAL;
	}

	public void produce(final int volume, final Planet planetOfOrigin) {
		this.amount += volume;
		statistic.produced += volume;
		//		for (int i = 0; i < volume; i++) {
		//			this.volume.add(new Volume(planetOfOrigin));
		//		}
	}

	public void sell(final int volume) {
		if (volume < 0) {
			final int a = 23;
		}
		this.amount -= volume;
		statistic.sold += volume;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
}
