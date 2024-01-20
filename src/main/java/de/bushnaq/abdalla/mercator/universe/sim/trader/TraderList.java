/* ---------------------------------------------------------------------------
 * BEGIN_PROJECT_HEADER
 *
 *       RRRR  RRR    IIIII    CCCCC      OOOO    HHH  HHH
 *       RRRR  RRRR   IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR  RRRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR RRRR    IIIII  CCCC       OOO  OOO  HHHHHHHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR   RRRR  IIIII    CCCCC      OOOO    HHH  HHH
 *
 *       Copyright 2005 by Ricoh Europe B.V.
 *
 *       This material contains, and is part of a computer software program
 *       which is, proprietary and confidential information owned by Ricoh
 *       Europe B.V.
 *       The program, including this material, may not be duplicated, disclosed
 *       or reproduced in whole or in part for any purpose without the express
 *       written authorization of Ricoh Europe B.V.
 *       All authorized reproductions must be marked with this legend.
 *
 *       Department : European Development and Support Center
 *       Group      : Printing & Fax Solution Group
 *       Author(s)  : bushnaq
 *       Created    : 13.02.2005
 *
 *       Project    : com.abdalla.bushnaq.mercator
 *       Product Id : <Product Key Index>
 *       Component  : <Project Component Name>
 *       Compiler   : Java/Eclipse
 *
 * END_PROJECT_HEADER
 * -------------------------------------------------------------------------*/
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
