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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import de.bushnaq.abdalla.engine.CustomizedSpriteBatch;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader2DRenderer;

import java.util.ArrayList;
import java.util.List;

//TODO should be deleted
public class Render2DMaster {
    public AtlasManager          atlasManager;
    //	TextureAtlas atlas;
    public CustomizedSpriteBatch batch;
    public OrthographicCamera    camera;
    // Color[] distinctiveColorArray;
    public List<Color>           distinctiveColorlist            = new ArrayList<Color>();
    public List<Color>           distinctiveTransparentColorlist = new ArrayList<Color>();
    //	BitmapFont menuFont;
    public ShowGood              showGood                        = ShowGood.Name;
    public Universe              universe;
    float       centerX;
    float       centerY;
    int         defaultFontSize     = GameEngine2D.FONT_SIZE;
    //	public FontData[] fontDataList = { new FontData("default-font", "fonts/Roboto-Bold.ttf", Screen2D.FONT_SIZE), new FontData("zoomin-default-font", "fonts/Roboto-Bold.ttf", Screen2D.FONT_SIZE), new FontData("time-machine-font", "fonts/Roboto-Bold.ttf", Screen2D.TIME_MACHINE_FONT_SIZE), new FontData("chart-font", "fonts/Roboto-Bold.ttf", Screen2D.CHART_FONT_SIZE), new FontData("menu-font", "fonts/Roboto-Regular.ttf", Screen2D.MENU_FONT_SIZE) };
    int         height;
    GlyphLayout layout              = new GlyphLayout();
    int         timeMachineFontSize = GameEngine2D.TIME_MACHINE_FONT_SIZE;
    int         width;

    public Render2DMaster(final Universe universe) {
        this.universe = universe;
    }

    public Color amountColor(final Good good) {
        return availabilityColor(good.getAmount(), good.getMaxAmount());
    }

    public Color availabilityColor(final float amount, final float maxAmount) {
        if (amount >= 0.5 * maxAmount) {
            return Color.GREEN;
        } else if (amount >= 0.3 * maxAmount) {
            return Color.ORANGE;
        } else {
            return GameEngine2D.DARK_RED_COLOR;
        }
    }

    void bar(final TextureRegion image, final float aX1, final float aY1, final float aX2, final float aY2, final Color color) {
        final float       x1     = transformX(aX1);
        final float       y1     = transformY(aY1);
        final float       x2     = transformX(aX2);
        final float       y2     = transformY(aY2);
        final float       width  = x2 - x1 + 1;
        final float       height = y2 - y1 - 1;
        final Vector3     p1     = new Vector3(x1, y1, 0);
        final Vector3     p2     = new Vector3(x2, y2, 0);
        final BoundingBox bb     = new BoundingBox(p2, p1);
        // Vector3[] v3 = camera.frustum.planePoints;
        if (camera.frustum.boundsInFrustum(bb)) {
            batch.setColor(color);
            batch.draw(image, x1, y1, width, height);
        }
    }

    void bar(final TextureRegion image, final float aX1, final float aY1, final float aX2, final float aY2, final float aZ, final Color color) {
        final float       x1     = transformX(aX1, aZ);
        final float       y1     = transformY(aY1, aZ);
        final float       x2     = transformX(aX2, aZ);
        final float       y2     = transformY(aY2, aZ);
        final float       width  = x2 - x1 + 1;
        final float       height = y2 - y1 - 1;
        final Vector3     p1     = new Vector3(x1, y1, 0);
        final Vector3     p2     = new Vector3(x2, y2, 0);
        final BoundingBox bb     = new BoundingBox(p2, p1);
        // Vector3[] v3 = camera.frustum.planePoints;
        if (camera.frustum.boundsInFrustum(bb)) {
            batch.setColor(color);
            batch.draw(image, x1, y1, width, height);
        }
    }

    public void circle(final TextureRegion image, final float aX1, final float aY1, final float radius, final float width, final Color color, final int edges) {
        final float x1 = transformX(aX1);
        final float y1 = transformY(aY1);
        if (camera.frustum.sphereInFrustum(x1, y1, 0.0f, radius)) {
            batch.setColor(color);
            batch.circle(image, x1, y1, radius, width, edges);
        }
    }

    public void create(AtlasManager atlasManager) {
        camera = new OrthographicCamera(300, 300 * (600 / 800));
        Planet planet = universe.findBusyCenterPlanet();
        if (planet == null && !universe.planetList.isEmpty()) planet = universe.planetList.get(0);
        Vector3 lookat;
        if (planet != null)
            lookat = new Vector3(planet.x, 0, planet.z);
        else
            lookat = new Vector3(0, 0, 0);
        camera.position.set(lookat.x, lookat.z, 0);
        camera.zoom = 1.0f;
        camera.update();
        batch = new CustomizedSpriteBatch(5460);
        if (atlasManager != null)
            this.atlasManager = atlasManager;
        else {
            atlasManager = new AtlasManager();
            atlasManager.init();
        }
        //		initTextures();
        //		initFonts();
        initColors();
    }

    public void dispose() {
        batch.dispose();
        //		for (FontData fontData : fontDataList) {
        //			fontData.font.dispose();
        //		}
        //		defaultFont.dispose();
        //		menuFont.dispose();
        //		timeMachineFont.dispose();
        //		atlas.dispose();
    }

    public void fillCircle(final TextureRegion image, final float aX1, final float aY1, final float radius, final int edges, final Color color) {
        final float x1 = transformX(aX1);
        final float y1 = transformY(aY1);
        if (camera.frustum.sphereInFrustum(x1, y1, 0.0f, radius)) {
            batch.setColor(color);
            batch.fillCircle(image, x1, y1, radius, edges);
        }
    }

    public void fillPie(final TextureRegion region, final float x, final float y, final float startRadius, final float endRadius, final float startAngle, final float endAngle, final Color color, final int edges, final BitmapFont font, final Color nameColor, final String name) {
        final float x1 = transformX(x);
        final float y1 = transformY(y);
        if (camera.frustum.sphereInFrustum(x1, y1, 0.0f, endRadius)) {
            batch.setColor(color);
            batch.fillPie(region, x1, y1, startRadius, endRadius, startAngle, endAngle, edges);
            if (name != null) {
                batch.setColor(nameColor);
                final float angle = startAngle + (endAngle - startAngle) / 2;
                // ---Center
                layout.setText(font, name);
                final float tx = x + (float) Math.sin(angle) * (startRadius + (endRadius - startRadius) / 2) - layout.width / 2;
                final float ty = y - (float) Math.cos(angle) * (startRadius + (endRadius - startRadius) / 2) - layout.height / 2;
                text(tx, ty, font, nameColor, nameColor, name);
            }
        }
    }

    private void initColors() {
        {
            distinctiveColorlist.add(new Color(0.2f, 0.2f, 0.2f, 0.5f));
            final float factor = (universe.sectorList.size() / 8) / 2.0f;
            final int   c      = (int) Math.ceil(universe.sectorList.size() / 6.0);
            for (float i = 0; i < Math.ceil(universe.sectorList.size() / 6.0); i++) {
                final float low = 1.0f - (i + 1) * factor;
                //				System.out.println(low * 255);
                final float high  = 1.0f - i * factor;
                final float alpha = 1.f;
                // distinctiveColorlist.add( new Color( high, high, high, alpha ) );
                distinctiveColorlist.add(new Color(high, high, low, alpha));
                distinctiveColorlist.add(new Color(high, low, high, alpha));
                distinctiveColorlist.add(new Color(low, high, high, alpha));
                distinctiveColorlist.add(new Color(high, low, low, alpha));
                distinctiveColorlist.add(new Color(low, low, high, alpha));
                distinctiveColorlist.add(new Color(low, high, low, alpha));
                // distinctiveColorlist.add( new Color( low, high, low, alpha ) );
                // distinctiveColorlist.add( new Color( low, low, high, alpha ) );
                // distinctiveColorlist.add( new Color( low, 1.0f, 1.0f, alpha ) );
                // distinctiveColorlist.add( new Color( 1.0f, low, 1.0f, alpha ) );
                // distinctiveColorlist.add( new Color( 1.0f, 1.0f, low, alpha ) );
                // distinctiveColorlist.add( new Color( low, low, low, alpha ) );
            }
            // distinctiveColorArray = distinctiveColorlist.toArray( new Color[0] );
        }
        distinctiveTransparentColorlist.add(new Color(0.2f, 0.2f, 0.2f, 0.5f));
        final float factor = (universe.sectorList.size() / 8) / 2.0f;
        final int   c      = (int) Math.ceil(universe.sectorList.size() / 6.0);
        for (float i = 0; i < Math.ceil(universe.sectorList.size() / 6.0); i++) {
            final float low = 1.0f - (i + 1) * factor;
            //			System.out.println(low * 255);
            final float high  = 1.0f - i * factor;
            final float alpha = 0.4f;
            // distinctiveColorlist.add( new Color( high, high, high, alpha ) );
            distinctiveTransparentColorlist.add(new Color(high, high, low, alpha));
            distinctiveTransparentColorlist.add(new Color(high, low, high, alpha));
            distinctiveTransparentColorlist.add(new Color(low, high, high, alpha));
            distinctiveTransparentColorlist.add(new Color(high, low, low, alpha));
            distinctiveTransparentColorlist.add(new Color(low, low, high, alpha));
            distinctiveTransparentColorlist.add(new Color(low, high, low, alpha));
            // distinctiveColorlist.add( new Color( low, high, low, alpha ) );
            // distinctiveColorlist.add( new Color( low, low, high, alpha ) );
            // distinctiveColorlist.add( new Color( low, 1.0f, 1.0f, alpha ) );
            // distinctiveColorlist.add( new Color( 1.0f, low, 1.0f, alpha ) );
            // distinctiveColorlist.add( new Color( 1.0f, 1.0f, low, alpha ) );
            // distinctiveColorlist.add( new Color( low, low, low, alpha ) );
        }
        // distinctiveColorArray = distinctiveColorlist.toArray( new Color[0] );
    }

    //	private void initFonts() {
    //		for (int index = 0; index < fontDataList.length; index++) {
    //			FontData fontData = fontDataList[index];
    //			PixmapPacker packer = new PixmapPacker(256, 128, Format.RGBA8888, 0, true);
    //			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontData.file));
    //			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    //			parameter.size = (fontData.fontSize);
    //			parameter.packer = packer;
    //			FreeTypeBitmapFontData mainFontData = generator.generateData(parameter);
    //			// fontData.font = generator.generateFont(parameter);
    //			generator.dispose(); // don't forget to dispose to avoid memory leaks!
    //			fontData.font = new BitmapFont(mainFontData, atlas.findRegion(fontData.name), true);
    //			packer.dispose();
    //			//			fontData.font.setUseIntegerPositions(false);
    //		}
    //		defaultFont = fontDataList[0].font;
    //		zoominDefaultFont = fontDataList[1].font;
    //		timeMachineFont = fontDataList[2].font;
    //		chartFont = fontDataList[3].font;
    //		menuFont = fontDataList[4].font;
    //		//		{
    //		//			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontDataList[0].file));
    //		//			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    //		//			parameter.size = (fontDataList[0].fontSize);
    //		//			defaultFont = generator.generateFont(parameter);
    //		//		}
    //		//		zoominDefaultFont = new BitmapFont();
    //		//		timeMachineFont = new BitmapFont();
    //		//		chartFont = new BitmapFont();
    //		//		menuFont = new BitmapFont();
    //	}

    //	private void initTextures() {
    //		atlas = new TextureAtlas(Gdx.files.internal("mercatorAtlas.atlas"));
    //		systemTexture = atlas.findRegion("system");
    //		planetTexture = atlas.findRegion("planet");
    //		simTexture = atlas.findRegion("sim");
    //		factoryTexture = atlas.findRegion("factory");
    //		gaugeTexture = atlas.findRegion("gauge");
    //		traderTexture = atlas.findRegion("trader");
    //		goodTexture = atlas.findRegion("good");
    //		barTexture = atlas.findRegion("bar");
    //		dottedLineTexture = atlas.findRegion("dotted_line");
    //		//		backgroundAtlas = new TextureAtlas( Gdx.files.internal( "mercatorAtlas.atlas" ) );
    //		//		background = atlas.findRegion("backgrounda");
    //		background = atlas.findRegion("background/backgrounda");
    //	}

    public void lable(final float x, final float y, final float start, final float end, final BitmapFont font, final Color lableColor, final String name, final Color nameColor, final String value, final Color valueColor) {
        final float angle    = (float) (Math.PI / 6.0);
        final float x1       = (float) (x + Trader2DRenderer.TRADER_WIDTH / 2 + start * Math.sin(angle));
        final float y1       = (float) (y + Trader2DRenderer.TRADER_HEIGHT / 2 - start * Math.cos(angle));
        final float x2       = (float) (x + Trader2DRenderer.TRADER_WIDTH / 2 + end * Math.sin(angle));
        final float y2       = (float) (y + Trader2DRenderer.TRADER_HEIGHT / 2 - end * Math.cos(angle));
        final float x3       = x2 + Trader2DRenderer.TRADER_WIDTH * 3 * camera.zoom;
        final float thicknes = 2.0f * camera.zoom;
        line(atlasManager.dottedLineTextureRegion, x1, y1, x2, y2, lableColor, thicknes);
        line(atlasManager.dottedLineTextureRegion, x2, y2, x3, y2, lableColor, thicknes);
        layout.setText(font, name);
        text(x2, y2 - layout.height * 1.1f, font, nameColor, nameColor, name);
        layout.setText(font, value);
        text(x2, y2 + layout.height * 0.1f, font, valueColor, valueColor, value);
    }

    public void line(final TextureRegion texture, final float aX1, final float aY1, final float aX2, final float aY2, final Color color, final float aThickness) {
        final float x1 = transformX(aX1);
        final float y1 = transformY(aY1);
        final float x2 = transformX(aX2);
        final float y2 = transformY(aY2);
        // the center of your hand
        final Vector3 center = new Vector3(x1, y1, 0);
        // you need a vector from the center to your touchpoint
        final Vector3 touchPoint = new Vector3(x2, y2, 0);
        touchPoint.sub(center);
        // now convert into polar angle
        double rotation = Math.atan2(touchPoint.y, touchPoint.x);
        // rotation should now be between -PI and PI
        // so scale to 0..1
        rotation = (rotation + Math.PI) / (Math.PI * 2);
        // SpriteBatch.draw needs degrees
        rotation *= 360;
        // add Offset because of reasons
        rotation += 90;
        batch.setColor(color);
        batch.draw(texture, x1, // x, center of rotation
                y1, // y, center of rotation
                aThickness / 2, // origin x in the texture region
                0, // origin y in the texture region
                aThickness, // width
                touchPoint.len(), // height
                1.0f, // scale x
                1.0f, // scale y
                (float) rotation);
    }

    void moveCenter(final GameEngine2D screen, final int aX, final int aY) {
        //		System.out.println("------------------------------------------");
        // System.out.printf( "mouse x=%d, y=%d\n", aX, aY );
        // System.out.printf( "canvas x=%d, y=%d\n", myCanvas.getCanvas().getX(),
        // myCanvas.getCanvas().getY() );
        // Point pt = new Point( myCanvas.getCanvas().getLocation() );
        // SwingUtilities.convertPointToScreen( pt, myCanvas.getCanvas().getParent() );
        // System.out.printf( "points x=%d, y=%d\n", pt.x, pt.y );
        // System.out.printf( "delta x=%d, y=%d\n", aX - width / 2, height / 2 - aY );
        centerX += (aX - width / 2) / camera.zoom;
        centerY += (aY - height / 2) / camera.zoom;
        //		System.out.println("------------------------------------------");
    }

    public Color priceColor(final Good good) {
        return availabilityColor(good.price, good.getMaxPrice());
    }

    public Color queryCreditColor(final float creadits, final float startCredits) {
        if (creadits < startCredits / 2) {
            return Color.RED;
        } else if (creadits < startCredits) {
            return Color.ORANGE;
        } else if (creadits > startCredits * 2) {
            return Color.GREEN;
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
            return GameEngine2D.DARK_RED_COLOR;
        }
    }

    void soomIn(final GameEngine2D screen, final int aX, final int aY) {
        if (camera.zoom > 1.0) {
            camera.zoom /= 1.2f;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            atlasManager.defaultFont.getData().setScale(camera.zoom, camera.zoom);
            defaultFontSize = (int) (GameEngine2D.FONT_SIZE * camera.zoom);
            atlasManager.timeMachineFont.getData().setScale(camera.zoom, camera.zoom);
            timeMachineFontSize = (int) (GameEngine2D.TIME_MACHINE_FONT_SIZE * camera.zoom);
            //			System.out.printf("Zoom = %f\n", camera.zoom);
        }
    }

    void soomOut(final GameEngine2D screen, final int aX, final int aY) {
        if (camera.zoom < 8.0) {
            camera.zoom *= 1.2f;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            atlasManager.defaultFont.getData().setScale(camera.zoom, camera.zoom);
            defaultFontSize = (int) (GameEngine2D.FONT_SIZE * camera.zoom);
            atlasManager.timeMachineFont.getData().setScale(camera.zoom, camera.zoom);
            timeMachineFontSize = (int) (GameEngine2D.TIME_MACHINE_FONT_SIZE * camera.zoom);
            //			System.out.printf("Zoom = %f\n", camera.zoom);
        }
    }

    public void text(final float aX1, final float aY1, final BitmapFont font, final Color aBackgroundColor, final Color aTextColor, final String aString) {
        final float x1 = transformX(aX1);
        final float y1 = transformY(aY1);
        // layout.setText( font, aString );
        // Vector3 p1 = new Vector3( x1, y1, 0 );
        // Vector3 p2 = new Vector3( x1 + layout.width, y1 + layout.height, 0 );
        // BoundingBox bb = new BoundingBox( p2, p1 );
        // if ( camera.frustum.boundsInFrustum( bb ) )
        {
            final float ascent = font.getAscent() - 1;// awtFont.getLineMetrics( aString, fontRenderContext ).getAscent();
            font.setColor(aTextColor);
            font.draw(batch, aString, x1, (int) (y1 - ascent));
        }
    }

    float transformX(final float aX) {
        return (aX - centerX) + width / 2;
    }

    float transformX(final float aX, final float aZ) {
        return (aX - centerX / aZ / camera.zoom) * camera.zoom + width / 2;
    }

    float transformY(final float aY) {
        return height / 2 - (aY - centerY);
    }

    float transformY(final float aY, final float aZ) {
        return height / 2 - (aY - centerY / aZ / camera.zoom) * camera.zoom;
    }

    float untransformX(final float aX) {
        return (aX - width / 2) * camera.zoom + width / 2;
    }

    float untransformY(final float aY) {
        return (aY - height / 2) * camera.zoom + height / 2;
    }
}
