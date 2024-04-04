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

package de.bushnaq.abdalla.mercator.universe.path;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;

public class JumpGate2DRenderer extends ObjectRenderer<GameEngine2D> {
    static final         Color JUMPGATE_COLOR = new Color(0.275f, 0.314f, 0.314f, 1.0f);
    private static final float MIN_RADIUS     = Planet2DRenderer.PLANET_SIZE * 4.0f + 3;
    // static final Color SELECTED_JUMPGATE_COLOR = Color.ORANGE;
    Path jumpGate;

    public JumpGate2DRenderer(final Path jumpGate) {
        this.jumpGate = jumpGate;
    }

    private void drawJumpGate(final float x, final float z, final Path jumpGate, final RenderEngine2D<GameEngine2D> renderEngine) {
        final Vector2 target = new Vector2(jumpGate.target.x, jumpGate.target.z);
        final Vector2 start  = new Vector2(jumpGate.source.x, jumpGate.source.z);
//		final Vector2 line = target.sub(start);
//		final float length = line.len();
//		final float tx = jumpGate.target.x;
//		final float tz = jumpGate.target.z;
//		final float x1 = x + (tx - x) * MIN_RADIUS / length;
//		final float z1 = z + (tz - z) * MIN_RADIUS / length;
//		final float tx2 = x + (tx - x) / 2;
//		final float tz2 = z + (tz - z) / 2;
        Color color;
        // float thickness = jumpGate.usage;
        float thickness = 1.3f * renderEngine.getGameEngine().renderEngine.camera.zoom;
        if (jumpGate.closed) {
            color = Color.RED;
        } else if (jumpGate.selected) {
            color     = GameEngine2D.SELECTED_COLOR;
            thickness = 3.3f * renderEngine.getGameEngine().renderEngine.camera.zoom;
            //		} else if ( jumpGate.planet.sector == jumpGate.targetPlanet.sector) {
            //			color = renderMaster.distinctiveTransparentColorlist.get(jumpGate.planet.sector.type);
        } else {
            color = JUMPGATE_COLOR;
        }
        final Color c = new Color(color);
        c.a = 0.45f;
        renderEngine.getGameEngine().renderEngine.batch.line(renderEngine.getGameEngine().atlasManager.dottedLineTextureRegion, start.x, 0, start.y, target.x, 0, target.y, c, thickness);

    }

    @Override
    public void render(final float x, final float z, final RenderEngine2D<GameEngine2D> renderEngine, final int index, final boolean selected) {
        drawJumpGate(x, z, jumpGate, renderEngine);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        // TODO Auto-generated method stub
        return false;
    }
}
