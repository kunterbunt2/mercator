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

import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final Class<?>    classFilter;
    private final List<Event> filteredList     = new ArrayList<Event>();
    private final EventLevel  level;
    private final Logger      logger           = LoggerFactory.getLogger(this.getClass());
    public        List<Event> eventList        = new ArrayList<Event>();
    private       boolean     enablePrintEvent = false;
    private       Object      objectFilter;

    /**
     * @param level  set to a specific EventLevel
     * @param filter set to a specific class or to null to disable filtering
     */
    public EventManager(final EventLevel level, final Class<?> filter) {
        this.level       = level;
        this.classFilter = filter;
    }

    public void add(final EventLevel level, final long when, final Object who, final String what) {
        if (level.ordinal() >= this.level.ordinal() && (classFilter == null || classFilter.isAssignableFrom(who.getClass()))) {
            final Event e = new Event(level, when, who, what);
            eventList.add(e);
            if (e.who == objectFilter) {
                filteredList.add(e);
                if (enablePrintEvent)
                    if (Sim.class.isInstance(e.who))
                        logger.info(String.format("%s %s %s %s", TimeUnit.toString(e.when), e.level.name(), ((Trader) e.who).getName(), e.what));
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

    public boolean isEnabled() {
        return !level.equals(EventLevel.none);
    }

    public void setEnablePrintEvent(final boolean enablePrintEvent) {
        this.enablePrintEvent = enablePrintEvent;
    }

    public void setObjectFilter(final Object objectFilter) {
        this.objectFilter = objectFilter;
    }
}
