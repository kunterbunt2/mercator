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

package de.bushnaq.abdalla.mercator.ui.frame;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.Event;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EventTableModel extends AbstractTableModel {

    private static final String[]    columnNames       = {"#", "Time", "Event", "Who"};
    private static final long        serialVersionUID  = 4803847753013026463L;
    private final        Universe    universe;
    private              List<Event> filteredEventList = null;
    private              Object      lastFiltered      = null;

    public EventTableModel(final Universe universe) {
        this.universe     = universe;
        filteredEventList = universe.eventManager.eventList;
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public int getRowCount() {
        return filteredEventList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        if (universe.selected != lastFiltered) {
            filteredEventList = universe.eventManager.filter(universe.selected);
            lastFiltered      = universe.selected;
        } else {
            //use list
        }

        if (filteredEventList.size() > 0) {
            final int index = filteredEventList.size() - 1 - row;
            switch (col) {
                case 0:
                    return index;
                case 1:
                    return TimeUnit.toString(filteredEventList.get(index).when);
                case 2:
                    return filteredEventList.get(index).what;
                case 3:
                    return filteredEventList.get(index).who;
            }
        } else {
            return "-";
        }
        return null;
    }

}
