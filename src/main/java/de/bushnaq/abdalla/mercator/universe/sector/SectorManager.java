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
        int    maxCount  = 0;
        Sector maxSector = null;
        for (final Sector sector : this.keySet()) {
            if (get(sector).count > maxCount) {
                maxCount  = get(sector).count;
                maxSector = sector;
            }
        }
        if (maxCount >= 2) {
            return maxSector;
        }
        return null;
    }
}
