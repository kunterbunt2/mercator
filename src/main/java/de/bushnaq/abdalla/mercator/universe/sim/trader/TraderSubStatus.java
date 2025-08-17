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

/**
 * subStatus should change in following order
 * TRADER_STATUS_ALIGNING
 * TRADER_STATUS_WAITING_FOR_WAYPOINT
 * TRADER_STATUS_ACCELERATING
 * TRADER_STATUS_DECELERATING
 */
public enum TraderSubStatus {
    TRADER_STATUS_NA("N/A", false),//
    TRADER_STATUS_ALIGNING("Maneuvering", true),//
    TRADER_STATUS_WAITING_FOR_WAYPOINT("In-Queue", false),//
    TRADER_STATUS_ACCELERATING("Accelerating", true),//
    TRADER_STATUS_DECELERATING("Decelerating", true),//
    TRADER_STATUS_DOCKING_ACC("Docking Acc.", false),//
    TRADER_STATUS_DOCKING_DEC("Docking Dec.", false),//
    TRADER_STATUS_DOCKED("Docked", false),//
    TRADER_STATUS_UNDOCKING_ACC("Undocking Acc.", false),//
    TRADER_STATUS_UNDOCKING_DEC("undocking Dec.", false),
    TRADER_STATUS_REQUESTING_UNDOCKING("Req. undock", false);
    private       String  name;
    private final boolean traveling;

    TraderSubStatus(final String name, boolean traveling) {
        this.setName(name);
        this.traveling = traveling;
    }

    public String getName() {
        return name;
    }

    public boolean isTraveling() {
        return traveling;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
