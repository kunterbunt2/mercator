package de.bushnaq.abdalla.mercator.util;

/**
 * @author bushnaq Created 13.02.2005
 */
public class TableLocation {
	// ---DATA
	public int x;
	public int y;

	public TableLocation() {
		x = y = 0;
	}

	public TableLocation(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	// public float queryDistance( TableLocation aLocation )
	// {
	// return ( Math.abs( ( x - aLocation.x ) ) + Math.abs( ( y - aLocation.y ) ) )
	// * Planet.MIN_PLANET_DISTANCE;
	// }
}
