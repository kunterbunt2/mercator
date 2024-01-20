package com.scottlogic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class NaturalSortedListTest {

	//Test whether the NaturalSortedList sorts integer into the expected order.
	@Test
	public void testWithIntegers() {
		final NaturalSortedList<Integer> nsl = new NaturalSortedList<Integer>();
		final List<Integer> regList = new ArrayList<Integer>();

		for (int i = 0; i < 1000; i++) {
			final int toAdd = i % 10;
			nsl.add(toAdd);
			nsl.add(0 - toAdd); //add negatives too..
			regList.add(toAdd);
			regList.add(0 - toAdd);
		}

		Collections.sort(regList); //should put it in the same order as nsl list..

		assertEquals(regList.size(), nsl.size());
		final Iterator<Integer> nslItr = nsl.iterator();
		final Iterator<Integer> regListItr = regList.iterator();
		while (regListItr.hasNext()) {
			assertEquals(regListItr.next(), nslItr.next());
		}
	}
}
