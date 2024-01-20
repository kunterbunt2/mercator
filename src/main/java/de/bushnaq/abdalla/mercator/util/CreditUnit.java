package de.bushnaq.abdalla.mercator.util;

public class CreditUnit {
	public static String toString(final float credits) {
		if (credits > 1000000.0f) {
			return String.format("%.1f Mc", credits / 1000000.0f);
		} else if (credits > 1000.0f) {
			return String.format("%.1f Tc", credits / 1000.0f);
		} else {
			return String.format("%.0f c", credits);
		}
	}
}
