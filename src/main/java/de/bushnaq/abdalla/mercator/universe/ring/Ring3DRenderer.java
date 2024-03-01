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

package de.bushnaq.abdalla.mercator.universe.ring;

import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class Ring3DRenderer extends ObjectRenderer<GameEngine3D> {

    private final Ring ring;

    public Ring3DRenderer(final Ring ring) {
        this.ring = ring;
    }

    @Override
    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        final float delta  = (ring.universe.size + 1) * Planet.PLANET_DISTANCE * 2;
        final float deltaA = 360.0f / ring.segments;
        //sector
        //		for (float a = 0; a < 360; a += deltaA) {
        //			for (int w = -ring.width; w <= ring.width; w++) {
        //				float x1 = w * Ring.SECTOR_SIZE;
        //				float z1 = (float) (Math.sin(a - deltaA) * ring.radius);
        //				float y1 = (float) (Math.cos(a - deltaA) * ring.radius);
        //				float x2 = w * Ring.SECTOR_SIZE + Ring.SECTOR_SIZE;
        //				float z2 = (float) (Math.sin(a + deltaA) * ring.radius);
        //				float y2 = (float) (Math.cos(a + deltaA) * ring.radius);
        //
        //				//earth
        //				{
        //					final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.sector), null);
        //					sectorInstance.instance.transform.setToRotation(Vector3.X, a);
        //					sectorInstance.instance.transform.translate(x1, ring.radius-500, 0);
        //					sectorInstance.instance.transform.scale(Ring.SECTOR_SIZE, 8, Ring.SECTOR_SIZE);
        //					sectorInstance.update();
        //					renderMaster.sceneManager.addStatic(sectorInstance);
        //				}
        //				//water
        //				{
        //					final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.water), null);
        //					sectorInstance.instance.transform.setToRotation(Vector3.X, a);
        //					sectorInstance.instance.transform.translate(x1,ring.radius, 0);
        //					sectorInstance.instance.transform.scale(Ring.SECTOR_SIZE, 1, Ring.SECTOR_SIZE);
        //					sectorInstance.update();
        //					renderMaster.sceneManager.addStatic(sectorInstance);
        //				}
        //			}
        //		}
        //		System.out.println("finished");
        {
            final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.sector), null);
            sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.SECTOR_Y, 0, delta, 8, delta);
            sectorInstance.update();
            renderEngine.addStatic(sectorInstance);

        }
        //water
        {
            final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.waterModel), null);
            sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.WATER_Y, 0, delta, 1, delta);
            sectorInstance.update();
            renderEngine.addStatic(sectorInstance);
        }
    }

}
