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

package de.bushnaq.abdalla.mercator.universe.ring;

import de.bushnaq.abdalla.mercator.renderer.RenderablePosition;
import de.bushnaq.abdalla.mercator.universe.Universe;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Ring extends RenderablePosition {
    public static final int      SECTOR_SIZE = 1024;
    public              float    radius;
    public              Universe universe;
    int segments;
    int width;

    public Ring(final Universe universe) {
        super(0, 0, 0);
        this.universe = universe;
        width         = universe.size * 2;
        segments      = 60;
        final float alpha = 360.0f / segments;
        radius = (float) (SECTOR_SIZE / (2 * Math.sin(Math.PI * alpha / (2 * 180))));
        set3DRenderer(new Ring3DRenderer(this));
    }

}
