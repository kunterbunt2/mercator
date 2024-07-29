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

package de.bushnaq.abdalla.mercator.engine.demo;

import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScheduledTask {
    public          long         durationMs;//duration of this task
    protected final GameEngine3D gameEngine;
    protected final Logger       logger = LoggerFactory.getLogger(this.getClass());
    long taskStartTime;

    public ScheduledTask(GameEngine3D gameEngine, int durationSeconds) {
        this.gameEngine = gameEngine;
        this.durationMs = durationSeconds * 1000L;
    }

    /**
     * @return true if task has finished and can be removed from the queue
     */
    public abstract boolean execute(float deltaTime);

    public abstract void subexecute(float deltaTime);
}
