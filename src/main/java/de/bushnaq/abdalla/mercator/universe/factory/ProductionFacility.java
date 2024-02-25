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

import de.bushnaq.abdalla.mercator.renderer.Renderable;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.SimList;
import de.bushnaq.abdalla.mercator.universe.sim.SimProfession;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

public abstract class ProductionFacility extends Renderable {
    protected static final long                     TIME_NEEDED_TO_PRODUCE = 1L * TimeUnit.TICKS_PER_DAY;
    public                 SimList                  engineers;
    public                 int                      lastYearProducedAmount;
    public                 Planet                   planet;
    public                 Good                     producedGood;
    public                 float                    productionProgress;
    public                 ProductionFacilityStatus status                 = ProductionFacilityStatus.RESEARCHING;
    protected              int                      currentProducedAmount;
    long lastProductionTime = 0L;

    public ProductionFacility(final Planet planet, final Good producedGood) {
        this.planet       = planet;
        engineers         = new SimList(planet);
        this.producedGood = producedGood;
        set2DRenderer(new Factory2DRenderer(this));
        set3DRenderer(new Factory3DRenderer(this));
    }

    public void addEngineer(final Sim engineer) {
        engineers.add(engineer);
    }

    public abstract void advanceInTime(long currentTime);

    public void employ(final Sim sim) {
        sim.profession         = SimProfession.ENGINEERING;
        sim.productionFacility = this;
        addEngineer(sim);
    }

    public abstract String getName();

    // public abstract int queryEngineersNeeded();

    public String getStatusName() {
        return status.getName();
    }

    public float queryAverageProfit() {
        // TODO Auto-generated method stub
        return 0;
    }

    public abstract float queryProfit();

    public void removeEngineer(final Sim sim) {
        engineers.remove(sim);
    }

    public void unemploy(final Sim sim) {
        removeEngineer(sim);
        sim.productionFacility = null;
        sim.profession         = SimProfession.UNIMPLOYED;
    }
}
