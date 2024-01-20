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
