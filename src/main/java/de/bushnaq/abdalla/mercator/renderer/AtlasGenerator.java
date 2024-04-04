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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.bushnaq.abdalla.engine.util.FontData;
import de.bushnaq.abdalla.mercator.desktop.Context;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class AtlasGenerator {
    private static final int MAX_ITERATIONS = 64;

    private int calculatePageSize(final int i) {
        return 64 * i;
    }

    private boolean foundMissingImage(TextureAtlas atlas, File srcDir) {
        Collection<File> files = FileUtils.listFiles(srcDir, new String[]{"png"}, false);
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".9.png"))
                fileName = fileName.substring(0, fileName.indexOf(".9.png")) + ".png";
            TextureAtlas.AtlasRegion atlasRegion = atlas.findRegion(removeFileExtension(fileName));
            if (atlasRegion == null) {
                return true;
            }
        }
        return false;
    }

    private void generateFonts(final FontData[] fontDataList) throws Exception {
        for (final FontData fontData : fontDataList) {
            File fontFile = new File("app/assets/raw/" + fontData.name + ".png");
            if (!fontFile.exists()) {
                //font was never generated
                int i = 1;
                int pageSize;
                for (; i < MAX_ITERATIONS; i++) {
                    pageSize = calculatePageSize(i);
                    try {

                        final PixmapPacker packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
                        {
                            final FreeTypeFontGenerator                       generator = new FreeTypeFontGenerator(Gdx.files.internal(fontData.file));
                            final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                            parameter.size   = (fontData.fontSize);
                            parameter.packer = packer;
                            generator.generateData(parameter);
                            generator.dispose(); // don't forget to dispose to avoid memory leaks!
                        }
                        final Array<PixmapPacker.Page> pages = packer.getPages();
                        if (pages.size == 1) {
                            final PixmapPacker.Page p      = pages.get(0);
                            final Pixmap            pixmap = p.getPixmap();
                            System.out.println("Generating font '" + fontData.name + ".png'.");
                            final FileHandle fh = new FileHandle("app/assets/raw/" + fontData.name + ".png");
                            PixmapIO.writePNG(fh, pixmap);
                            pixmap.dispose();
                            break;
                        }
                    } catch (final GdxRuntimeException e) {
                        if (e.getMessage().equals("Page size too small for pixmap.")) {
                            //ignore
                        } else {
                            throw e;
                        }
                    }
                }
                if (i == MAX_ITERATIONS)
                    throw new Exception(String.format("Page size of %d too small for font: %s", calculatePageSize(MAX_ITERATIONS), fontData.name));
            }
        }
    }

    public void generateIfNeeded(AtlasManager atlasManager) throws Exception {
        boolean generateAtlas = false;
        generateFonts(atlasManager.fontDataList);
        File atlasFile = new File("app/assets/atlas/atlas.png");
        File srcDir1   = new File("app/assets/textures/");
        File srcDir2   = new File("app/assets/ui/");
        if (!atlasFile.exists()) {
            //atlas does not exist
            generateAtlas = true;
        } else {
            long lastAtlasGeneration = atlasFile.lastModified();
            long lastPngGeneration1  = getNewestFile(srcDir1);
            long lastPngGeneration2  = getNewestFile(srcDir2);
            if (lastAtlasGeneration < lastPngGeneration1 || lastAtlasGeneration < lastPngGeneration2) {
                //there is at least on png file newer than the atlas
                generateAtlas = true;
            } else {
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Context.getAppFolderName() + "/assets/atlas/atlas.atlas"));
                if (foundMissingImage(atlas, srcDir1)) {
                    //there is at least one file missing in the current atlas
                    generateAtlas = true;
                } else if (foundMissingImage(atlas, srcDir2)) {
                    //there is at least one file missing in the current atlas
                    generateAtlas = true;
                }
                atlas.dispose();
            }
        }
        if (generateAtlas) {
            (atlasFile).delete();
            (new File("app/assets/atlas/atlas.atlas")).delete();
            FileUtils.copyDirectory(srcDir1, new File("app/assets/raw/"));
            FileUtils.copyDirectory(srcDir2, new File("app/assets/raw/"));
            TexturePacker.process("app/assets/raw", "app/assets/atlas/", "atlas");
        }
    }

    private long getNewestFile(File srcDir) {
        long             lastPngGeneration = 0;
        Collection<File> files             = FileUtils.listFiles(srcDir, null, false);
        for (File file : files) {
            lastPngGeneration = Math.max(lastPngGeneration, file.lastModified());
        }
        return lastPngGeneration;
    }

    private String removeFileExtension(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }
}
