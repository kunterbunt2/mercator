package de.bushnaq.abdalla.mercator.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.ModelCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Render3DMaster {
	//	public SceneAsset cubeAluminiumBrushed;
	//	public SceneAsset cubeWhitePowderCoatingTexture;
	//	public SceneAsset cubeRed;
	//	public SceneAsset cubeBlack;
	public SceneAsset BoomBox;
	//	public SceneAsset roundedCube;
	public Model buildingCube;
	public MercatorRandomGenerator createRG = new MercatorRandomGenerator(1, null);
	//	public SceneAsset asphalt;
	//	public SceneAsset asphaltCracked;
	//	public Model building1;
	//	public SceneAsset animatedCube;
	public SceneAsset cube;
	//	public SceneAsset cube1;
	public Model cubeBase1;
	public Model cubeEmissive;
	//	public SceneAsset Cracked_ice;
	//	public SceneAsset cubeGoldLeaves;
	//	public SceneAsset cubeGoldNatural;
	public Model cubeGood;
	//	public SceneAsset cubeMetalBaseGrungy;
	//	public SceneAsset cubeRedMetalPlates;
	//	public SceneAsset cubeMatalSolidPaintWhite;
	//	public Model boxModel2;
	//	public Model boxModel3;
	//	public Model whietBoxModel;
	//	public int width;
	//	public Model cubeTrans1;
	//	public Model cubeTrans2;
	//	TextureAtlas atlas;
	//	public SceneAsset cubeWhite;
	//	public PerspectiveCamera camera;
	//	float centerX;
	//	float centerY;
	private Color[] distinctiveColor;
	private final InputProcessor inputProcessor;
	public Model jumpGate;
	//	public SceneAsset Metal_Floor_01;
	//	public SceneAsset MetalRoughSpheres;
	//	public SceneAsset NormalTangentTest;
	public Model planet;
	//	public SceneAsset rock;
	public SceneAsset Rock_Alien_02;
	//	private int height;
	//	private List<ModelInstance> instances = new ArrayList<ModelInstance>();
	//	private final BoundingBox sceneBox = new BoundingBox();
	//	private TextureRegion planetTexture;
	public SceneManager sceneManager;
	public Model sector;
	public ShowGood showGood = ShowGood.Name;
	public Model trader;
	//	public SceneAsset sphereMatalSolidPaintWhite;
	public SceneAsset turbine;
	public Universe universe;
	public Model water;
	public SceneAsset wheel;
	//	Model postScreenQuad;

	public Render3DMaster(final Universe universe, final InputProcessor inputProcessor) {
		this.universe = universe;
		this.inputProcessor = inputProcessor;
	}

	Color amountColor(final Good good) {
		return availabilityColor(good.getAmount(), good.getMaxAmount());
	}

	Color availabilityColor(final float amount, final float maxAmount) {
		if (amount >= 0.5 * maxAmount) {
			return Color.GREEN;
		} else if (amount >= 0.3 * maxAmount) {
			return Color.ORANGE;
		} else {
			return Color.RED;
		}
	}

	public void create() throws Exception {
		//		cubeGoldLeaves = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-Gold_leafs/cube-Gold_leafs.gltf"));
		//		NormalTangentTest = new GLTFLoader().load(Gdx.files.internal("models/glTF/NormalTangentTest/glTF/NormalTangentTest.gltf"));
		//		initTextures();
		//		this.sceneBox.set(new Vector3(-3, -3, -3), new Vector3(3, 3, 3));

		sceneManager = new SceneManager(universe, inputProcessor);

		initColors();
		final Texture texture = new Texture(Gdx.files.internal("tiles.png"));
		final TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);
		final ModelBuilder modelBuilder = new ModelBuilder();
		final ModelCreator modelCreator = new ModelCreator();
		//		cubeWhite = modelCreator.createBox(tiles[1][0],
		//				new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createDiffuse(Color.WHITE)),
		//				Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		//		cubeWhite = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-white.gltf"));
		//		boxModel2 = modelCreator.createBox(tiles[1][1],new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createDiffuse(Color.WHITE)),Usage.Position | Usage.Normal | Usage.TextureCoordinates);

		//		boxModel3 = modelCreator.createBox(tiles[1][2],	new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createDiffuse(Color.WHITE)),Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		//		whietBoxModel = modelCreator.createBox(tiles[1][3],	new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createDiffuse(Color.WHITE)),Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		//		cubeGoldNatural = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-gold_natural/cube-gold_natural.gltf"));
		//		cubeWhitePowderCoatingTexture = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-White Powder Coating Texture/cube-White Powder Coating Texture.gltf"));
		//		cubeAluminiumBrushed = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-Aluminium_brushed/cube-Aluminium_brushed.gltf"));
		//		cubeMetalBaseGrungy = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-Metal_Base_Grungy/cube-Metal_Base_Grungy.gltf"));

		//		cubeRed = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-red.gltf"));
		//		cubeBlack = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-black.gltf"));
		BoomBox = new GLTFLoader().load(Gdx.files.internal("models/glTF/BoomBox.gltf"));
		//		cube1 = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-Bronze_yellow/cube-Bronze_yellow.gltf"));
		//		cubeMatalSolidPaintWhite = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-Matal_solid_paint_white/cube-Matal_solid_paint_white.gltf"));
		//		sphereMatalSolidPaintWhite = new GLTFLoader().load(Gdx.files.internal("models/glTF/sphere_Matal solid paint white/sphere_Matal solid paint white.gltf"));
		//		cube1 = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube-water/cube-water.gltf"));
		//		Metal_Floor_01 = new GLTFLoader().load(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01.gltf"));
		//		MetalRoughSpheres = new GLTFLoader().load(Gdx.files.internal("models/glTF/MetalRoughSpheres/glTF/MetalRoughSpheres.gltf"));

		Rock_Alien_02 = new GLTFLoader().load(Gdx.files.internal("models/glTF/Rock_Alien_02/Rock_Alien_02.gltf"));
		//		rock = new GLTFLoader().load(Gdx.files.internal("models/glTF/Rock/Rock.gltf"));
		{
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.3f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.2f);
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.LIGHT_GRAY);
			//			Attribute normal = PBRFloatAttribute.createNormalScale(0.0f);
			//			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
			//			Attribute culling = IntAttribute.createCullFace(1);
			//			Attribute shininess = PBRFloatAttribute.createShininess(1.0f);
			final Material material = new Material(metallic, roughness, color /*, culling, normal, occlusion, shininess */);
			cubeBase1 = modelCreator.createBox(material);
		}
		{
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
			final Attribute color = PBRColorAttribute.createBaseColorFactor(Color.BLACK);
			//			Attribute normal = PBRFloatAttribute.createNormalScale(0.0f);
			//			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(0.0f);
			//			Attribute culling = IntAttribute.createCullFace(0);
			//			Attribute shininess = PBRFloatAttribute.createShininess(1.0f);
			final Material material = new Material(metallic, roughness, color/* , culling, normal, occlusion, shininess */);
			//			trader = modelCreator.createBox(material);
			trader = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
		}
		//		{
		//			final Attribute metallic = PBRFloatAttribute.createMetallic(0.9f);
		//			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
		//			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
		//			final Attribute blending = new BlendingAttribute(0.7f); // opacity is set by pbrMetallicRoughness below
		//			final Material material = new Material(metallic, roughness, color/* , culling, normal, occlusion, shininess */, blending);
		//			cubeTrans1 = modelCreator.createBox(material);
		//		}
		//		{
		//			final Attribute metallic = PBRFloatAttribute.createMetallic(1.0f);
		//			final Attribute roughness = PBRFloatAttribute.createRoughness(0.0f);
		//			final Material material = new Material(metallic, roughness/*, color , culling, normal, occlusion, shininess , blending*/);
		//			cubeTrans2 = modelCreator.createBox(material);
		//		}
		{
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
			//			Attribute culface = IntAttribute.createCullFace(1);
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
			//			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
			//			Attribute normal = PBRFloatAttribute.createNormalScale(1.0f);
			final Material material = new Material(metallic, roughness, color/*, culface, normal, occlusion*/);
			//			cubeGood = modelCreator.createBox(material);
			cubeGood = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
			//			Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, new Color(1.0f, 1.0f, 1.0f, 1.0f));
			//			Attribute colorTex = PBRTextureAttribute.createBaseColorTexture(sceneClusterManager.getAtlasManager().good_baseColorRegion);

			//			Attribute metallic = PBRFloatAttribute.createMetallic(1.0f);
			//			Attribute roughness = PBRFloatAttribute.createRoughness(1.0f);
			//			Attribute metallicRoughnessTex = PBRTextureAttribute.createMetallicRoughnessTexture(sceneClusterManager.getAtlasManager().good_occlusionRoughnessMetallicRegion);

			//			Attribute culface = IntAttribute.createCullFace(1);
			//			Attribute normal = PBRFloatAttribute.createNormalScale(1.0f);
			//			Attribute normalTex = PBRTextureAttribute.createNormalTexture(sceneClusterManager.getAtlasManager().good_normalRegion);
			//			Material material = new Material(color, /*colorTex,*/ metallic, roughness, metallicRoughnessTex, culface/*, normal, normalTex*/);

			//			cubeGood1 = modelCreator.createBox(material);
		}

		{
			final Attribute emissive = ColorAttribute.createEmissive(Color.YELLOW);
			final Material material = new Material(emissive);
			cubeEmissive = modelCreator.createBox(material);
		}
		//		{
		//			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.BLACK);
		//			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
		//			final Attribute roughness = PBRFloatAttribute.createRoughness(0.2f);
		//			final Material material = new Material(metallic, roughness, color);
		//			building1 = modelCreator.createBox(material);
		//		}
		{
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
			final Material material = new Material(metallic, roughness, color);
			planet = modelCreator.createBox(material);
		}
		{
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
			final Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
			//			final Attribute culling = IntAttribute.createCullFace(1);
			//			final Material material = new Material(metallic, roughness, color);
			//			final Material material = new Material(metallic, roughness, color, culling/*, normal*/, occlusion/*, shininess */);
			final Material material = new Material(metallic, roughness, color, occlusion);
			buildingCube = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
		}
		{
			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
			final Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
			//			final Attribute culling = IntAttribute.createCullFace(1);
			//			final Material material = new Material(metallic, roughness, color);
			//			final Material material = new Material(metallic, roughness, color, culling/*, normal*/, occlusion/*, shininess */);
			final Material material = new Material(metallic, roughness, color, occlusion);
			jumpGate = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
		}
		{
			wheel = new GLTFLoader().load(Gdx.files.internal("models/glTF/wheel/wheel.gltf"));
			final Material material = wheel.scene.model.materials.get(0);
			material.set(buildingCube.materials.get(0));
			set(buildingCube.materials.get(0), material);

		}
		turbine = new GLTFLoader().load(Gdx.files.internal("models/glTF/turbine/turbine.gltf"));
		//		animatedCube = new GLTFLoader().load(Gdx.files.internal("models/glTF/AnimatedCube/glTF/AnimatedCube.gltf"));
		//		cube = new GLTFLoader().load(Gdx.files.internal("models/glTF/cube/cube.gltf"));
		//		Cracked_ice = new GLTFLoader().load(Gdx.files.internal("models/glTF/Cracked_ice/Cracked_ice.gltf"));
		{
			final Attribute metallic = PBRFloatAttribute.createMetallic(0.0f);
			final Attribute roughness = PBRFloatAttribute.createRoughness(0.3f);
			final Attribute color = PBRColorAttribute.createBaseColorFactor(new Color(Color.WHITE));
			final Material material = new Material(metallic, roughness, color);
			sector = modelCreator.createBox(material);
			sector = Rock_Alien_02.scene.model;
			final Material material2 = sector.materials.get(0);
			final Attribute attribute = material2.get(PBRTextureAttribute.MetallicRoughnessTexture);
			final PBRTextureAttribute a = (PBRTextureAttribute) attribute;
			a.scaleU = 10;
			a.scaleV = 10;
		}

		{
			//		cubeWhite = modelCreator.createBox(tiles[1][0],
			//				new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createDiffuse(Color.WHITE)),
			//				Usage.Position | Usage.Normal | Usage.TextureCoordinates);

			//			ModelBuilder modelBuilder = new ModelBuilder();
			final ColorAttribute diffuseColor = ColorAttribute.createDiffuse(Color.WHITE);
			final TextureAttribute diffuseTexture = TextureAttribute.createDiffuse(texture);
			final Material material = new Material(diffuseColor, diffuseTexture);
			material.id = "water";
			water = createSquare(modelBuilder, 0.5f, 0.5f, material);
			//			water = modelBuilder.createBox(1f, 1f, 1f, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);

			//			final Attribute color = ColorAttribute.createDiffuse(Color.WHITE);
			//			Material material = new Material(TextureAttribute.createDiffuse(tiles[5][5]), color);
			//			material.id = "water";
			//			water = modelCreator.createBox(tiles[5][5], material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			//			final Attribute color = new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE);
			//			final Attribute metallic = PBRFloatAttribute.createMetallic(0.5f);
			//			final Attribute roughness = PBRFloatAttribute.createRoughness(0.2f);
			//			final Material material = new Material(/*metallic, roughness,*/ color);
			//			material.id = "water";
			//			water = modelCreator.createBox(material, 0);
			//			water = new GLTFLoader().load(Gdx.files.internal("models/glTF/water/water.gltf"));
			//			Material material = water.scene.model.materials.get(0);
			//			material.set(new BlendingAttribute(0.4f));
		}
		//		asphalt = new GLTFLoader().load(Gdx.files.internal("models/glTF/Asphalt/Asphalt.gltf"));
		//		{
		//		asphaltCracked = new GLTFLoader().load(Gdx.files.internal("models/glTF/asphaltCracked/asphaltCracked.gltf"));
		//			asphaltCracked.scene.model.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.BLACK));
		//		}
		//		{
		//			final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
		//			final Attribute metallic = PBRFloatAttribute.createMetallic(0.9f);
		//			final Attribute roughness = PBRFloatAttribute.createRoughness(0.4f);
		//			final Material material = new Material(metallic, roughness, color);
		//
		//			roundedCube = new GLTFLoader().load(Gdx.files.internal("models/glTF/roundedCube/roundedCube.gltf"));
		//			Material material2 = roundedCube.scene.model.materials.get(0);
		//			Iterator<Attribute> i = material.iterator();
		//			material2.clear();
		//			while (i.hasNext()) {
		//				Attribute a = i.next();
		//				material2.set(a);
		//			}
		//
		//		}

		//		{
		//			final ColorAttribute diffuseColor = ColorAttribute.createDiffuse(Color.WHITE);
		//			final TextureAttribute diffuseTexture = TextureAttribute.createDiffuse(texture);
		//			final Material material = new Material(diffuseColor, diffuseTexture);
		//			material.id = "post";
		//			postScreenQuad = createSquare(modelBuilder, 1920f / 2, 1080f / 2, material);
		//		}
	}

	private Model createSquare(final ModelBuilder modelBuilder, final float sx, final float sz, final Material material) {
		return modelBuilder.createRect(-sx, 0f, sz, sx, 0f, sz, sx, 0f, -sz, -sx, 0f, -sz, 0f, 1f, 0f, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	}

	public void dispose() throws Exception {
		sceneManager.dispose();
		//		manager.dispose();
		//		font.dispose();
		//		atlas.dispose();
	}

	public Color getDistinctiveColor(final int index) {
		return distinctiveColor[index % distinctiveColor.length];
	}

	private void initColors() {
		final float high = 1.0f;
		final float alpha = 0.1f;
		final List<Color> distinctiveColorlist = new ArrayList<Color>();
		for (float i = 1; i < universe.size; i++) {
			final float low = 1.0f - i * 0.2f;
			distinctiveColorlist.add(new Color(high, high, high, alpha));
			distinctiveColorlist.add(new Color(high, low, low, alpha));
			distinctiveColorlist.add(new Color(low, high, low, alpha));
			distinctiveColorlist.add(new Color(low, low, high, alpha));
			distinctiveColorlist.add(new Color(low, 1.0f, 1.0f, alpha));
			distinctiveColorlist.add(new Color(1.0f, low, 1.0f, alpha));
			distinctiveColorlist.add(new Color(1.0f, 1.0f, low, alpha));
			distinctiveColorlist.add(new Color(low, low, low, alpha));
		}
		distinctiveColor = distinctiveColorlist.toArray(new Color[0]);
	}

	Color priceColor(final Good good) {
		return availabilityColor(good.price, good.getMaxPrice());
	}

	public Color queryCreditColor(final Planet planet) {
		if (planet.getCredits() < Planet.PLANET_START_CREDITS / 2) {
			return Color.RED;
		} else if (planet.getCredits() < Planet.PLANET_START_CREDITS) {
			return Color.ORANGE;
		} else {
			return Color.WHITE;
		}
	}

	Color satesfactionColor(final float satisfactionFactor) {
		if (satisfactionFactor >= 50) {
			return Color.GREEN;
		} else if (satisfactionFactor >= 30) {
			return Color.ORANGE;
		} else {
			return Color.RED;
		}
	}

	void set(final Material material1, final Material material2) {
		final Iterator<Attribute> i = material1.iterator();
		material2.clear();
		while (i.hasNext()) {
			final Attribute a = i.next();
			material2.set(a);
		}
	}

}
