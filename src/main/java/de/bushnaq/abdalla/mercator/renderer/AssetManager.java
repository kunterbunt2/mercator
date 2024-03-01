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

package de.bushnaq.abdalla.mercator.renderer;

import com.badlogic.gdx.Gdx;
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
import de.bushnaq.abdalla.engine.util.ModelCreator;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.Iterator;

/**
 * loads 3D model assets for rendering
 */
public class AssetManager {
    //    public SceneAsset              BoomBox;
    public SceneAsset              Rock_Alien_02;
    public Model                   buildingModel;
    public MercatorRandomGenerator createRG = new MercatorRandomGenerator(1, null);
    //    public SceneAsset              cubeAluminiumBrushed;
    public Model                   cubeBase1;
    public Model                   cubeModel;//used for debugging
    //    public SceneAsset              cubeGoldLeaves;
    public Model                   goodContainer;
    public Model                   jumpGate;
    public Model                   land;
    public Model                   mirrorModel;
    public Model                   planetModel;
    public Model                   redEmissiveModel;
    public Model                   sector;
    public ShowGood                showGood = ShowGood.Name;
    public SceneAsset              trader;
    //    public SceneAsset              traderAsset;
    public SceneAsset              turbine;
    public Universe                universe;
    public Model                   waterModel;
//    public SceneAsset              wheel;

    public AssetManager(final Universe universe) {
        this.universe = universe;
    }

//    Color amountColor(final Good good) {
//        return availabilityColor(good.getAmount(), good.getMaxAmount());
//    }

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
//        cubeGoldLeaves = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/cube-Gold_leafs/cube-Gold_leafs.gltf"));
        final Texture           texture      = new Texture(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/tiles.png"));
        final TextureRegion[][] tiles        = TextureRegion.split(texture, 32, 32);
        final ModelBuilder      modelBuilder = new ModelBuilder();
        final ModelCreator      modelCreator = new ModelCreator();
//        cubeAluminiumBrushed = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/cube-Aluminium_brushed/cube-Aluminium_brushed.gltf"));
//        BoomBox              = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/BoomBox.gltf"));
        {
            final Attribute metallic  = PBRFloatAttribute.createMetallic(0.3f);
            final Attribute roughness = PBRFloatAttribute.createRoughness(0.2f);
            final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.LIGHT_GRAY);
            //			Attribute normal = PBRFloatAttribute.createNormalScale(0.0f);
            //			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
            //			Attribute culling = IntAttribute.createCullFace(1);
            //			Attribute shininess = PBRFloatAttribute.createShininess(1.0f);
            final Material material = new Material(metallic, roughness, color /*, culling, normal, occlusion, shininess */);
            cubeBase1 = modelCreator.createBox(material);
        }
        createTrader();
        createGoodContainer(modelBuilder);

        createRedEmissiveModel(modelCreator);
        createPlanet(modelCreator);
        {
            final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.DARK_GRAY);
            final Attribute metallic  = PBRFloatAttribute.createMetallic(0.0f);
            final Attribute roughness = PBRFloatAttribute.createRoughness(1.0f);
            final Material  material  = new Material(metallic, roughness, color);
            land = modelCreator.createBox(material);
        }
        createBuilding(modelBuilder);
        createJumpgate(modelBuilder);
//        {
//            wheel = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/wheel/wheel.gltf"));
//            final Material material = wheel.scene.model.materials.get(0);
//            material.set(buildingCube.materials.get(0));
//            set(buildingCube.materials.get(0), material);
//        }
        createTurbine();
        {
//            final Attribute metallic  = PBRFloatAttribute.createMetallic(0.0f);
//            final Attribute roughness = PBRFloatAttribute.createRoughness(1.0f);
//            final Attribute color     = PBRColorAttribute.createBaseColorFactor(new Color(Color.WHITE));
//            final Material  material  = new Material(metallic, roughness, color);
//            sector = modelCreator.createBox(material);
            Rock_Alien_02 = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/Rock_Alien_02/Rock_Alien_02.gltf"));
            sector        = Rock_Alien_02.scene.model;
            final Material            material2 = sector.materials.get(0);
            final Attribute           attribute = material2.get(PBRTextureAttribute.MetallicRoughnessTexture);
            final PBRTextureAttribute a         = (PBRTextureAttribute) attribute;
            a.scaleU = 10;
            a.scaleV = 10;
        }

        createWater(texture, modelBuilder);
        createMirror(texture, modelBuilder);
        createCube(modelBuilder);
    }

    private void createBuilding(ModelBuilder modelBuilder) {
        final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
        final Attribute metallic  = PBRFloatAttribute.createMetallic(0.5f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
        final Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
        //			final Attribute culling = IntAttribute.createCullFace(1);
        //			final Material material = new Material(metallic, roughness, color);
        //			final Material material = new Material(metallic, roughness, color, culling/*, normal*/, occlusion/*, shininess */);
        final Material material = new Material(metallic, roughness, color, occlusion);
        buildingModel = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
    }

    private void createCube(ModelBuilder modelBuilder) {
        final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
        final Attribute metallic  = PBRFloatAttribute.createMetallic(0.5f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
        final Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
        final Material  material  = new Material(metallic, roughness, color, occlusion);
        cubeModel = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
    }

    private void createGoodContainer(ModelBuilder modelBuilder) {
        final Attribute color = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
        //			Attribute culface = IntAttribute.createCullFace(1);
        final Attribute metallic  = PBRFloatAttribute.createMetallic(0.5f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
        //			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
        //			Attribute normal = PBRFloatAttribute.createNormalScale(1.0f);
        final Material material = new Material(metallic, roughness, color/*, culface, normal, occlusion*/);
        //			cubeGood = modelCreator.createBox(material);
        goodContainer = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
    }

    private void createJumpgate(ModelBuilder modelBuilder) {
        final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
        final Attribute metallic  = PBRFloatAttribute.createMetallic(1f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
        final Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(1.0f);
        //			final Attribute culling = IntAttribute.createCullFace(1);
        //			final Material material = new Material(metallic, roughness, color);
        //			final Material material = new Material(metallic, roughness, color, culling/*, normal*/, occlusion/*, shininess */);
        final Material material = new Material(metallic, roughness, color, occlusion);
        jumpGate = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
    }

    private void createMirror(Texture texture, ModelBuilder modelBuilder) {
        final ColorAttribute   diffuseColor   = ColorAttribute.createDiffuse(Color.BLACK);
        final TextureAttribute diffuseTexture = TextureAttribute.createDiffuse(texture);
        final Material         material       = new Material(diffuseColor, diffuseTexture);
        material.id = "mirror";
        mirrorModel = createSquare(modelBuilder, 0.5f, 0.5f, material);
    }

    private void createPlanet(ModelCreator modelCreator) {
        final Attribute color     = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.WHITE);
        final Attribute metallic  = PBRFloatAttribute.createMetallic(0.5f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.5f);
        final Material  material  = new Material(metallic, roughness, color);
        planetModel = modelCreator.createBox(material);
    }

    private void createRedEmissiveModel(ModelCreator modelCreator) {
        final Attribute emissive = ColorAttribute.createEmissive(Color.RED);
        final Material  material = new Material(emissive);
        redEmissiveModel = modelCreator.createBox(material);
    }

    private Model createSquare(final ModelBuilder modelBuilder, final float sx, final float sz, final Material material) {
        return modelBuilder.createRect(-sx, 0f, sz, sx, 0f, sz, sx, 0f, -sz, -sx, 0f, -sz, 0f, 1f, 0f, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }

    private void createTrader() {
        final Attribute metallic  = PBRFloatAttribute.createMetallic(0.7f);
        final Attribute roughness = PBRFloatAttribute.createRoughness(0.3f);
        final Attribute color     = PBRColorAttribute.createBaseColorFactor(Color.BLACK);
        //			Attribute normal = PBRFloatAttribute.createNormalScale(0.0f);
        //			Attribute occlusion = PBRFloatAttribute.createOcclusionStrength(0.0f);
        //			Attribute culling = IntAttribute.createCullFace(0);
        //			Attribute shininess = PBRFloatAttribute.createShininess(1.0f);
        final Material material = new Material(metallic, roughness, color/* , culling, normal, occlusion, shininess */);
        //			trader = modelCreator.createBox(material);
//            trader      = modelBuilder.createBox(1.0f, 1.0f, 1.0f, material, Usage.Position | Usage.Normal);
        trader = new GLBLoader().load(Gdx.files.internal(String.format(AtlasManager.getAssetsFolderName() + "/models/trader.glb")));
        for (Material m : trader.scene.model.materials) {
//        Material m = trader.scene.model.materials.get(0);
            m.set(metallic);
            m.set(roughness);
            m.set(color);
        }
    }

    private void createTurbine() {
        turbine = new GLTFLoader().load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/models/glTF/turbine/turbine.gltf"));
    }

    private void createWater(Texture texture, ModelBuilder modelBuilder) {
        final ColorAttribute   diffuseColor   = ColorAttribute.createDiffuse(Color.WHITE);
        final TextureAttribute diffuseTexture = TextureAttribute.createDiffuse(texture);
        final Material         material       = new Material(diffuseColor, diffuseTexture);
        material.id = "water";
        waterModel  = createSquare(modelBuilder, 0.5f, 0.5f, material);
    }

    public void dispose() throws Exception {
//		sceneManager.dispose();
        //		manager.dispose();
        //		font.dispose();
        //		atlas.dispose();
    }

//    public Color getDistinctiveColor(final int index) {
//        return distinctiveColor[index % distinctiveColor.length];
//    }

//    private void initColors() {
//        final float       high                 = 1.0f;
//        final float       alpha                = 0.1f;
//        final List<Color> distinctiveColorlist = new ArrayList<Color>();
//        for (float i = 1; i < universe.size; i++) {
//            final float low = 1.0f - i * 0.2f;
//            distinctiveColorlist.add(new Color(high, high, high, alpha));
//            distinctiveColorlist.add(new Color(high, low, low, alpha));
//            distinctiveColorlist.add(new Color(low, high, low, alpha));
//            distinctiveColorlist.add(new Color(low, low, high, alpha));
//            distinctiveColorlist.add(new Color(low, 1.0f, 1.0f, alpha));
//            distinctiveColorlist.add(new Color(1.0f, low, 1.0f, alpha));
//            distinctiveColorlist.add(new Color(1.0f, 1.0f, low, alpha));
//            distinctiveColorlist.add(new Color(low, low, low, alpha));
//        }
//        distinctiveColor = distinctiveColorlist.toArray(new Color[0]);
//    }

    Color priceColor(final Good good) {
        return availabilityColor(good.price, good.getMaxPrice());
    }

//    public Color queryCreditColor(final Planet planet) {
//        if (planet.getCredits() < Planet.PLANET_START_CREDITS / 2) {
//            return Color.RED;
//        } else if (planet.getCredits() < Planet.PLANET_START_CREDITS) {
//            return Color.ORANGE;
//        } else {
//            return Color.WHITE;
//        }
//    }

//    Color satesfactionColor(final float satisfactionFactor) {
//        if (satisfactionFactor >= 50) {
//            return Color.GREEN;
//        } else if (satisfactionFactor >= 30) {
//            return Color.ORANGE;
//        } else {
//            return Color.RED;
//        }
//    }

    void set(final Material material1, final Material material2) {
        final Iterator<Attribute> i = material1.iterator();
        material2.clear();
        while (i.hasNext()) {
            final Attribute a = i.next();
            material2.set(a);
        }
    }

}
