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

package de.bushnaq.abdalla.mercator.ui;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

import java.util.ArrayList;
import java.util.List;

public class GraphChartData {
    public static final int    CREDIT_HISTORY_SIZE = 250;
    public              String caption;
    public              Color  color;
    List<GraphChartPointData> items     = new ArrayList<GraphChartPointData>();
    int                       startTime = 0;
    int                       zoom      = 100;

    public GraphChartData(final String caption, final Color color) {
        super();
        this.caption = caption;
        this.color   = color;
    }

    public void add(final long currentTime) {
        add(currentTime, 1);
    }

    public void add(final long time, final int dead) {
        final GraphChartPointData graphChartPointData = get(time / (TimeUnit.TICKS_PER_DAY * zoom));
        graphChartPointData.absolute += dead;
    }

    private GraphChartPointData get(final long currentTime) {
        GraphChartPointData item = null;
        if (currentTime >= items.size() + startTime) {
            item = new GraphChartPointData();
            items.add(item);
            while (items.size() > CREDIT_HISTORY_SIZE) {
                items.remove(0);
                startTime++;
            }
        } else {
            item = items.get((int) currentTime - startTime);
        }
        return item;
    }
}
