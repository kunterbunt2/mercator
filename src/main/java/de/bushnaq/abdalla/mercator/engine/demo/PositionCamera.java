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

package de.bushnaq.abdalla.mercator.engine.demo;

import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import static de.bushnaq.abdalla.mercator.engine.GameEngine3D.*;

public class PositionCamera extends ScheduledTask {
    private final Vector3 lookAt;
    private final String  name;
    private final Vector3 position;
    private final int     zoomIndex;

    public PositionCamera(GameEngine3D gameEngine, int afterSeconds, int zoomIndex, Vector3 position, Vector3 lookAt) {
        super(gameEngine, afterSeconds);
        this.zoomIndex = zoomIndex;
        this.position  = position;
        this.lookAt    = lookAt;
        name           = null;
    }

    public PositionCamera(GameEngine3D gameEngine, int afterSeconds, int zoomIndex, String name) {
        super(gameEngine, afterSeconds);
        this.zoomIndex = zoomIndex;
        lookAt         = null;
        position       = null;
        this.name      = name;
    }

    public PositionCamera(GameEngine3D gameEngine, int afterSeconds, int zoomIndex) {
        super(gameEngine, afterSeconds);
        this.zoomIndex = zoomIndex;
        lookAt         = null;
        position       = null;
        name           = null;
    }

    @Override
    public void execute() {
        if (position == null || lookAt == null) {
            Planet planet;
            if (name == null)
                planet = gameEngine.universe.planetList.findBusyCenterPlanet();
            else
//        Planet planet = gameEngine.universe.traderList.findByName(Debug.getFilterTrader()).planet;
//        Planet planet = gameEngine.universe.planetList.findByName(Debug.getFilterPlanet());
                planet = gameEngine.universe.planetList.findByName(name);
            if (planet == null && !gameEngine.universe.planetList.isEmpty()) planet = gameEngine.universe.planetList.get(0);
            Vector3 lookat;
            if (planet != null) lookat = new Vector3(planet.x, 0, planet.z);
            else
                lookat = new Vector3(0, 0, 0);
            gameEngine.getCamera().position.set(lookat.x + CAMERA_OFFSET_X / Universe.WORLD_SCALE, lookat.y + CAMERA_OFFSET_Y / Universe.WORLD_SCALE, lookat.z + CAMERA_OFFSET_Z / Universe.WORLD_SCALE);
            gameEngine.getCamera().up.set(0, 1, 0);
            gameEngine.getCamera().lookAt(lookat);
//            gameEngine.getCamera().near = 2f;
//            gameEngine.getCamera().far  = 8000f;
            gameEngine.getCamera().update();
            gameEngine.getCamera().setDirty(true);
            gameEngine.getCamController().zoomIndex = zoomIndex;
        } else {
            gameEngine.getCamera().position.set(position);
            gameEngine.getCamera().up.set(Vector3.Y);
            gameEngine.getCamera().lookAt(lookAt);
            gameEngine.getCamController().zoomIndex = zoomIndex;
            gameEngine.getCamera().update();
            gameEngine.getCamera().setDirty(true);
        }
    }

    @Override
    public boolean requiresBlending() {
        return true;
    }
}
