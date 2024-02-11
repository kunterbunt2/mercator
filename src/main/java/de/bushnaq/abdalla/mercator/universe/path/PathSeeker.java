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

package de.bushnaq.abdalla.mercator.universe.path;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import java.util.HashMap;
import java.util.Map;

public class PathSeeker {
    public float goodPrice; // ---used by queryBestPlanetToSell
    public float planetValue; // ---used by queryBestPlanetToSell
    public float time; // ---used by queryBestPlanetToSell
    GoodType                    destinationGoodType = null;
    Map<Waypoint, ShortestPath> pathMap             = new HashMap<Waypoint, ShortestPath>();

    public PathSeeker() {
    }

    void findDestination(final Waypoint sourcePlanet, final Waypoint aDestinationPlanet, final int aMaxDistance) {
        for (final Path jumpGate : sourcePlanet.pathList) {
            final float distance = get(sourcePlanet).distance + sourcePlanet.queryDistance(jumpGate.target);
            // if( distance < aMaxDistance )
            {
                if (distance < get(jumpGate.target).distance) {
                    get(jumpGate.target).distance               = distance;
                    get(jumpGate.target).pathSeekerNextWaypoint = sourcePlanet;
                    /*
                     * if( jumpGate->Planet == aDestinationPlanet ) { } else
                     */
                    {
                        findDestination(jumpGate.target, aDestinationPlanet, aMaxDistance);
                    }
                } else {
                    // ---Someone else was already here
                }
            }
        }
    }

    public ShortestPath get(final Waypoint planet) {
        ShortestPath path = pathMap.get(planet);
        if (path == null) {
            path = new ShortestPath(planet);
            pathMap.put(planet, path);
        }
        return path;
    }

    /**
     * This is a very simple path seeker that calculates the minimum distance of
     * every planet in the universe to the portPlanet.
     *
     * @param portPlanet
     * @param aMaxDistance
     */
    public void mapGalaxy(final Waypoint portPlanet, final int aMaxDistance) {
        put(portPlanet, 0);
        markNeighborDistance(portPlanet, aMaxDistance);
    }

    void markNeighborDistance(final Waypoint planet, final int aMaxDistance) {
        // ---For every jumpgate
        for (final Path jumpGate : planet.pathList) {
            final float distance = get(planet).distance + planet.queryDistance(jumpGate.target);
            if (distance < aMaxDistance) {
                if (distance < get(jumpGate.target).distance) {
                    get(jumpGate.target).distance               = distance;
                    get(jumpGate.target).pathSeekerNextWaypoint = planet;
                    markNeighborDistance(jumpGate.target, aMaxDistance);
                } else {
                    // ---Someone else was already here
                }
            }
        }
    }

    private void put(final Waypoint portPlanet, final int i) {
        pathMap.put(portPlanet, new ShortestPath(portPlanet, i, null));
    }

    float queryDistance(final Planet aSourcePlanet, final Planet aDestinationPlanet, final int aMaxDistance) {
        queryFirstWaypoint(aSourcePlanet, aDestinationPlanet, aMaxDistance);
        return get(aDestinationPlanet).distance;
    }

    Waypoint queryFirstWaypoint(final Planet sourcePlanet, final Planet destinationPlanet, final int maxDistance) {
        // ---We start from the source
        // ---Reset the distance
        // Sleep(1000);
        // for ( Planet planet : planetList )
        // {
        // get( planet ).pathSeekerDistance = 999992;
        // get( planet ).pathSeekerNextWaypoint = null;
        // }
        if ((sourcePlanet == destinationPlanet) || (destinationPlanet == null)) {
            get(sourcePlanet).distance = 0;
            return null;
        } else {
            get(sourcePlanet).distance = 0;
            findDestination(sourcePlanet, destinationPlanet, maxDistance);
            // ---Mark the path
            /*
             * BcPlanet* planet = aDestinationPlanet; { while( planet->NextWaypointToStart
             * != aSourcePlanet ) { planet->Selected = true; planet =
             * planet->NextWaypointToStart; } //planet->Selected = true;
             * planet->NextWaypointToStart->Selected = true; }
             */
            return queryNextWaypoint(sourcePlanet, destinationPlanet);
        }
    }

    Waypoint queryNextWaypoint(final Waypoint sourcePlanet, Waypoint aDestinationPlanet) {
        while (get(aDestinationPlanet).pathSeekerNextWaypoint != sourcePlanet) {
            aDestinationPlanet = get(aDestinationPlanet).pathSeekerNextWaypoint;
        }
        return aDestinationPlanet;
    }
}
