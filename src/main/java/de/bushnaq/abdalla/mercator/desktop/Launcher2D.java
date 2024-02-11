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

public class Launcher2D {
    private static final int UNIVERSE_GENERATION_RANDOM_SEED = 1;

    public static void main(final String[] args) throws Exception {
        final GraphicsDimentions gd       = GraphicsDimentions.D2;
        final Universe           universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
        universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 10L * TimeUnit.TICKS_PER_DAY);
//		final Screen2D screen = new Screen2D(universe);
//		final DesktopLauncher1 launcher = new DesktopLauncher1(universe, screen, false);
//		synchronized (launcher) {
//			launcher.wait();
//		}
//		System.out.println("DesktopLauncher exiting");
    }

}
