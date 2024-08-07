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

package de.bushnaq.abdalla.mercator.engine.camera;

/**
 * properties that can change when moving from one camera zoom index to the next
 */
public class CameraProperties {
    public final float distanceXZ;//distance to look-at point
    public final float distanceY;//height of look-at point
    public final float far;//the far clipping plane distance, has to be positive
    public final float focalDistance;
    public final float y;//height of camera

    public CameraProperties(float y, float distanceXZ, float distanceY, float far, float focalDistance) {
        this.y             = y;
        this.distanceXZ    = distanceXZ;
        this.distanceY     = distanceY;
        this.far           = far;
        this.focalDistance = focalDistance;
    }

    public CameraProperties(float y, float distanceXZ, float distanceY, float far) {
        this.y             = y;
        this.distanceXZ    = distanceXZ;
        this.distanceY     = distanceY;
        this.far           = far;
        this.focalDistance = 0;
    }
}
