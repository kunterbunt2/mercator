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

package de.bushnaq.abdalla.mercator.renderer.reports;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class PieChartData {
    public String                    caption;
    public List<PieChartSectionData> pices = new ArrayList<PieChartSectionData>();

    public PieChartData(final String caption) {
        super();
        this.caption = caption;
    }

    public void add(final String name, final String absolute, final float percentage, final Color color) {
        final PieChartSectionData c = new PieChartSectionData();
        c.name       = name;
        c.percentage = percentage;
        c.absolute   = absolute;
        c.color      = color;
        pices.add(c);
    }
}
