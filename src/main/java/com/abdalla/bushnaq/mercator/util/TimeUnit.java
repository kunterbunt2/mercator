package com.abdalla.bushnaq.mercator.util;

/**
 * @author abdalla Keeps the number accurate to the second decimal
 */
public class TimeUnit {
	public static final long DAYS_PER_YEAR = 100L;
	public static final long TICKS_PER_DAY = 100L;
	public static final long TICKS_PER_YEAR = DAYS_PER_YEAR * TICKS_PER_DAY;

	public static long days(final long time) {
		return time / TimeUnit.TICKS_PER_DAY;
	}

	public static boolean isInt(final long currentTime) {
		return ((currentTime / 100) * 100 - currentTime) == 0L;
	}

	public static String toString(final long time) {
		return toString(time, TimeAccuracy.HOUR_ACCURACY);
	}

	public static String toString(final long time, final TimeAccuracy dayAccuracy) {
		final long h = time - (time / TimeUnit.TICKS_PER_DAY) * TimeUnit.TICKS_PER_DAY;
		final long d = (time / TimeUnit.TICKS_PER_DAY) - ((time / (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR)) * TimeUnit.DAYS_PER_YEAR);
		final long y = time / (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR);
		if (h != 0 && dayAccuracy == TimeAccuracy.HOUR_ACCURACY) {
			return String.format("%d.%02d.%d", y, d, h);
		} else if (d == 0 && dayAccuracy == TimeAccuracy.YEAR_ACCURACY) {
			return String.format("%d.", y);
		} else {
			return String.format("%d.%02d", y, d);
		}
	}

	public static long years(final long time) {
		return time / (TimeUnit.DAYS_PER_YEAR * TimeUnit.TICKS_PER_DAY);
	}
}
