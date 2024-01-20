/* ---------------------------------------------------------------------------
 * BEGIN_PROJECT_HEADER
 *
 *       RRRR  RRR    IIIII    CCCCC      OOOO    HHH  HHH
 *       RRRR  RRRR   IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR  RRRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR RRRR    IIIII  CCCC       OOO  OOO  HHHHHHHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR   RRRR  IIIII    CCCCC      OOOO    HHH  HHH
 *
 *       Copyright 2005 by Ricoh Europe B.V.
 *
 *       This material contains, and is part of a computer software program
 *       which is, proprietary and confidential information owned by Ricoh
 *       Europe B.V.
 *       The program, including this material, may not be duplicated, disclosed
 *       or reproduced in whole or in part for any purpose without the express
 *       written authorization of Ricoh Europe B.V.
 *       All authorized reproductions must be marked with this legend.
 *
 *       Department : European Development and Support Center
 *       Group      : Printing & Fax Solution Group
 *       Author(s)  : bushnaq
 *       Created    : 13.02.2005
 *
 *       Project    : com.abdalla.bushnaq.mercator
 *       Product Id : <Product Key Index>
 *       Component  : <Project Component Name>
 *       Compiler   : Java/Eclipse
 *
 * END_PROJECT_HEADER
 * -------------------------------------------------------------------------*/
package de.bushnaq.abdalla.mercator.universe.planet;

import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.jumpgate.JumpGate;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Waypoint;

/**
 * @author bushnaq Created 13.02.2005
 */
public class PlanetList extends Vector<Planet> {
	private static final long serialVersionUID = 4661905529459408641L;

	public void clearSeed() {
		for (final Planet planet : this) {
			planet.seed = null;
		}
	}

	public int getIndex(final Planet planet) {
		int index = 0;
		for (final Planet p : this) {
			if (p == planet)
				return index;
			index++;
		}
		return -1;
	}

	public void markTraderPath(final Trader aTrader) {
		if (aTrader != null) {
			// ---Deselect all jump gates
			for (final Planet planet : this) {
				for (final JumpGate jumpGate : planet.jumpGateList) {
					jumpGate.selected = false;
				}
			}
			int waypointIndex = 0;
			while (waypointIndex < aTrader.waypointList.size()) {
				final Waypoint waypoint = aTrader.waypointList.get(waypointIndex);
				// ---For each waypoint, mark the jumppoint to the next one
				for (final JumpGate jumpGate : waypoint.planet.jumpGateList) {
					if ((waypointIndex + 1 < aTrader.waypointList.size()) && (jumpGate.targetPlanet == aTrader.waypointList.get(waypointIndex + 1).planet)) {
						jumpGate.selected = true;
					} else if ((waypointIndex > 0) && (jumpGate.targetPlanet == aTrader.waypointList.get(waypointIndex - 1).planet)) {
						jumpGate.selected = true;
					} else {
					}
				}
				waypointIndex++;
			}
		}
	}

	public Planet queryPlanetByLocation(final float x, final float y) {
		for (final Planet planet : this) {
			if (planet.x == x && planet.y == y)
				return planet;
		}
		return null;
	}
}
