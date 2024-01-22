package de.bushnaq.abdalla.mercator.universe.path;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class WaypointList extends Vector<WaypointProxy> {
	public void add(final Waypoint aWaypoint) {
		final WaypointProxy waypoint = new WaypointProxy();
		insertElementAt(waypoint, 0);
		waypoint.waypoint = aWaypoint;
	}
}
