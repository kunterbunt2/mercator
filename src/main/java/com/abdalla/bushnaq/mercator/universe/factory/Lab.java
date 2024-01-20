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
package com.abdalla.bushnaq.mercator.universe.factory;

import com.abdalla.bushnaq.mercator.universe.good.Good;
import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.util.Transaction;

/**
 * @author bushnaq
 * Created 13.02.2005
 */
/**
 * A lab belongs to a planet. A lab consumes labor of technicalLevel N from the
 * planet to produce one good blueprint of technicalLevel N+1 for the planet.
 * The cost of research is equal to the cost of labor.
 *
 *
 * @author abdalla bushnaq
 *
 */
public class Lab extends ProductionFacility {
	public Lab(final Planet planet, final Good producedGood) {
		super(planet, producedGood);
	}

	@Override
	public void advanceInTime(final long currentTime) {
		if (lastProductionTime + TIME_NEEDED_TO_PRODUCE >= currentTime) {
			// --Do we have enough engineers?
			if (engineers.size() <= 0) {
				status = ProductionFacilityStatus.NO_ENGINEERS;
				return;
			}
			// ---Can the planet afford this production?
			{
				float engineeringCost = 0;
				engineeringCost += engineers.getTotalCost();
				if (planet.getCredits() < engineeringCost) {
					status = ProductionFacilityStatus.CANNOT_AFFORD_ENGINEERS;
					return;
				}
			}
			// ---Produce
			// ---Pay the engineers
			for (final Sim sim : engineers) {
				Transaction.pay(currentTime, sim.cost, 1, planet, sim);
				// planet.pay( currentTime, sim.cost, 1, sim );
			}
			productionProgress += engineers.size();
			while (productionProgress > queryEngineersNeeded()) {
				productionProgress -= queryEngineersNeeded();
				producedGood.produce(1, planet);
			}
			status = ProductionFacilityStatus.RESEARCHING;
			lastProductionTime = currentTime;
		}
	}

	@Override
	public String getName() {
		return "L-" + producedGood.type.getName().substring(2);
	}

	public int queryEngineersNeeded() {
		return producedGood.type.getTechnicalLevel() * 100;
	}

	@Override
	public float queryProfit() {
		return 0;
	}
}
