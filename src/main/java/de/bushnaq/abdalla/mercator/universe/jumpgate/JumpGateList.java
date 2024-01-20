package de.bushnaq.abdalla.mercator.universe.jumpgate;

import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * @author bushnaq Created 13.02.2005
 */
public class JumpGateList extends Vector<JumpGate> {
	private static final long serialVersionUID = -6879517895353709879L;

	public JumpGate queryJumpGateTo(final Planet aTargetPlanet) {
		for (final JumpGate jumpGate : this) {
			if (jumpGate.targetPlanet == aTargetPlanet) {
				return jumpGate;
			}
		}
		return null;
	}

	public void reduceUsage() {
		for (final JumpGate jumpGate : this) {
			if (jumpGate.usage > 1) {
				jumpGate.usage = 1;
			}
		}
	}
}
