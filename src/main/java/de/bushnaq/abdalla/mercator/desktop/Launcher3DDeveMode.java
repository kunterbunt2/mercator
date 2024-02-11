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

package de.bushnaq.abdalla.mercator.desktop;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

public class Launcher3DDeveMode {
    private static final int UNIVERSE_GENERATION_RANDOM_SEED = 3;
    private static final int UNIVERSE_SIZE                   = 3;

    public static void main(final String[] args) throws Exception {
        final GraphicsDimentions gd       = GraphicsDimentions.D3;
        final Universe           universe = new Universe("U-0", gd, EventLevel.info, Sim.class);
        universe.create(UNIVERSE_GENERATION_RANDOM_SEED, UNIVERSE_SIZE, 10L * TimeUnit.TICKS_PER_DAY);
        final Sim trader = universe.traderList.seekTraderByName("T-49");
        universe.eventManager.setObjectFilter(trader);
        universe.eventManager.setEnablePrintEvent(true);
        universe.setSelected(trader, true);
//		final Screen3D screen = new Screen3D(universe, LaunchMode.development);
//		new DesktopLauncher(universe);
    }

}
