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

public class AnnulusSegment {
    float maxAngle;
    float maxRadius;
    float minAngle;
    float minRadius;
    float x;
    float y;

    public AnnulusSegment(final float x, final float y, final float minRadius, final float maxRadius, final float minAngle, final float maxAngle) {
        this.x         = x;
        this.y         = y;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minAngle  = minAngle;
        this.maxAngle  = maxAngle;
    }

    public boolean contains(final float x, final float y) {
        final float h     = y - this.y;
        final float w     = x - this.x;
        final float r     = (float) Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));
        float       angle = (float) Math.asin(w / r);
        if (h > 0) {
            angle = (float) Math.PI - angle;
        }
        if (w < 0) {
            angle += Math.PI;
        }
        if (angle >= minAngle && angle <= maxAngle && r > minRadius && r <= maxRadius)
            return true;
        return false;
    }

    public void setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
}
