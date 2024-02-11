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

package de.bushnaq.abdalla.mercator.util;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    public static final int CREDIT_HISTORY_SIZE = 200;
    List<HistoryItem> items     = new ArrayList<HistoryItem>();
    long              lastQuery = Long.MIN_VALUE;
    int               startTime = 0;
    private int[] lastAnualTradingGoodVolume;

    public HistoryManager() {
    }

    public HistoryItem get(final long currentTime) {
        HistoryItem item = null;
        if (currentTime >= items.size() + startTime) {
            item = new HistoryItem();
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

    public int getAnualExportAmountOfGoods() {
        int       amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.exportedAmountOfGoods;
        }
        return amount;
    }

    public float getAnualExportCredits() {
        float     amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.exportedCredits;
        }
        return amount;
    }

    public int getAnualImportAmountOfGoods() {
        int       amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.importedAmountOfGoods;
        }
        return amount;
    }

    public float getAnualImportedCredits() {
        float     amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.importedCredits;
        }
        return amount;
    }

    public float getAnualLocalCreditsEarned() {
        float     amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.localCreditsEarned;
        }
        return amount;
    }

    public float getAnualLocalCreditsSpent() {
        float     amount   = 0;
        final int lastYear = ((startTime + 99) / 100) * 100;
        for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
            final HistoryItem item = items.get(i);
            amount += item.localCreditsSpent;
        }
        return amount;
    }

    public int[] getAnualTradingGoodVolume() {
        if (lastQuery != startTime + items.size()) {
            final int[] amount   = new int[GoodType.values().length];
            final int   lastYear = ((startTime + 99) / 100) * 100;
            for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
                final HistoryItem item = items.get(i);
                for (int index = 0; index < GoodType.values().length; index++) {
                    amount[index] += item.tradedGoodVolume[index];
                }
            }
            lastQuery                  = startTime + items.size();
            lastAnualTradingGoodVolume = amount;
            return amount;
        } else {
            return lastAnualTradingGoodVolume;
        }
    }
}
