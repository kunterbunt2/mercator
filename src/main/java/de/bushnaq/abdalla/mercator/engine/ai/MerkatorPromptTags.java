package de.bushnaq.abdalla.mercator.engine.ai;

import de.bushnaq.abdalla.engine.ai.PromptTags;
import de.bushnaq.abdalla.engine.audio.CommunicationPartner;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

public class MerkatorPromptTags extends PromptTags {
    public static final String DESTINATION_TAG = "<destination>";
    public static final String SHIP_TAG        = "<ship>";
    public static final String STATION_TAG     = "<station>";
    public static final String TONNAGE         = "<cargo>";

    public MerkatorPromptTags(Trader trader, Planet planet) {

        float amount = 0;
        for (final Good g : trader.getGoodList()) {
            amount += g.getAmount();
        }

        StringBuilder tonnage = new StringBuilder();
        for (Good good : trader.getGoodList()) {
            if (good.getAmount() != 0)
                tonnage.append(String.format("%d kiloton '%s'", good.getAmount(), good.type.getName()));
        }

        addPreTag(TONNAGE, tonnage.toString());
        addPreTag(SHIP_TAG, trader.getName());
        addPreTag(STATION_TAG, planet.getName());
        addPreTag(DESTINATION_TAG, trader.navigator.destinationPlanet.getName());
    }

    public MerkatorPromptTags(Planet planet, CommunicationPartner from) {
        addPreTag(STATION_TAG, planet.getName());
        addPreTag(SHIP_TAG, from.getName());
    }
}
