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
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 * 36,00 export has stopped for one year
 */
public class SimDeathPerformanceTest {
    private static final int UNIVERSE_SIZE = 10;

    @Test
    public void testTrading() throws Exception {
        final Universe[] universeList = new Universe[2];
        // ---create 10 different universes with 10 different random seeds
        for (int i = 0; i < universeList.length; i++) {
            final Universe universe = new Universe("U-" + i, GraphicsDimentions.D2, EventLevel.all, null);
            universeList[i] = universe;
                              universe.timeDelta = 20L;
                              universe.useFixedDelta = true;
            universe.create(i, UNIVERSE_SIZE, 0);
        }
        Universe  bestUniverse = null;
        long      bestTime     = 0;
        final int limit        = 0/* universeList[0].traderList.size() / 10 */;
        for (float i = 1; i < 20; i++) {
            for (final Universe universe : universeList) {
                universe.advanceInTime(100 * TimeUnit.TICKS_PER_DAY);//advance one year
                if (universe.deadSimList.size() <= limit) {
                    bestTime     = universe.currentTime;
                    bestUniverse = universe;
                }
            }
        }
        for (final Universe universe : universeList) {
            System.out.printf("-------------------------------------------------------\n");
            System.out.printf(universe.getName() + "\n");
            //			System.out.printf("%s\n", TimeUnit.toString(universe.deadSimList.get(0).lastTimeAdvancement));
            System.out.printf("%s\n", Precision.round((universe.simList.size() / (universe.simList.size() + universe.deadSimList.size())) * 100, 0));
            if (universe.deadSimList.size() > 0)
                universe.deadSimList.get(0).eventManager.print(System.out);
            System.out.printf("-------------------------------------------------------\n");
        }
        if (bestUniverse != null) {
            System.out.println(String.format("Best result was %s in %s", bestUniverse.getName(), TimeUnit.toString(bestTime)));
            //			if (bestUniverse.deadSimList.size() > 0)
            //				bestUniverse.deadSimList.get(0).eventManager.print(System.out);
        } else {
            fail("filaed to find one universe that matches the criteria");
        }
    }
}
