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

package de.bushnaq.abdalla.mercator.universe.land;

import de.bushnaq.abdalla.mercator.renderer.RenderablePosition;
import de.bushnaq.abdalla.mercator.universe.Universe;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Land extends RenderablePosition {

    private final Universe universe;

    public Land(final float x, final float y, final float z, final Universe universe) {
        super(x, y, z);
        this.universe = universe;
        //		set2DRenderer(new Land2DRenderer(this));
        set3DRenderer(new Land3DRenderer(this));
    }

}
