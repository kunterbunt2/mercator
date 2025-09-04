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
import de.bushnaq.abdalla.engine.chronos.Task;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import static de.bushnaq.abdalla.mercator.engine.GameEngine3D.*;

public class MercatorPositionCamera<T extends GameEngine3D> extends Task<T> {
    private final String name;
    private final int    zoomIndex;

    public MercatorPositionCamera(T gameEngine, int zoomIndex, String name) {
        super(gameEngine, 0);
        this.zoomIndex = zoomIndex;
        this.name      = name;
    }

    @Override
    public boolean execute(float deltaTime) {
        GameEngine3D ge     = gameEngine;
        Planet       planet = ge.universe.planetList.findByName(name);
//        if (planet == null && !gameEngine.universe.planetList.isEmpty()) planet = gameEngine.universe.planetList.get(0);
        Vector3 lookat;
        if (planet != null) lookat = new Vector3(planet.x, 0, planet.z);
        else lookat = new Vector3(0, 0, 0);
        ge.getCamera().position.set(lookat.x + CAMERA_OFFSET_X / Universe.WORLD_SCALE, lookat.y + CAMERA_OFFSET_Y / Universe.WORLD_SCALE, lookat.z + CAMERA_OFFSET_Z / Universe.WORLD_SCALE);
        ge.getCamera().up.set(0, 1, 0);
        ge.getCamera().lookAt(lookat);
//            gameEngine.getCamera().near = 2f;
//            gameEngine.getCamera().far  = 8000f;
        ge.getCamController().setTargetZoomIndex(zoomIndex);
        ge.getCamController().setZoomIndex(zoomIndex);
        ge.getCamController().update(true);
        ge.getCamera().update(true);
        ge.getCamera().setDirty(true);
        gameEngine.getUniverse().updateSelectedPlanet();
        return true;
    }

    @Override
    public long secondToRun() {
        return 0;
    }

    @Override
    public void subExecute(float deltaTime) {

    }
}
