package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render2DMaster;
import de.bushnaq.abdalla.mercator.renderer.Screen2D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

public class Trader2DRenderer extends ObjectRenderer {
	private static final float RADIUS = Planet2DRenderer.PLANET_SIZE * 2.0f;
	// static final Color SELECTED_TRADER_COLOR = Color.ORANGE; //0xffff0000;
	public static final Color TADER_COLOR_IS_GOOD = Color.LIGHT_GRAY; // 0xaaaaaa
	public static final Color TRADER_COLOR = new Color(.7f, .7f, .7f, 0.45f); // 0xffcc5555;
	public static final float TRADER_HEIGHT = 17;
	public static final Color TRADER_OF_SELECTED_PLANET_COLOR1 = Color.RED; // 0xffff0000;
	public static final Color TRADER_OF_SELECTED_PLANET_COLOR2 = new Color(1f, .5f, 0f, 1f); // 0xffff8800;
	public static final float TRADER_WIDTH = 17;
	Circle circle;
	private final Trader trader;

	public Trader2DRenderer(final Trader trader) {
		this.trader = trader;
		circle = new Circle(0, 0, TRADER_WIDTH + 1);
	}

	private void drawTrader(final Trader trader, final Render2DMaster renderMaster, final int index, final boolean selected) {
		Color color;
		if (selected) {
			color = Screen2D.SELECTED_COLOR;
		}
		// else if ( ( renderMaster.universe.selectedPlanet != null ) && ( (
		// renderMaster.universe.selectedPlanet == trader.destinationPlanet ) || (
		// renderMaster.universe.selectedPlanet == trader.sourcePlanet ) ) )
		// {
		// if ( ( renderMaster.universe.currentTime % 2 == 0 ) )
		// {
		// color = TRADER_OF_SELECTED_PLANET_COLOR1;
		// }
		// else
		// {
		// color = TRADER_OF_SELECTED_PLANET_COLOR2;
		// }
		// }
		else {
			color = TRADER_COLOR;
		}
		if (!trader.traderStatus.isGood()) {
			color = TADER_COLOR_IS_GOOD;
		}
		float x = 0;
		float y = 0;
		final float deltaAngle = (float) (Math.PI / 8);
		if (trader.destinationWaypointPlanet != null) {
			// ---Traveling
			if (trader.destinationWaypointDistance != 0) {
				x = (trader.planet.x + (trader.destinationWaypointPlanet.x - trader.planet.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance);
				y = (trader.planet.y + (trader.destinationWaypointPlanet.y - trader.planet.y) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance);
			} else {
				x = trader.planet.x;
				y = trader.planet.y;
			}
		} else {
			x = (float) (trader.planet.x + RADIUS * Math.sin(trader.planet.orbitAngle + deltaAngle * index));
			y = (float) (trader.planet.y + RADIUS * Math.cos(trader.planet.orbitAngle + deltaAngle * index));
		}
		final float hps = TRADER_WIDTH / 2;
		renderMaster.fillCircle(renderMaster.atlasManager.planetTextureRegion, x, y, hps + 1, 32, color);
		// renderMaster.bar( renderMaster.fillCircle.get( TRADER_WIDTH, TRADER_HEIGHT ),
		// x - hps, y - hps, x + hps, y + hps, color );
		if (renderMaster.camera.zoom < 3.0f) {
			renderMaster.lable(x - hps, y - hps, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderMaster.atlasManager.defaultFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderMaster.queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));
		}
		circle.setPosition(x, y);
	}

	@Override
	public void render(final float px, final float py, final Render2DMaster renderMaster, final int index, final boolean selected) {
		drawTrader(trader, renderMaster, index, selected);
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		return circle.contains(x, y);
	}
}
