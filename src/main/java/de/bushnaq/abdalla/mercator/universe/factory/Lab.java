/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.universe.factory;

import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.Transaction;

/**
 * @author bushnaq
 * Created 13.02.2005
 */

/**
 * A lab belongs to a planet. A lab consumes labor of technicalLevel N from the
 * planet to produce one good blueprint of technicalLevel N+1 for the planet.
 * The cost of research is equal to the cost of labor.
 *
 * @author abdalla bushnaq
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
            status             = ProductionFacilityStatus.RESEARCHING;
            lastProductionTime = currentTime;
        }
    }

    @Override
    public String getName() {
        return "L-" + producedGood.type.getName().substring(2);
    }

    @Override
    public float queryProfit() {
        return 0;
    }

    public int queryEngineersNeeded() {
        return producedGood.type.getTechnicalLevel() * 100;
    }
}
