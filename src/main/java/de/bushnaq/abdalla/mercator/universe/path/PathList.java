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

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class PathList extends Vector<Path> {
    private static final long serialVersionUID = -6879517895353709879L;

    public Path queryJumpGateTo(final Waypoint aTargetPlanet) {
        for (final Path jumpGate : this) {
            if (jumpGate.target == aTargetPlanet) {
                return jumpGate;
            }
        }
        return null;
    }

    public void reduceUsage() {
        for (final Path jumpGate : this) {
            if (jumpGate.usage > 1) {
                jumpGate.usage = 1;
            }
        }
    }
}
