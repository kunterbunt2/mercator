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

import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.universe.path.WaypointProxy;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class PlanetList extends Vector<Planet> {
    private static final long serialVersionUID = 4661905529459408641L;

    public void clearSeed() {
        for (final Planet planet : this) {
            planet.seed = null;
            //			for (Path p : planet.pathList)
            crearSeed(planet);
        }
    }

    private void crearSeed(final Waypoint w) {
        for (final Path path : w.pathList) {
            if (path.target.seed != null) {
                path.target.seed = null;
                crearSeed(path.target);
            } else {
                //already seeded
            }
        }
    }

    public Planet findBusyCenterPlanet() {
        Planet planet      = null;
        float  minDistance = Float.MAX_VALUE;
        for (final Planet p : this) {
            if (p.pathList.size() > 3) {
                final float distance = p.x * p.x + p.z * p.z;
                if (distance < minDistance) {
                    minDistance = distance;
                    planet      = p;
                }
            }
        }
        return planet;
    }

    public Planet findByName(String name) {
        for (final Planet p : this) {
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }

    public int getIndex(final Planet planet) {
        int index = 0;
        for (final Planet p : this) {
            if (p == planet)
                return index;
            index++;
        }
        return -1;
    }

    public void markTraderPath(final Trader aTrader) {
        if (aTrader != null) {
            // ---Deselect all jump gates
            for (final Planet planet : this) {
                for (final Path jumpGate : planet.pathList) {
                    jumpGate.selected = false;
                }
            }
            int waypointIndex = 0;
            while (waypointIndex < aTrader.waypointList.size()) {
                final WaypointProxy waypoint = aTrader.waypointList.get(waypointIndex);
                // ---For each waypoint, mark the jumppoint to the next one
                for (final Path jumpGate : waypoint.waypoint.pathList) {
                    if ((waypointIndex + 1 < aTrader.waypointList.size()) && (jumpGate.target == aTrader.waypointList.get(waypointIndex + 1).waypoint)) {
                        jumpGate.selected = true;
                    } else if ((waypointIndex > 0) && (jumpGate.target == aTrader.waypointList.get(waypointIndex - 1).waypoint)) {
                        jumpGate.selected = true;
                    } else {
                    }
                }
                waypointIndex++;
            }
        }
    }

    public Planet queryPlanetByLocation(final float x, final float z) {
        for (final Planet planet : this) {
            final float x1 = (float) Math.floor(planet.x / Planet.PLANET_DISTANCE);
            final float x2 = (float) Math.floor(x / Planet.PLANET_DISTANCE);
            if (Math.floor(planet.x / Planet.PLANET_DISTANCE) == Math.floor(x / Planet.PLANET_DISTANCE) && Math.floor(planet.z / Planet.PLANET_DISTANCE) == Math.floor(z / Planet.PLANET_DISTANCE))
                return planet;
        }
        return null;
    }
}
