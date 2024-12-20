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

import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;

public class Transaction {
    public static void pay(final long currentTime, final float price, final int transactionAmount, final TradingPartner from, final TradingPartner to) {
        from.setCredits(from.getCredits() - price * transactionAmount);
        to.ern(currentTime, price * transactionAmount);
        from.getHistoryManager().get(currentTime).buy(null, price * transactionAmount, transactionAmount, to.getPlanet());
        // from.getPlanet().universe.eventManager.add( currentTime, this, String.format(
        // "on planet %s payed %s %.2f credits", planet.getName(), sim.getName(),
        // sim.cost ) );
    }

    public static void trade(final long currentTime, final GoodType goodType, final float price, final int transactionAmount, final TradingPartner from, final TradingPartner to, final TradingPartner producer, final boolean reselling) {
        {
            final Good good = to.getGoodList().getByType(goodType);
            good.buy(transactionAmount);
            if (reselling) {
                good.price = price;// ---remember the price we bought this good
            }
            to.setCredits(to.getCredits() - transactionAmount * price);
            to.setLastTransaction(currentTime);
            if (to.getPlanet().universe.eventManager.isEnabled())
                to.getPlanet().universe.eventManager.add(EventLevel.trace, currentTime, to, String.format("buys %s from %s produced at %s", goodType.getName(), from.getName(), producer.getName()));
            to.getHistoryManager().get(currentTime).buy(good, price * transactionAmount, transactionAmount, from);
            to.getPlanet().universe.getHistoryManager().get(currentTime).buy(good, price * transactionAmount, transactionAmount, from);
        }
        {
            final Good good = from.getGoodList().getByType(goodType);
            good.sell(transactionAmount);
            good.indicateBuyInterest(currentTime);
            from.setCredits(from.getCredits() + transactionAmount * price);
            from.setLastTransaction(currentTime);
            // from.getPlanet().universe.eventManager.add( currentTime, from, String.format(
            // "%s sells %s to %s", from.getName(), goodType.getName(), to.getName() ) );
            from.getHistoryManager().get(currentTime).sell(good, price * transactionAmount, transactionAmount, to);
            from.getPlanet().getHistoryManager().get(currentTime).sell(good, price * transactionAmount, transactionAmount, to);
        }
    }
}
