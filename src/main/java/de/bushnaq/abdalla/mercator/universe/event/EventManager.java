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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager {
    protected final Class<?>        classFilter;
    private         boolean         enablePrintEvent     = true;
    public          boolean         enabled              = false;
    public          List<Event>     eventList            = new ArrayList<Event>();
    private final   String          fileName;
    private final   ExecutorService fileWriterExecutor   = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "EventFileWriter");
        t.setDaemon(true);
        return t;
    });
    private final   List<Event>     filteredList         = new ArrayList<Event>();
    protected final EventLevel      level;
    private final   Logger          logger               = LoggerFactory.getLogger(this.getClass());
    private         Object          objectFilter;
    private final   boolean         writeAllEventsToFile = false; // Write all events to file, not just filtered ones

    public EventManager(final EventLevel level, final Class<?> filter, String fileName) {
        this.level       = level;
        this.classFilter = filter;
        this.fileName    = fileName;
        new File(fileName).delete();
    }

    public void add(final EventLevel level, final long when, final Object who, final String what) {
        if (level.ordinal() >= this.level.ordinal() && (classFilter == null || classFilter.isAssignableFrom(who.getClass()))) {
            final Event e = new Event(level, when, who, what);
            eventList.add(e);
            if (e.who == objectFilter) {
                filteredList.add(e);
                if (enablePrintEvent) {
                    logger.info(formatEventForObject(e));
                }
            }
        }
        {
            final Event e              = new Event(level, when, who, what);
            String      formattedEvent = formatEventForObject(e);
            if (Debug.isFiltered(who)) {
                logger.info(formattedEvent);
                writeEventToFile(e);
            } else if (writeAllEventsToFile) {
                writeEventToFile(e);
            }
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
            case Trader trader -> String.format("[TRADER ] %10s | %s | %15s | %s", timeStr, levelStr, trader.getName(), event.what);
            case Sim sim -> String.format("[SIM    ] %10s | %s | %15s | %s", timeStr, levelStr, sim.getName(), event.what);
            case Planet planet -> String.format("[PLANET ] %10s | %s | %15s | %s", timeStr, levelStr, planet.getName(), event.what);
            default -> String.format("[UNKNOWN] %10s | %s | %s | %s", timeStr, levelStr, event.who.getClass().getSimpleName(), event.what);
        };
    }

    private String getWhoName(Event event) {
        return switch (event.who) {
            case Trader trader -> trader.getName();
            case Sim sim -> sim.getName();
            case Planet planet -> planet.getName();
            default -> event.who.getClass().getSimpleName();
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

    /**
     * Shutdown the event manager and wait for pending file writes to complete
     */
    public void shutdown() {
        fileWriterExecutor.shutdown();
        try {
            if (!fileWriterExecutor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                logger.warn("File writer executor did not terminate gracefully, forcing shutdown");
                fileWriterExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fileWriterExecutor.shutdownNow();
        }
    }

    private void writeEventToFile(Event event) {
        fileWriterExecutor.submit(() -> {
            try {
                // Create directory if it doesn't exist
                File debugDir = new File("debug/events");
                if (!debugDir.exists()) {
                    debugDir.mkdirs();
                }

                try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
                    String formattedEvent = formatEventForObject(event);
                    writer.println(formattedEvent);
                } catch (IOException e) {
                    logger.error("Failed to write event to file", e);
                }
            } catch (Exception e) {
                logger.error("Unexpected error in file writing task", e);
            }
        });
    }
}
