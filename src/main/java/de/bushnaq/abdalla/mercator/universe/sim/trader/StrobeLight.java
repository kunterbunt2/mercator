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
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;

enum LightMode {
    ON, OFF
}

public class StrobeLight {
    public static final  float                    LIGHT_MAX_INTENSITY          = 1000f;
    public static final  float                    LIGHT_OFF_DURATION_AVERAGE   = 3f;
    public static final  float                    LIGHT_OFF_DURATION_DEVIATION = 0.1f;
    public static final  float                    LIGHT_ON_DURATION            = 1.1f;
    public static final  float                    LIGHT_SIZE                   = .2f;
    //    final static         Vector3                  yVector                      = new Vector3(0, 1, 0);
    private static final float                    PY2                          = 3.14159f / 2;
    private final        GameObject<GameEngine3D> bokehGameObject;
    private final        Vector3                  bokehScaling                 = new Vector3(LIGHT_SIZE * 5, LIGHT_SIZE * 5, LIGHT_SIZE * 5);
    public               Vector3                  delta                        = new Vector3();
    public final         GameObject<GameEngine3D> lightGameObject;
    private              LightMode                lightMode                    = LightMode.OFF;
    private final        Vector3                  lightScaling                 = new Vector3(LIGHT_SIZE, LIGHT_SIZE, LIGHT_SIZE);
    public               float                    lightTimer                   = 0;
    public final         PointLight               pointLight;

    //    public StrobeLight(RenderEngine3D<GameEngine3D> renderEngine, float deltaX, float deltaY, float deltaZ, GameObject<GameEngine3D> gameObject) {
//        delta.set(deltaX, deltaY, deltaZ);
//        this.gameObject = gameObject;
//        this.pointLight = new PointLight();
//        renderEngine.addDynamic(gameObject);
//    }
    public StrobeLight(RenderEngine3D<GameEngine3D> renderEngine, Vector3 delta, GameObject<GameEngine3D> lightGameObject, GameObject<GameEngine3D> bokehGameObject) {
        this.delta.set(delta);
        this.lightGameObject = lightGameObject;
        this.bokehGameObject = bokehGameObject;
        this.pointLight      = new PointLight();
        renderEngine.addDynamic(lightGameObject);
//        renderEngine.addDynamic(bokehGameObject);
    }

    private void animate(RenderEngine3D<GameEngine3D> renderEngine) {
        final float deltaTime = Gdx.graphics.getDeltaTime();
        if (lightTimer <= 0f) {
            //lightMode
            // 0, wait
            switch (lightMode) {
                case ON: {
                    resetLightOffTimer();
                    lightMode = LightMode.OFF;//wait for light to go on
                    renderEngine.remove(pointLight, true);
                    {
                        float lightIntensity = calculateIntensity() / LIGHT_MAX_INTENSITY;
                        for (Material m : lightGameObject.instance.materials) {
                            m.set(PBRColorAttribute.createAmbient(new Color(0, 0, 0, 1f)));
                            m.set(PBRColorAttribute.createEmissive(new Color(0, 0, 0, 1f)));
                        }
                    }
//                    if (Debug.isFilter(trader.getName())) {
//                        logger.info("lights off");
//                    }
                }
                break;
                case OFF: {
                    resetLightOnTimer();
                    lightMode = LightMode.ON;//wait for light to go off
                    final float intensity = (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * LIGHT_MAX_INTENSITY);
                    pointLight.setIntensity(intensity);
                    {
                        float           lightIntensity = calculateIntensity() / LIGHT_MAX_INTENSITY;
                        final Attribute emissive       = PBRColorAttribute.createEmissive(new Color(lightIntensity, 0, 0, 1f));
                        for (Material m : lightGameObject.instance.materials) {
//                            m.set(color);
//                            m.set(metallic);
//                            m.set(roughness);
                            m.set(emissive);
                        }
                    }
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
            lightGameObject.instance.transform.setToTranslation(translation.x, translation.y, translation.z);
            lightGameObject.instance.transform.rotate(Vector3.Y, rotation);
            lightGameObject.instance.transform.translate(delta);
            lightGameObject.instance.transform.scale(lightScaling.x, lightScaling.y, lightScaling.z);


            lightGameObject.update();
            Vector3 lightTranslation = new Vector3();
            lightGameObject.instance.transform.getTranslation(lightTranslation);

            bokehGameObject.instance.transform.setToTranslation(translation.x, translation.y, translation.z);
            bokehGameObject.instance.transform.rotate(Vector3.Y, rotation);
            bokehGameObject.instance.transform.translate(delta);
            if (lightMode == LightMode.OFF)
                bokehGameObject.instance.transform.scale(.1f, .1f, .1f);
            else
                bokehGameObject.instance.transform.scale(bokehScaling.x, bokehScaling.y, bokehScaling.z);
            bokehGameObject.update();

            pointLight.set(Color.RED, lightTranslation.x + 0.2f, lightTranslation.y, lightTranslation.z, intensity);
            animate(renderEngine);
        }
    }
}
