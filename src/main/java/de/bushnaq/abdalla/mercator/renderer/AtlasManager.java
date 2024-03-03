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
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.kotcrab.vis.ui.VisUI;
import de.bushnaq.abdalla.engine.util.FontData;
import de.bushnaq.abdalla.mercator.desktop.Context;

public class AtlasManager {
    public static final int          SOOMIN_FONT_SIZE = 10;
    private static      String       assetsFolderName;
    public              TextureAtlas atlas;
    public              AtlasRegion  barTextureRegion;
    public              BitmapFont   chartFont;
    public              BitmapFont   defaultFont;
    public              BitmapFont   demoBigFont;
    public              BitmapFont   demoMidFont;
    public              AtlasRegion  dottedLineTextureRegion;
    public              AtlasRegion  factoryTextureRegion;
    public              FontData[]   fontDataList     = {new FontData("default-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", GameEngine2D.FONT_SIZE),//
            new FontData("zoomin-default-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", SOOMIN_FONT_SIZE),//
            new FontData("time-machine-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", GameEngine2D.TIME_MACHINE_FONT_SIZE),//
            new FontData("chart-font", Context.getAppFolderName() + "/assets/fonts/Roboto-bold.ttf", GameEngine2D.CHART_FONT_SIZE),//
            new FontData("menu-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Regular.ttf", GameEngine2D.MENU_FONT_SIZE),//
            new FontData("menu-font-bold", Context.getAppFolderName() + "/assets/fonts/Roboto-bold.ttf", GameEngine2D.MENU_FONT_SIZE),//
            new FontData("model-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 64),//
            new FontData("demo-big-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 128),//
            new FontData("demo-mid-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Regular.ttf", 32)//
    };
    public              AtlasRegion  gaugeTextureRegion;
    public              BitmapFont   menuBoldFont;
    //	public Texture good_baseColor;
    //	public AtlasRegion good_baseColorRegion;
    //	public Texture good_normal;
    //	public AtlasRegion good_normalRegion;
    //	public Texture good_occlusionRoughnessMetallic;
    //	public AtlasRegion good_occlusionRoughnessMetallicRegion;
    public              BitmapFont   menuFont;
    public              BitmapFont   modelFont;
    public              AtlasRegion  planetTextureRegion;
    public              AtlasRegion  simTextureRegion;
    public              AtlasRegion  systemTextureRegion;
    public              BitmapFont   timeMachineFont;
    public              AtlasRegion  traderTextureRegion;
    public              BitmapFont   zoominDefaultFont;

    public AtlasManager() {
    }

    public static String getAssetsFolderName() {
        return assetsFolderName;
    }

    public void dispose() {
        for (final FontData fontData : fontDataList) {
            fontData.font.dispose();
        }
        atlas.dispose();
    }

    public void init() {
        assetsFolderName = Context.getAppFolderName() + "/assets";
        initTextures();
        initFonts();
    }

    private void initFonts() {
        for (int index = 0; index < fontDataList.length; index++) {
            final FontData    fontData    = fontDataList[index];
            final AtlasRegion atlasRegion = atlas.findRegion(fontData.name);
            atlasRegion.getRegionWidth();
            atlasRegion.getRegionHeight();
            final PixmapPacker          packer    = new PixmapPacker(atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), Format.RGBA8888, 1, false);
            final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontData.file));
            final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size   = (fontData.fontSize);
            parameter.packer = packer;
            final BitmapFont generateFont = generator.generateFont(parameter);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
            fontData.font = new BitmapFont(generateFont.getData(), atlas.findRegion(fontData.name), true);
            packer.dispose();
            fontData.font.setUseIntegerPositions(false);
        }
        defaultFont       = fontDataList[0].font;
        zoominDefaultFont = fontDataList[1].font;
        timeMachineFont   = fontDataList[2].font;
        chartFont         = fontDataList[3].font;
        menuFont          = fontDataList[4].font;
        menuBoldFont      = fontDataList[5].font;
        modelFont         = fontDataList[6].font;
        demoBigFont       = fontDataList[7].font;
        demoMidFont       = fontDataList[8].font;
    }

    private void initTextures() {
        atlas                = new TextureAtlas(Gdx.files.internal(Context.getAppFolderName() + "/assets/atlas/atlas.atlas"));
        systemTextureRegion  = atlas.findRegion("system");
        planetTextureRegion  = atlas.findRegion("planet");
        simTextureRegion     = atlas.findRegion("sim");
        factoryTextureRegion = atlas.findRegion("factory");
        gaugeTextureRegion   = atlas.findRegion("gauge");
        traderTextureRegion  = atlas.findRegion("trader");
        //		good_baseColorRegion = atlas.findRegion("good_baseColor");
        //		good_normalRegion = atlas.findRegion("good_normal");
        //		good_occlusionRoughnessMetallicRegion = atlas.findRegion("good_occlusionRoughnessMetallic");
        barTextureRegion        = atlas.findRegion("bar");
        dottedLineTextureRegion = atlas.findRegion("dotted_line");
        //		good_baseColor = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_basecolor.jpg"));
        //		good_occlusionRoughnessMetallic = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_metallic-Metal_Floor_01_roughness.png"));
        //		good_normal = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_normal.jpg"));
        VisUI.load(VisUI.SkinScale.X2);
        VisUI.getSkin().getFont("default-font").getData().markupEnabled = true;
        VisUI.getSkin().getFont("small-font").getData().markupEnabled   = true;
        Colors.put("BOLD", new Color(0x1BA1E2FF));
    }
}
