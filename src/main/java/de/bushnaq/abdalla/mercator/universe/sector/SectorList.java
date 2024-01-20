package de.bushnaq.abdalla.mercator.universe.sector;

import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.tools.Tools;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SectorList extends Vector<Sector> {
	public static final int ABANDONED_SECTOR_INDEX = 0;

	// private static final long serialVersionUID = 4497410859034679358L;
	public Sector[][] sectorMap;

	public void createSectors(final int numberOfSectors) {
		clear();
		{
			int count = 0;
			add(new Sector(0, "Abandoned"));
			for (int i = 1; i <= numberOfSectors; i++) {
				count++;
				add(new Sector(i, "S-" + i));
			}
			Tools.print(String.format("generated %d sectors.\n", count));
		}
		sectorMap = new Sector[numberOfSectors * 2][numberOfSectors * 2];
	}

	public Sector getAbandonedSector() {
		return get(ABANDONED_SECTOR_INDEX);
	}
}
