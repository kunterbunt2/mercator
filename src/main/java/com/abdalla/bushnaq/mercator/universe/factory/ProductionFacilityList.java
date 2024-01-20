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
package com.abdalla.bushnaq.mercator.universe.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.abdalla.bushnaq.mercator.universe.good.GoodType;

/**
 * @author bushnaq Created 13.02.2005
 */
public class ProductionFacilityList extends Vector<ProductionFacility> {
	private static final long serialVersionUID = -6305763147343940720L;
	private final Map<GoodType, ProductionFacility> ProductionFacilityTypeFinder = new HashMap<GoodType, ProductionFacility>();

	public void addProductionFacility(final ProductionFacility productionFacility) {
		add(productionFacility);
		ProductionFacilityTypeFinder.put(productionFacility.producedGood.type, productionFacility);
	}

	public void advanceInTime(final long currentTime) {
		for (final ProductionFacility productionFacility : this) {
			productionFacility.advanceInTime(currentTime);
		}
	}

	public ProductionFacility getByType(final GoodType type) {
		return ProductionFacilityTypeFinder.get(type);
	}
}
