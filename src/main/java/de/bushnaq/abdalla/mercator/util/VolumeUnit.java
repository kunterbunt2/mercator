package de.bushnaq.abdalla.mercator.util;

public class VolumeUnit {
	public static String toString(final float volumne) {
		if (volumne > 1000000.0f) {
			return String.format("%.1f MT", volumne / 1000000.0f);
		} else if (volumne > 1000.0f) {
			return String.format("%.1f TT", volumne / 1000.0f);
		} else {
			return String.format("%.0f T", volumne);
		}
	}
}
