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

public class TimeStatistic {
    public  long    averageTime = 0;
    public  long    lastTime    = 0;
    public  long    maxTime     = 0;
    public  long    minTime     = 0;
    public  long    time        = 0;
    private long    count       = 0;
    private boolean measuring   = false;
    private long    sum         = 0;

    public TimeStatistic() throws Exception {
        start();
    }

    public long getTime() {
        return System.currentTimeMillis() - time;
    }

    public void pause() throws Exception {
        if (measuring) {
            time      = System.currentTimeMillis() - time;
            measuring = false;
        } else {
            throw new Exception("Cannot pause measurment. TimeStatistic not in measuring mode.");
        }
    }

    public void restart() throws Exception {
        stop();
        start();
    }

    public void resume() throws Exception {
        if (!measuring) {
            measuring = true;
            time      = System.currentTimeMillis() - time;
        } else {
            throw new Exception("Cannot resume measurment. TimeStatistic is already in measuring mode.");
        }
    }

    public void start() throws Exception {
        if (!measuring) {
            measuring = true;
            time      = System.currentTimeMillis();
        } else {
            throw new Exception("Cannot start measurment. TimeStatistic is already in measuring mode.");
        }
    }

    public void stop() throws Exception {
        if (measuring) {
            time     = System.currentTimeMillis() - time;
            lastTime = time;
            sum += lastTime;
            count++;
            minTime     = Math.min(lastTime, minTime);
            maxTime     = Math.max(lastTime, maxTime);
            averageTime = sum / count;
            measuring   = false;
        } else {
            throw new Exception("Cannot stop measurment. TimeStatistic not in measuring mode.");
        }
    }
}
