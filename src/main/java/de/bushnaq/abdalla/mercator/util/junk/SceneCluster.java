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

package de.bushnaq.abdalla.mercator.util.junk;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import de.bushnaq.abdalla.mercator.renderer.AssetManager;
import de.bushnaq.abdalla.mercator.shader.GameSettings;

import java.util.ArrayList;
import java.util.List;

public class SceneCluster {

    private final Environment         ambientEnvironment   = new Environment();
    // ModelInstance uberModelInstance;
    private final AssetManager        renderMaster;
    private final List<ModelInstance> renderModelInstances = new ArrayList<ModelInstance>();
    BoundingBox         clusterBoundingBox;
    List<ModelInstance> dynamicModelInstances = new ArrayList<ModelInstance>();
    Array<PointLight>   dynamicPointLights    = new Array<PointLight>();
    Environment         renderEnvironment     = new Environment();
    List<ModelInstance> staticModelInstances  = new ArrayList<ModelInstance>();
    Array<PointLight>   staticPointLights     = new Array<PointLight>();
    private DirectionalShadowLight shadowLight;

    public SceneCluster(final AssetManager renderMaster) {
        this.renderMaster = renderMaster;
    }

    public void add(final ModelInstance instance, final boolean dynamic) {
        if (dynamic) {
            dynamicModelInstances.add(instance);
        } else {
            staticModelInstances.add(instance);
        }
    }

    public void add(final PointLight pointLight, final boolean dynamic) {
        if (dynamic) {
            dynamicPointLights.add(pointLight);
        } else {
            staticPointLights.add(pointLight);
        }
    }

    private void drawShadowBatch(final ModelBatch shadowBatch, final ModelInstance uberModel, final PerspectiveCamera camera) {
        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());

        shadowBatch.render(uberModel);
        //		for (GameModel mdl : engine.getDynamicModels()) {
        //			if (isVisible(camera, mdl)) {
        //				shadowBatch.render(mdl.modelInstance);
        //			}
        //		}
        shadowBatch.end();
        shadowLight.end();
    }

    public void removeDynamic() {
        dynamicModelInstances.clear();
        dynamicPointLights.clear();
    }

    public void render(final ModelBatch modelBatch, final Camera camera) {
        renderModelInstances.clear();
        renderModelInstances.addAll(staticModelInstances);
        renderModelInstances.addAll(dynamicModelInstances);
        renderEnvironment.clear();
        setEnvironmentLights(new Vector3(Vector3.Y).scl(-1));
        //		setEnvironmentLights(new Vector3(1,-1,-1).nor());
        //		renderEnvironment.set(ambientEnvironment);
        for (final PointLight p : staticPointLights)
            renderEnvironment.add(p);
        for (final PointLight p : dynamicPointLights)
            renderEnvironment.add(p);
        //		ModelCache cache = new ModelCache();
        //		cache.begin();
        //		cache.add(renderModelInstances);
        //		cache.end();
        //		ModelInstance uberModel = MeshMerger.optimizeModels(renderModelInstances,renderMaster.cubeWhite.materials.get(0));
        //		if (uberModel != null)
        //			drawShadowBatch(shadowBatch, uberModel, camera);
        //		modelBatch.render(cache, renderEnvironment);
        //		if (uberModel != null)
        //		{
        //			modelBatch.begin(camera);
        //			modelBatch.render(uberModel, renderEnvironment);
        //			modelBatch.end();
        //		}

    }

    public void set(final Environment ambientEnvironment) {
        this.ambientEnvironment.set(ambientEnvironment);
    }

    public void setEnvironmentLights(/* Array<BaseLight<?>> lights, */ final Vector3 sunDirection) {
        renderEnvironment = new Environment();
        renderEnvironment.add((shadowLight = new DirectionalShadowLight(GameSettings.SHADOW_MAP_WIDTH, GameSettings.SHADOW_MAP_HEIGHT, GameSettings.SHADOW_VIEWPORT_WIDTH, GameSettings.SHADOW_VIEWPORT_HEIGHT, GameSettings.SHADOW_NEAR, GameSettings.SHADOW_FAR)).set(GameSettings.SHADOW_INTENSITY, GameSettings.SHADOW_INTENSITY, GameSettings.SHADOW_INTENSITY, sunDirection.nor()));
        renderEnvironment.shadowMap = shadowLight;

        final float ambientLight = GameSettings.SCENE_AMBIENT_LIGHT;
        renderEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1));
        //		for (BaseLight<?> light : lights) {
        //			renderEnvironment.add(light);
        //		}
    }

}
