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

import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetCommunicationPartner;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderCommunicationPartner;

public class Debug {
    private static final boolean enablePlanetFilter = true;
    private static final boolean enableTraderFilter = true;
    //    private static final String  filterPlanet       = "Leo Minor Port";
//    private static final String  filterPlanet       = "Pollux Hub";
    private static final String  filterPlanet       = "Pollux Hub";
    //    private static final String  filterTrader       = "Starpath";
//    private static final String  filterTrader       = "Frontier Star";
    private static final String  filterTrader       = "Damocles";

    public static String getFilterPlanet() {
        return filterPlanet;
    }

    public static String getFilterTrader() {
        return filterTrader;
    }

    public static boolean isFilterPlanet(String name) {
        return enablePlanetFilter && name.equals(filterPlanet);
    }

    public static boolean isFilterTrader(String name) {
        return enableTraderFilter && name.equals(filterTrader);
    }

    public static boolean isFiltered(Object object) {
        if (object instanceof Trader) {
            return isFilterTrader(((Trader) object).getName());
        } else if (object instanceof TraderCommunicationPartner) {
            return isFilterTrader(((TraderCommunicationPartner) object).getName());
        } else if (object instanceof Planet) {
            return isFilterPlanet(((Planet) object).getName());
        } else if (object instanceof PlanetCommunicationPartner) {
            return isFilterTrader(((PlanetCommunicationPartner) object).getName());
        }
        return false;
    }
}
