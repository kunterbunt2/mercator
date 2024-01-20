package de.bushnaq.abdalla.mercator.universe.sim.trader;

import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.sim.Sim;

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
			if (trader.getName() == aName) {
				return trader;
			}
		}
		return null;
	}
}
