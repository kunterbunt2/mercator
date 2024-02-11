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

package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.mercator.universe.sim.Sim;

import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class TraderList extends Vector<Trader> {
    private static final long serialVersionUID = 3695436401556359801L;

    /*
     * BcTrader* BcUniverse::GetTraderByLocation( int aX, int aY ) { for( BcTrader*
     * trader = TraderList.First(); trader; trader = trader->Next() ) { if(
     * trader->X == aX && trader->Y == aY ) return trader; } return 0; }
     */
    /*
     * Trade FindTraderLinkByTrader( Trader aTrader ) { for ( Trader trader : this )
     * { if ( _traderLink.Trader == aTrader ) { return _traderLink; } else { } }
     * return null; }
     *
     * RcTraderLink remove( Trader aTrader ) { RcTraderLink _traderLink =
     * FindTraderLinkByTrader( aTrader ); remove( _traderLink ); return _traderLink;
     * }
     *
     * RcTraderLink add( Trader aTrader ) { RcTraderLink _traderLink = new
     * RcTraderLink(); _traderLink.Trader = aTrader; add( _traderLink ); return
     * _traderLink; }
     */
    public void kill(final Sim trader) {
        trader.planet.traderList.remove(trader);
        remove(trader);
    }

    public Sim seekTraderByName(final String aName) {
        for (final Sim trader : this) {
            if (trader.getName().equals(aName)) {
                return trader;
            }
        }
        return null;
    }
}
