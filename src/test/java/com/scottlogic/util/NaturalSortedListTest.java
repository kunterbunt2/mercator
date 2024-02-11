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

package com.scottlogic.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NaturalSortedListTest {

    //Test whether the NaturalSortedList sorts integer into the expected order.
    @Test
    public void testWithIntegers() {
        final NaturalSortedList<Integer> nsl     = new NaturalSortedList<Integer>();
        final List<Integer>              regList = new ArrayList<Integer>();

        for (int i = 0; i < 1000; i++) {
            final int toAdd = i % 10;
            nsl.add(toAdd);
            nsl.add(0 - toAdd); //add negatives too..
            regList.add(toAdd);
            regList.add(0 - toAdd);
        }

        Collections.sort(regList); //should put it in the same order as nsl list..

        assertEquals(regList.size(), nsl.size());
        final Iterator<Integer> nslItr     = nsl.iterator();
        final Iterator<Integer> regListItr = regList.iterator();
        while (regListItr.hasNext()) {
            assertEquals(regListItr.next(), nslItr.next());
        }
    }
}
