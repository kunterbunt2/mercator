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

package de.bushnaq.abdalla.mercator.universe.good;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.mercator.renderer.Screen2D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.util.AnnulusSegment;

public class Good2DRenderer extends ObjectRenderer<Screen2D> {
    static final         Color GOOD_COLOR            = new Color(0.09f, 0.388f, 0.69f, 0.8f); // 0xff000000;
    static final         float GOOD_HEIGHT           = 12 * 4;
    static final         float GOOD_WIDTH            = 24 * 4;
    static final         Color NOT_TRADED_GOOD_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.8f);
    private static final float ANGLE_BORDER          = (float) Math.PI / 256;
    private static final float MAX_RADIUS            = Planet2DRenderer.PLANET_SIZE * 7.5f;
    private static final float MIN_ANGLE             = (float) Math.PI / 12;
    private static final float MIN_RADIUS            = Planet2DRenderer.PLANET_SIZE * 6f + 3;
    private final        Good  good;
    // static final Color SELECTED_GOOD_COLOR = Screen.DARK_RED_COLOR;
    AnnulusSegment annulusSegment;

    public Good2DRenderer(final Good good) {
        this.good = good;
    }

    private void drawGood(final float aX, final float aY, final Good good, final RenderEngine2D<Screen2D> renderEngine, final int index, final boolean selected) {
        Color color;
        if (selected) {
            color = Screen2D.SELECTED_COLOR;
        } else if (good.isTraded(renderEngine.getGameEngine().universe.currentTime)) {
            color = GOOD_COLOR;
        } else {
            color = NOT_TRADED_GOOD_COLOR;
        }
        // if ( good.type == GoodType.Food && !good.isTraded(
        // universe.currentTime ) )
        // System.out.printf( "planet %s last time %d now %d\n", planet.name,
        // good.lastBuyInterest, universe.currentTime );
        // float x = aX * Screen.PLANET_DISTANCE + Screen.PLANET_ATMOSPHARE_SIZE / 2 -
        // Screen.GOOD_WIDTH;
        // float y = aY * Screen.PLANET_DISTANCE - Screen.PLANET_ATMOSPHARE_SIZE / 2 +
        // index * Screen.GOOD_HEIGHT;
        // renderMaster.bar( renderMaster.goodTexture, x, y, x + Screen.GOOD_WIDTH - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, color );
        final float tx          = aX;
        final float ty          = aY;
        final float minAngle    = (float) Math.PI / 2 + MIN_ANGLE * index;
        final float maxAngle    = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
        String      name        = null;
        float       deltaRadius = 0;
        Color       barColor    = null;
        switch (renderEngine.getGameEngine().showGood) {
            case Price:
                name = String.format("%.0f", good.price);
                barColor = renderEngine.getGameEngine().priceColor(good);
                deltaRadius = (maxAngle - minAngle) * good.price / good.getMaxPrice();
                break;
            case Name:
                name = good.type.getName();
                barColor = renderEngine.getGameEngine().amountColor(good);
                deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
                break;
            case Volume:
                name = String.format("%.0f", good.getAmount());
                barColor = renderEngine.getGameEngine().amountColor(good);
                deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
                break;
        }
        // if ( renderMaster.camera.zoom > 1.2f )
        // {
        // name = null;
        // }
        if (renderEngine.getGameEngine().renderEngine.camera.zoom < 7.0f) {
            renderEngine.getGameEngine().renderEngine.fillPie(renderEngine.getGameEngine().atlasManager.factoryTextureRegion, tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 8, renderEngine.getGameEngine().atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, name);
            renderEngine.getGameEngine().renderEngine.fillPie(renderEngine.getGameEngine().atlasManager.gaugeTextureRegion, tx, ty, MAX_RADIUS - 5, MAX_RADIUS, maxAngle - deltaRadius, maxAngle, barColor, 8, renderEngine.getGameEngine().atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, "");
        }
        annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
    }

    @Override
    public void render(final float x, final float y, final RenderEngine2D<Screen2D> renderEngine, final int index, final boolean selected) {
        drawGood(x, y, good, renderEngine, index, selected);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return annulusSegment.contains(x, y);
    }
}
