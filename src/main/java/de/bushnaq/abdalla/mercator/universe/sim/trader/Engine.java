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
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.util.Debug;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.CLOSING;
import static de.bushnaq.abdalla.mercator.universe.planet.DockingDoor.DockingDoorState.LOWERING;
import static de.bushnaq.abdalla.mercator.universe.sim.trader.Trader3DRenderer.TRADER_DOCKING_HEIGHT;
import static de.bushnaq.abdalla.mercator.universe.sim.trader.Trader3DRenderer.TRADER_FLIGHT_HEIGHT;


public class Engine {
    public static final  float                    ENGINE_TO_REALITY_FACTOR     = 10;
    public static final  float                    LIGHT_MAX_INTENSITY          = 600f;
    public static final  float                    LIGHT_MIN_INTENSITY          = 500f;
    public static final  float                    LIGHT_OFF_DURATION_AVERAGE   = 0.2f;
    public static final  float                    LIGHT_OFF_DURATION_DEVIATION = 0.1f;
    public static final  float                    LIGHT_ON_DURATION            = 0.1f;
    public static final  float                    LIGHT_SIZE                   = .2f;
    public static final  int                      MAX_ENGINE_SPEED             = 100;
    public static final  float                    MIN_ENGINE_SPEED             = .1f;
    final static         Vector3                  yVector                      = new Vector3(0, 1, 0);
    private static final float                    ENGINE_FORCE                 = 3f;//newton
    //    private static final float                    MAX_TIME_DELTA               = 0.1f;//everything above will be ignored as a glitch
    private static final float                    PY2                          = 3.14159f / 2;
    public final         PointLight               pointLight;
    private final        Logger                   logger                       = LoggerFactory.getLogger(this.getClass());
    private final        float[]                  position                     = new float[3];
    private final        Trader                   trader;
    private final        float[]                  velocity                     = new float[3];
    public               int                      lightMode                    = 0;
    //    public float getAcceleration() {
//        return acceleration;
//    }
    public               float                    lightTimer                   = 0;
    private              float                    engineSpeed                  = MIN_ENGINE_SPEED;
    private              GameObject<GameEngine3D> gameObject;
    private              boolean                  gameObjectAdded              = false;
    private              OggPlayer                oggPlayer;

    public Engine(Trader trader) {
        this.trader     = trader;
        this.pointLight = new PointLight();
    }

    public void advanceInTime(float realTimeDelta) {
//        if (realTimeDelta < MAX_TIME_DELTA)
        if ((trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DECELERATING) && realTimeDelta != 0) {
            calculateEngineSpeed(realTimeDelta);
            float delta = getEngineSpeed() * realTimeDelta * 10;
            trader.destinationWaypointDistanceProgress += delta;
            trader.destinationPlanetDistanceProgress += delta;
        } else if ((trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_DEC) && realTimeDelta != 0) {
            //docking
            calculateEngineSpeed(realTimeDelta);
            float delta = getEngineSpeed() * realTimeDelta * 10;
            trader.y -= delta;
        } else if ((trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_DEC) && realTimeDelta != 0) {
            //undocking
            calculateEngineSpeed(realTimeDelta);
            float delta = getEngineSpeed() * realTimeDelta * 10;
            trader.y += delta;
        }
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
                            PBRColorAttribute baseColorFactor = PBRColorAttribute.createBaseColorFactor(new Color(Color.LIGHT_GRAY));
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

    float calculateAcceleration() {
        //todo only needed to be calculated once every time amount of goods change
        //max engine speed depends on how much goods we are carrying.
        float amount = 0;
        for (final Good g : trader.getGoodList()) {
            amount += g.getAmount();
        }
        return ENGINE_FORCE / amount;
    }

    void calculateEngineSpeed(float timeDelta) {
        // are we paused?
        if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DECELERATING) {
            if (trader.targetWaypoint == null || trader.destinationWaypointDistance == 0) {
                engineSpeed = MIN_ENGINE_SPEED;
                if (Debug.isFilterTrader(trader.getName()))
                    logger.info("*** min engine speed");
            } else {
                float acceleration = calculateAcceleration();
                float progress     = trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance;
                if (progress < 0.5f) {
                    //accelerating
//                    if (trader.subStatus == TraderSubStatus.TRADER_STATUS_DECELERATING)
//                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ACCELERATING);
                    engineSpeed = Math.min(engineSpeed + acceleration * timeDelta * 10, MAX_ENGINE_SPEED);
//                    if (Debug.isFilter(trader.getName()))
//                        logger.info("engineSpeed=" + engineSpeed + " acceleration=" + acceleration);
//                    if (Debug.isFilterTrader(trader.getName())) logger.info("engine acceleration currentMaxEngineSpeed=" + engineSpeed);
                } else if (progress < 1.0f) {
                    //deceleration
                    if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING) {
                        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DECELERATING);
                        if (trader.targetWaypoint == trader.destinationPlanet)
                            trader.destinationPlanet.dockingDoors.setDockingDoorStatus(LOWERING);
                    }
                    engineSpeed = Math.max(engineSpeed - acceleration * timeDelta * 10, MIN_ENGINE_SPEED);
//                    if (Debug.isFilterTrader(trader.getName())) logger.info("engine deceleration currentMaxEngineSpeed=" + engineSpeed);
                } else {
                    //reached the port
                    //after docking
//                    if (trader.targetWaypoint.city == null) {
//                        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKING_ACC);
//                    } else
//                        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKING_ACC);
                }
            }
        } else if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_DEC) {
            // descend to dock
            float acceleration = calculateAcceleration();
            float progress     = (TRADER_FLIGHT_HEIGHT - trader.y) / (TRADER_FLIGHT_HEIGHT - TRADER_DOCKING_HEIGHT);
            if (progress < 0.5) {
                //accelerating
//                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKING_ACC);
                engineSpeed = Math.min(engineSpeed + acceleration * timeDelta * 10, MAX_ENGINE_SPEED);
//                    if (Debug.isFilter(trader.getName()))
//                        logger.info("engineSpeed=" + engineSpeed + " acceleration=" + acceleration);
//                if (Debug.isFilterTrader(trader.getName())) logger.info(String.format("engine acceleration engineSpeed=%f height=%f", engineSpeed, trader.y));
            } else if (progress < 1.0f) {
                //deceleration
                if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_ACC) {
                    trader.destinationPlanet.dockingDoors.setDockingDoorStatus(CLOSING);
                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKING_DEC);
                }
                engineSpeed = Math.max(engineSpeed - acceleration * timeDelta * 10, MIN_ENGINE_SPEED);
//                if (Debug.isFilterTrader(trader.getName())) logger.info(String.format("engine deceleration engineSpeed=%f height=%f", engineSpeed, trader.y));
            } else {
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKED);
            }
        } else if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_DEC) {
            // ascend to undock
            float acceleration = calculateAcceleration();
            float progress     = (trader.y - TRADER_DOCKING_HEIGHT) / (TRADER_FLIGHT_HEIGHT - TRADER_DOCKING_HEIGHT);
            if (progress < 0.5) {
                //accelerating
//                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC);
//                if (Debug.isFilterTrader(trader.getName())) logger.info(String.format("pre undocking engine acceleration engineSpeed=%f progress=%f height=%f", engineSpeed, progress, trader.y));
                engineSpeed = Math.min(engineSpeed + acceleration * timeDelta * 10, MAX_ENGINE_SPEED);
//                if (Debug.isFilterTrader(trader.getName()))
//                    logger.info("engineSpeed=" + engineSpeed + " acceleration=" + acceleration);
//                if (Debug.isFilterTrader(trader.getName())) logger.info(String.format("undocking engine acceleration=%f timeDelta0%f engineSpeed=%f progress=%f height=%f", acceleration, timeDelta, engineSpeed, progress, trader.y));
            } else if (progress < 1.0f) {
                //deceleration
                if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC) {
                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_UNDOCKING_DEC);
                }
                engineSpeed = Math.max(engineSpeed - acceleration * timeDelta * 10, MIN_ENGINE_SPEED);
//                if (Debug.isFilterTrader(trader.getName())) logger.info(String.format("undocking engine deceleration=%f timeDelta0%f engineSpeed=%f progress=%f height=%f", acceleration, timeDelta, engineSpeed, progress, trader.y));
            } else {
                //after undocking
                trader.destinationPlanet.dockingDoors.setDockingDoorStatus(CLOSING);
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ALIGNING);
                trader.getThrusters().startRotation();
                trader.communicationPartner.requestUndocking();
            }
        }
    }

    public float calculateIntensity() {
        return LIGHT_MIN_INTENSITY + (float) Math.abs(Math.sin(PY2 * (lightTimer / LIGHT_ON_DURATION)) * (LIGHT_MAX_INTENSITY - LIGHT_MIN_INTENSITY));
    }

    public void create(RenderEngine3D<GameEngine3D> renderEngine) {
        gameObject = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.flame.scene.model), trader);
        try {
            oggPlayer = renderEngine.getGameEngine().audioEngine.createAudioProducer(OggPlayer.class);
            oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/large-rocket-engine-86240.ogg"));
            oggPlayer.setGain(100.0f);
            oggPlayer.setAmbient(false);
            oggPlayer.setLoop(true);
        } catch (OpenAlException e) {
            throw new RuntimeException(e);
        }
    }

    public float getEngineSpeed() {
        return engineSpeed;
    }

    public void resetLightOffTimer() {
        lightTimer = LIGHT_OFF_DURATION_AVERAGE + LIGHT_OFF_DURATION_DEVIATION / 2 - (LIGHT_OFF_DURATION_DEVIATION * ((float) Math.random()));
    }

    public void resetLightOnTimer() {
        lightTimer = LIGHT_ON_DURATION;
    }

    public void start() {
        engineSpeed = MIN_ENGINE_SPEED;
    }

    public void update(RenderEngine3D<GameEngine3D> renderEngine, Vector3 translation) throws Exception {
        if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DECELERATING) {
            float direction;
            if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING) {
                direction = 1;
            } else {
                direction = -1;
            }
            position[0] = translation.x;
            position[1] = translation.y;
            position[2] = translation.z;
            velocity[0] = trader.speed.x;
            velocity[1] = 0;
            velocity[2] = trader.speed.z;
            oggPlayer.setPositionAndVelocity(position, velocity);
            if (renderEngine.getCamera().position.dst(translation) < 1000) {
                oggPlayer.play();
            } else {
                oggPlayer.pause();
            }
            if (!gameObjectAdded) {
                renderEngine.addDynamic(gameObject);
                renderEngine.add(pointLight, true);
                gameObjectAdded = true;
            }
            animate(renderEngine);
            gameObject.instance.transform.setToTranslation(translation);
            gameObject.instance.transform.rotate(yVector, trader.getThrusters().rotation);
            float factor = 4;
            gameObject.instance.transform.translate(0, 0, direction * Trader3DRenderer.TRADER_SIZE_Z / 2);//position ion beam
            gameObject.instance.transform.rotate(Vector3.Y, direction * -90 + factor - (float) Math.random() * factor * 2);
            gameObject.instance.transform.rotate(Vector3.Z, factor - (float) Math.random() * factor * 2);
            gameObject.instance.transform.rotate(Vector3.X, factor - (float) Math.random() * factor * 2);
            gameObject.instance.transform.scale(4, 4, 4);
            gameObject.update();
            final float intensity        = calculateIntensity();
            Vector3     lightTranslation = new Vector3();
            gameObject.instance.transform.getTranslation(lightTranslation);
            pointLight.set(Color.WHITE, lightTranslation.x + 0.2f, lightTranslation.y, lightTranslation.z, intensity);
        }
//        else
//            if (trader.subStatus == TraderSubStatus.TRADER_STATUS_DECELERATING)
//        {
//            if (renderEngine.getCamera().position.dst(translation) < 1000) {
//                oggPlayer.play();
//            } else {
//                oggPlayer.pause();
//            }
//            if (!gameObjectAdded) {
//                renderEngine.addDynamic(gameObject);
//                renderEngine.add(pointLight, true);
//                gameObjectAdded = true;
//            }
//            animate(renderEngine);
//            gameObject.instance.transform.setToTranslation(translation);
//            gameObject.instance.transform.rotate(yVector, trader.getThrusters().rotation);
//            gameObject.instance.transform.translate(0, 0, -Trader3DRenderer.TRADER_SIZE_Z / 2);
//            float factor = 4;
//            gameObject.instance.transform.rotate(Vector3.Y, 90 + factor - (float) Math.random() * factor * 2);
//            gameObject.instance.transform.rotate(Vector3.Z, factor - (float) Math.random() * factor * 2);
//            gameObject.instance.transform.rotate(Vector3.X, factor - (float) Math.random() * factor * 2);
//            gameObject.instance.transform.scale(4, 4, 4);
//            gameObject.update();
//            final float intensity        = calculateIntensity();
//            Vector3     lightTranslation = new Vector3();
//            gameObject.instance.transform.getTranslation(lightTranslation);
//            pointLight.set(Color.WHITE, lightTranslation.x + 0.2f, lightTranslation.y, lightTranslation.z, intensity);
//        }
        else {
            if (gameObjectAdded) {
                renderEngine.remove(pointLight, true);
                renderEngine.removeDynamic(gameObject);
                gameObjectAdded = false;
            }

        }
    }
}
