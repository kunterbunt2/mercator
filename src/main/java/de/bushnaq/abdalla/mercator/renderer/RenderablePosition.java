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

import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.util.Location3D;

public class RenderablePosition extends Location3D {

    public ObjectRenderer<GameEngine2D> renderer2D;
    public ObjectRenderer<GameEngine3D> renderer3D;

    public RenderablePosition(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public ObjectRenderer<GameEngine2D> get2DRenderer() {
        return renderer2D;
    }

    public ObjectRenderer<GameEngine3D> get3DRenderer() {
        return renderer3D;
    }

    public void set2DRenderer(final ObjectRenderer<GameEngine2D> renderer2D) {
        this.renderer2D = renderer2D;
    }

    public void set3DRenderer(final ObjectRenderer<GameEngine3D> renderer3D) {
        this.renderer3D = renderer3D;
    }
}
