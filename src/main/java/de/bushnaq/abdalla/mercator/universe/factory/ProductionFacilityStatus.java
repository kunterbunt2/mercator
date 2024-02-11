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

package de.bushnaq.abdalla.mercator.universe.factory;

public enum ProductionFacilityStatus {
    CANNOT_AFFORD_ENGINEERS("cannot afford engineers"), NO_ENGINEERS("No engineers"), NO_INPUT_GOODS("Not enough input goods"), NO_PROFIT("No expected profit"), NO_STORAGE("Not enough storage"), OFFLINE("Offline"), PRODUCING("Producing"), RESEARCHING("Researching");

    private String name;

    ProductionFacilityStatus(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
