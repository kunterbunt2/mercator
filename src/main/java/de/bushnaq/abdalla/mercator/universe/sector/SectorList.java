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

package de.bushnaq.abdalla.mercator.universe.sector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SectorList extends Vector<Sector> {
    public static final int    ABANDONED_SECTOR_INDEX = 0;
    private final       Logger logger                 = LoggerFactory.getLogger(this.getClass());

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
