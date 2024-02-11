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

import de.bushnaq.abdalla.mercator.renderer.Renderable;

/**
 * @author bushnaq Created 13.02.2005
 */

/**
 * A path is a connection between two Waypoints
 */
public class Path extends Renderable {
    public boolean  closed   = false;
    public boolean  selected = false;
    public Waypoint source   = null;
    public Waypoint target   = null;
    public float    usage    = 0;

    public Path(final Waypoint planet, final Waypoint targetPlanet) {
        this.source = planet;
        this.target = targetPlanet;
        selected    = false;
        closed      = false;
        usage       = 1;
        set2DRenderer(new JumpGate2DRenderer(this));
        set3DRenderer(new JumpGate3DRenderer(this));
    }

}
