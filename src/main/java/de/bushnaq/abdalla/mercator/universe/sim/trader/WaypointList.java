package de.bushnaq.abdalla.mercator.universe.sim.trader;

import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * @author bushnaq Created 13.02.2005
 */
public class WaypointList extends Vector<Waypoint> {
	private static final long serialVersionUID = -3947322543765989531L;

	public void add(final Planet aPlanet) {
		final Waypoint waypoint = new Waypoint();
		insertElementAt(waypoint, 0);
		waypoint.planet = aPlanet;
	}
}
