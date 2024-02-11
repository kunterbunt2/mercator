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

import de.bushnaq.abdalla.mercator.universe.sector.SectorList;

import javax.swing.table.AbstractTableModel;

public class SectorsTableModel extends AbstractTableModel {
    private static final long       serialVersionUID = 4803847753013026463L;
    private final        String[]   columnNames      = {"Name", "Credits", "NumberOfPlanets"};
    private final        SectorList systemList;

    public SectorsTableModel(final SectorList systemList) {
        this.systemList = systemList;
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
        return systemList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        switch (col) {
            case 0:
                return systemList.get(row).name;
            case 1:
                return systemList.get(row).credits;
            case 2:
                return systemList.get(row).numberOfPlanets;
        }
        return null;
    }

}
