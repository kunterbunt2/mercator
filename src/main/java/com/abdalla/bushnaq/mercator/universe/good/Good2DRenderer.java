package com.abdalla.bushnaq.mercator.universe.good;

import com.abdalla.bushnaq.mercator.renderer.ObjectRenderer;
import com.abdalla.bushnaq.mercator.renderer.Render2DMaster;
import com.abdalla.bushnaq.mercator.renderer.Screen2D;
import com.abdalla.bushnaq.mercator.universe.planet.Planet2DRenderer;
import com.abdalla.bushnaq.mercator.util.AnnulusSegment;
import com.badlogic.gdx.graphics.Color;

public class Good2DRenderer extends ObjectRenderer {
	private static final float ANGLE_BORDER = (float) Math.PI / 256;
	static final Color GOOD_COLOR = new Color(0.09f, 0.388f, 0.69f, 0.8f); // 0xff000000;
	static final float GOOD_HEIGHT = 12 * 4;
	static final float GOOD_WIDTH = 24 * 4;
	private static final float MAX_RADIUS = Planet2DRenderer.PLANET_SIZE * 7.5f;
	private static final float MIN_ANGLE = (float) Math.PI / 12;
	private static final float MIN_RADIUS = Planet2DRenderer.PLANET_SIZE * 6f + 3;
	static final Color NOT_TRADED_GOOD_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.8f);
	// static final Color SELECTED_GOOD_COLOR = Screen.DARK_RED_COLOR;
	AnnulusSegment annulusSegment;
	private final Good good;

	public Good2DRenderer(final Good good) {
		this.good = good;
	}

	private void drawGood(final float aX, final float aY, final Good good, final Render2DMaster renderMaster, final int index, final boolean selected) {
		Color color;
		if (selected) {
			color = Screen2D.SELECTED_COLOR;
		} else if (good.isTraded(renderMaster.universe.currentTime)) {
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
		final float tx = aX;
		final float ty = aY;
		final float minAngle = (float) Math.PI / 2 + MIN_ANGLE * index;
		final float maxAngle = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
		String name = null;
		float deltaRadius = 0;
		Color barColor = null;
		switch (renderMaster.showGood) {
		case Price:
			name = String.format("%.0f", good.price);
			barColor = renderMaster.priceColor(good);
			deltaRadius = (maxAngle - minAngle) * good.price / good.getMaxPrice();
			break;
		case Name:
			name = good.type.getName();
			barColor = renderMaster.amountColor(good);
			deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
			break;
		case Volume:
			name = String.format("%.0f", good.getAmount());
			barColor = renderMaster.amountColor(good);
			deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
			break;
		}
		// if ( renderMaster.camera.zoom > 1.2f )
		// {
		// name = null;
		// }
		if (renderMaster.camera.zoom < 7.0f) {
			renderMaster.fillPie(renderMaster.atlasManager.factoryTextureRegion, tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 8, renderMaster.atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, name);
			renderMaster.fillPie(renderMaster.atlasManager.gaugeTextureRegion, tx, ty, MAX_RADIUS - 5, MAX_RADIUS, maxAngle - deltaRadius, maxAngle, barColor, 8, renderMaster.atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, "");
		}
		annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
	}

	@Override
	public void render(final float x, final float y, final Render2DMaster renderMaster, final int index, final boolean selected) {
		drawGood(x, y, good, renderMaster, index, selected);
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		return annulusSegment.contains(x, y);
	}
}
