package de.bushnaq.abdalla.mercator.universe.jumpgate;

import de.bushnaq.abdalla.mercator.renderer.Renderable;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * @author bushnaq Created 13.02.2005
 */
public class JumpGate extends Renderable {
	public boolean closed = false;
	public Planet planet = null;
	public boolean selected = false;
	public Planet targetPlanet = null;
	public float usage = 0;

	public JumpGate(final Planet planet, final Planet targetPlanet) {
		this.planet = planet;
		this.targetPlanet = targetPlanet;
		selected = false;
		closed = false;
		usage = 1;
		set2DRenderer(new JumpGate2DRenderer(this));
		set3DRenderer(new JumpGate3DRenderer(this));
	}

}
