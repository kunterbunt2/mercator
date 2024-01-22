package de.bushnaq.abdalla.mercator.universe.path;

public class ShortestPath {
	public float distance = 999992; // ---Used by pathseeker algorithm temporary. This is the accumulation of distance over all planets from the current port.
	public Waypoint pathSeekerNextWaypoint; // ---Used by the pathseeker algorithm temporary. This is the next planet on the calculated route to the current port.
	//	public Planet planet;

	public ShortestPath(final Waypoint planet) {
		//		this.planet = planet;
	}

	public ShortestPath(final Waypoint planet, final int pathSeekerDistance, final Waypoint pathSeekerNextWaypoint) {
		//		this.planet = planet;
		this.distance = pathSeekerDistance;
		this.pathSeekerNextWaypoint = pathSeekerNextWaypoint;
	}
}
