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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;

public class StrobeLight {
    public static final  float                    LIGHT_MAX_INTENSITY          = 10000f;
    public static final  float                    LIGHT_OFF_DURATION_AVERAGE   = 3f;
    public static final  float                    LIGHT_OFF_DURATION_DEVIATION = 0.1f;
    public static final  float                    LIGHT_ON_DURATION            = 0.1f;
    public static final  float                    LIGHT_SIZE                   = .2f;
    final static         Vector3                  yVector                      = new Vector3(0, 1, 0);
    private static final float                    PY2                          = 3.14159f / 2;
    public final         GameObject<GameEngine3D> gameObject;
    public final         PointLight               pointLight;
    private final        Vector3                  lightScaling                 = new Vector3(LIGHT_SIZE, LIGHT_SIZE, LIGHT_SIZE);
    public               Vector3                  delta                        = new Vector3();
    public               int                      lightMode                    = 0;
    public               float                    lightTimer                   = 0;

    //    public StrobeLight(RenderEngine3D<GameEngine3D> renderEngine, float deltaX, float deltaY, float deltaZ, GameObject<GameEngine3D> gameObject) {
//        delta.set(deltaX, deltaY, deltaZ);
//        this.gameObject = gameObject;
//        this.pointLight = new PointLight();
//        renderEngine.addDynamic(gameObject);
//    }
    public StrobeLight(RenderEngine3D<GameEngine3D> renderEngine, Vector3 delta, GameObject<GameEngine3D> gameObject) {
        this.delta.set(delta);
        this.gameObject = gameObject;
        this.pointLight = new PointLight();
        renderEngine.addDynamic(gameObject);
    }

    private void animate(RenderEngine3D<GameEngine3D> renderEngine) {
        final float deltaTime = Gdx.graphics.getDeltaTime();
        if (lightTimer <= 0f) {
            //lightMode
            // 0, wait
            switch (lightMode) {
                case 0: {
                    resetLightOffTimer();
                    lightMode = 1;//wait for light to go on
                    renderEngine.remove(pointLight, true);
//                    if (Debug.isFilter(trader.getName())) {
//                        logger.info("lights off");
//                    }
                }
                break;
                case 1: {
                    resetLightOnTimer();
                    lightMode = 0;//wait for light to go off
                    final float intensity = (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * LIGHT_MAX_INTENSITY);
                    pointLight.setIntensity(intensity);
                    renderEngine.add(pointLight, true);
//                    if (Debug.isFilter(trader.getName())) {
//                        logger.info("lights on");
//                    }
                }
                break;
            }
        } else {
            lightTimer -= deltaTime;
        }
    }

    public float calculateIntensity() {
        return (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * LIGHT_MAX_INTENSITY);
    }

    public void resetLightOffTimer() {
        lightTimer = LIGHT_OFF_DURATION_AVERAGE + LIGHT_OFF_DURATION_DEVIATION / 2 - (LIGHT_OFF_DURATION_DEVIATION * ((float) Math.random()));
    }

    public void resetLightOnTimer() {
        lightTimer = LIGHT_ON_DURATION;
    }

    public void update(RenderEngine3D<GameEngine3D> renderEngine, Vector3 translation, float rotation) {
//        if (direction.x != 0f || direction.y != 0f || direction.z != 0f)
        {
            final float intensity = calculateIntensity();
            gameObject.instance.transform.setToTranslation(translation.x, translation.y, translation.z);
//            gameObject.instance.transform.rotateTowardDirection(direction, Vector3.Y);
            gameObject.instance.transform.rotate(yVector, rotation);
            gameObject.instance.transform.translate(delta);
            gameObject.instance.transform.scale(lightScaling.x, lightScaling.y, lightScaling.z);
            gameObject.update();
            Vector3 lightTranslation = new Vector3();
            gameObject.instance.transform.getTranslation(lightTranslation);
            pointLight.set(Color.RED, lightTranslation.x + 0.2f, lightTranslation.y, lightTranslation.z, intensity);
            animate(renderEngine);
        }
    }
}
