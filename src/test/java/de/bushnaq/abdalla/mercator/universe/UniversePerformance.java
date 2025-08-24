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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bushnaq.abdalla.mercator.universe;

import de.bushnaq.abdalla.mercator.desktop.DesktopContextFactory;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author bushnaq
 */
public class UniversePerformance {
    private static final int MAX_CREATE_TIME                 = 1000;
    private static final int MAX_MILLINIUM_TIME              = 1000;
    private static final int UNIVERSE_GENERATION_RANDOM_SEED = 5;

    @Test
    public void advanceInTime() throws Exception {
        System.out.println("-----------------------------------------------------------------------");
        final long     time     = System.currentTimeMillis();
        final Universe universe = new Universe("U-fast", GraphicsDimentions.D2, EventLevel.none, null);
        universe.timeDelta = 100L;
        universe.create(new GameEngine3D(new DesktopContextFactory(), universe, LaunchMode.normal), UNIVERSE_GENERATION_RANDOM_SEED, 10, 100000L * TimeUnit.TICKS_PER_DAY);
        System.out.printf("%d planets\n", universe.planetList.size());
        System.out.printf("%d traders\n", universe.traderList.size());
        final long duration = System.currentTimeMillis() - time;
        assertTrue(duration < MAX_MILLINIUM_TIME, "Advancing time is taking too long");
    }

    @Test
    public void advanceSlowInTime() throws Exception {
        System.in.read();
        System.out.println("-----------------------------------------------------------------------");
        final long     time     = System.currentTimeMillis();
        final Universe universe = new Universe("U-slow", GraphicsDimentions.D2, EventLevel.none, null);
        universe.timeDelta = 10L;
        universe.create(new GameEngine3D(new DesktopContextFactory(), universe, LaunchMode.normal), UNIVERSE_GENERATION_RANDOM_SEED, 10, 100000L * TimeUnit.TICKS_PER_DAY);
        System.out.printf("%d planets\n", universe.planetList.size());
        System.out.printf("%d traders\n", universe.traderList.size());
        final long duration = System.currentTimeMillis() - time;
        assertTrue(duration < MAX_MILLINIUM_TIME, "Advancing time is taking too long");
    }

    @Test
    public void create() throws Exception {
        // ---create 10 different universes with 10 different random seeds
        for (int universeIndex = 0; universeIndex < 10; universeIndex++) {
            for (int i = 1; i < 11; i++) {
                System.out.println("-----------------------------------------------------------------------");
                final long time = System.currentTimeMillis();
                System.out.printf("Universe seed = %d\n", universeIndex);
                final Universe universe = new Universe("U-create", GraphicsDimentions.D2, EventLevel.none, null);
                universe.create(new GameEngine3D(new DesktopContextFactory(), universe, LaunchMode.normal), universeIndex, i, 100L * TimeUnit.TICKS_PER_DAY);
                System.out.printf("%d planets, %d traders\n", universe.planetList.size(), universe.traderList.size());
                final long duration = System.currentTimeMillis() - time;
                assertTrue(duration < MAX_CREATE_TIME, String.format("Creading universe [seed %d and size %d] is taking too long (> 1000ms) %dms", universeIndex, i, duration));
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final UniversePerformance testObject = new UniversePerformance();
        testObject.advanceInTime();
    }
}
