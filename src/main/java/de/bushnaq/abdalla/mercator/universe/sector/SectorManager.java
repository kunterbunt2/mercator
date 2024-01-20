package de.bushnaq.abdalla.mercator.universe.sector;

import java.util.HashMap;

public class SectorManager extends HashMap<Sector, Count> {
	private static final long serialVersionUID = -4033227627491402580L;

	public void add(final Sector sector) {
		final Count count = get(sector);
		if (count != null) {
			count.count++;
		} else {
			put(sector, new Count(1));
		}
	}

	public Sector getSector() {
		int maxCount = 0;
		Sector maxSector = null;
		for (final Sector sector : this.keySet()) {
			if (get(sector).count > maxCount) {
				maxCount = get(sector).count;
				maxSector = sector;
			}
		}
		if (maxCount >= 2) {
			return maxSector;
		}
		return null;
	}
}
