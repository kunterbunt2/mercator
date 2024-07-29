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

import static de.bushnaq.abdalla.mercator.engine.demo.DemoMode.*;

public abstract class WaitAfterExecuteAbstractTask extends ScheduledTask {
    protected final Logger   logger = LoggerFactory.getLogger(this.getClass());
    private         DemoMode mode   = START;

    public WaitAfterExecuteAbstractTask(GameEngine3D gameEngine, int afterSeconds) {
        super(gameEngine, afterSeconds);
    }

    public boolean execute(float deltaTime) {
        boolean returnValue = false;
        switch (mode) {
            case EXECUTE -> {
                subexecute(deltaTime);
                mode = WAIT;
            }
            case START -> {
                mode          = EXECUTE;
                taskStartTime = System.currentTimeMillis();
            }
            case WAIT -> {
                if (taskStartTime + durationMs <= System.currentTimeMillis()) {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

}
