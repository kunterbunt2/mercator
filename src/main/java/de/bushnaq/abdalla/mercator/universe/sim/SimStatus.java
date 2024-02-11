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

package de.bushnaq.abdalla.mercator.universe.sim;

public enum SimStatus {
    DEAD_REASON_NO_FOOD("Dead, no products"), DEAD_REASON_NO_MONEY("Dead, poor"), LIVING("Living"), /*SOCIAL_WELFARE("Social welfare"),*/ STARVING_NO_GOODS("no products"), STARVING_NO_MONEY("poor"), UNKNOWN("Unknown");

    private String name;

    SimStatus(final String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
