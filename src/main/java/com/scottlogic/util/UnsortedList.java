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

import java.util.*;

/**
 * Implementation of a regular list that uses an AVL tree.  Supports
 * all optional operations.
 * <p>
 * Performs the operations: {@code #add(int, Object)}, {@code get} and {@code #remove(int)} operations in
 * in time <i>O(log(n))</i> and
 * the {@code #contains(Object)} and {@link #remove(Object)} operations in time <i>O(n)</i>,
 * where <i>n</i> is the number of elements in the list.
 * <p>
 * This implementation is not synchronised so if you need multi-threaded access then consider wrapping
 * it using the {@link Collections#synchronizedList} method.
 * <p>
 * The iterators this list provides are <i>fail-fast</i>, so any structural
 * modification, other than through the iterator itself, cause it to throw a
 * {@link ConcurrentModificationException}.
 *
 * @param <T> the type of element that this sorted list will store.
 * @author Mark Rhodes
 * @version 1.0
 * @see Collection
 * @see AbstractList
 */
public class UnsortedList<T> extends SortedList<T> {

    private static final long               serialVersionUID = 6720718365032108011L;
    //a dummy comparator which works on any object and simply returns 0..
    private static       Comparator<Object> DUMMY_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(final Object a, final Object b) {
            return 0;
        }
    };

    /**
     * Constructs a new {@code UnsortedList}.
     */
    public UnsortedList() {
        super(DUMMY_COMPARATOR);
    }

    /**
     * Adds the given object to the end of this {@code UnsortedList}, which can be
     * <code>null</code>.
     *
     * @param object the object to add.
     * @return <code>true</code>.
     */
    @Override
    public boolean add(final T object) {
        add(new UnsortedNode(object, size())); //uses the #add(Node) method of the super class..
        return true;
    }

    /**
     * Returns whether or not the given object whether or not the given object is present
     * in this {@code UnsortedList}.
     * <p>
     * This implementation takes <i>O(n)</i> time, where <i>n</i> is the number of element in this list.
     *
     * @return <code>true</code> if the given object is present in this {@code UnsortedList}
     * and <code>false</code> otherwise.
     */
    @Override
    public boolean contains(final Object obj) {
        boolean           found = false;
        final Iterator<T> itr   = iterator();
        while (itr.hasNext()) {
            if (itr.next().equals(obj)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Removes the first element in the list with the given value, if such
     * a node exists, otherwise does nothing.  Works in time <i>O(n)</i>, where
     * <i>i</i> is the number of elements in this {@code UnsortedList}.
     * <p>
     * Returns whether or not a matching element was found and removed or not.
     *
     * @param value the object to remove from this {@code SortedList}.
     * @return <code>true</code> if the given object was found in this
     * {@code SortedList} and removed, <code>false</code> otherwise.
     */
    @Override
    public boolean remove(final Object value) {
        boolean   treeAltered = false;
        final int size        = size();
        for (int i = 0; i < size; i++) {
            final Node nodeAtIndex = findNodeAtIndex(i);
            if (nodeAtIndex.getValue().equals(value)) { //nulls not allowed in the list, so don't need to deal with that case.
                remove(nodeAtIndex);
                treeAltered = true;
                modCount++;
                break;
            }
        }
        return treeAltered;
    }

    /**
     * Adds the given element to the head of this list. Shifts the elements currently
     * in the list (if any) to the right (adds one to their indices).
     * <p>
     * Works in time <i>O(log(n))</i>, where <i>n</i> is the number of elements in the list.
     *
     * @param element the object to by inserted.
     * @throws IllegalArgumentException in the case that the given element is null.
     */
    public void addToHead(final T element) {
        add(0, element);
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @param index   the index in this {@code UnsortedList} to add this given element.
     * @param element the object to add to this {@code UnsortedList}.
     * @return the element that was replaced at the given index.
     * @throws IllegalArgumentException  in the case that the element is <code>null</code>.
     * @throws IndexOutOfBoundsException in the case that (0 <= index < size()) does not hold.
     */
    @Override
    public T set(final int index, final T element) {
        final T removed = remove(index);
        add(index, element);
        return removed;
    }

    /**
     * Inserts the specified element at the specified position in this list. Shifts the element currently
     * at that position (if any) and any subsequent elements to the right (adds one to their indices).
     * <p>
     * Works in time <i>O(log(n))</i>, where <i>n</i> is the number of elements in the list.
     *
     * @param index   at which the element should be added.
     * @param element the object to by inserted.
     * @throws IllegalArgumentException  in the case that the given element is null.
     * @throws IndexOutOfBoundsException in the case that (0 <= index <= size()) does not hold.
     */
    @Override
    public void add(final int index, final T element) {
        if (element == null) {
            throw new IllegalArgumentException("Null elements can  not be added to UnsortedLists.");
        }
        final UnsortedNode toAdd = new UnsortedNode(element, index);
        add(toAdd); //uses the #add(Node) method of the super class..
    }

    /**
     * Class representing the individual nodes of an unsorted list
     * extends the regular SortedList.Node class by storing the
     * position of the node in the tree.
     */
    private class UnsortedNode extends Node {

        //"cached" index of this node in the list and the modCount when it was set..
        private int indexInList;
        private int modCountWhenIndexSet;

        //Constructs a new Node object which should be positioned at the
        //given index when inserted into the tree..
        UnsortedNode(final T obj, final int initialIndex) {
            super(obj);
            indexInList          = initialIndex;
            modCountWhenIndexSet = modCount;
        }

        /**
         * Compares this node with the given node - which must be an {@code UnsortedNode} instance
         * in the same {@code UnsortedList}.  The comparison is based on the current position
         * of the nodes in the list.
         * <p>
         * Nodes with equal indices are compared on whether they are in the tree yet or not; those
         * in the tree are considered to be larger.
         *
         * @return the index of this node in the list minus the index of the given node in the list.
         */
        @Override
        @SuppressWarnings("unchecked")
        public int compareTo(final Node other) {
            final UnsortedNode otherUS = (UnsortedNode) other;
            int                cmp     = getIndexInList() - otherUS.getIndexInList();
            if (cmp == 0) { //indices are equal..
                if (isInTree()) {
                    if (!otherUS.isInTree()) {
                        cmp = 1; //treat this as larger..
                    }
                } else {
                    if (otherUS.isInTree()) {
                        cmp = -1; //treat this as smaller..
                    }
                }
            }
            return cmp;
        }

        /**
         * Gets the index of this node in the list, using the cached value if it is not
         * stale and otherwise calculating it from the ancestor nodes and setting the cached value.
         */
        @SuppressWarnings("unchecked")
        private int getIndexInList() {
            //go up the tree till we find a fresh parent or get to the root and store the path on a stack..
            final LinkedList<UnsortedNode> path    = new LinkedList<UnsortedNode>();
            UnsortedNode                   current = this;
            while (current != null && current.modCountWhenIndexSet != modCount) {
                path.push(current);
                current = (UnsortedNode) current.getParent();
            }

            //pop elements off the stack updating them as you go..
            while (!path.isEmpty()) {
                current = path.pop();
                final UnsortedNode parent = (UnsortedNode) current.getParent(); //this parent can only be fresh or null..
                if (parent == null) { //root case..
                    final Node leftChild = current.getLeftChild();
                    current.indexInList = (leftChild == null) ? 0 : leftChild.sizeOfSubTree();
                } else { //non-root case..
                    if (current.isLeftChildOfParent()) {
                        final Node rightChild = current.getRightChild();
                        current.indexInList = parent.indexInList - (rightChild == null ? 1 : 1 + rightChild.sizeOfSubTree());
                    } else { //current is right child of parent case..
                        final Node leftChild = current.getLeftChild();
                        current.indexInList = parent.indexInList + (leftChild == null ? 1 : 1 + leftChild.sizeOfSubTree());
                    }
                }
                //register it as being fresh..
                current.modCountWhenIndexSet = modCount;
            }

            //on the last iteration of the above loop this is set correctly..
            return indexInList;
        }

        //Determines whether this node is actually in the tree already..
        private boolean isInTree() {
            return getParent() != null || getRoot() == this;
        }
    }
}
