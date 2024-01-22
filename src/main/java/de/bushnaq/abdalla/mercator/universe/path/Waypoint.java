package de.bushnaq.abdalla.mercator.universe.path;

import de.bushnaq.abdalla.mercator.renderer.RenderablePosition;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sector.Sector;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

/**
 * Waypoints are locations in space that connect to other waypoints through a path stored in the pathlist
 * Some waypoints are cities, in which case the planet points to it
 * A waypoint belongs to one of the political sectors
 *
 */
public class Waypoint extends RenderablePosition {

	public Planet city = null;//not null if this waypoint is also a city
	public String name;
	public PathList pathList = new PathList();
	public Sector sector = null;//political affiliation
	public Object seed = null;//temporary usage, is used to detect of a waypoint has been visited by an algorithm
	//TODO!!! should be moved to a temporary map
	public Trader trader = null;

	public Waypoint(final String name, final float x, final float y, final float z) {
		super(x, y, z);
		this.name = name;
	}

	//	public Waypoint(String name, float x, float y, float z, Planet city) {
	//		super(x, y, z);
	//		this.city = city;
	//		this.name = name;
	//	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return getName();
	}

}
