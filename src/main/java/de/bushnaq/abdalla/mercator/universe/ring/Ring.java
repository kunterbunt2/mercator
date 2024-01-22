package de.bushnaq.abdalla.mercator.universe.ring;

import de.bushnaq.abdalla.mercator.renderer.RenderablePosition;
import de.bushnaq.abdalla.mercator.universe.Universe;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Ring extends RenderablePosition {
	public static final int SECTOR_SIZE = 1024;
	public float radius;
	int segments;
	public Universe universe;

	int width;

	public Ring(final Universe universe) {
		super(0, 0, 0);
		this.universe = universe;
		width = universe.size * 2;
		segments = 60;
		final float alpha = 360.0f / segments;
		radius = (float) (SECTOR_SIZE / (2 * Math.sin(Math.PI * alpha / (2 * 180))));
		set3DRenderer(new Ring3DRenderer(this));
	}

}
