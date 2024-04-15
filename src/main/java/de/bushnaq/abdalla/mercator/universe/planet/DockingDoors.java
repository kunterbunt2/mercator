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

import com.badlogic.gdx.Gdx;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;

import java.util.ArrayList;

public class DockingDoors extends ArrayList<DockingDoor> {
    private final float[]   position = new float[3];
    private       OggPlayer oggPlayer;

    public DockingDoors(Planet planet) {
        add(new DockingDoor(planet, planet.getName().substring(2, 3), -32f, 0));
        add(new DockingDoor(planet, planet.getName().substring(3, planet.getName().length()), 32f, 0));
        position[0] = planet.x;
        position[1] = planet.y;
        position[2] = planet.z;
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
        try {
            oggPlayer = renderEngine.getGameEngine().audioEngine.createAudioProducer(OggPlayer.class);
            oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/docking-door.ogg"));
            oggPlayer.setGain(200.0f);
            oggPlayer.setAmbient(false);
            oggPlayer.setLoop(true);
        } catch (OpenAlException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(RenderEngine3D<GameEngine3D> renderEngine) throws OpenAlException {
        for (DockingDoor dockingDoor : this) {
            dockingDoor.render(renderEngine);
        }
        oggPlayer.setPositionAndVelocity(position, null);
        if (renderEngine.getCamera().position.dst(position[0], position[1], position[2]) < 1000) {
            switch (get(0).getDockingDoorState()) {
                case OPEN, CLOSED -> {
                    oggPlayer.pause();
                }
                case LOWERING, CLOSING, RAISING, OPENING -> {
                    oggPlayer.play();
                }
            }
        } else {
            oggPlayer.pause();
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
