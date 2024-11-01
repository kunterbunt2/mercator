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

import de.bushnaq.abdalla.mercator.universe.event.Event;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.EventManager;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 * 36,00 export has stopped for one year
 */
public class RandomGeneratorTest {
    private static final int MAX_TEXT_LENGTH                 = 100000;
    private static final int UNIVERSE_GENERATION_RANDOM_SEED = 0;

    @Test
    public void testParralelReproducability() throws Exception {
        final EventManager            eventManager0 = new EventManager(EventLevel.all, null);
        final EventManager            eventManager1 = new EventManager(EventLevel.all, null);
        final MercatorRandomGenerator g0            = new MercatorRandomGenerator(UNIVERSE_GENERATION_RANDOM_SEED, eventManager0);
        final MercatorRandomGenerator g1            = new MercatorRandomGenerator(UNIVERSE_GENERATION_RANDOM_SEED, eventManager1);
        final int[]                   results0      = new int[MAX_TEXT_LENGTH];
        final int[]                   results1      = new int[MAX_TEXT_LENGTH];
        for (int i = 0; i < MAX_TEXT_LENGTH; i++) {
            results0[i] = g0.nextInt(i, this, 3);
            float empty = (float) Math.random();
            results1[i] = g1.nextInt(i, this, 3);
            empty       = (float) Math.random();
            if (results0[i] != results1[i]) {
                final List<Event> eventList0 = eventManager0.eventList;
                final List<Event> eventList1 = eventManager1.eventList;
                for (int e = 0; e < Math.min(eventList0.size(), eventList1.size()); e++) {
                    final Event event0 = eventList0.get(e);
                    final Event event1 = eventList1.get(e);
                    if (event0.when != event1.when || !event0.what.equals(event1.what)) {
                        System.out.printf("%s %s %s %s\n", "g0", TimeUnit.toString(event0.when), event0.getWhosName(), event0.what);
                        for (final StackTraceElement trace : event0.stackTrace)
                            System.out.println("\t" + trace);
                        System.out.printf("%s %s %s %s\n", "g1", TimeUnit.toString(event1.when), event1.getWhosName(), event1.what);
                        for (final StackTraceElement trace : event1.stackTrace)
                            System.out.println("\t" + trace);
                        System.out.printf("-\n");
                    }
                }
                assertEquals(eventList0.size(), eventList1.size(), String.format("in year %d event size is different", g0.index));
            }
        }
    }

    @Test
    public void testSerializedReproducability() throws Exception {
        final EventManager            eventManager0 = new EventManager(EventLevel.all, null);
        final EventManager            eventManager1 = new EventManager(EventLevel.all, null);
        final MercatorRandomGenerator g0            = new MercatorRandomGenerator(UNIVERSE_GENERATION_RANDOM_SEED, eventManager0);
        final MercatorRandomGenerator g1            = new MercatorRandomGenerator(UNIVERSE_GENERATION_RANDOM_SEED, eventManager1);
        final int[]                   results0      = new int[MAX_TEXT_LENGTH];
        final int[]                   results1      = new int[MAX_TEXT_LENGTH];
        for (int i = 0; i < MAX_TEXT_LENGTH; i++) {
            results0[i] = g0.nextInt(i, this, 3);
            final float empty = (float) Math.random();
        }
        for (int i = 0; i < MAX_TEXT_LENGTH; i++) {
            results1[i] = g1.nextInt(i, this, 3);
            final float empty = (float) Math.random();
        }
        //		System.out.println(results0[1818]);
        for (int i = 0; i < MAX_TEXT_LENGTH; i++) {
            if (results0[i] != results1[i]) {
                final List<Event> eventList0 = eventManager0.eventList;
                final List<Event> eventList1 = eventManager1.eventList;
                for (int e = 0; e < Math.min(eventList0.size(), eventList1.size()); e++) {
                    final Event event0 = eventList0.get(e);
                    final Event event1 = eventList1.get(e);
                    if (event0.when != event1.when || !event0.what.equals(event1.what)) {
                        System.out.printf("%s %s %s %s\n", "g0", TimeUnit.toString(event0.when), event0.getWhosName(), event0.what);
                        for (final StackTraceElement trace : event0.stackTrace)
                            System.out.println("\t" + trace);
                        System.out.printf("%s %s %s %s\n", "g1", TimeUnit.toString(event1.when), event1.getWhosName(), event1.what);
                        for (final StackTraceElement trace : event1.stackTrace)
                            System.out.println("\t" + trace);
                        System.out.printf("-\n");
                    }
                }
                assertEquals(eventList0.size(), eventList1.size(), String.format("in year %d event size is different", g0.index));
            }
        }
    }
}
