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

package de.bushnaq.abdalla.mercator.universe.sim;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class SimNeedList extends Vector<SimNeed> implements Cloneable {
    private static final float G01_CREDIT_LIMIT = 0;
    private static final float G02_CREDIT_LIMIT = 0;
    private static final float G03_CREDIT_LIMIT = 0;
    private static final float G04_CREDIT_LIMIT = 0;

    @Override
    public SimNeedList clone() {
        final SimNeedList simNeedsList = (SimNeedList) super.clone();
        return simNeedsList;
    }

    public void createGoodList() {
        removeAllElements();
        createGoodList(Sim.BUYS_GOODS_EVERY, Sim.DIES_IF_NOT_CONSUMED_WITHIN);
    }

    /*
     * good technology level 0 costs s
     *
     */
    private void createGoodList(final long consumeEvery, final long dieIfNotConsumedWithin) {
        add(new SimNeed(GoodType.FOOD, consumeEvery, dieIfNotConsumedWithin, G01_CREDIT_LIMIT));
        add(new SimNeed(GoodType.G02, consumeEvery, dieIfNotConsumedWithin, G02_CREDIT_LIMIT));
        add(new SimNeed(GoodType.G03, consumeEvery, dieIfNotConsumedWithin, G03_CREDIT_LIMIT));
        add(new SimNeed(GoodType.G04, consumeEvery, dieIfNotConsumedWithin, G04_CREDIT_LIMIT));
    }

    public SimNeed getByType(final GoodType type) {
        for (final SimNeed good : this) {
            if (good.type == type) {
                return good;
            } else {
            }
        }
        return null;
    }
    // int queryAmount()
    // {
    // int amount = 0;
    // for ( SimNeeds good : this )
    // {
    // amount += good.amount;
    // }
    // return amount;
    // }
    // public String queryFirstGoodName()
    // {
    // Good good = queryFirstGood();
    // if ( good != null )
    // return good.Name;
    // return "";
    // }
    //
    // public Good queryFirstGood()
    // {
    // for ( Good good : this )
    // {
    // if ( good.amount > 0 )
    // return good;
    // }
    // return null;
    // }
}
