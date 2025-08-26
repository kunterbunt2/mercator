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
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;

import java.util.ArrayList;

public class DockingDoors extends ArrayList<DockingDoor> {
    private       OggPlayer oggPlayer;
    private final Planet    planet;
    private final float[]   position = new float[3];

    public DockingDoors(Planet planet) {
        this.planet = planet;
        add(new DockingDoor(planet, planet.getName().substring(0, planet.getName().length() / 2), -32f, 0));
        add(new DockingDoor(planet, planet.getName().substring(planet.getName().length() / 2), 32f, 0));
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
            oggPlayer = renderEngine.getGameEngine().audioEngine.createAudioProducer(OggPlayer.class, planet.getName() + "-docking-doors");
            oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/docking-door.ogg"));
            oggPlayer.setGain(200.0f);
            oggPlayer.setAmbient(false);
            oggPlayer.setLoop(true);
            oggPlayer.ignore(true);
        } catch (OpenAlException e) {
            throw new RuntimeException(e);
        }
    }

    public DockingDoor.DockingDoorState getDockingDoorStatus() {
        return get(0).getDockingDoorState();
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
        switch (dockingDoorState) {
            case CLOSING -> {
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, "Closing docking doors.");
            }
            case LOWERING -> {
                planet.eventManager.add(EventLevel.trace, planet.currentTime, planet, "Opening docking doors.");
            }
        }
        for (DockingDoor dockingDoor : this) {
            dockingDoor.setDockingDoorState(dockingDoorState);
        }
    }
}
