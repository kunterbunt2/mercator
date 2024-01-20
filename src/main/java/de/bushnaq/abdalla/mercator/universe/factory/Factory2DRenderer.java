package de.bushnaq.abdalla.mercator.universe.factory;

import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render2DMaster;
import de.bushnaq.abdalla.mercator.renderer.Screen2D;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.AnnulusSegment;
import com.badlogic.gdx.graphics.Color;

public class Factory2DRenderer extends ObjectRenderer {
	private static final float ANGLE_BORDER = (float) Math.PI / 256;
	static final Color FACTORY_COLOR = new Color(0.09f, 0.388f, 0.69f, 0.5f);
	private static final float MAX_RADIUS = Planet2DRenderer.PLANET_SIZE * 6f;
	private static final float MIN_ANGLE = (float) Math.PI / 12;
	private static final float MIN_RADIUS = Planet2DRenderer.PLANET_SIZE * 4.5f + 3;
	static final Color NOT_PRODUCING_FACTORY_COLOR = new Color(0.475f, 0.035f, 0.027f, 0.8f);
	// static final Color SELECTED_FACTORY_COLOR = Color.GOLD;
	AnnulusSegment annulusSegment;
	private final ProductionFacility productionFacility;

	public Factory2DRenderer(final ProductionFacility productionFacility) {
		this.productionFacility = productionFacility;
	}

	private void drawFactory(final float x, final float y, final ProductionFacility productionFacility, final int index, final Render2DMaster renderMaster, final boolean selected) {
		Color color;
		if (selected) {
			color = Screen2D.SELECTED_COLOR;
		} else if (productionFacility.status != ProductionFacilityStatus.PRODUCING) {
			color = NOT_PRODUCING_FACTORY_COLOR;
		} else {
			color = FACTORY_COLOR;
		}
		final float tx = x;
		final float ty = y;
		final float minAngle = (float) Math.PI / 2 + MIN_ANGLE * index;
		final float maxAngle = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
		String name = null;
		// if ( renderMaster.camera.zoom < 1.3f )
		// {
		// name = productionFacility.getName();
		// }
		name = productionFacility.getName();
		if (renderMaster.camera.zoom < 7.0f) {
			renderMaster.fillPie(renderMaster.atlasManager.factoryTextureRegion, tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 8, renderMaster.atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, name);
			// renderSims( productionFacility, renderMaster, tx, ty );
			float amount = 0;
			float max = 0;
			if (Factory.class.isInstance(productionFacility)) {
				final Factory factory = (Factory) productionFacility;
				for (final Good good : factory.inputGood) {
					max += good.getMaxAmount();
					amount += good.getAmount();
				}
				if (max != 0) {
					final Good good = factory.inputGood.queryFirstGood();
					if (good != null) {
						final Color barColor = renderMaster.availabilityColor(amount, max);
						final float deltaRadius = (maxAngle - minAngle) * (max - amount) / max;
						renderMaster.fillPie(renderMaster.atlasManager.gaugeTextureRegion, tx, ty, Planet2DRenderer.PLANET_SIZE * 4 - 5, Planet2DRenderer.PLANET_SIZE * 4, maxAngle - deltaRadius, maxAngle, barColor, 8, renderMaster.atlasManager.zoominDefaultFont, Screen2D.TEXT_COLOR, productionFacility.getName());
					}
				}
			}
		}
		annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
	}

	@Override
	public void render(final float x, final float y, final Render2DMaster renderMaster, final int index, final boolean selected) {
		drawFactory(x, y, productionFacility, index, renderMaster, selected);
	}

	private void renderSims(final ProductionFacility productionFacility, final Render2DMaster renderMaster, final float tx, final float ty) {
		int simIndex = 0;
		for (final Sim sim : productionFacility.engineers) {
			sim.get2DRenderer().render(tx, ty, renderMaster, simIndex++, false);
		}
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		if (productionFacility.planet.getName().equals("P-93")) {
			final int a = 34;
		}
		return annulusSegment.contains(x, y);
	}
}
