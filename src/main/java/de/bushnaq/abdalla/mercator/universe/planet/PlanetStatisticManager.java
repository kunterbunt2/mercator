package de.bushnaq.abdalla.mercator.universe.planet;

import java.util.HashMap;
import java.util.Map;

public class PlanetStatisticManager {
	Map<String, PlanetStatistic> statisticMap = new HashMap<String, PlanetStatistic>();

	public int getAmount(final String name) {
		final PlanetStatistic planetStatistic = statisticMap.get(name);
		if (planetStatistic != null) {
			return planetStatistic.goodAmount;
		} else {
			return 0;
		}
	}

	public void transported(final Planet from, final int amount) {
		PlanetStatistic planetStatistic = statisticMap.get(from.getName());
		if (planetStatistic != null) {
			planetStatistic.goodAmount += amount;
		} else {
			planetStatistic = new PlanetStatistic(amount);
		}
		statisticMap.put(from.getName(), planetStatistic);
	}
}
