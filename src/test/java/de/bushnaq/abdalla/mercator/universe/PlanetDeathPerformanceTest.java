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

import de.bushnaq.abdalla.mercator.desktop.DesktopContextFactory;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 * 36,00 export has stopped for one year
 */
public class PlanetDeathPerformanceTest {
    private static final int UNIVERSE_SIZE = 10;

    @Test
    public void testDeath() throws Exception {
        final Universe[] universeList = new Universe[1];
        // ---create 10 different universes with 10 different random seeds
        for (int i = 0; i < universeList.length; i++) {
            final Universe universe = new Universe("U-" + i, GraphicsDimentions.D2, EventLevel.none, null);
            universeList[i] = universe;
                              universe.timeDelta = 100L;
            universe.create(new GameEngine3D(new DesktopContextFactory(), universe, LaunchMode.normal), i, UNIVERSE_SIZE, 0);
        }
        Universe  bestUniverse = null;
        long      bestTime     = 0;
        final int limit        = 0/* universeList[0].traderList.size() / 10 */;
        for (float i = 1; i < 10f; i++) {
            for (final Universe universe : universeList) {
                universe.advanceInTime(100 * TimeUnit.TICKS_PER_DAY);
                if (universe.deadPlanetList.size() <= limit) {
                    bestTime     = universe.currentTime;
                    bestUniverse = universe;
                }
            }
        }
        if (!bestUniverse.deadPlanetList.isEmpty()) {
            bestUniverse.deadPlanetList.get(0).eventManager.print(System.out);
            for (final Universe universe : universeList) {
                System.out.printf("%s\n", TimeUnit.toString(universe.deadPlanetList.get(0).currentTime));
                // System.out.printf( "%s\n", TimeUnit.toString( universe.deadTraderList.get(
                // universe.deadTraderList.size() - 1 ).lastTimeAdvancement ) );
            }
        }
        fail(String.format("Best result was %s in %s", bestUniverse.getName(), TimeUnit.toString(bestTime)));
    }
}
