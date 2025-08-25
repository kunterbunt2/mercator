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
import de.bushnaq.abdalla.mercator.universe.factory.Factory;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacilityList;

import javax.swing.table.AbstractTableModel;

public class PlanetFactoriesTableModel extends AbstractTableModel {

    private static final long     serialVersionUID = 4803847753013026463L;
    private final        String[] columnNames      = {"Name", "Needs", "Produces", "Status"};
    private final        Universe universe;

    public PlanetFactoriesTableModel(final Universe universe) {
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
            return universe.selectedPlanet.productionFacilityList.size();
        }
        return 0;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        if (universe.selectedPlanet != null) {
            final ProductionFacilityList productionFacilityList = universe.selectedPlanet.productionFacilityList;
            final ProductionFacility     productionFacility     = productionFacilityList.get(row);
            switch (col) {
                case 0:
                    return productionFacility.getName();
                case 1:
                    if (productionFacility instanceof Factory) {
                        final Factory factory = (Factory) productionFacility;
                        if (factory.inputGood.size() != 0) {
                            return factory.inputGood.get(0).type.getName();
                        } else {
                            return "N/A";
                        }
                    }
                case 2:
                    return productionFacility.producedGood.type.getName();
                case 3:
                    return productionFacility.getStatusName();
            }
        }
        return null;
    }

}
