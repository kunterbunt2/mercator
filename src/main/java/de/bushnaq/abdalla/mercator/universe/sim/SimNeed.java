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

package de.bushnaq.abdalla.mercator.universe.sim;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SimNeed implements Cloneable, Comparable<SimNeed> {
    public long     consumeEvery; // ---Needs to consume before this time to keep satisfied
    public float    creditLimit; // sim has to own at least this much of credits to think of buying this good
    public long     dieIfNotConsumedWithin;
    public long     lastConsumed; // ---Last time the sim has consumed this good
    public int      totalConsumed;
    public GoodType type;

    public SimNeed(final GoodType food, final long consumeEvery, final long dieIfNotConsumedWithin, final float creditLimit) {
        this.type                   = food;
        this.consumeEvery           = consumeEvery;
        this.dieIfNotConsumedWithin = dieIfNotConsumedWithin;
        this.creditLimit            = creditLimit;
    }

    @Override
    public SimNeed clone() throws CloneNotSupportedException {
        final SimNeed simNeeds = (SimNeed) super.clone();
        return simNeeds;
    }

    @Override
    public int compareTo(final SimNeed compareObject) {
        if (lastConsumed < compareObject.lastConsumed)
            return -1;
        else if (lastConsumed == compareObject.lastConsumed)
            return 0;
        else
            return 1;
    }

    public void consume(final long currentTime) {
        lastConsumed = currentTime;
        totalConsumed++;
    }
}
