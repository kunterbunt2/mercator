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

import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;

import java.util.ArrayList;

public class DockingDoors extends ArrayList<DockingDoor> {
    public DockingDoors(Planet planet) {
        add(new DockingDoor(planet, planet.getName().substring(0, 2), -32f, 0));
        add(new DockingDoor(planet, planet.getName().substring(2, planet.getName().length()), 32f, 0));
    }

    public void advanceInTime(float realTimeDelta) {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.advanceInTime(realTimeDelta);
        }
    }

    public void create(RenderEngine3D<GameEngine3D> renderEngine) {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.create(renderEngine);
        }
    }

    public void render(RenderEngine3D<GameEngine3D> renderEngine) {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.render(renderEngine);
        }
    }

    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine) {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.renderText(renderEngine);
        }
    }

    public void setDockingDoorStatus(DockingDoor.DockingDoorState dockingDoorState) {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.setDockingDoorState(dockingDoorState);
        }
    }
}
