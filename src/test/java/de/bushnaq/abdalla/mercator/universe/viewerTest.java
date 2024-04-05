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

package de.bushnaq.abdalla.mercator.universe;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import de.bushnaq.abdalla.engine.util.ModelCreator;
import de.bushnaq.abdalla.mercator.desktop.DesktopContextFactory;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class viewerTest implements ApplicationListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    DesktopContextFactory contextFactory = new DesktopContextFactory();
    GameEngine3D          gameEngine;

    @Override
    public void create() {
        try {
            final GraphicsDimentions gd = GraphicsDimentions.D3;
            contextFactory.create();
            Universe universe = new Universe("U-0", gd, EventLevel.warning, Sim.class);
            gameEngine = new GameEngine3D(contextFactory, universe, LaunchMode.development);
            gameEngine.create();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        //		SceneAsset model1 = new GLTFLoader().load(Gdx.files.internal("models/glTF/glTF-Sample-Models-master/2.0/MetalRoughSpheres/glTF/MetalRoughSpheres.gltf"));
        final SceneAsset model1 = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/cube_gold_m100_r100.gltf"));
        {
            //			Material material = model1.scene.model.materials.get(0);
            //			System.out.println("----------------------------------------------------------");
            //			Iterator<Attribute> it = material.iterator();
            //			while (it.hasNext()) {
            //				Attribute next = it.next();
            //				System.out.println(
            //						String.format("%s=%s.%d", next.getClass().getSimpleName(), next.toString(), next.type));
            //			}
            //			System.out.println("----------------------------------------------------------");
        }
        //		ModelInstance instance1 = new ModelInstance(model1.scene.model);
        //		instance1.transform.setToTranslationAndScaling(0, 0, 0, 100, 100, 100);
        //		Scene scene1 = new Scene(instance1);
        //		screen.renderMaster.sceneClusterManager.sceneManager.addScene(scene1);

        {
            final Attribute     metallic     = PBRFloatAttribute.createMetallic(1f);
            final Attribute     roughness    = PBRFloatAttribute.createRoughness(0f);
            final Attribute     color        = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.YELLOW);
            final Attribute     culling      = IntAttribute.createCullFace(0);
            final Material      material     = new Material(metallic, roughness, color, culling);
            final ModelCreator  modelCreator = new ModelCreator();
            final Model         model2       = modelCreator.createBox(material);
            final ModelInstance instance2    = new ModelInstance(model2);
            instance2.transform.setToTranslationAndScaling(0, 0, 0, 100, 100, 100);
            //			Scene scene2 = new Scene(instance2);
            //			screen.renderMaster.sceneClusterManager.add(instance2, true);
        }

    }

    // base color yellow or gray
    // metallic metallic 0 - 100%
    // Specular 0.5
    // Sheen Tint 0.5
    // clearcoat roughness 0.03

    @Override
    public void resize(final int width, final int height) {
        gameEngine.resize(width, height);

    }

    @Override
    public void render() {
        gameEngine.render();
    }

    @Override
    public void pause() {
        gameEngine.pause();

    }

    @Override
    public void resume() {
        gameEngine.resume();

    }

    @Override
    public void dispose() {
        gameEngine.dispose();

    }

    @Test
    public void view() throws Exception {

        final GraphicsDimentions             gd       = GraphicsDimentions.D3;
        final Universe                       universe = new Universe("U-0", gd, EventLevel.none, null);
        final Lwjgl3ApplicationConfiguration config   = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setForegroundFPS(0);
        config.setResizable(false);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2); // use GL 3.0 (emulated by OpenGL 3.2)
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        config.setTitle("Mercator");
        {
            ShaderProgram.prependVertexCode   = "#version 150\n"//
                    + "#define GLSL3\n"//
                    + "#ifdef GLSL3\n"//
                    + "#define attribute in\n"//
                    + "#define varying out\n"//
                    + "#endif\n";//
            ShaderProgram.prependFragmentCode = "#version 150\n"//
                    + "#define GLSL3\n"//
                    + "#ifdef GLSL3\n"//
                    + "#define textureCube texture\n"//
                    + "#define texture2D texture\n"//
                    + "#define varying in\n"//
                    + "#endif\n";//
        }
        //		screen = new Screen3D(universe, null, config);
        new Lwjgl3Application(this, config);

        synchronized (this) {
            this.wait();
        }
    }

}
