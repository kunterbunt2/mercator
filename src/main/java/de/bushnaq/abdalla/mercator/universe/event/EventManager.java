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

import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.engine.event.IEvent;
import de.bushnaq.abdalla.engine.event.IEventManager;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetCommunicationPartner;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderCommunicationPartner;
import de.bushnaq.abdalla.mercator.util.Debug;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EventManager implements IEventManager {
    protected final Class<?>        classFilter;
    private         boolean         enablePrintEvent     = true;
    private final   boolean         enabled              = false;
    protected final List<IEvent>    eventList            = new CopyOnWriteArrayList<>();
    private final   String          fileName;
    private final   ExecutorService fileWriterExecutor   = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "EventFileWriter");
        t.setDaemon(true);
        return t;
    });
    private final   List<IEvent>    filteredList         = new ArrayList<>();
    protected final EventLevel      level;
    protected final Logger          logger               = LoggerFactory.getLogger(this.getClass());
    private         Object          objectFilter;
    protected final boolean         writeAllEventsToFile = false; // Write all events to file, not just filtered ones

    public EventManager(final EventLevel level, final Class<?> filter, String fileName) {
        this.level       = level;
        this.classFilter = filter;
        this.fileName    = fileName;
        new File(fileName).delete();
    }

    @Override
    public void add(final EventLevel level, final long when, final Object who, final String what) {
        if (level.ordinal() >= this.level.ordinal() && (classFilter == null || classFilter.isAssignableFrom(who.getClass()))) {
            final Event e = new Event(level, when, who, what);
            eventList.add(e);
            if (e.getWho() == objectFilter) {
                filteredList.add(e);
                if (enablePrintEvent) {
                    logger.info(formatEventForObject(e));
                }
            }
        }
        {
            final Event e = new Event(level, when, who, what);
            if (Debug.isFiltered(who)) {
                String formattedEvent = formatEventForObject(e);
                logger.info(formattedEvent);
                writeEventToFile(e);
            } else if (writeAllEventsToFile) {
                writeEventToFile(e);
            }
        }
    }

    @Override
    public void clear() {
        eventList.clear();
    }

    @Override
    public List<IEvent> filter(final Object objectFilter) {
        filteredList.clear();
        for (final IEvent e : eventList) {
            if (e.getWho() == objectFilter) {
                filteredList.add(e);
            }
        }
        this.objectFilter = objectFilter;
        return filteredList;
    }

    @Override
    public String formatEventForObject(IEvent event) {
        String timeStr  = TimeUnit.toString(event.getWhen(), false);
        String levelStr = event.getLevel().name();

        return switch (event.getWho()) {
            case Trader trader -> String.format("[TRADER ] %10s | %s | %15s | %s", timeStr, levelStr, trader.getName(), event.getWhat());
            case TraderCommunicationPartner tcp -> String.format("[TRADER ] %10s | %s | %15s | %s", timeStr, levelStr, tcp.getName(), event.getWhat());
            case Sim sim -> String.format("[SIM    ] %10s | %s | %15s | %s", timeStr, levelStr, sim.getName(), event.getWhat());
            case Planet planet -> String.format("[PLANET ] %10s | %s | %15s | %s", timeStr, levelStr, planet.getName(), event.getWhat());
            case PlanetCommunicationPartner pcp -> String.format("[PLANET ] %10s | %s | %15s | %s", timeStr, levelStr, pcp.getName(), event.getWhat());
            default -> String.format("[UNKNOWN] %10s | %s | %s | %s", timeStr, levelStr, event.getWho().getClass().getSimpleName(), event.getWhat());
        };
    }

    @Override
    public List<IEvent> getEventList() {
        return eventList;
    }

    @Override
    public String getWhoName(IEvent event) {
        return switch (event.getWho()) {
            case Trader trader -> trader.getName();
            case Sim sim -> sim.getName();
            case Planet planet -> planet.getName();
            default -> event.getWho().getClass().getSimpleName();
        };
    }

    @Override
    public boolean isEnabled() {
        return !level.equals(EventLevel.none);
    }

    @Override
    public void print(final PrintStream out) {
    }

    @Override
    public void setEnablePrintEvent(final boolean enablePrintEvent) {
        this.enablePrintEvent = enablePrintEvent;
    }

    @Override
    public void setObjectFilter(final Object objectFilter) {
        this.objectFilter = objectFilter;
    }

    /**
     * Shutdown the event manager and wait for pending file writes to complete
     */
    @Override
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

    @Override
    public void writeEventToFile(IEvent event) {
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
