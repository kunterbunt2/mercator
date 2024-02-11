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

package de.bushnaq.abdalla.mercator.universe;

import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.SimStatus;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 * 36,00 export has stopped for one year
 */
public class Seed5TradingStabilityTest {
    private static final int UNIVERSE_SIZE = 10;

    @Test
    public void testTrading() throws Exception {
        final Universe[] universeList = new Universe[1];
        // ---create 10 different universes with 10 different random seeds
        for (int i = 0; i < universeList.length; i++) {
            universeList[i]           = new Universe("U-" + i, GraphicsDimentions.D2, EventLevel.none, null);
            universeList[i].timeDelta = 100L;
            universeList[i].create(5, UNIVERSE_SIZE, 100L * TimeUnit.TICKS_PER_DAY);
        }
        for (float i = 1; i < 15f; i++) {
            for (int universeIndex = 0; universeIndex < universeList.length; universeIndex++) {
                final Universe universe = universeList[universeIndex];
                universe.advanceInTime(TimeUnit.DAYS_PER_YEAR * TimeUnit.TICKS_PER_DAY);
                // long time = System.currentTimeMillis();
                float   anualExportAmountTotal       = 0;
                float   anualImportAmountTotal       = 0;
                float   anualLocalCreditsEarnedTotal = 0;
                float   anualLocalCreditsSpentTotal  = 0;
                boolean oneSimIsNotStarving          = false;
                for (final Planet planet : universe.planetList) {
                    anualExportAmountTotal += planet.getHistoryManager().getAnualExportAmountOfGoods();
                    anualImportAmountTotal += planet.getHistoryManager().getAnualImportAmountOfGoods();
                    anualLocalCreditsEarnedTotal += planet.getHistoryManager().getAnualLocalCreditsEarned();
                    anualLocalCreditsSpentTotal += planet.getHistoryManager().getAnualLocalCreditsSpent();
                    for (final Sim sim : planet.simList) {
                        if (sim.status != SimStatus.STARVING_NO_MONEY)
                            oneSimIsNotStarving = true;
                    }
                }
                // ---are any sims not starving?
                assertTrue(oneSimIsNotStarving, String.format("in year %s in universe [seed %d and size %d] all sims where starving", TimeUnit.toString(universe.currentTime), universeIndex, UNIVERSE_SIZE));
                // ---is commerce happening in our universe?
                assertTrue(anualExportAmountTotal > 0f, String.format("in year %s in universe [seed %d and size %d] export has stopped for one year", TimeUnit.toString(universe.currentTime), universeIndex, UNIVERSE_SIZE));
                assertTrue(anualImportAmountTotal > 0f, String.format("in year %s in universe [seed %d and size %d] import has stopped for one year", TimeUnit.toString(universe.currentTime), universeIndex, UNIVERSE_SIZE));
                assertTrue(anualLocalCreditsEarnedTotal > 0f, String.format("in year %s in universe [seed %d and size %d] local credit earning has stopped for one year", TimeUnit.toString(universe.currentTime), universeIndex, UNIVERSE_SIZE));
                assertTrue(anualLocalCreditsSpentTotal > 0f, String.format("in year %s in universe [seed %d and size %d] local credits earning has stopped for one year", TimeUnit.toString(universe.currentTime), universeIndex, UNIVERSE_SIZE));
                // long duration = System.currentTimeMillis()-time;
            }
        }
    }
}
