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

package de.bushnaq.abdalla.mercator.universe.sim;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.util.AnnulusSegment;

public class Sim3DRenderer extends ObjectRenderer<GameEngine3D> {
    static final         Color BAD_COLOR    = new Color(0.8f, 0.0f, 0.0f, 0.8f);
    static final         Color GOOD_COLOR   = new Color(0.0f, 0.1f, 0.0f, 0.8f); // 0xff000000;
    private static final float ANGLE_BORDER = (float) Math.PI / 256;
    private static final float MAX_RADIUS   = Planet2DRenderer.PLANET_SIZE * 4.5f;
    private static final float MIN_ANGLE    = (float) Math.PI / 24;
    private static final float MIN_RADIUS   = Planet2DRenderer.PLANET_SIZE * 4.0f + 3;
    private final        Sim   sim;
    AnnulusSegment annulusSegment;

    public Sim3DRenderer(final Sim sim) {
        this.sim = sim;
    }

    @Override
    public void render2D(final float x, final float y, final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        // Color color = renderMaster.satesfactionColor( sim.getSatisfactionFactor(
        // renderMaster.universe.currentTime ) );
        final Color color1   = Color.RED;
        final Color color2   = Color.GREEN;
        final float tx       = x;
        final float ty       = y;
        final float minAngle = (float) Math.PI / 2 + MIN_ANGLE * index;
        final float maxAngle = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
        if (renderEngine.getCamera().position.y < 3000) {
            final Color color = new Color(color1);
            color.lerp(color2, sim.getSatisfactionFactor(renderEngine.getGameEngine().universe.currentTime) / (100f));
            renderEngine.renderutils2Dxz.fillPie(renderEngine.getGameEngine().getAtlasManager().simTextureRegion, tx, 0, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 4, renderEngine.getGameEngine().getAtlasManager().zoominDefaultFont, GameEngine2D.TEXT_COLOR, "");
        }
        annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return annulusSegment.contains(x, y);
    }
}
