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
import de.bushnaq.abdalla.mercator.universe.path.WaypointProxy;

import javax.swing.table.AbstractTableModel;

public class TraderWaypointTableModel extends AbstractTableModel {

    private static final long     serialVersionUID = 1L;
    private final        String[] columnNames      = {"City", "Name", "Sector", "Trader"};
    private final        Universe universe;

    public TraderWaypointTableModel(final Universe universe) {
        this.universe = universe;
    }

    @Override
    public Class getColumnClass(final int c) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        if (universe.selectedTrader != null && universe.selectedTrader.navigator != null) {
            return universe.selectedTrader.navigator.waypointList.size();
        }
        return 0;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        if (universe.selectedTrader != null && universe.selectedTrader.navigator != null) {
            final WaypointProxy waypointProxy = universe.selectedTrader.navigator.waypointList.get(row);
            if (waypointProxy != null && waypointProxy.waypoint != null) {
                switch (col) {
                    case 0: // City
                        return waypointProxy.waypoint.city != null ? waypointProxy.waypoint.city.getName() : "";
                    case 1: // Name
                        return waypointProxy.waypoint.name != null ? waypointProxy.waypoint.name : "";
                    case 2: // Sector
                        return waypointProxy.waypoint.sector != null ? waypointProxy.waypoint.sector.name : "";
                    case 3: // Trader
                        return waypointProxy.waypoint.trader != null ? waypointProxy.waypoint.trader.getName() : "";
                }
            }
        }
        return "";
    }
}
