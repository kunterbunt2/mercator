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

package de.bushnaq.abdalla.mercator.util;

import de.bushnaq.abdalla.mercator.universe.event.SimEvent;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PlanetEventManager {
    public List<SimEvent> eventList = new ArrayList<SimEvent>();
    Planet planet;

    public PlanetEventManager(final Planet planet) {
        this.planet = planet;
    }

    public void add(final long when, final int volume, final SimEventType eventType, final float credits, final String what) {
        eventList.add(new SimEvent(when, volume, eventType, credits, what));
    }

    public void print() {
        try (PrintStream out = new PrintStream(planet.getName() + ".txt", "UTF-8")) {
            print(out);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void print(final PrintStream out) {
        out.printf("%s\n", planet.getName());
        out.printf("%3s %4s %4s %7s %8s %s\n", "-ID", "TIME", "-VOL", "CREDITS", "---EVENT", "DESCRIPTION");
        for (final SimEvent simEvent : eventList) {
            out.printf("%s %s %4d %7.2f %8s %s\n", planet.getName(), TimeUnit.toString(simEvent.when), simEvent.volume, simEvent.credits, simEvent.eventType.name, simEvent.what);
        }
    }
}
