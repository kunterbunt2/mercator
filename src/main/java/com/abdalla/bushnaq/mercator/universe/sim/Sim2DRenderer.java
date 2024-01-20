package com.abdalla.bushnaq.mercator.universe.sim;

import com.abdalla.bushnaq.mercator.renderer.ObjectRenderer;
import com.abdalla.bushnaq.mercator.renderer.Render2DMaster;
import com.abdalla.bushnaq.mercator.renderer.Screen2D;
import com.abdalla.bushnaq.mercator.universe.planet.Planet2DRenderer;
import com.abdalla.bushnaq.mercator.util.AnnulusSegment;
import com.badlogic.gdx.graphics.Color;

public class Sim2DRenderer extends ObjectRenderer {
	private static final float ANGLE_BORDER = (float) Math.PI / 256;
	static final Color BAD_COLOR = new Color(0.8f, 0.0f, 0.0f, 0.8f);
	static final Color GOOD_COLOR = new Color(0.0f, 0.1f, 0.0f, 0.8f); // 0xff000000;
	private static final float MAX_RADIUS = Planet2DRenderer.PLANET_SIZE * 4.5f;
	private static final float MIN_ANGLE = (float) Math.PI / 24;
	private static final float MIN_RADIUS = Planet2DRenderer.PLANET_SIZE * 4.0f + 3;
	AnnulusSegment annulusSegment;
	private final Sim sim;

	public Sim2DRenderer(final Sim sim) {
		this.sim = sim;
	}

	@Override
	public void render(final float x, final float y, final Render2DMaster renderMaster, final int index, final boolean selected) {
		// Color color = renderMaster.satesfactionColor( sim.getSatisfactionFactor(
		// renderMaster.universe.currentTime ) );
		final Color color1 = Color.RED;
		final Color color2 = Color.GREEN;
		final float tx = x;
		final float ty = y;
		final float minAngle = (float) Math.PI / 2 + MIN_ANGLE * index;
		final float maxAngle = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
		if (renderMaster.camera.zoom < 7.0f) {
			final Color color = new Color(color1);
			color.lerp(color2, sim.getSatisfactionFactor(renderMaster.universe.currentTime) / (100f));
			renderMaster.fillPie(renderMaster.atlasManager.simTextureRegion, tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 4, renderMaster.atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, "");
		}
		annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		return annulusSegment.contains(x, y);
	}
}
