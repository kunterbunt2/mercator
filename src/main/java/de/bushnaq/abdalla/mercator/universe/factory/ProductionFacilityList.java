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

package de.bushnaq.abdalla.mercator.universe.factory;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class ProductionFacilityList extends Vector<ProductionFacility> {
    private static final long                              serialVersionUID             = -6305763147343940720L;
    private final        Map<GoodType, ProductionFacility> ProductionFacilityTypeFinder = new HashMap<GoodType, ProductionFacility>();

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
