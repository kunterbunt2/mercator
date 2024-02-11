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

import de.bushnaq.abdalla.mercator.renderer.Renderable;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Good extends Renderable implements Cloneable {
    private static final long          MAX_BUY_INTEREST_INTERVAL = 250 * TimeUnit.TICKS_PER_DAY;
    public               int           averageAmount             = 0;
    public               float         averagePrice              = 0;
    public               long          lastBuyInterest           = Long.MIN_VALUE;
    public               float         price                     = getAveragePrice();
    public               GoodStatistic statistic                 = new GoodStatistic();
    public               GoodType      type;
    private              int           amount                    = 0;
    //	public List<Volume> volume = new LinkedList<Volume>();

    public Good(final GoodType type, final float averagePrice, final int averageAmount, final int amount, final Planet planetOfOrigin) {
        this.type = type;
        // this.Name = type.getName();
        this.averagePrice  = averagePrice;
        this.averageAmount = averageAmount;
        set2DRenderer(new Good2DRenderer(this));
        set3DRenderer(new Good3DRenderer(this));
        produce(amount, planetOfOrigin);
    }

    public void buy(final int volume) {
        this.amount += volume;
        statistic.bought += volume;
    }

    public void calculatePrice(final long currentTime) {
        if (isTraded(currentTime)) {
            // ---The price will tend to move to meet the criteria of the market
            // ---A trader tends to pay less for it if he does not own the money
            //max price is 250 times the average price
            //min price is 1
            if (amount >= getAverageAmount()) {
                price = (int) (getAveragePrice() - (((float) amount - getAverageAmount()) / getAverageAmount()) * (getAveragePrice() - 1));
            } else {
                price = (int) (getAveragePrice() * 5 - (((float) amount) / getAverageAmount()) * (getAveragePrice() * 4)) * 10;
            }
            // if ( price < 0 )
            // {
            // System.out.println( price );
            // }
            // float volumeInfluence = ( getMaxPriceDelta() * ( amount - getAverageAmount()
            // ) ) / getMaxAmountDelta();
            // ---Food 10 - -6 = 16
            // price = (float)( getAveragePrice() / Math.log10( ((amount/getAverageAmount())
            // ) );
            // Price = GetAveragePrice() - ( GetMaxPriceDelta()*(Amount -
            // GetAverageAmount()) )/GetMaxAmountDelta();
        } else {
            price = getAveragePrice();
        }
    }

    @Override
    public Good clone() throws CloneNotSupportedException {
        final Good good = (Good) super.clone();
        return good;
    }

    public void consume(final int volume) {
        this.amount -= volume;
        statistic.consumed += volume;
    }

    public int getAmount() {
        return amount;
    }

    public int getAverageAmount() {
        return averageAmount;
    }

    public float getAveragePrice() {
        return averagePrice;
    }

    public int getMaxAmount() {
        return getAverageAmount() + getMaxAmountDelta();
    }

    // public String getName()
    // {
    // return type.getName();
    // }
    public int getMaxAmountDelta() {
        return getAverageAmount();
    }

    public float getMaxPrice() {
        return /* (int)( getAveragePrice() + getMaxPriceDelta() ) */getAveragePrice() * 5;
    }

    public float getMaxPriceDelta() {
        return /* 5.0f + */(getAveragePrice() * 0.99f);
    }

    public int getMinAmount() {
        return getAverageAmount() - getMaxAmountDelta();
    }

    public float getMinPrice() {
        return 1/* (int)( getAveragePrice() - getMaxPriceDelta() ) */;
    }

    public void indicateBuyInterest(final long currentTime) {
        lastBuyInterest = currentTime;
    }

    public boolean isTraded(final long currentTime) {
        return currentTime - lastBuyInterest < MAX_BUY_INTEREST_INTERVAL;
    }

    public void produce(final int volume, final Planet planetOfOrigin) {
        this.amount += volume;
        statistic.produced += volume;
        //		for (int i = 0; i < volume; i++) {
        //			this.volume.add(new Volume(planetOfOrigin));
        //		}
    }

    public void sell(final int volume) {
        if (volume < 0) {
            final int a = 23;
        }
        this.amount -= volume;
        statistic.sold += volume;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }
}
