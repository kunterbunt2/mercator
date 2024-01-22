package de.bushnaq.abdalla.mercator.universe.path;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class PathList extends Vector<Path> {
	private static final long serialVersionUID = -6879517895353709879L;

	public Path queryJumpGateTo(final Waypoint aTargetPlanet) {
		for (final Path jumpGate : this) {
			if (jumpGate.target == aTargetPlanet) {
				return jumpGate;
			}
		}
		return null;
	}

	public void reduceUsage() {
		for (final Path jumpGate : this) {
			if (jumpGate.usage > 1) {
				jumpGate.usage = 1;
			}
		}
	}
}
