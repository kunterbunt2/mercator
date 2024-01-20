package com.abdalla.bushnaq.mercator.universe.planet;

public class Path {
	public float distance = 999992; // ---Used by pathseeker algorithm temporary. This is the accumulation of
									// distance over all planets from the current port.
	public Planet pathSeekerNextWaypoint; // ---Used by the pathseeker algorithm temporary. This is the next planet on
											// the calculated route to the current port.
	public Planet planet;

	public Path(final Planet planet) {
		this.planet = planet;
	}

	public Path(final Planet planet, final int pathSeekerDistance, final Planet pathSeekerNextWaypoint) {
		this.planet = planet;
		this.distance = pathSeekerDistance;
		this.pathSeekerNextWaypoint = pathSeekerNextWaypoint;
	}
}
