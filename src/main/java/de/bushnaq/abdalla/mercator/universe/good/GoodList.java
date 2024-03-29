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

package de.bushnaq.abdalla.mercator.universe.good;

import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class GoodList extends Vector<Good> {
    private static final long                serialVersionUID = -1781731576270326943L;
    private final        Map<GoodType, Good> goodTypeFinder   = new HashMap<GoodType, Good>();

    private void addGood(final Good good) {
        add(good);
        goodTypeFinder.put(good.type, good);
    }

    public void calculatePrice(final long currentTime) {
        for (final Good good : this) {
            good.calculatePrice(currentTime);
        }
    }

    public void consume(final GoodType type, final int i) {
        final Good good = getByType(type);
        good.consume(1);
    }

    public void createEmptyGoodList() {
        removeAllElements();
        createGoodList(50, 0, null);
    }

    /*
     * good technology level 0 costs s
     *
     */
    private void createGoodList(final int averageAmount, final int amount, final Planet planet) {
        addGood(new Good(GoodType.FOOD, 5, averageAmount, amount, planet));
        addGood(new Good(GoodType.G02, 5, averageAmount, amount, planet));
        addGood(new Good(GoodType.G03, 5, averageAmount, amount, planet));
        addGood(new Good(GoodType.G04, 5, averageAmount, amount, planet));
    }

    public void createGoodList(final Planet planetOfOrigin) {
        removeAllElements();
        createGoodList(100, 100, planetOfOrigin);
    }

    public Good getByType(final GoodType type) {
        return goodTypeFinder.get(type);
        // for ( Good good : this )
        // {
        // if ( good.type == type )
        // {
        // return good;
        // }
        // else
        // {
        // }
        // }
        // return null;
    }

    public int queryAmount() {
        int amount = 0;
        for (final Good good : this) {
            amount += good.getAmount();
        }
        return amount;
    }

    public Good queryFirstGood() {
        for (final Good good : this) {
            if (good.getAmount() > 0)
                return good;
        }
        return null;
    }

    public String queryFirstGoodName() {
        final Good good = queryFirstGood();
        if (good != null)
            return good.type.getName();
        return "";
    }
}
