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
import de.bushnaq.abdalla.mercator.universe.sim.Sim;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class PlanetSimsTableModel extends AbstractTableModel {

    private static final long        serialVersionUID = 4803847753013026463L;
    private final        String[]    columnNames      = {"Name", "Credits", "Satisfaction", "Profession", "Factory", "Status"};
    private final        Vector<Sim> simList          = new Vector<Sim>();
    private final        Universe    universe;

    public PlanetSimsTableModel(final Universe universe) {
        this.universe = universe;
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
        if (universe.selectedPlanet != null) {
            return universe.selectedPlanet.simList.size();
        }
        return 0;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        if (universe.selectedPlanet != null) {
            simList.clear();
            simList.addAll(universe.selectedPlanet.simList);
            simList.addAll(universe.selectedPlanet.deadSimList);
            final Sim sim = simList.get(row);
            switch (col) {
                case 0:
                    return sim.getName();
                case 1:
                    return sim.getCredits();
                case 2:
                    return sim.getSatisfactionFactor(universe.currentTime);
                case 3:
                    return sim.profession.name();
                case 4:
                    if (sim.productionFacility != null) {
                        return sim.productionFacility.getName();
                    } else {
                        return "-";
                    }
                case 5:
                    return sim.status.getName();
            }
        }
        return null;
    }

}
