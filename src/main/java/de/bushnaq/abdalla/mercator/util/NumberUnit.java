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

public class NumberUnit {
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
