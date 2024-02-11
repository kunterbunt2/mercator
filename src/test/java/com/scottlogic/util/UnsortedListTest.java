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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the UnsortedList class, based on the correctness of java.util.ArrayList.
 *
 * @author mrhodes
 */
public class UnsortedListTest {

    //Tests whether the add at index method works..
    @Test
    public void addElementToHeadOfList() {
        final UnsortedList<Integer> ul = new UnsortedList<Integer>();
        final List<Integer>         al = new ArrayList<Integer>();

        //add some initial values..
        for (int i = 0; i < 2; i++) {
            al.add(0, i);
            ul.addToHead(i);
        }

        //check the lists are equal..
        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    //Tests adding some elements to the end of the list to see if order is preserved..
    @Test
    public void addElementsToEndOfList() {

        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            al.add(i);
            ul.add(i);
        }

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    //Tests running a million iterations of add/remove/contains calls based on Random data..
    @Test
    public void randomTest() {
        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        final Random rand = new Random(0); //use the same seed so it won't be affect bty when it's run..
        for (int i = 0; i < 1000000; i++) {
            final double r = rand.nextDouble();
            if (r < 1.0 / 3.0) { //add an element at some position..
                if (!al.isEmpty()) {
                    final int indexToAdd = rand.nextInt(al.size());
                    final int valueToAdd = rand.nextInt(1000000);
                    al.add(indexToAdd, valueToAdd);
                    ul.add(indexToAdd, valueToAdd);
                }
            } else if (r < 2.0 / 3.0) { //remove an element if possible..
                if (!al.isEmpty()) {
                    final boolean byIndex = rand.nextBoolean();
                    if (byIndex) {
                        final int indexToRemove = rand.nextInt(al.size());
                        al.remove(indexToRemove);
                        ul.remove(indexToRemove);
                    } else { //by object..
                        final int valueToRemove = rand.nextInt(1000000);
                        assertEquals(al.remove(new Integer(valueToRemove)), ul.remove(new Integer(valueToRemove)));
                    }
                }
            } else { //run the contains method..
                final int valueToFind = rand.nextInt(1000000);
                assertEquals(al.contains(valueToFind), ul.contains(valueToFind));
            }
        }

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();

        //remove at the end..
        while (ulItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    //Tests whether the add at index method works..
    @Test
    public void testAddAtIndex() {
        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        //add some initial values..
        for (int i = 0; i < 10; i++) {
            al.add(i);
            ul.add(i);
        }

        //add elements at different indices (front, middle and end).
        al.add(0, -1);
        ul.add(0, -1);
        al.add(0, -2);
        ul.add(0, -2);

        al.add(2, -3);
        ul.add(2, -3);
        al.add(5, -4);
        ul.add(5, -4);

        al.add(al.size(), -5);
        ul.add(ul.size(), -5);
        al.add(al.size(), -6);
        ul.add(ul.size(), -6);

        //check the lists are equal..
        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    @Test
    public void testContains() {
        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        //add lots of elements multiple times..
        for (int i = 1; i < 10; i++) {
            al.add(i);
            ul.add(i);
        }

        //add lots of elements multiple times..
        for (int i = 0; i < 11; i++) {
            assertEquals(al.contains(i), ul.contains(i));
        }
    }

    //Tests all the method of the iterator..
    @Test
    public void testItertor() {

        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            al.add(i);
            ul.add(i);
        }

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();

        //remove first..
        ulItr.next();
        ulItr.remove();
        alItr.next();
        alItr.remove();

        //remove somewhere in middle (enough times to cause rebalance)..
        for (int i = 0; i < 50; i++) {
            ulItr.next();
            alItr.next();
        }

        for (int i = 0; i < 2; i++) {
            final int a = alItr.next();
            System.out.println(a);
            alItr.remove();
            final int u = ulItr.next();
            System.out.println(u);
            ulItr.remove();
        }

        //remove at the end..
        while (ulItr.hasNext()) {
            ulItr.next();
            alItr.next();
        }
        ulItr.remove();
        alItr.remove();

        //see if the lists are the same..
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    //Tests the remove by index method..
    @Test
    public void testRemoveIndex() {

        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        //add lots of elements multiple times..
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 100; i++) {
                al.add(i);
                ul.add(i);
            }
        }

        //try removing some (Head middle near the head and the end..
        assertEquals(al.remove(0), ul.remove(0));
        assertEquals(al.remove(1), ul.remove(1));
        assertEquals(al.remove(al.size() - 1), ul.remove(ul.size() - 1));

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    //Tests the remove Object method..
    @Test
    public void testRemoveObject() {

        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        //add lots of elements multiple times..
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 100; i++) {
                al.add(i);
                ul.add(i);
            }
        }

        //try removing some..
        for (int i = 0; i < 100; i++) {
            assertEquals(al.remove((Object) i), ul.remove((Object) i));
        }

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();
        while (alItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

    @Test
    public void testSet() {
        final List<Integer> ul = new UnsortedList<Integer>();
        final List<Integer> al = new ArrayList<Integer>();

        //add initial values..
        for (int i = 0; i < 10; i++) {
            al.add(i);
            ul.add(i);
        }

        ul.set(0, 5);
        al.set(0, 5);

        ul.set(5, -5);
        al.set(5, -5);

        ul.set(ul.size() - 1, -5);
        al.set(ul.size() - 1, -5);

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();

        //remove at the end..
        while (ulItr.hasNext()) {
            assertEquals(alItr.next(), ulItr.next());
        }
    }

}
