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

package de.bushnaq.abdalla.mercator.universe.good;

public enum GoodType {
    FOOD(0, "Food"), G02(0, "Medecine"), G03(0, "Media"), G04(0, "Electronics")/*, G11(1, "G-11"), G12(1, "G-12"), G13(1, "G-13"), G14(1, "G-14")*/;

    private String name;
    private int    technicalLevel;

    GoodType(final int technicalLevel, final String name) {
        this.technicalLevel = technicalLevel;
        this.name           = name;
    }

    public String getName() {
        return name;
    }

    public int getTechnicalLevel() {
        return technicalLevel;
    }
}
