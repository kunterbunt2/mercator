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
import de.bushnaq.abdalla.mercator.renderer.AtlasManager;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * thrusters for rotational maneuvering
 */
public class ManeuveringSystem {
    public static final  float          MAX_ROTATION_SPEED = 15;
    public static final  float          MIN_ROTATION_SPEED = 0.1f;
    final static         Vector2        zVector            = new Vector2(0, -1);
    private static final float          THRUSTER_FORCE     = 0.8f;//newton
    private final        Logger         logger             = LoggerFactory.getLogger(this.getClass());
    private final        float[]        position           = new float[3];
    private final        List<Thruster> thrusters          = new ArrayList<>();
    private final        Trader         trader;
    private final        float[]        velocity           = new float[3];
    public               float          rotation           = 270;//0 degrees orientation
    public               float          rotationSpeed      = 0;
    float             endRotation   = 90;
    OggPlayer         oggPlayer;
    float             progress      = 0;
    RotationDirection rotationDirection;
    float             startRotation = 1000;
    private RotationAcelleration rotationAcelleration = RotationAcelleration.ACCELERATING;

    public ManeuveringSystem(Trader trader) {
        this.trader = trader;

    }

    public void advanceInTime(float timeDelta) {
        if (trader.subStatus == TraderSubStatus.TRADER_STATUS_ALIGNING) {
            progress      = calculateRotationProgress();
            rotationSpeed = calculateRotationSpeed(timeDelta);


            if (endRotation - rotation > 180) {
                rotationDirection = RotationDirection.CLOCKWISE;
                rotation -= rotationSpeed * timeDelta * 10;//other way around
            } else if (endRotation - rotation > 0) {
                rotationDirection = RotationDirection.COUNTER_CLOCKWISE;
                rotation += rotationSpeed * timeDelta * 10;
            } else {
                rotationDirection = RotationDirection.CLOCKWISE;
                rotation -= rotationSpeed * timeDelta * 10;
            }
//            if (getName().equals("T-25")) logger.info(String.format("rotationSpeed=%f rotation=%f endRotation=%f", rotationSpeed, rotation, endRotation));

            normalizeRotation();
        } else rotationDirection = RotationDirection.NON;
    }

    float calculateAngleDifference(float end, float start) {
        if (end - start > 180) {
            return 360 + start - end;//lets turn the other way is it is shorter
        } else {
            return Math.abs(end - start);
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
        return calculateAngleDifference(rotation, startRotation) / calculateAngleDifference(endRotation, startRotation);
    }

    private float calculateRotationSpeed(float timeDelta) {
//        float deltaRealTime = Gdx.graphics.getDeltaTime();
        float acceleration = calculateRotationAcceleration();
//        float a1           = calculateAngleDifference(rotation, startRotation);
//        float a2           = calculateAngleDifference(endRotation, startRotation);
        if (progress < 0.5) {
            //accelerating
            rotationAcelleration = RotationAcelleration.ACCELERATING;
//            if (getName().equals("T-25")) logger.info("rotation acceleration");
            return Math.min(rotationSpeed + acceleration * timeDelta * 10, MAX_ROTATION_SPEED);
        } else {
            //deceleration
            rotationAcelleration = RotationAcelleration.DECELLERATING;
//            if (getName().equals("T-25")) logger.info("rotation deceleration");
            return Math.max(rotationSpeed - acceleration * timeDelta * 10, MIN_ROTATION_SPEED);
        }
    }

    public void create(AudioEngine audioEngine) {
        try {
            oggPlayer = audioEngine.createAudioProducer(OggPlayer.class);
            oggPlayer.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/thrusters_loopwav-14699.ogg"));
            oggPlayer.setGain(150.0f);
            oggPlayer.setAmbient(false);
        } catch (OpenAlException e) {
            throw new RuntimeException(e);
        }
    }

    public void endRotation() {
        startRotation = 1000;
    }

    public List<Thruster> getThrusters() {
        return thrusters;
    }

    private void normalizeRotation() {
        if (rotation < 0) rotation += 360;
        if (rotation > 360) rotation -= 360;
    }

    public boolean reachedTarget() {
        return progress >= 1.0;
    }

    public void startRotation() {
        if (startRotation == 1000) {
            Waypoint    targetWaypoint = trader.waypointList.get(trader.destinationWaypointIndex).waypoint;
            final float scalex         = (targetWaypoint.x - trader.sourceWaypoint.x);
            final float scaley         = (targetWaypoint.y - trader.sourceWaypoint.y);
            final float scalez         = (targetWaypoint.z - trader.sourceWaypoint.z);
            startRotation = rotation;
            Vector2 d = new Vector2(scalex, scalez);
            endRotation = zVector.angleDeg(d);
        }
    }

    public void updateThrusters(final RenderEngine3D<GameEngine3D> renderEngine, final Vector3 translation) throws Exception {
        if (renderEngine.getCamera().frustum.pointInFrustum(translation.x, translation.y, translation.z)) {
            boolean on = false;
            for (Thruster thruster : thrusters) {
                if (thruster.update(renderEngine, trader, translation, rotation, rotationDirection, rotationAcelleration))
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
//                        if (trader.getName().equals("T-25"))
//                            logger.info("play");
                        oggPlayer.play();
                    } else {
//                        if (trader.getName().equals("T-25"))
//                            logger.info("pause");
                        oggPlayer.pause();
                    }
                } catch (OpenAlException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
//                    if (trader.getName().equals("T-25"))
//                        logger.info("pause");
                    oggPlayer.pause();
                } catch (OpenAlException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
