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

public enum TraderStatus {
    TRADER_STATUS_BUYING("Buy", true),//
    TRADER_STATUS_CANNOT_BUY("X buy", false),//
    TRADER_STATUS_CANNOT_SELL("X sell", false),//
    TRADER_STATUS_RESTING("Rest", true),// resting after selling
    TRADER_STATUS_SELLING("Sell", true),//
    TRADER_STATUS_UNKNOWN("Unknown", false),//
    TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY("Waiting for a good price to buy...", true),//
    TRADER_STATUS_WAITING_TO_SELL("Waiting to sell...", true);

    private String  displayName;
    private boolean good;

    TraderStatus(final String displayName, final boolean good) {
        this.setDisplayName(displayName);
        this.setGood(good);
    }

    public String getDisplayName() {
        return displayName;
    }


    public boolean isGood() {
        return good;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setGood(final boolean good) {
        this.good = good;
    }

}

