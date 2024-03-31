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

public class Debug {
    private static boolean enablePlanetFilter = true;
    private static boolean enableTraderFilter = false;
    private static String  filterPlanet       = "P-81";
    private static String  filterTrader       = "T-37";

//    public static String getCallerMethodName() {
//        return StackWalker.getInstance()
//                .walk(s -> s.skip(1).findFirst())
//                .get()
//                .getMethodName();
//    }

    public static String getFilterPlanet() {
        return filterPlanet;
    }

    public static String getFilterTrader() {
        return filterTrader;
    }

    public static boolean isFilterPlanet(String name) {
        return enablePlanetFilter && name.equals(filterPlanet);
    }

    public static boolean isFilterTrader(String name) {
        return enableTraderFilter && name.equals(filterTrader);
    }
}
