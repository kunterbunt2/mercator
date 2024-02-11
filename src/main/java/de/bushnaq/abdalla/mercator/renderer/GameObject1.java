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

package de.bushnaq.abdalla.mercator.renderer;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.scene3d.animation.AnimationControllerHack;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class GameObject1 {

    public final Vector3                 center                 = new Vector3();
    public       BoundingBox             boundingBox            = new BoundingBox();
    public       AnimationControllerHack controller;
    public       ModelInstanceHack       instance;
    public       Object                  interactive;
    public       BoundingBox             transformedBoundingBox = new BoundingBox();

    public GameObject1(final ModelInstanceHack instance, final Object interactive) {
        this.instance    = instance;
        this.interactive = interactive;
        boundingBox      = new BoundingBox();
        instance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(center);
    }

    public void update() {
        transformedBoundingBox.set(boundingBox).mul(instance.transform);
    }
}
