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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SortedListTest {

    //Comparator for comparing integers..
    Comparator<Integer> comp = new Comparator<Integer>() {
        @Override
        public int compare(final Integer one, final Integer two) {
            return one.intValue() - two.intValue();
        }
    };

    //builds a tree where a double left rotation is required..
    @Test
    public void addWhereDoubleLeftRotateRequired() {
        //first test where change happens as root..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);
        list.add(3);
        list.add(2);

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));

        //second test where problem isn't at the root..
        list.clear();
        list.add(0);
        list.add(-1);
        list.add(1);
        list.add(3);
        list.add(2);

        test.clear();
        test.add(0);
        test.add(-1);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));
    }

    //builds a tree where a double right rotation is required..
    @Test
    public void addWhereDoubleRightRotateRequired() {
        //first test where change is at root..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(3);
        list.add(1);
        list.add(2);

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));

        //second test where it's not at the root..
        list.clear();
        list.add(4);
        list.add(5);
        list.add(3);
        list.add(1);
        list.add(2);

        test.clear();
        test.add(4);
        test.add(5);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));
    }

    //builds a tree where one left rotation is required..
    @Test
    public void addWhereLeftRotateRequired() {
        //first test when change happens at the root..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);
        list.add(2);
        list.add(3);

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));

        //second test where not at the root..
        list.clear();
        list.add(0);
        list.add(-1);
        list.add(1);
        list.add(2);
        list.add(3);

        test.clear();
        test.add(0);
        test.add(-1);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));
    }

    //builds a tree where one right rotation is required..
    @Test
    public void addWhereRightRotateRequired() {
        //first test where needs to be done at root..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(3);
        list.add(2);
        list.add(1);

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));

        //second test where not at the root..
        list.clear();
        list.add(0);
        list.add(-1);
        list.add(3);
        list.add(2);
        list.add(1);

        test.clear();
        test.add(0);
        test.add(-1);
        test.add(2);
        test.add(1);
        test.add(3);

        assertTrue(list.structurallyEqualTo(test));
    }

    //Tests adding some elements to the end of the list then removing
    //at the front middle and end..
    @Test
    public void addingToEndAndRemoving() {

        final List<Integer> ul = new SortedList<Integer>(comp);
        final List<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 6; i++) {
            al.add(i);
            ul.add(i);
        }

        final Iterator<Integer> ulItr = ul.iterator();
        final Iterator<Integer> alItr = al.iterator();

        alItr.next();
        ulItr.next();
        final int a = alItr.next();
        System.out.println(a);
        alItr.remove();
        final int u = ulItr.next();
        System.out.println(u);
        ulItr.remove();

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

    //Tests whether the tree remains balanced after many additions and removals.
    @Test
    public void checkBalance() {
        //build a random tree..
        final Random              rand = new Random(0);
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 10000; i++) {
            list.add(rand.nextInt(1000));
        }
        for (int i = 0; i < 400; i++) {
            list.remove(i);
        }

        System.out.println("MinBalance: " + list.minBalanceFactor());
        System.out.println("MaxBalance: " + list.maxBalanceFactor());

        assertTrue(list.minBalanceFactor() > -2);
        assertTrue(list.maxBalanceFactor() < 2);
    }

    //Tests the clear method..
    @Test
    public void clear() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);
        list.add(2);
        list.add(3);
        list.clear();

        assertTrue(!list.contains(1));
    }

    //Sees if the contains method works as expected..
    @Test
    public void contains() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(7);
        list.add(8);

        for (int i = 1; i <= 8; i++) {
            assertTrue(list.contains(i));
        }
        assertTrue(!list.contains(9));
    }

    //Tests the get methods..
    @Test
    public void get() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(i);
            }
        }

        for (int i = 0; i < 300; i++) {
            assertEquals(i / 3, (long) list.get(i));
        }
    }

    //Tests the isEmpty method..
    //@Test
    public void isEmpty() {
        assertTrue(new SortedList<Integer>(comp).isEmpty());
    }

    //Tests if the itr throws concurrent modification errors as expected..
    @SuppressWarnings({"rawtypes"})
    @Test
    public void iteratorModException() {
        //build a tree..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        //get an iterator for it, then modify it..
        final Iterator itr = list.iterator();
        list.add(101);

        ConcurrentModificationException modException = null;
        try {
            itr.next();
        } catch (final ConcurrentModificationException e) {
            modException = e;
        }

        assertTrue(modException != null);
    }

    //Tests if the itr remove method works as expected..
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void iteratorRemove() {
        //build a random tree..
        final Random              rand = new Random(1002);
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100000; i++) {
            list.add(rand.nextInt(10));
        }
        //Get copy of it..
        final List copy = new ArrayList(Arrays.asList(list.toArray()));

        //remove 1st, 3rd and last elements..
        final Iterator itr = list.iterator();
        itr.next();
        itr.remove();
        itr.next();
        itr.next();
        itr.remove();
        while (itr.hasNext()) {
            itr.next();
        }
        itr.remove();

        //do the same with the copy..
        copy.remove(0);
        copy.remove(1);
        copy.remove(copy.size() - 1);

        assertEquals(copy.size(), list.size());

        for (int i = 0; i < copy.size(); i++) {
            assertEquals(copy.get(i), list.get(i));
        }
    }

    @Test
    public void iterator_over_empty_list() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        final Iterator<Integer>   itr  = list.iterator();
        while (itr.hasNext()) {
            itr.next();
        }
    }

    @Test
    public void iterator_remove_all() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 6; i++) {
                list.add(i);
            }
        }

        final Iterator<Integer> itr = list.iterator();
        itr.next();
        itr.remove();
        assertEquals(new Integer(0), itr.next());

        itr.remove();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
        assertTrue(list.isEmpty());
    }

    @Test
    public void iterator_through_the_whole_list() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        final Iterator<Integer> itr = list.iterator();
        int                     i   = 0;
        while (itr.hasNext()) {
            assertEquals(new Integer(i), itr.next());
            i++;
        }
    }

    @Test
    public void listIteraterAdd() {
        assertThrows(UnsupportedOperationException.class, () -> {
            final SortedList<Integer> list = new SortedList<Integer>(comp);
            for (int i = 0; i < 100; i++) {
                list.add(i);
            }
            final ListIterator<Integer> itr = list.listIterator(0);
            itr.next();
            itr.add(5);
        });
    }

    @Test
    public void listIteraterPrevious() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        final ListIterator<Integer> listItr = list.listIterator(100);
        final int                   i       = 0;
        while (listItr.hasNext()) {
            assertEquals(new Integer(99 - i), listItr.hasPrevious());
        }
    }

    @Test
    public void listIteraterSet() {
        assertThrows(UnsupportedOperationException.class, () -> {
            final SortedList<Integer> list = new SortedList<Integer>(comp);
            for (int i = 0; i < 100; i++) {
                list.add(i);
            }
            final ListIterator<Integer> itr = list.listIterator(0);
            itr.next();
            itr.set(5);
        });
    }

    //removes a left leaf where no rebalancing is required..
    @Test
    public void removeLeaf() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(60);
        list.remove(new Integer(60));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(30);
        test.add(15);
        test.add(40);
        test.add(10);
        test.add(20);
        test.add(35);
        test.add(73);
        test.add(3);
        test.add(36);

        assertTrue(list.structurallyEqualTo(test));
    }

    //removes a leaf node where no rebalancing is required..
    @Test
    public void removeLeafWithRebalance() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(60);
        list.remove(new Integer(20));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(30);
        test.add(10);
        test.add(40);
        test.add(3);
        test.add(15);
        test.add(35);
        test.add(73);
        test.add(36);
        test.add(60);

        assertTrue(list.structurallyEqualTo(test));
    }

    //removes the root of the tree so that it needs to be rebalanced..
    @Test
    public void removeNonRootWithTwoChildren() {
        //first where no rebalancing is required..
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(60);
        list.remove(new Integer(40));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(30);
        test.add(15);
        test.add(60);
        test.add(10);
        test.add(20);
        test.add(35);
        test.add(73);
        test.add(3);
        test.add(36);

        assertTrue(list.structurallyEqualTo(test));

        //second where rebalancing is required..
        list.clear();
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(60);
        list.add(83);
        list.add(79);
        list.remove(new Integer(40));

        test.clear();
        test.add(30);
        test.add(15);
        test.add(60);
        test.add(10);
        test.add(20);
        test.add(35);
        test.add(79);
        test.add(3);
        test.add(36);
        test.add(73);
        test.add(83);

        assertTrue(list.structurallyEqualTo(test));
    }

    //removes the root of the tree so that it needs to be rebalanced..
    @Test
    public void removeRoot() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(3);
        list.remove(new Integer(30));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(15);
        test.add(10);
        test.add(35);
        test.add(3);
        test.add(20);
        test.add(40);

        assertTrue(list.structurallyEqualTo(test));
    }

    //Tests removing elements using their index..
    @Test
    public void removeWithIndex() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (int i = 99; i >= 0; i--) {
            list.remove(i);
        }
        assertTrue(list.isEmpty());
    }

    //removes a node that has a left child but no right one and no rebalancing is required..
    @Test
    public void removeWithJustLeftChild() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(60);
        list.remove(new Integer(73));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(30);
        test.add(15);
        test.add(40);
        test.add(10);
        test.add(20);
        test.add(35);
        test.add(60);
        test.add(3);
        test.add(36);

        assertTrue(list.structurallyEqualTo(test));
    }

    //removes a node that has a right child but no left one and no rebalancing is required..
    @Test
    public void removeWithJustRightChild() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(30);
        list.add(15);
        list.add(40);
        list.add(10);
        list.add(20);
        list.add(35);
        list.add(73);
        list.add(3);
        list.add(36);
        list.add(85);
        list.remove(new Integer(73));

        final SortedList<Integer> test = new SortedList<Integer>(comp);
        test.add(30);
        test.add(15);
        test.add(40);
        test.add(10);
        test.add(20);
        test.add(35);
        test.add(85);
        test.add(3);
        test.add(36);

        assertTrue(list.structurallyEqualTo(test));
    }

    //Tests removing values using their value..
    @Test
    public void removeWithValue() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (int i = 0; i < 100; i++) {
            list.remove(new Integer(i));
        }
        assertTrue(list.isEmpty());
    }

    @Test
    public void size_on_empty_list() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        assertEquals(0, list.size());
    }

    @Test
    public void size_on_list_with_2_elems() {
        //with new child on right..
        SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);
        list.add(2);

        assertEquals(2, list.size());

        //with new child on left..
        list = new SortedList<Integer>(comp);
        list.add(2);
        list.add(1);

        assertEquals(2, list.size());
    }

    @Test
    public void size_with_1_elem() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        list.add(1);

        assertEquals(1, list.size());
    }

    //Tests the size methods..
    @Test
    public void size_with_lots_of_elements() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 1; j++) {
                list.add(i);
            }
        }
        assertEquals(5, (long) list.size());
    }

    public void testEqualElements() {
        final SortedList<IntWrapper> list = new SortedList<IntWrapper>(new Comparator<IntWrapper>() {
            @Override
            public int compare(final IntWrapper a, final IntWrapper b) {
                return a.compareTo(b);
            }
        });
        for (int i = 0; i < 100; i++) {
            list.add(new IntWrapper(0, i));
        }
        //should use same order as added..
        for (int i = 0; i < 100; i++) {
            list.get(i).equals(new IntWrapper(0, i));
        }
    }

    //Tests the toArray(T[] array) method..
    @Test
    public void toArrayWithType() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(i);
            }
        }

        //test where array is too small..
        Integer[] array = list.toArray(new Integer[0]);
        assertTrue(array.length == 300);

        for (int i = 0; i < 300; i++) {
            assertEquals((long) (array[i]), (long) i / 3);
        }

        //test where array is same size..
        array = list.toArray(new Integer[300]);
        assertTrue(array.length == 300);

        for (int i = 0; i < 300; i++) {
            assertEquals((long) (array[i]), (long) i / 3);
        }

        //test where array is too big..
        array      = list.toArray(new Integer[1000]);
        array[300] = -1;
        assertTrue(array.length == 1000);

        for (int i = 0; i < 300; i++) {
            assertEquals((long) (array[i]), (long) i / 3);
        }
        assertEquals((long) array[300], -1);
    }

    //Tests the toArray() method..
    @Test
    public void toObjectArray() {
        final SortedList<Integer> list = new SortedList<Integer>(comp);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(i);
            }
        }

        System.out.println("Size: " + list.size());

        final Object[] array = list.toArray();
        assertTrue(array.length == 300);

        for (int i = 0; i < 300; i++) {
            assertEquals((long) ((Integer) array[i]), (long) i / 3);
        }
    }

    private static class IntWrapper implements Comparable<IntWrapper> {

        final int compValue; //the value the comparator will work on..
        final int otherValue; //another value.

        IntWrapper(final int compValue, final int otherValue) {
            this.compValue  = compValue;
            this.otherValue = otherValue;
        }

        @Override
        public int compareTo(final IntWrapper other) {
            return compValue - other.compValue;
        }

        @Override
        public boolean equals(final Object other) {
            return ((IntWrapper) other).compValue == compValue && ((IntWrapper) other).otherValue == otherValue;
        }
    }
}
