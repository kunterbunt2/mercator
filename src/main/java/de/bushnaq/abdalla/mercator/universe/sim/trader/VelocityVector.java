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

package de.bushnaq.abdalla.mercator.universe.sim.trader;

import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class VelocityVector extends ObjectRenderer<GameEngine3D> {
    public static final float                    VECTOR_LENGTH = 4000f;
    public static final float                    VECTOR_WIDTH  = .2f;
    private             GameObject<GameEngine3D> gameObject;

    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        gameObject = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.cubeModel), null, this);
        renderEngine.addDynamic(gameObject);
    }

    public void update(final RenderEngine3D<GameEngine3D> renderEngine, Trader trader) throws Exception {
        gameObject.instance.transform.setToTranslation(trader.navigator.previousWaypoint.x, trader.navigator.previousWaypoint.y + Trader3DRenderer.TRADER_FLIGHT_HEIGHT, trader.navigator.previousWaypoint.z);
        gameObject.instance.transform.rotate(Vector3.Y, trader.getManeuveringSystem().rotation);
        gameObject.instance.transform.translate(0, 0, -VECTOR_LENGTH / 2);
        gameObject.instance.transform.scale(VECTOR_WIDTH, VECTOR_WIDTH, VECTOR_LENGTH);
        gameObject.update();
    }
}
