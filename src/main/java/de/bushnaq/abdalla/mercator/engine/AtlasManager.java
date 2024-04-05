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
import de.bushnaq.abdalla.engine.util.AtlasGenerator;
import de.bushnaq.abdalla.engine.util.FontData;
import de.bushnaq.abdalla.mercator.desktop.Context;

import java.io.File;

public class AtlasManager {
    private static String       assetsFolderName;
    public         TextureAtlas atlas;
    public         BitmapFont   bold128Font;
    public         BitmapFont   bold256Font;
    public         BitmapFont   chartFont;
    public         AtlasRegion  dashTextureRegion;
    public         AtlasRegion  dashTextureRegion16;
    public         BitmapFont   defaultFont;
    public         BitmapFont   demoMidFont;
    public         AtlasRegion  dottedLineTextureRegion;
    public         AtlasRegion  factoryTextureRegion;
    public         FontData[]   fontData = {new FontData("default-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", GameEngine2D.FONT_SIZE),//
            new FontData("zoomin-default-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 10),//
            new FontData("time-machine-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", GameEngine2D.TIME_MACHINE_FONT_SIZE),//
            new FontData("chart-font", Context.getAppFolderName() + "/assets/fonts/Roboto-bold.ttf", GameEngine2D.CHART_FONT_SIZE),//
            new FontData("menu-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Regular.ttf", GameEngine2D.MENU_FONT_SIZE),//
            new FontData("menu-font-bold", Context.getAppFolderName() + "/assets/fonts/Roboto-bold.ttf", GameEngine2D.MENU_FONT_SIZE),//
            new FontData("model-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 64),//
            new FontData("bold-128", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 128),//
            new FontData("bold-256", Context.getAppFolderName() + "/assets/fonts/Roboto-Bold.ttf", 256),//
            new FontData("demo-mid-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Regular.ttf", 32),//
            new FontData("logo-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Thin.ttf", 128), //
            new FontData("version-font", Context.getAppFolderName() + "/assets/fonts/Roboto-Thin.ttf", 16) //
    };
    public         AtlasRegion  gaugeTextureRegion;
    public         BitmapFont   logoFont;
    public         BitmapFont   menuBoldFont;
    public         BitmapFont   menuFont;
    public         BitmapFont   modelFont;
    public         AtlasRegion  patternCircle12;
    public         AtlasRegion  patternCircle24;
    public         AtlasRegion  planetTextureRegion;
    public         AtlasRegion  simTextureRegion;
    public         AtlasRegion  systemTextureRegion;
    public         BitmapFont   timeMachineFont;
    public         AtlasRegion  traderTextureRegion;
    public         BitmapFont   versionFont;
    public         BitmapFont   zoominDefaultFont;

    public AtlasManager() {
    }

    public static String getAssetsFolderName() {
        return assetsFolderName;
    }

    public void dispose() {
        for (final FontData fontData : fontData) {
            fontData.font.dispose();
        }
        atlas.dispose();
    }

    public void init() throws Exception {
        assetsFolderName = Context.getAppFolderName() + "/assets/";
        initTextures();
        initFonts();
    }

    private void initFonts() {
        for (int index = 0; index < fontData.length; index++) {
            final FontData    fontData    = this.fontData[index];
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
        defaultFont       = fontData[0].font;
        zoominDefaultFont = fontData[1].font;
        timeMachineFont   = fontData[2].font;
        chartFont         = fontData[3].font;
        menuFont          = fontData[4].font;
        menuBoldFont      = fontData[5].font;
        modelFont         = fontData[6].font;
        bold128Font       = fontData[7].font;
        bold256Font       = fontData[8].font;
        demoMidFont       = fontData[9].font;
        logoFont          = fontData[10].font;
        versionFont       = fontData[11].font;
    }

    private void initTextures() throws Exception {
        AtlasGenerator atlasGenerator = new AtlasGenerator();
        atlasGenerator.setOutputFolder(getAssetsFolderName() + "atlas/");
        atlasGenerator.setInputFolders(new File[]{new File(getAssetsFolderName() + "textures/"), new File(getAssetsFolderName() + "ui/")});
        atlasGenerator.setFontData(fontData);
        atlasGenerator.generateIfNeeded();
        atlas                   = new TextureAtlas(Gdx.files.internal(getAssetsFolderName() + "atlas/atlas.atlas"));
        systemTextureRegion     = atlas.findRegion("system");
        planetTextureRegion     = atlas.findRegion("planet");
        simTextureRegion        = atlas.findRegion("sim");
        factoryTextureRegion    = atlas.findRegion("factory");
        gaugeTextureRegion      = atlas.findRegion("gauge");
        traderTextureRegion     = atlas.findRegion("trader");
        dashTextureRegion       = atlas.findRegion("dash");
        dashTextureRegion16     = atlas.findRegion("dash16");
        patternCircle12         = atlas.findRegion("pattern-circle-12");
        patternCircle24         = atlas.findRegion("pattern-circle-24");
        dottedLineTextureRegion = atlas.findRegion("dotted_line");
        VisUI.load(VisUI.SkinScale.X2);
        VisUI.getSkin().getFont("default-font").getData().markupEnabled = true;
        VisUI.getSkin().getFont("small-font").getData().markupEnabled   = true;
        Colors.put("BOLD", new Color(0x1BA1E2FF));
    }
}
