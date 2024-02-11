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

package de.bushnaq.abdalla.mercator.util;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Location3D {
    // ---DATA
    public float x;
    public float y;
    public float z;

    public Location3D() {
        x = y = z = 0;
    }

    public Location3D(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float queryDistance(final Location3D aLocation) {
        return (float) Math.sqrt(Math.pow(x - aLocation.x, 2) + Math.pow(y - aLocation.y, 2) + Math.pow(z - aLocation.z, 2));
    }
}
