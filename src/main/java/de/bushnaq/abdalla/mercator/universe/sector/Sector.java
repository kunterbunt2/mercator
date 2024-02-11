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

package de.bushnaq.abdalla.mercator.universe.sector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Sector {
    public int    credits         = 0;
    public String name            = null;
    public int    numberOfPlanets = 0;
    public int    type            = 0;

    public Sector() {
    }

    public Sector(final int type, final String name) {
        credits         = 0;
        this.name       = name;
        numberOfPlanets = 0;
        this.type       = type;
    }

}
