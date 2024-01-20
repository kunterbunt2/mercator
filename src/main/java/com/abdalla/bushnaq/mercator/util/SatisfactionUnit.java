package com.abdalla.bushnaq.mercator.util;

public class SatisfactionUnit {
	public static String toString(final float number) {
		if (number > 1000000.0f) {
			return String.format("%.1f M", number / 1000000.0f);
		} else if (number > 1000.0f) {
			return String.format("%.1f T", number / 1000.0f);
		} else {
			return String.format("%.0f", number);
		}
	}
}
