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

package de.bushnaq.abdalla.mercator.universe.land;

import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class Land3DRenderer extends ObjectRenderer<GameEngine3D> {
    private static final float      HILL_HIGHT = 16;
    private static final float      LAND_HIGHT = Planet3DRenderer.PLANET_HIGHT + HILL_HIGHT;
    private static final float      LAND_SIZE  = Planet.PLANET_DISTANCE;
    private final        Land       land;
    private              GameObject instance;

    public Land3DRenderer(final Land planet) {
        this.land = planet;
    }

    @Override
    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        createLand(renderEngine);
    }

    private void createLand(final RenderEngine3D<GameEngine3D> renderEngine) {
        instanciateLand(renderEngine, land.x, land.y, land.z);
    }

    private void instanciateLand(final RenderEngine3D<GameEngine3D> renderEngine, final float x, final float y, final float z) {
        instance = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.land), null);
        instance.instance.transform.setToTranslationAndScaling(x, y - LAND_HIGHT / 2 + HILL_HIGHT, z, LAND_SIZE, LAND_HIGHT + HILL_HIGHT, LAND_SIZE);
        instance.update();
        renderEngine.addStatic(instance);
    }

}
