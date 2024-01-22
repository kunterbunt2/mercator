package de.bushnaq.abdalla.mercator.universe.path;

import de.bushnaq.abdalla.mercator.renderer.Renderable;

/**
 * @author bushnaq Created 13.02.2005
 */
/**
 * A path is a connection between two Waypoints
 *
 */
public class Path extends Renderable {
	public boolean closed = false;
	public boolean selected = false;
	public Waypoint source = null;
	public Waypoint target = null;
	public float usage = 0;

	public Path(final Waypoint planet, final Waypoint targetPlanet) {
		this.source = planet;
		this.target = targetPlanet;
		selected = false;
		closed = false;
		usage = 1;
		set2DRenderer(new JumpGate2DRenderer(this));
		set3DRenderer(new JumpGate3DRenderer(this));
	}

}
