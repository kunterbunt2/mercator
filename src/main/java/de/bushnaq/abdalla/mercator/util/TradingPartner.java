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

import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * @author abdalla Defines an interface to allow the exchange of goods and
 * credits between two TradingPartner. All transactions are handled
 * through static methods of the Transaction class.
 */
public interface TradingPartner {
    void ern(long currentTime, float f);

    float getCredits();

    GoodList getGoodList();

    HistoryManager getHistoryManager();

    String getName();

    Planet getPlanet();

    void setCredits(float credits);

    void setLastTransaction(long currentTime);
}
