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

public class SimEvent extends Event {
    public float        credits;
    public SimEventType eventType;
    public int          volume;
//    public String       what;
//    public long         when;

    public SimEvent(final long when, final Object who, final int volume, final SimEventType eventType, final float credits, final String what) {
        super(EventLevel.info, when, who, what);
//        this.when      = when;
        this.volume    = volume;
        this.eventType = eventType;
        this.credits   = credits;
//        this.what      = what;
    }
}
