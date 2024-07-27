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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.CLOSED;
import static de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer.STATION_Z_SHIFT;

public class DockingDoor {
    private static final float                    DOCKING_DOOR_HEIGHT = 8;
    private static final float                    HORIZONTAL_SPEED    = 15f;
    private static final Color                    PLANET_NAME_COLOR   = new Color(0xffa500ff);
    private static final float                    VERTICAL_SPEED      = 2f;
    private static final float                    XZ_MOVEMENT_LEEWAY  = 58f;
    private              GameObject<GameEngine3D> dockingDoorGameObject;
    private              DockingDoorState         dockingDoorState    = CLOSED;
    private final        float                    dx;
    private final        float                    dz;
    private final        Logger                   logger              = LoggerFactory.getLogger(this.getClass());
    private final        Planet                   planet;
    private final        float                    scalingX;
    private final        float                    scalingZ;
    private final        float                    singDx;
    private final        float                    singDz;
    private final        String                   title;
    private              float                    x                   = 0;
    private              float                    y                   = 0;
    private              float                    z                   = 0;

    public DockingDoor(Planet planet, String title, float dx, float dz) {
        this.planet = planet;
        this.title  = title;
        this.dx     = dx;
        this.dz     = dz;
        singDz      = -Math.signum(dz);
        singDx      = -Math.signum(dx);
        scalingX    = Math.abs(dx * 2) + Math.abs(dz * 4);
        scalingZ    = Math.abs(dz * 2) + Math.abs(dx * 4);
    }

    public void advanceInTime(float realTimeDelta) {
        switch (dockingDoorState) {
            case OPEN -> {
//                dockingDoorStatus = DockingDoorStatus.CLOSING;
            }
            case CLOSED -> {
//                dockingDoorStatus = DockingDoorStatus.LOWERING;
            }
            case LOWERING -> {
                y -= realTimeDelta * VERTICAL_SPEED;
                if (y <= planet.y + STATION_Z_SHIFT - DOCKING_DOOR_HEIGHT / 2 - 5f) {
                    y                = planet.y + STATION_Z_SHIFT - DOCKING_DOOR_HEIGHT / 2 - 5f;
                    dockingDoorState = DockingDoorState.OPENING;
                }
            }
            case OPENING -> {
                z -= realTimeDelta * singDz * HORIZONTAL_SPEED;
                x -= realTimeDelta * singDx * HORIZONTAL_SPEED;
                if (dz != 0 && Math.abs(planet.z + dz - z) > XZ_MOVEMENT_LEEWAY) {
                    z                = planet.z + dz - singDz * XZ_MOVEMENT_LEEWAY;
                    dockingDoorState = DockingDoorState.OPEN;
                }
                if (dx != 0 && Math.abs(planet.x + dx - x) > XZ_MOVEMENT_LEEWAY) {
                    x                = planet.x + dx - singDx * XZ_MOVEMENT_LEEWAY;
                    dockingDoorState = DockingDoorState.OPEN;
                }
            }
            case CLOSING -> {
                z += realTimeDelta * singDz * HORIZONTAL_SPEED;
                x += realTimeDelta * singDx * HORIZONTAL_SPEED;
                if (dz != 0 && Math.abs(planet.z + dz - z) < 1f) {
                    z                = planet.z + dz;
                    dockingDoorState = DockingDoorState.RAISING;
                }
                if (dx != 0 && Math.abs(planet.x + dx - x) < 1f) {
                    x                = planet.x + dx;
                    dockingDoorState = DockingDoorState.RAISING;
                }
            }
            case RAISING -> {
                y += realTimeDelta * VERTICAL_SPEED;
                if (y >= planet.y + STATION_Z_SHIFT - DOCKING_DOOR_HEIGHT / 2) {
                    y                = planet.y + STATION_Z_SHIFT - DOCKING_DOOR_HEIGHT / 2;
                    dockingDoorState = DockingDoorState.CLOSED;
                }
            }
        }
    }

    public void create(RenderEngine3D<GameEngine3D> renderEngine) {
        x                     = planet.x + dx;
        y                     = planet.y + STATION_Z_SHIFT - DOCKING_DOOR_HEIGHT / 2;
        z                     = planet.z + dz;
        dockingDoorGameObject = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.dockingDoorModel), planet, planet.renderer3D);
        dockingDoorGameObject.instance.transform.setToTranslation(x, 0, z);
        dockingDoorGameObject.update();
        renderEngine.addDynamic(dockingDoorGameObject);

    }

    public DockingDoorState getDockingDoorState() {
        return dockingDoorState;
    }

    public void render(RenderEngine3D<GameEngine3D> renderEngine) {
        dockingDoorGameObject.instance.transform.setToTranslationAndScaling(x, y, z, scalingX, DOCKING_DOOR_HEIGHT, scalingZ);
        dockingDoorGameObject.update();
    }

    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine) {
        Vector3          translation = new Vector3(x, y, z);
        final BitmapFont font        = renderEngine.getGameEngine().getAtlasManager().bold256Font;
        renderEngine.renderEngine25D.renderTextCenterOnTop(translation, 0, 0, 4 + .1f, 0, font, Color.BLACK, PLANET_NAME_COLOR, title, 64);

    }

    public void setDockingDoorState(DockingDoorState dockingDoorState) {
        this.dockingDoorState = dockingDoorState;
    }

    public enum DockingDoorState {
        OPEN,
        CLOSED,
        LOWERING,
        OPENING,
        CLOSING,
        RAISING
    }

}
