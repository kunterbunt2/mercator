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

package de.bushnaq.abdalla.mercator.util;

/**
 * @author abdalla Keeps the number accurate to the second decimal
 */
public class TimeUnit {
    public static final long DAYS_PER_YEAR  = 100L;//one year is 100 days
    public static final long TICKS_PER_DAY  = 100L;//one day is 100ms
    public static final long TICKS_PER_YEAR = DAYS_PER_YEAR * TICKS_PER_DAY;//one year is 10s

    public static long days(final long time) {
        return time / TimeUnit.TICKS_PER_DAY;
    }

    public static boolean isInt(final long currentTime) {
        return ((currentTime / 100) * 100 - currentTime) == 0L;
    }

    public static String toString(final long time) {
        return toString(time, TimeAccuracy.HOUR_ACCURACY, true);
    }

    public static String toString(final long time, boolean trim) {
        return toString(time, TimeAccuracy.HOUR_ACCURACY, trim);
    }

    public static String toString(final long time, final TimeAccuracy dayAccuracy) {
        return toString(time, dayAccuracy, true);
    }

    public static String toString(final long time, final TimeAccuracy dayAccuracy, boolean trim) {
        final long h = time - (time / TimeUnit.TICKS_PER_DAY) * TimeUnit.TICKS_PER_DAY;
        final long d = (time / TimeUnit.TICKS_PER_DAY) - ((time / (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR)) * TimeUnit.DAYS_PER_YEAR);
        final long y = time / (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR);
        if ((h != 0 || !trim) && dayAccuracy == TimeAccuracy.HOUR_ACCURACY) {
            return String.format("%03d.%02d.%02d", y, d, h);
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
