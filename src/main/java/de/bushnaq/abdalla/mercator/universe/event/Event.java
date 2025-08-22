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

package de.bushnaq.abdalla.mercator.universe.event;

import de.bushnaq.abdalla.mercator.universe.UniverseGenerator;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;

public class Event {
    static boolean             enableStackTrace = false;
    public EventLevel          level;
    public StackTraceElement[] stackTrace;
    public String              what;
    public long                when;
    public Object              who;

    public Event(final EventLevel level, final long when, final Object who, final String what) {
        this.level = level;
        this.when  = when;
        this.who   = who;
        this.what  = what;
        if (enableStackTrace)
            stackTrace = Thread.currentThread().getStackTrace();
    }

    public String getWhosName() {
        if (who instanceof Planet) {
            return ((Planet) who).getName();
        } else if (who instanceof Trader) {
            return ((Sim) who).getName();
        } else if (who instanceof Sim) {
            return ((Sim) who).getName();
        } else if (who instanceof ProductionFacility) {
            return ((ProductionFacility) who).getName();
        } else if (who instanceof Good) {
            return ((Good) who).type.getName();
        } else if (who instanceof MercatorRandomGenerator) {
            return "MercatorRandomGenerator";
        } else if (who instanceof UniverseGenerator) {
            return "UniverseGenerator";
        }
        return null;
    }
}
