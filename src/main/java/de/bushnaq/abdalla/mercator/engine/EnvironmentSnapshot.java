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

package de.bushnaq.abdalla.mercator.engine;

import com.badlogic.gdx.graphics.Cubemap;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;

public class EnvironmentSnapshot {
    private SceneSkybox environmentCubemap;
    private Cubemap     irradianceMap;
    private Cubemap     radianceMap;

    public EnvironmentSnapshot(Cubemap environmentCubemap, Cubemap irradianceMap, Cubemap radianceMap) {
        this.environmentCubemap = new SceneSkybox(environmentCubemap);
        this.irradianceMap      = irradianceMap;
        this.radianceMap        = radianceMap;
    }

    public SceneSkybox getEnvironmentCubemap() {
        return environmentCubemap;
    }

    public Cubemap getIrradianceMap() {
        return irradianceMap;
    }

    public Cubemap getRadianceMap() {
        return radianceMap;
    }
}
