package de.bushnaq.abdalla.mercator.universe.sector;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SectorList extends Vector<Sector> {
	public static final int ABANDONED_SECTOR_INDEX = 0;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
			logger.info(String.format("generated %d sectors.", count));
		}
		sectorMap = new Sector[numberOfSectors * 2][numberOfSectors * 2];
	}

	public Sector getAbandonedSector() {
		return get(ABANDONED_SECTOR_INDEX);
	}
}
