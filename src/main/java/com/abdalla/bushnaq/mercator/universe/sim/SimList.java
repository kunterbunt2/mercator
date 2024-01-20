package com.abdalla.bushnaq.mercator.universe.sim;

import java.util.Vector;

import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.universe.tools.Tools;
import com.abdalla.bushnaq.mercator.util.MercatorRandomGenerator;

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
			final Sim sim = new Sim(planet, name, credits);
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
