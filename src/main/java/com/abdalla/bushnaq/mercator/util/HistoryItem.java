package com.abdalla.bushnaq.mercator.util;

import java.util.Arrays;

import com.abdalla.bushnaq.mercator.universe.good.Good;
import com.abdalla.bushnaq.mercator.universe.good.GoodType;
import com.abdalla.bushnaq.mercator.universe.sim.trader.Trader;

public class HistoryItem {
	int exportedAmountOfGoods;
	float exportedCredits;
	int importedAmountOfGoods;
	float importedCredits;
	int localAmountOfGoodsBought;
	int localAmountOfGoodsSold;
	float localCreditsEarned;
	float localCreditsSpent;
	int[] tradedGoodVolume = new int[GoodType.values().length];

	public HistoryItem() {
		Arrays.fill(tradedGoodVolume, 0);
	}

	public void buy(final Good portGood, final float transactionCost, final int transactionAmount, final TradingPartner from) {
		if (portGood != null) {
			tradedGoodVolume[portGood.type.ordinal()]++;
		}
		if (Trader.class.isInstance(from)) {
			importedAmountOfGoods += transactionAmount;
			exportedCredits += transactionCost;
		} else {
			localAmountOfGoodsBought += transactionAmount;
			localCreditsSpent += transactionCost;
		}
	}

	public void sell(final Good portGood, final float transactionCost, final int transactionAmount, final TradingPartner to) {
		if (Trader.class.isInstance(to)) {
			localAmountOfGoodsSold += transactionAmount;
			localCreditsEarned += transactionCost;
		} else {
			exportedAmountOfGoods += transactionAmount;
			importedCredits += transactionCost;
		}
	}
}
