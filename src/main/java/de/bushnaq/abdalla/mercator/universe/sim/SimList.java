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

package de.bushnaq.abdalla.mercator.universe.sim;

import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.tools.Tools;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;

import java.util.Vector;

public class SimList extends Vector<Sim> {
    private static final long serialVersionUID = 3312851264723041047L;
    Planet planet;

    public SimList(final Planet planet) {
        this.planet = planet;
    }

    public void advanveInTime(final long currentTime, final MercatorRandomGenerator randomGenerator, final Planet planet) {
        int i = 0;
        while (i < size()) {
            final Sim sim = get(i);
            if (sim.advanveInTime(currentTime, randomGenerator, this)) {
                planet.setCredits(planet.getCredits() + sim.getCredits());
                sim.setCredits(0);
                kill(sim);
                planet.universe.deadSimStatistics.add(currentTime);
            } else {
                i++;
            }
        }
    }

    public void create(final Planet planet, final float credits, final int size) {
        final int lastIndex = size();
        for (int i = lastIndex; i < lastIndex + size; i++) {
            final String name = "S-" + i;
            final Sim    sim  = new Sim(planet, name, credits);
            add(sim);
        }
    }

    public Sim getFirstUnemployed() {
        for (final Sim sim : this) {
            if (sim.profession == SimProfession.UNIMPLOYED)
                return sim;
        }
        return null;
    }

    public int getNumberOfUnenployed() {
        int count = 0;
        for (final Sim sim : this) {
            if (sim.profession == SimProfession.UNIMPLOYED)
                count++;
        }
        return count;
    }

    public float getTotalCost() {
        float cost = 0.0f;
        for (final Sim sim : this) {
            cost += sim.cost;
        }
        return cost;
    }

    public void kill(final Sim sim) {
        Tools.print(String.format("%s.%s is dead.\n", sim.planet.getName(), sim.getName()));
        planet.remove(sim);
        remove(sim);
    }
}
