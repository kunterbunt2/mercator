package de.bushnaq.abdalla.mercator.util;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Location3D {
	// ---DATA
	public float x;
	public float y;
	public float z;

	public Location3D() {
		x = y = z = 0;
	}

	public Location3D(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float queryDistance(final Location3D aLocation) {
		return (float) Math.sqrt(Math.pow(x - aLocation.x, 2) + Math.pow(y - aLocation.y, 2));
		//		return (Math.abs((x - aLocation.x)) + Math.abs((y - aLocation.y))) * Planet.MIN_PLANET_DISTANCE;
		//TODO needs adapting for 2D
	}
}
