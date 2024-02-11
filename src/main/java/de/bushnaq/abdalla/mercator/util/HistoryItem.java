/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.util;

import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

import java.util.Arrays;

public class HistoryItem {
    int   exportedAmountOfGoods;
    float exportedCredits;
    int   importedAmountOfGoods;
    float importedCredits;
    int   localAmountOfGoodsBought;
    int   localAmountOfGoodsSold;
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
