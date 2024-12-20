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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.CustomizedSpriteBatch;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;

public class Thruster {
    public static final  float                    LIGHT_MAX_INTENSITY          = 600f;
    public static final  float                    LIGHT_MIN_INTENSITY          = 500f;
    public static final  float                    LIGHT_OFF_DURATION_AVERAGE   = 0.2f;
    public static final  float                    LIGHT_OFF_DURATION_DEVIATION = 0.1f;
    public static final  float                    LIGHT_ON_DURATION            = 0.1f;
    public static final  float                    LIGHT_SIZE                   = .2f;
    final static         Vector3                  xVector                      = new Vector3(1, 0, 0);
    final static         Vector3                  yVector                      = new Vector3(0, 1, 0);
    final static         Vector3                  zVector                      = new Vector3(0, 0, 1);
    private static final float                    PY2                          = 3.14159f / 2;
    public final         GameObject<GameEngine3D> gameObject;
    public final         PointLight               pointLight;
    private final        Vector3                  lightScaling                 = new Vector3(LIGHT_SIZE, LIGHT_SIZE, LIGHT_SIZE);
    private final        float                    rotation;
    private final        RotationDirection        rotationDirection;
    public               Vector3                  delta                        = new Vector3();
    public               Vector3                  direction                    = new Vector3();
    public               int                      lightMode                    = 0;
    public               float                    lightTimer                   = 0;
    private              boolean                  gameObjectAdded              = false;

    public Thruster(RenderEngine3D<GameEngine3D> renderEngine, Vector3 delta, Vector3 direction, RotationDirection rotationDirection, float rotation, GameObject<GameEngine3D> gameObject) {
        this.delta.set(delta);
        this.direction.set(direction);
        this.rotationDirection = rotationDirection;
        this.rotation          = rotation;
        this.gameObject        = gameObject;
        this.pointLight        = new PointLight();
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
//                    renderEngine.remove(pointLight, true);
                    for (Material m : gameObject.instance.materials) {
                        if (m.id.equals("flame.material")) {
                            PBRColorAttribute baseColorFactor = PBRColorAttribute.createBaseColorFactor(new Color(Color.WHITE));
                            m.set(baseColorFactor);
                        }
                    }
                }
                break;
                case 1: {
                    resetLightOnTimer();
                    lightMode = 0;//wait for light to go off
                    final float intensity = (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * LIGHT_MAX_INTENSITY);
                    pointLight.setIntensity(intensity);
//                    renderEngine.add(pointLight, true);
                    for (Material m : gameObject.instance.materials) {
                        if (m.id.equals("flame.material")) {
                            PBRColorAttribute baseColorFactor = PBRColorAttribute.createBaseColorFactor(new Color(Color.WHITE));
                            m.set(baseColorFactor);
                        }
                    }
                }
                break;
            }
        } else {
            lightTimer -= deltaTime;
        }
    }

    public float calculateIntensity() {
        return LIGHT_MIN_INTENSITY + (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * (LIGHT_MAX_INTENSITY - LIGHT_MIN_INTENSITY));
    }

    public void resetLightOffTimer() {
        lightTimer = LIGHT_OFF_DURATION_AVERAGE + LIGHT_OFF_DURATION_DEVIATION / 2 - (LIGHT_OFF_DURATION_DEVIATION * ((float) Math.random()));
    }

    public void resetLightOnTimer() {
        lightTimer = LIGHT_ON_DURATION;
    }

    public boolean update(RenderEngine3D<GameEngine3D> renderEngine, Trader trader, Vector3 translation, float rotation, RotationDirection rotationDirection, RotationAcceleration rotationAcceleration) throws Exception {
        boolean on = false;
//        if (direction.x != 0f || direction.y != 0f || direction.z != 0f)
//        mp3Player.setPositionAndVelocity(position, velocity);
        if (rotationDirection != RotationDirection.NON && (this.rotationDirection == rotationDirection && rotationAcceleration == RotationAcceleration.ACCELERATING || this.rotationDirection != rotationDirection && rotationAcceleration == RotationAcceleration.DECELLERATING)) {
            on = true;
            if (!gameObjectAdded) {
                renderEngine.addDynamic(gameObject);
                renderEngine.add(pointLight, true);
                gameObjectAdded = true;
            }

            final CustomizedSpriteBatch batch = renderEngine.renderEngine25D.batch;
            final Matrix4               m     = new Matrix4();
            {
                //move center of text to center of trader
                m.setToTranslation(translation.x, translation.y, translation.z);
                m.rotate(yVector, rotation);
                //move to the top and back on engine
                m.translate(delta);
                //rotate into the xz layer
                m.rotate(xVector, -90);
                //scale to fit trader engine
            }
            batch.setTransformMatrix(m);
            float cr = (float) Math.random();
            float ar = (float) Math.random();
            Color c  = new Color(cr, cr, cr, ar / 4);
            float z  = (1f - (float) Math.random()) / 2;
            float x  = +(1f - (float) Math.random()) * 2;
            float t  = (float) Math.random();
            batch.setColor(Color.WHITE);
            float thickness = .3f + t / 2;

//            batch.line(renderEngine.getGameEngine().getAtlasManager().systemTextureRegion, 0, 0, 0, direction.x + x, 0, direction.z + z, c, thickness);
            animate(renderEngine);

            gameObject.instance.transform.setToTranslation(translation);
            gameObject.instance.transform.rotate(yVector, rotation);
            gameObject.instance.transform.translate(delta);
            float factor = 2;
            gameObject.instance.transform.rotate(Vector3.Y, this.rotation + factor - (float) Math.random() * factor * 2);
            gameObject.instance.transform.rotate(Vector3.Z, factor - (float) Math.random() * factor * 2);
            gameObject.instance.transform.rotate(Vector3.X, factor - (float) Math.random() * factor * 2);
//            gameObject.instance.transform.scale(scaling.x, scaling.y, scaling.z);
            gameObject.update();
            final float intensity        = calculateIntensity();
            Vector3     lightTranslation = new Vector3();
            gameObject.instance.transform.getTranslation(lightTranslation);
            pointLight.set(Color.WHITE, lightTranslation.x + 0.2f, lightTranslation.y, lightTranslation.z, intensity);
        } else {
            on = false;
            if (gameObjectAdded) {
                renderEngine.remove(pointLight, true);
                renderEngine.removeDynamic(gameObject);
                gameObjectAdded = false;
            }
        }
        return on;
    }
}
