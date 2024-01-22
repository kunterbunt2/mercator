package de.bushnaq.abdalla.mercator.universe.land;

import de.bushnaq.abdalla.mercator.renderer.RenderablePosition;
import de.bushnaq.abdalla.mercator.universe.Universe;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Land extends RenderablePosition {

	private final Universe universe;

	public Land(final float x, final float y, final float z, final Universe universe) {
		super(x, y, z);
		this.universe = universe;
		//		set2DRenderer(new Land2DRenderer(this));
		set3DRenderer(new Land3DRenderer(this));
	}

}
