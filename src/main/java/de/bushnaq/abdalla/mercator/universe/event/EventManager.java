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

package de.bushnaq.abdalla.mercator.universe.event;

import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.Debug;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final Class<?>    classFilter;
    private       boolean     enablePrintEvent = false;
    public        List<Event> eventList        = new ArrayList<Event>();
    private final List<Event> filteredList     = new ArrayList<Event>();
    private final EventLevel  level;
    private final Logger      logger           = LoggerFactory.getLogger(this.getClass());
    private       Object      objectFilter;

    /**
     * @param level  set to a specific EventLevel
     * @param filter set to a specific class or to null to disable filtering
     */
    public EventManager(final EventLevel level, final Class<?> filter) {
        this.level       = level;
        this.classFilter = filter;

        // Remove events.txt file at startup to start with a clean log
        File eventsFile = new File("events.txt");
        if (eventsFile.exists()) {
            boolean deleted = eventsFile.delete();
            if (!deleted) {
                logger.warn("Could not delete existing events.txt file");
            }
        }
    }

    public void add(final EventLevel level, final long when, final Object who, final String what) {
        if (level.ordinal() >= this.level.ordinal() && (classFilter == null || classFilter.isAssignableFrom(who.getClass()))) {
            final Event e = new Event(level, when, who, what);
            eventList.add(e);
            if (e.who == objectFilter) {
                filteredList.add(e);

                // Write to events.txt file with different formats based on object type

                if (enablePrintEvent) {
                    if (e.who instanceof Trader) {
                        logger.info(String.format("%s %s %s %s", TimeUnit.toString(e.when), e.level.name(), ((Trader) e.who).getName(), e.what));
                    } else if (e.who instanceof Sim) {
                        logger.info(String.format("%s %s %s %s", TimeUnit.toString(e.when), e.level.name(), ((Sim) e.who).getName(), e.what));
                    }
                }
            }
        }
        if (Debug.isFiltered(who)) {
            final Event e = new Event(level, when, who, what);
            writeEventToFile(e);
        }
    }

    public List<Event> filter(final Object objectFilter) {
        filteredList.clear();
        for (final Event e : eventList) {
            if (e.who == objectFilter) {
                filteredList.add(e);
            }
        }
        this.objectFilter = objectFilter;
        return filteredList;
    }

    private String formatEventForObject(Event event) {
        String timeStr  = TimeUnit.toString(event.when);
        String levelStr = event.level.name();

        return switch (event.who) {
            case Trader trader -> String.format("[TRADER ] %10s | %s | %s | %s", timeStr, levelStr, trader.getName(), event.what);
            case Sim sim -> String.format("[SIM    ] %10s | %s | %s | %s", timeStr, levelStr, sim.getName(), event.what);
            case Planet planet -> String.format("[PLANET] %10s | %s | %s | %s", timeStr, levelStr, planet.getName(), event.what);
            default -> String.format("[UNKNOWN] %10s | %s | %s | %s", timeStr, levelStr, event.who.getClass().getSimpleName(), event.what);
        };
    }

    public boolean isEnabled() {
        return !level.equals(EventLevel.none);
    }

    public void setEnablePrintEvent(final boolean enablePrintEvent) {
        this.enablePrintEvent = enablePrintEvent;
    }

    public void setObjectFilter(final Object objectFilter) {
        this.objectFilter = objectFilter;
    }

    private void writeEventToFile(Event event) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("events.txt", true))) {
            String formattedEvent = formatEventForObject(event);
            writer.println(formattedEvent);
        } catch (IOException e) {
            logger.error("Failed to write event to file", e);
        }
    }
}
