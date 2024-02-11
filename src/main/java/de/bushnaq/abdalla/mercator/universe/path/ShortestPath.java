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

public class ShortestPath {
    public float    distance = 999992; // ---Used by pathseeker algorithm temporary. This is the accumulation of distance over all planets from the current port.
    public Waypoint pathSeekerNextWaypoint; // ---Used by the pathseeker algorithm temporary. This is the next planet on the calculated route to the current port.
    //	public Planet planet;

    public ShortestPath(final Waypoint planet) {
        //		this.planet = planet;
    }

    public ShortestPath(final Waypoint planet, final int pathSeekerDistance, final Waypoint pathSeekerNextWaypoint) {
        //		this.planet = planet;
        this.distance               = pathSeekerDistance;
        this.pathSeekerNextWaypoint = pathSeekerNextWaypoint;
    }
}
