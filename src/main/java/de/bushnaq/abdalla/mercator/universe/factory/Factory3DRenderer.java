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

package de.bushnaq.abdalla.mercator.universe.factory;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.util.AnnulusSegment;

public class Factory3DRenderer extends ObjectRenderer<GameEngine3D> {
    static final         Color              FACTORY_COLOR               = new Color(0.09f, 0.388f, 0.69f, 0.5f);
    static final         Color              NOT_PRODUCING_FACTORY_COLOR = new Color(0.475f, 0.035f, 0.027f, 0.8f);
    private static final float              ANGLE_BORDER                = (float) Math.PI / 256;
    private static final float              MAX_RADIUS                  = Planet2DRenderer.PLANET_SIZE * 6f;
    private static final float              MIN_ANGLE                   = (float) Math.PI / 12;
    private static final float              MIN_RADIUS                  = Planet2DRenderer.PLANET_SIZE * 4.5f + 3;
    private final        ProductionFacility productionFacility;
    // static final Color SELECTED_FACTORY_COLOR = Color.GOLD;
    AnnulusSegment annulusSegment;

    public Factory3DRenderer(final ProductionFacility productionFacility) {
        this.productionFacility = productionFacility;
    }

    private void drawFactory(final float x, final float y, final ProductionFacility productionFacility, final int index, final RenderEngine3D<GameEngine3D> renderEngine, final boolean selected) {
        Color color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else if (productionFacility.status != ProductionFacilityStatus.PRODUCING) {
            color = NOT_PRODUCING_FACTORY_COLOR;
        } else {
            color = FACTORY_COLOR;
        }
        final float tx       = x;
        final float ty       = y;
        final float minAngle = (float) Math.PI / 2 + MIN_ANGLE * index;
        final float maxAngle = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
        String      name     = null;
        // if ( renderMaster.camera.zoom < 1.3f )
        // {
        // name = productionFacility.getName();
        // }
        name = productionFacility.getName();
        if (renderEngine.getCamera().position.y < 3000) {
            renderEngine.renderutils2Dxz.fillPie(renderEngine.getGameEngine().getAtlasManager().factoryTextureRegion, tx, 0, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 8, renderEngine.getGameEngine().getAtlasManager().zoominDefaultFont, GameEngine2D.TEXT_COLOR, name);
            // renderSims( productionFacility, renderMaster, tx, ty );
            float amount = 0;
            float max    = 0;
            if (Factory.class.isInstance(productionFacility)) {
                final Factory factory = (Factory) productionFacility;
                for (final Good good : factory.inputGood) {
                    max += good.getMaxAmount();
                    amount += good.getAmount();
                }
                if (max != 0) {
                    final Good good = factory.inputGood.queryFirstGood();
                    if (good != null) {
                        final Color barColor    = renderEngine.getGameEngine().availabilityColor(amount, max);
                        final float deltaRadius = (maxAngle - minAngle) * (max - amount) / max;
                        renderEngine.renderutils2Dxz.fillPie(renderEngine.getGameEngine().getAtlasManager().gaugeTextureRegion, tx, 0, ty, Planet2DRenderer.PLANET_SIZE * 4 - 5, Planet2DRenderer.PLANET_SIZE * 4, maxAngle - deltaRadius, maxAngle, barColor, 8, renderEngine.getGameEngine().getAtlasManager().zoominDefaultFont, GameEngine2D.TEXT_COLOR, productionFacility.getName());
                    }
                }
            }
        }
        annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
    }

    @Override
    public void render2D(final float x, final float y, final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        drawFactory(x, y, productionFacility, index, renderEngine, selected);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
//        if (productionFacility.planet.getName().equals("P-93")) {
//            final int a = 34;
//        }
        return annulusSegment.contains(x, y);
    }

//    private void renderSims(final ProductionFacility productionFacility, final RenderEngine2D<GameEngine2D> renderEngine, final float tx, final float ty) {
//        int simIndex = 0;
//        for (final Sim sim : productionFacility.engineers) {
//            sim.get2DRenderer().render(tx, ty, renderEngine, simIndex++, false);
//        }
//    }
}
