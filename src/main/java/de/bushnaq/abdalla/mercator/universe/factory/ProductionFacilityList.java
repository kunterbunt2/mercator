package de.bushnaq.abdalla.mercator.universe.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

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
