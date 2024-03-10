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

import de.bushnaq.abdalla.mercator.universe.good.Good;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {
    public static final  int    MAX_ENGINE_SPEED = 100;
    public static final  float  MIN_ENGINE_SPEED = .1f;
    private static final float  ENGINE_FORCE     = 3f;//newton
    private final        Logger logger           = LoggerFactory.getLogger(this.getClass());
    private final        Trader trader;
    //    private              float  acceleration;
    private              float  engineSpeed      = MIN_ENGINE_SPEED;

    public Engine(Trader trader) {
        this.trader = trader;
    }

    public void advanceInTime(float realTimeDelta) {
        if (trader.subStatus == TraderSubStatus.TRADER_STATUS_TRAVELLING && realTimeDelta != 0) {
            calculateEngineSpeed(realTimeDelta);
            float delta = getEngineSpeed() * realTimeDelta * 10;
            trader.destinationWaypointDistanceProgress += delta;
            trader.destinationPlanetDistanceProgress += delta;
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
        if (trader.subStatus == TraderSubStatus.TRADER_STATUS_TRAVELLING) {
            if (trader.targetWaypoint == null || trader.destinationWaypointDistance == 0) {
                engineSpeed = MIN_ENGINE_SPEED;
            } else {
                float acceleration = calculateAcceleration();
                float progress     = trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance;
                if (progress < 0.5) {
                    //accelerating
                    engineSpeed = Math.min(engineSpeed + acceleration * timeDelta * 10, MAX_ENGINE_SPEED);
//                    if (trader.getName().equals("T-1"))
//                        logger.info("engineSpeed=" + engineSpeed + " acceleration=" + acceleration);
//                    if (getName().equals("T-25")) logger.info("engine acceleration currentMaxEngineSpeed=" + engineSpeed);
                } else /*if (destinationPlanetDistance - destinationPlanetDistanceProgress <= ACCELLERATION_DISTANCE)*/ {
                    //deceleration
                    engineSpeed = Math.max(engineSpeed - acceleration * timeDelta * 10, MIN_ENGINE_SPEED);
//                    if (getName().equals("T-25")) logger.info("engine deceleration currentMaxEngineSpeed=" + engineSpeed);
                }
            }
        }
    }

//    public float getAcceleration() {
//        return acceleration;
//    }

    public float getEngineSpeed() {
        return engineSpeed;
    }

    public void start() {
        engineSpeed = MIN_ENGINE_SPEED;
    }
}
