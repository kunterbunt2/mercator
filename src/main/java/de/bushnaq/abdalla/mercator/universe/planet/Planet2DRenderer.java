package de.bushnaq.abdalla.mercator.universe.planet;

import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render2DMaster;
import de.bushnaq.abdalla.mercator.renderer.Screen2D;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

public class Planet2DRenderer extends ObjectRenderer {
	public static final float PLANET_SIZE = 33;
	// static final Color PLANET_COLOR = new Color( 0.8f, 0.8f, 0.8f, 1.0f );
	// //0xff8888cc;
	// static final Color PLANET_RING_COLOR = new Color( 0.6f, 0.6f, 0.6f, 1.0f );
	// //0xff8888cc;
	// static final float PLANET_ATMOSPHARE_SIZE = 96 * 4;
	// static final Color SELECTED_PLANET_COLOR = Color.ORANGE;
	Circle circle;
	private final Planet planet;

	public Planet2DRenderer(final Planet planet) {
		this.planet = planet;
		circle = new Circle(planet.x, planet.y, PLANET_SIZE / 2 + 1);
	}

	@Override
	public void render(final float px, final float py, final Render2DMaster renderMaster, final int index, final boolean selected) {
		renderPlanet(planet, renderMaster, planet == renderMaster.universe.selectedPlanet);
	}

	private void renderFactory(final Planet planet, final Render2DMaster renderMaster) {
		for (final ProductionFacility productionFacility : planet.productionFacilityList) {
			int index = productionFacility.producedGood.type.ordinal();
			productionFacility.get2DRenderer().render(planet.x, planet.y, renderMaster, index++, planet.universe.selectedProductionFacility == productionFacility);
		}
	}

	private void renderPlanet(final Planet planet, final Render2DMaster renderMaster, final boolean selected) {
		final float x = planet.x;
		final float y = planet.y;
		final float hps = PLANET_SIZE / 2;
		Color color;
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
			color = Screen2D.SELECTED_COLOR;
		} else if (planet.status.getName().equals("Dead")) {
			color = Screen2D.DEAD_COLOR;
		} else {
			color = renderMaster.distinctiveColorlist.get(planet.sector.type);
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
			renderMaster.fillCircle(renderMaster.atlasManager.planetTextureRegion, x, y, hps + 1, 32, color);
			final int rings = (int) (planet.getCredits() / Planet.PLANET_START_CREDITS) + 1;
			if (renderMaster.camera.zoom < 10.0f) {
				for (int ring = 0; ring < rings; ring++) {
					final int index = (ring + 1);
					final float x1 = x;
					final float y1 = y;
					renderMaster.circle(renderMaster.atlasManager.planetTextureRegion, x1, y1, PLANET_SIZE * index, 8f, renderMaster.distinctiveTransparentColorlist.get(planet.sector.type), 32);
					// renderMaster.bar( renderMaster.circle.get( x2 - x1 + 1, y2 - y1 + 1 ), x1,
					// y1, x2, y2, color );
				}
				renderMaster.lable(x, y, PLANET_SIZE * 4, PLANET_SIZE * 5, renderMaster.atlasManager.defaultFont, color, planet.getName(), color, String.format("%.0f", planet.getCredits()), renderMaster.queryCreditColor(planet.getCredits(), Planet.PLANET_START_CREDITS));
			} else {
				renderMaster.lable(x, y, PLANET_SIZE * 1, PLANET_SIZE * 2, renderMaster.atlasManager.defaultFont, color, planet.getName(), color, String.format("%.0f", planet.getCredits()), renderMaster.queryCreditColor(planet.getCredits(), Planet.PLANET_START_CREDITS));
			}
			renderMaster.text(x, y + 100, renderMaster.atlasManager.defaultFont, color, color, planet.sector.name);
			renderSims(planet, renderMaster, x, y, hps);
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
		renderFactory(planet, renderMaster);
	}

	private void renderSims(final Planet planet, final Render2DMaster renderMaster, final float x, final float y, final float hps) {
		{
			int simIndex = 0;
			for (final Sim sim : planet.simList) {
				sim.get2DRenderer().render(planet.x, planet.y, renderMaster, simIndex++, false);
			}
		}
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		return circle.contains(x, y);
	}

}
