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
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;

import javax.swing.table.AbstractTableModel;

public class TradersTableModel extends AbstractTableModel {
    private static final long       serialVersionUID = 4257926461207710123L;
    private final        String[]   columnNames      = {"Name", "Max load", "Credits", "Status", "Sim Status", "Satisfaction", "Waiting", "Loading", "Planet", "Bought at", "Over port", "Selling to"};
    private final        TraderList traderList;
    private final        Universe   universe;

    public TradersTableModel(final Universe universe) {
        this.universe   = universe;
        this.traderList = universe.traderList;
    }

    @Override
    public Class getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
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
        return traderList.size();
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        if (traderList.size() > 0) {
            switch (col) {
                case 0:
                    return traderList.get(row).getName();
                case 1:
                    return traderList.get(row).goodSpace;
                case 2:
                    return traderList.get(row).getCredits();
                case 3:
                    return traderList.get(row).getTraderStatus().getDisplayName();
                case 4:
                    return traderList.get(row).status.getName();
                case 5:
                    return traderList.get(row).getSatisfactionFactor(universe.currentTime);
                case 6:
                    return traderList.get(row).portRestingTime;
                case 7:
                    return traderList.get(row).getGoodList().queryFirstGoodName();
                case 8:
                    return traderList.get(row).planet.getName();
                case 9:
                    if (traderList.get(row).navigator.sourcePlanet != null) {
                        return traderList.get(row).navigator.sourcePlanet.getName();
                    } else {
                        return "-";
                    }
                case 10:
                    if (traderList.get(row).navigator.nextWaypoint != null && traderList.get(row).navigator.nextWaypoint.city != null) {
                        return traderList.get(row).navigator.nextWaypoint.city.getName();
                    } else {
                        return "-";
                    }
                case 11:
                    if (traderList.get(row).navigator.sourcePlanet != null && traderList.get(row).navigator.destinationPlanet != null && traderList.get(row).navigator.sourcePlanet != traderList.get(row).navigator.destinationPlanet) {
                        return traderList.get(row).navigator.destinationPlanet.getName();
                    } else {
                        return "-";
                    }
            }
        } else {
            return "-";
            //			switch (col) {
            //			case 0:
            //				return "";
            //			case 1:
            //				return 0;
            //			case 2:
            //				return 0;
            //			case 3:
            //				return "";
            //			case 4:
            //				return "";
            //			case 5:
            //				return 0f;
            //			case 6:
            //				return 0L;
            //			case 7:
            //				return "";
            //			case 8:
            //				return "";
            //			case 9:
            //				return "";
            //			case 10:
            //				if (traderList.get(row).destinationWaypointPlanet != null) {
            //					return traderList.get(row).destinationWaypointPlanet.getName();
            //				} else {
            //					return "-";
            //				}
            //			case 11:
            //				if ( traderList.get(row).sourcePlanet != null && traderList.get(row).destinationPlanet != null && traderList.get(row).sourcePlanet != traderList.get(row).destinationPlanet) {
            //					return traderList.get(row).destinationPlanet.getName();
            //				} else {
            //					return "-";
            //				}
            //			}
        }
        return null;
    }

}
