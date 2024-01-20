package de.bushnaq.abdalla.mercator.universe;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import de.bushnaq.abdalla.mercator.util.EventLevel;
import de.bushnaq.abdalla.mercator.util.ModelCreator;
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

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class viewerTest implements ApplicationListener {
	Screen3D screen;

	@Override
	public void create() {
		screen.create();
		//		SceneAsset model1 = new GLTFLoader().load(Gdx.files.internal("models/glTF/glTF-Sample-Models-master/2.0/MetalRoughSpheres/glTF/MetalRoughSpheres.gltf"));
		final SceneAsset model1 = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube_gold_m100_r100.gltf"));
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
			final Attribute metallic = PBRFloatAttribute.createMetallic(1f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0f);
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.YELLOW);
			final Attribute culling = IntAttribute.createCullFace(0);
			final Material material = new Material(metallic, roughness, color, culling);
			final ModelCreator modelCreator = new ModelCreator();
			final Model model2 = modelCreator.createBox(material);
			final ModelInstance instance2 = new ModelInstance(model2);
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
	public void dispose() {
		screen.dispose();

	}

	@Override
	public void pause() {
		screen.pause();

	}

	@Override
	public void render() {
		screen.render();
	}

	@Override
	public void resize(final int width, final int height) {
		screen.resize(width, height);

	}

	@Override
	public void resume() {
		screen.resume();

	}

	@Test
	public void view() throws Exception {

		final GraphicsDimentions gd = GraphicsDimentions.D3;
		final Universe universe = new Universe("U-0", gd, EventLevel.none, null);
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(true);
		config.setForegroundFPS(0);
		config.setResizable(false);
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2); // use GL 3.0 (emulated by OpenGL 3.2)
//		config.useOpenGL3(true, 3, 2);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
		config.setTitle("Mercator");
		//		screen = new Screen3D(universe, null, config);
		new Lwjgl3Application(this, config);

		synchronized (this) {
			this.wait();
		}
	}

}
