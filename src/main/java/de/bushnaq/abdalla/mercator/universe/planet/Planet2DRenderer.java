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

package de.bushnaq.abdalla.mercator.universe.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine2D;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader2DRenderer;

public class Planet2DRenderer extends ObjectRenderer<GameEngine2D> {
    public static final float  PLANET_SIZE = 33;
    private final       Planet planet;
    // static final Color PLANET_COLOR = new Color( 0.8f, 0.8f, 0.8f, 1.0f );
    // //0xff8888cc;
    // static final Color PLANET_RING_COLOR = new Color( 0.6f, 0.6f, 0.6f, 1.0f );
    // //0xff8888cc;
    // static final float PLANET_ATMOSPHARE_SIZE = 96 * 4;
    // static final Color SELECTED_PLANET_COLOR = Color.ORANGE;
    Circle circle;

    public Planet2DRenderer(final Planet planet) {
        this.planet = planet;
        circle      = new Circle(planet.x, planet.z, PLANET_SIZE / 2 + 1);
    }

    @Override
    public void render(final float px, final float py, final RenderEngine2D<GameEngine2D> renderEngine, final int index, final boolean selected) {
        renderPlanet(planet, renderEngine, planet == renderEngine.getGameEngine().universe.selectedPlanet);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return circle.contains(x, y);
    }

    private void renderFactory(final Planet planet, final RenderEngine2D<GameEngine2D> renderEngine) {
        for (final ProductionFacility productionFacility : planet.productionFacilityList) {
            int index = productionFacility.producedGood.type.ordinal();
            productionFacility.get2DRenderer().render(planet.x, planet.z, renderEngine, index++, planet.universe.selectedProductionFacility == productionFacility);
        }
    }

    private void renderPlanet(final Planet planet, final RenderEngine2D<GameEngine2D> renderEngine, final boolean selected) {
        final float x   = planet.x;
        final float y   = planet.z;
        final float hps = PLANET_SIZE / 2;
        Color       color;
        // ---Planet color
        // if ( universe.selectedGoodIndex != -1 ) { Good good =
        // aPlanet.goodList.get( universe.selectedGoodIndex ); { // int color =
        // (int)( ( 255 * ( good.price - good.getMinPrice() ) ) / (
        // good.getMaxPriceDelta() * 2 ) ); // int c = ( color << 16 ) | ( color
        // << 8 ) | color; int planetDistance = PLANET_DISTANCE / 2 - 4; Bar(
        // aPlanet.x * PLANET_DISTANCE - planetDistance, aPlanet.y *
        // PLANET_DISTANCE - planetDistance, aPlanet.x * PLANET_DISTANCE +
        // planetDistance - 1 - 1, aPlanet.y * PLANET_DISTANCE + planetDistance
        // - 1 - 1, Color.black.getRGB() ); } }
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else if (planet.status.getName().equals("Dead")) {
            color = GameEngine2D.DEAD_COLOR;
        } else {
            color = renderEngine.getGameEngine().distinctiveColorlist.get(planet.sector.type);
        }
        {
            // int maxCredits = 0;
            // int minCredits = 9999;
            // for ( Planet tplanet : universe.planetList )
            // {
            // maxCredits = Math.max( maxCredits, tplanet.credits );
            // minCredits = Math.min( minCredits, tplanet.credits );
            // }
            // if ( planet.credits < Planet.PLANET_START_CREDITS )
            // {
            // ts = s + ( ( s / 2 ) * ( planet.credits ) ) / (
            // Planet.PLANET_START_CREDITS );
            // }
            // else
            // {
            // ts = s + ( 4 * ( planet.credits ) ) / (
            // Planet.PLANET_START_CREDITS );
            // }
            renderEngine.getGameEngine().renderEngine.fillCircle(renderEngine.getGameEngine().atlasManager.planetTextureRegion, x, y, hps + 1, 32, color);
            final int rings = (int) (planet.getCredits() / Planet.PLANET_START_CREDITS) + 1;
            if (renderEngine.getGameEngine().renderEngine.camera.zoom < 10.0f) {
                for (int ring = 0; ring < rings; ring++) {
                    final int   index = (ring + 1);
                    final float x1    = x;
                    final float y1    = y;
                    renderEngine.getGameEngine().renderEngine.circle(renderEngine.getGameEngine().atlasManager.planetTextureRegion, x1, y1, PLANET_SIZE * index, 8f, renderEngine.getGameEngine().distinctiveTransparentColorlist.get(planet.sector.type), 32);
                    // renderMaster.bar( renderMaster.circle.get( x2 - x1 + 1, y2 - y1 + 1 ), x1,
                    // y1, x2, y2, color );
                }
                renderEngine.getGameEngine().renderEngine.lable(renderEngine.getGameEngine().atlasManager.systemTextureRegion, x, y, Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, PLANET_SIZE * 4, PLANET_SIZE * 5, renderEngine.getGameEngine().atlasManager.defaultFont, color, planet.getName(), color, String.format("%.0f", planet.getCredits()), renderEngine.getGameEngine().queryCreditColor(planet.getCredits(), Planet.PLANET_START_CREDITS));
            } else {
                renderEngine.getGameEngine().renderEngine.lable(renderEngine.getGameEngine().atlasManager.systemTextureRegion, x, y, Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, PLANET_SIZE * 1, PLANET_SIZE * 2, renderEngine.getGameEngine().atlasManager.defaultFont, color, planet.getName(), color, String.format("%.0f", planet.getCredits()), renderEngine.getGameEngine().queryCreditColor(planet.getCredits(), Planet.PLANET_START_CREDITS));
            }
            renderEngine.getGameEngine().renderEngine.text(x, y + 100, renderEngine.getGameEngine().atlasManager.defaultFont, color, color, planet.sector.name);
            renderSims(planet, renderEngine, x, y, hps);
        }
        // float height = y - hps + Screen.TRADER_HEIGHT;
        // if ( planet.sector != null )
        // {
        // renderMaster.text( x - hps, height, color, Screen.TEXT_COLOR,
        // planet.sector.name );
        // height += Screen.TRADER_HEIGHT;
        // }
        // {
        // // Text( x-s, height, color, TEXT_COLOR, Printf( "%d-%d",
        // // aPlanet.QueryAgrecultureLevel(), aPlanet.QueryTechnologyLevel() )
        // // );
        // // height += TRADER_HEIGHT;
        // }
        renderFactory(planet, renderEngine);
    }

    private void renderSims(final Planet planet, final RenderEngine2D<GameEngine2D> renderEngine, final float x, final float y, final float hps) {
        {
            int simIndex = 0;
            for (final Sim sim : planet.simList) {
                sim.get2DRenderer().render(planet.x, planet.z, renderEngine, simIndex++, false);
            }
        }
    }

}
