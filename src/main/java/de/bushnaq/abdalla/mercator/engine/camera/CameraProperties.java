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
 *
 * @param cameraY             height of camera
 * @param distanceXZ          distance to look-at point
 * @param farClippingDistance the far clipping plane distance, has to be positive
 * @param lookatY             height of look-at point
 */
public record CameraProperties(float cameraY, float distanceXZ, float fieldOfView, float lookatY,
                               float farClippingDistance, float focalDistance) {

    public CameraProperties(float cameraY, float distanceXZ, float fieldOfView, float lookatY, float farClippingDistance) {
        this(cameraY, distanceXZ, fieldOfView, lookatY, farClippingDistance, 0);
    }
}
