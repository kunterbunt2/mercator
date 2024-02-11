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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TimeStatisticManager {
    Map<String, TimeStatistic> statisticMap = new HashMap<String, TimeStatistic>();

    public Set<String> getSet() {
        return statisticMap.keySet();
    }

    public TimeStatistic getStatistic(final String statisticName) {
        return statisticMap.get(statisticName);
    }

    public void pause(final String statisticName) throws Exception {
        final TimeStatistic planetStatistic = statisticMap.get(statisticName);
        if (planetStatistic != null) {
            planetStatistic.pause();
        } else {
            throw new Exception("Cannot pause measurment. TimeStatistic not in measuring mode.");
        }
    }

    public void resume(final String statisticName) throws Exception {
        TimeStatistic planetStatistic = statisticMap.get(statisticName);
        if (planetStatistic != null) {
            planetStatistic.resume();
        } else {
            planetStatistic = new TimeStatistic();
        }
        statisticMap.put(statisticName, planetStatistic);
    }

    public void start(final String statisticName) throws Exception {
        TimeStatistic planetStatistic = statisticMap.get(statisticName);
        if (planetStatistic != null) {
            planetStatistic.start();
        } else {
            planetStatistic = new TimeStatistic();
        }
        statisticMap.put(statisticName, planetStatistic);
    }

    public void stop(final String statisticName) throws Exception {
        final TimeStatistic planetStatistic = statisticMap.get(statisticName);
        if (planetStatistic != null) {
            planetStatistic.stop();
        } else {
            throw new Exception("Cannot stop measurment. TimeStatistic not in measuring mode.");
        }
    }
}
