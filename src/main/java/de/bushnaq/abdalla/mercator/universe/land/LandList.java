package de.bushnaq.abdalla.mercator.universe.land;

import java.util.Vector;

public class LandList extends Vector<Land> {

	public int getIndex(final Land land) {
		int index = 0;
		for (final Land p : this) {
			if (p == land)
				return index;
			index++;
		}
		return -1;
	}

}
