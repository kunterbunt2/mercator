package de.bushnaq.abdalla.mercator.engine.ai;

import de.bushnaq.abdalla.engine.ai.PromptTags;
import de.bushnaq.abdalla.engine.audio.CommunicationPartner;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

public class MerkatorPromptTags extends PromptTags {
    public static final String SHIP_TAG    = "<ship>";
    public static final String STATION_TAG = "<station>";
    public static final String TONNAGE     = "<tonnage>";

    public MerkatorPromptTags(Trader trader, Planet planet) {

        float amount = 0;
        for (final Good g : trader.getGoodList()) {
            amount += g.getAmount();
        }

        addPreTag(TONNAGE, (int) amount + "kt");
        addPreTag(SHIP_TAG, trader.getName());
        addPreTag(STATION_TAG, planet.getName());
    }

    public MerkatorPromptTags(Planet planet, CommunicationPartner from) {
        addPreTag(STATION_TAG, planet.getName());
        addPreTag(SHIP_TAG, from.getName());
    }
}
