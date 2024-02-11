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

package de.bushnaq.abdalla.mercator.gui.frame;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;

import javax.swing.table.AbstractTableModel;

public class PlanetsTableModel extends AbstractTableModel {
    private static final long       serialVersionUID = -4333102673911118458L;
    private final        String[]   columnNames      = {"Name", "Credits", "Satisfaction", "Sector"};
    private final        PlanetList planetList;
    private final        Universe   universe;

    public PlanetsTableModel(final Universe universe) {
        this.universe   = universe;
        this.planetList = universe.planetList;
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
        return planetList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        switch (col) {
            case 0:
                return planetList.get(row).getName();
            case 1:
                return planetList.get(row).getCredits();
            case 2:
                return planetList.get(row).getSatisfactionFactor(universe.currentTime);
            case 3:
                return planetList.get(row).sector.name;
        }
        return null;
    }

}
