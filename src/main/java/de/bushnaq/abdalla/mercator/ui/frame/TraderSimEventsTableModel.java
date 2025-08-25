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

import de.bushnaq.abdalla.engine.event.IEvent;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.SimEvent;

import javax.swing.table.AbstractTableModel;

public class TraderSimEventsTableModel extends AbstractTableModel {

    private static final long     serialVersionUID = 4803847753013026464L;
    private final        String[] columnNames      = {"Time", "Type", "Volume", "Credits", "Description"};
    private final        Universe universe;

    public TraderSimEventsTableModel(final Universe universe) {
        this.universe = universe;
    }

    @Override
    public Class getColumnClass(final int c) {
        if (getRowCount() > 0) {
            return getValueAt(0, c).getClass();
        }
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
    public synchronized int getRowCount() {
        if (universe.selectedTrader != null && universe.selectedTrader.eventManager != null) {
            // Count only SimEvents
            int simEventCount = 0;
            for (IEvent event : universe.selectedTrader.eventManager.getEventList()) {
                if (event instanceof SimEvent) {
                    simEventCount++;
                }
            }
            return simEventCount;
        }
        return 0;
    }

    @Override
    public synchronized Object getValueAt(final int rowIndex, final int columnIndex) {
        if (universe.selectedTrader != null && universe.selectedTrader.eventManager != null) {
            // Find all SimEvents and reverse the order so newest are on top
            java.util.List<SimEvent> simEvents = new java.util.ArrayList<>();
            for (IEvent event : universe.selectedTrader.eventManager.getEventList()) {
                if (event instanceof SimEvent) {
                    simEvents.add((SimEvent) event);
                }
            }

            if (rowIndex < simEvents.size()) {
                // Reverse index to show newest first
                final int      reversedIndex = simEvents.size() - 1 - rowIndex;
                final SimEvent simEvent      = simEvents.get(reversedIndex);

                switch (columnIndex) {
                    case 0:
                        return simEvent.getWhen();
                    case 1:
                        return simEvent.getEventType();
                    case 2:
                        return simEvent.getVolume();
                    case 3:
                        return simEvent.getCredits();
                    case 4:
                        return simEvent.getWhat();
                    default:
                        return "";
                }
            }
        }
        return "";
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        return false;
    }
}
