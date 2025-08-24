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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * thrusters for rotational maneuvering
 */
public class ManeuveringSystem {
    public static final  float                MAX_ROTATION_SPEED   = 15;
    public static final  float                MIN_ROTATION_SPEED   = 0.1f;
    private static final float                THRUSTER_FORCE       = 0.8f;//newton
    final static         Vector2              zVector              = new Vector2(0, -1);
    private              float                endRotation          = 90;
    private final        Logger               logger               = LoggerFactory.getLogger(this.getClass());
    private              OggPlayer            oggPlayer;
    private final        float[]              position             = new float[3];
    private              float                progress             = 0;
    public               float                rotation             = 270;//0 degrees orientation
    private              RotationAcceleration rotationAcceleration = RotationAcceleration.ACCELERATING;
    private              RotationDirection    rotationDirection;
    public               float                rotationSpeed        = 0;
    private              float                startRotation        = 1000;
    private final        List<Thruster>       thrusters            = new ArrayList<>();
    private final        Trader               trader;
    private final        float[]              velocity             = new float[3];

    public ManeuveringSystem(Trader trader) {
        this.trader = trader;

    }

    public void advanceInTime(float timeDelta) {
        if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ALIGNING) {
            progress      = calculateRotationProgress();
            rotationSpeed = calculateRotationSpeed(timeDelta);
            if (endRotation - rotation > 180) {
                rotationDirection = RotationDirection.CLOCKWISE;
                rotation -= rotationSpeed * timeDelta * 5;//other way around
//                if (Debug.isFilter(trader.getName()))
//                    logger.info(String.format("**** clockwise1 startRotation=%f rotation=%f endRotation=%f, progress=%f", startRotation, rotation, endRotation, progress));
            } else if (endRotation - rotation > 0) {
                rotationDirection = RotationDirection.COUNTER_CLOCKWISE;
                rotation += rotationSpeed * timeDelta * 5;
//                if (Debug.isFilter(trader.getName()))
//                    logger.info(String.format("**** counter-clockwise1 startRotation=%f rotation=%f endRotation=%f, progress=%f", startRotation, rotation, endRotation, progress));
            } else if (endRotation - rotation < -180) {
                rotationDirection = RotationDirection.COUNTER_CLOCKWISE;
                rotation += rotationSpeed * timeDelta * 5;
//                if (Debug.isFilter(trader.getName()))
//                    logger.info(String.format("**** counter-clockwise2 startRotation=%f rotation=%f endRotation=%f, progress=%f", startRotation, rotation, endRotation, progress));
            } else {
                rotationDirection = RotationDirection.CLOCKWISE;
                rotation -= rotationSpeed * timeDelta * 5;
//                if (Debug.isFilter(trader.getName()))
//                    logger.info(String.format("**** clockwise2 startRotation=%f rotation=%f endRotation=%f, progress=%f", startRotation, rotation, endRotation, progress));
            }

            normalizeRotation();
            endRotation();
        } else rotationDirection = RotationDirection.NON;
    }

    float calculateAngleDifference(float end, float start) {

        if (end - start > 180) {
            //359 - 1 -> 360 + 1 - 359 = 2
//            if (Debug.isFilter(trader.getName())) {
//                logger.info(String.format("case 1 startRotation=%f rotation=%f endRotation=%f progress0%f", startRotation, rotation, endRotation, progress));
//            }
            return 360 + start - end;//let's turn the other way as it is shorter
        } else if (end - start > 0) {
            //179 - 1 -> 179- 1 = 178
//            if (Debug.isFilter(trader.getName())) {
//                logger.info(String.format("case 2 startRotation=%f rotation=%f endRotation=%f progress0%f", startRotation, rotation, endRotation, progress));
//            }
            return end - start;
        } else if (end - start < -180) {
            //1 - 359 -> 360 + 1 - 359 = 2
//            if (Debug.isFilter(trader.getName())) {
//                logger.info(String.format("case 3 startRotation=%f rotation=%f endRotation=%f progress0%f", startRotation, rotation, endRotation, progress));
//            }
            return 350 + end - start;//let's turn the other way as it is shorter
        } else {
            //1 - 179 -> 179- 1 = 178
//            if (Debug.isFilter(trader.getName())) {
//                logger.info(String.format("case 4 startRotation=%f rotation=%f endRotation=%f progress0%f", startRotation, rotation, endRotation, progress));
//            }
            return start - end;
        }
    }

    private float calculateRotationAcceleration() {
        float amount = 0;
        for (final Good g : trader.getGoodList()) {
            amount += g.getAmount();
        }
        return THRUSTER_FORCE / amount;
    }

    private float calculateRotationProgress() {
        float p1 = calculateAngleDifference(startRotation, rotation);
        float p2 = calculateAngleDifference(startRotation, endRotation);
        float p  = p1 / p2;
//        if (Debug.isFilter(trader.getName())) {
//            logger.info(String.format("progress=%f %f/%f", p, p1, p2));
//        }
        return p;
    }

    private float calculateRotationSpeed(float timeDelta) {
        float acceleration = calculateRotationAcceleration();
        if (progress < 0.5) {
            //accelerating
            rotationAcceleration = RotationAcceleration.ACCELERATING;
//            if (Debug.isFilter(trader.getName())) logger.info("rotation acceleration");
            return Math.min(rotationSpeed + acceleration * timeDelta * 10, MAX_ROTATION_SPEED);
        } else {
            //deceleration
            rotationAcceleration = RotationAcceleration.DECELLERATING;
//            if (Debug.isFilter(trader.getName())) logger.info("rotation deceleration");
            return Math.max(rotationSpeed - acceleration * timeDelta * 10, MIN_ROTATION_SPEED);
        }
    }

    public void create(AudioEngine audioEngine) {
        try {
            oggPlayer = audioEngine.createAudioProducer(OggPlayer.class);
            oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/thrusters_loop.ogg"));
            oggPlayer.setGain(100.0f);
            oggPlayer.setAmbient(false);
            oggPlayer.setLoop(true);
        } catch (OpenAlException e) {
            throw new RuntimeException(e);
        }
    }

//    public void endRotation() {
//        startRotation = 1000;
//    }

    public void endRotation() {
        boolean reached = progress >= 1.0f;
        if (reached) {
//            if (Debug.isFilter(trader.getName()))
//                logger.info(String.format("**** startRotation=%f rotation=%f endRotation=%f progress=%f", startRotation, rotation, endRotation, progress));
            rotation = endRotation;
//            if (Debug.isFilter(trader.getName()))
//                logger.info("end");
            //TODO move to Navigator.reachedWaypoint?
            if (trader.navigator.reachedDestination()) {
                //case 3
                if (Debug.isFilterTrader(trader.getName()))
                    System.out.printf("reached destination %s port %s\n", trader.navigator.nextWaypoint.city.getName(), trader.navigator.destinationPlanet.getName());
                //dock
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_DOCKING);
                trader.communicationPartner.requestDocking(trader.navigator.nextWaypoint.city);
            } else if (trader.navigator.reachedTransit()) {
                //case 2
                if (Debug.isFilterTrader(trader.getName()))
                    System.out.printf("reached transit %s port %s\n", trader.navigator.nextWaypoint.city.getName(), trader.navigator.destinationPlanet.getName());
                //transition
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_TRANSITION);
                trader.communicationPartner.requestTransition(trader.navigator.nextWaypoint.city);
            } else {
                //wait
                if (Debug.isFilterTrader(trader.getName()))
                    System.out.printf("reached waypoint %s port %s\n", trader.navigator.nextWaypoint.getName(), trader.navigator.destinationPlanet.getName());
                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
            }
//            if (trader.navigator.nextWaypoint.city != null) {
//                if (trader.navigator.reachedDestination()) {
//                    //dock
//                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_DOCKING);
//                    trader.communicationPartner.requestDocking(trader.navigator.nextWaypoint.city);
//                } else if (trader.navigator.reachedTransit()) {
//                    //transition
//                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_TRANSITION);
//                    trader.communicationPartner.requestTransition(trader.navigator.nextWaypoint.city);
//                }
//            } else {
//                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
//            }
        } else {
//            if (Debug.isFilter(trader.getName()))
//                logger.info("not-end");
        }
    }

    public List<Thruster> getThrusters() {
        return thrusters;
    }

    private void normalizeRotation() {
        if (rotation < 0) rotation += 360;
        if (rotation > 360) rotation -= 360;
    }

    /**
     * Start the maneuvering to align with target waypoint
     * Should only be called after setting Trader.subStatus to TRADER_STATUS_ALIGNING
     */
    public void startRotation() {
        if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ALIGNING) {
            Waypoint    targetWaypoint = trader.navigator.waypointList.get(trader.navigator.destinationWaypointIndex).waypoint;
            final float scalex         = (targetWaypoint.x - trader.navigator.previousWaypoint.x);
            final float scaley         = (targetWaypoint.y - trader.navigator.previousWaypoint.y);
            final float scalez         = (targetWaypoint.z - trader.navigator.previousWaypoint.z);
            startRotation = rotation;
            Vector2 d = new Vector2(scalex, scalez);
            endRotation = zVector.angleDeg(d);
            progress    = 0f;
//            if (Debug.isFilter(trader.getName()))
//                logger.info(String.format("**** %s->%s startRotation=%f rotation=%f endRotation=%f", trader.sourceWaypoint.name, targetWaypoint.name, startRotation, rotation, endRotation));
        }
    }

    public void updateThrusters(final RenderEngine3D<GameEngine3D> renderEngine, final Vector3 translation) throws Exception {
        if (renderEngine.getCamera().frustum.pointInFrustum(translation.x, translation.y, translation.z)) {
            boolean on = false;
            for (Thruster thruster : thrusters) {
                if (thruster.update(renderEngine, trader, translation, rotation, rotationDirection, rotationAcceleration))
                    on = true;
            }
            if (on) {
                position[0] = translation.x;
                position[1] = translation.y;
                position[2] = translation.z;
                velocity[0] = trader.speed.x;
                velocity[1] = 0;
                velocity[2] = trader.speed.z;
                oggPlayer.setPositionAndVelocity(position, velocity);
                try {
                    if (renderEngine.getCamera().position.dst(translation) < 1000) {
//                        if (Debug.isFilter(trader.getName()))
//                            logger.info("play");
                        oggPlayer.play();
                    } else {
//                        if (Debug.isFilter(trader.getName()))
//                            logger.info("pause");
                        oggPlayer.pause();
                    }
                } catch (OpenAlException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
//                    if (Debug.isFilter(trader.getName()))
//                        logger.info("pause");
                    oggPlayer.pause();
                } catch (OpenAlException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
