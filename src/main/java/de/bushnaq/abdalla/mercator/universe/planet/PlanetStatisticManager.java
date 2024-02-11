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

package de.bushnaq.abdalla.mercator.universe.planet;

import java.util.HashMap;
import java.util.Map;

public class PlanetStatisticManager {
    Map<String, PlanetStatistic> statisticMap = new HashMap<String, PlanetStatistic>();

    public int getAmount(final String name) {
        final PlanetStatistic planetStatistic = statisticMap.get(name);
        if (planetStatistic != null) {
            return planetStatistic.goodAmount;
        } else {
            return 0;
        }
    }

    public void transported(final Planet from, final int amount) {
        PlanetStatistic planetStatistic = statisticMap.get(from.getName());
        if (planetStatistic != null) {
            planetStatistic.goodAmount += amount;
        } else {
            planetStatistic = new PlanetStatistic(amount);
        }
        statisticMap.put(from.getName(), planetStatistic);
    }
}
