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

package de.bushnaq.abdalla.mercator.universe.good;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.util.AnnulusSegment;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Good3DRenderer extends ObjectRenderer<GameEngine3D> {

    public static final  int                            CONTAINER_EDGE_SIZE        = 4;
    public static final  float                          GOOD_HEIGHT                = 8f;
    public static final  float                          GOOD_X                     = 8f / Universe.WORLD_SCALE;
    public static final  float                          GOOD_Y                     = 8f / Universe.WORLD_SCALE;
    public static final  float                          GOOD_Z                     = 8f / Universe.WORLD_SCALE;
    public static final  Color                          NOT_TRADED_GOOD_COLOR      = Color.LIGHT_GRAY; // 0xffbbbbbb;
    public static final  Color                          SELECTED_GOOD_COLOR        = Color.LIGHT_GRAY; // 0xffeeeeee;
    public static final  float                          SPACE_BETWEEN_GOOD         = GameEngine3D.SPACE_BETWEEN_OBJECTS * 2;
    static final         Color                          GOOD_COLOR                 = new Color(0.09f, 0.388f, 0.69f, 0.8f); // 0xff000000;
    private static final float                          ANGLE_BORDER               = (float) Math.PI / 256;
    private static final int                            GOOD_AMOUNT_DRAWING_FACTOR = 5;
    private static final Color                          GOOD_NAME_COLOR            = new Color(0.596f, 0.08f, 0.247f, 0.8f);
    private static final float                          MAX_RADIUS                 = Planet2DRenderer.PLANET_SIZE * 7.5f;
    private static final float                          MIN_ANGLE                  = (float) Math.PI / 12;
    private static final float                          MIN_RADIUS                 = Planet2DRenderer.PLANET_SIZE * 6f + 3;
    private static       Color                          DIAMON_BLUE_COLOR          = new Color(0x006ab6ff);
    private static       Color                          GRAY_COLOR                 = new Color(0x404853ff);
    private static       Color                          POST_GREEN_COLOR           = new Color(0x00614eff);
    private static       Color                          SCARLET_COLOR              = new Color(0xb00233ff);
    private final        Good                           good;
    private final        Logger                         logger                     = LoggerFactory.getLogger(this.getClass());
    private final        List<GameObject<GameEngine3D>> unusedMls                  = new ArrayList<>();
    private final        List<GameObject<GameEngine3D>> usedMls                    = new ArrayList<>();
    AnnulusSegment annulusSegment;

    public Good3DRenderer(final Good good) {
        this.good = good;
    }

    public static Color getColor(final int index) {
        switch (index) {
            case 0:
                return POST_GREEN_COLOR;
            case 1:
                return SCARLET_COLOR;
            case 2:
                return DIAMON_BLUE_COLOR;
            case 3:
                return GRAY_COLOR;
            case -1:
                return Color.WHITE;//we are not transporting any good
            default:
                return Color.WHITE;
        }
    }

    public static GameObject<GameEngine3D> instanciateGoodGameObject(final Good good, final RenderEngine3D<GameEngine3D> renderEngine) {
        GameObject<GameEngine3D> scene     = null;
        final Material           material1 = renderEngine.getGameEngine().assetManager.goodContainer.scene.model.materials.get(0);
        scene = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.goodContainer.scene.model), good);
        //TODO reuse instances
//        final Material            material2 = scene.instance.materials.get(0);
//        final Iterator<Attribute> i         = material1.iterator();
//        material2.clear();
//        while (i.hasNext()) {
//            final Attribute a = i.next();
//            material2.set(a);
//        }
        for (Material material : scene.instance.materials) {
//            if (material.id.equals("m.type"))
            {
                material.set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, getColor(good.type.ordinal())));
            }

        }

//        scene.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, getColor(good.type.ordinal())));
        return scene;
    }

    private void drawGood(final float aX, final float aY, final Good good, final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        Color color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else if (good.isTraded(renderEngine.getGameEngine().universe.currentTime)) {
            color = GOOD_COLOR;
        } else {
            color = NOT_TRADED_GOOD_COLOR;
        }
        // if ( good.type == GoodType.Food && !good.isTraded(
        // universe.currentTime ) )
        // System.out.printf( "planet %s last time %d now %d\n", planet.name,
        // good.lastBuyInterest, universe.currentTime );
        // float x = aX * Screen.PLANET_DISTANCE + Screen.PLANET_ATMOSPHARE_SIZE / 2 -
        // Screen.GOOD_WIDTH;
        // float y = aY * Screen.PLANET_DISTANCE - Screen.PLANET_ATMOSPHARE_SIZE / 2 +
        // index * Screen.GOOD_HEIGHT;
        // renderMaster.bar( renderMaster.goodTexture, x, y, x + Screen.GOOD_WIDTH - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, color );
        final float tx          = aX;
        final float ty          = aY;
        final float minAngle    = (float) Math.PI / 2 + MIN_ANGLE * index;
        final float maxAngle    = (float) Math.PI / 2 + MIN_ANGLE * (index + 1) - ANGLE_BORDER;
        String      name        = null;
        float       deltaRadius = 0;
        Color       barColor    = null;
        switch (renderEngine.getGameEngine().showGood) {
            case Price:
                name = String.format("%.0f", good.price);
                barColor = renderEngine.getGameEngine().priceColor(good);
                deltaRadius = (maxAngle - minAngle) * good.price / good.getMaxPrice();
                break;
            case Name:
                name = good.type.getName();
                barColor = renderEngine.getGameEngine().amountColor(good);
                deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
                break;
            case Volume:
                name = String.format("%.0f", good.getAmount());
                barColor = renderEngine.getGameEngine().amountColor(good);
                deltaRadius = (maxAngle - minAngle) * good.getAmount() / good.getMaxAmount();
                break;
        }
        // if ( renderMaster.camera.zoom > 1.2f )
        // {
        // name = null;
        // }
//        if (renderEngine.getCamera().position.y < 3000)
        {
            renderEngine.renderutils2Dxz.fillPie(renderEngine.getGameEngine().getAtlasManager().factoryTextureRegion, tx, 0, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle, color, 8, renderEngine.getGameEngine().getAtlasManager().zoominDefaultFont, GameEngine2D.TEXT_COLOR, name);
            renderEngine.renderutils2Dxz.fillPie(renderEngine.getGameEngine().getAtlasManager().gaugeTextureRegion, tx, 0, ty, MAX_RADIUS - 5, MAX_RADIUS, maxAngle - deltaRadius, maxAngle, barColor, 8, renderEngine.getGameEngine().getAtlasManager().zoominDefaultFont, GameEngine2D.TEXT_COLOR, "");
        }
        annulusSegment = new AnnulusSegment(tx, ty, MIN_RADIUS, MAX_RADIUS, minAngle, maxAngle);
    }

    @Override
    public void render2D(final float x, final float y, final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        drawGood(x, y, good, renderEngine, index, selected);
    }

    @Override
    public void renderText(final float aX, final float aY, final float aZ, final RenderEngine3D<GameEngine3D> renderEngine, final int index) {
//	public void renderText(final RenderEngine<Screen3D> renderEngine, final int index) {
        {
            final float dy = -Planet3DRenderer.PLANET_3D_SIZE / 2 + index * (CONTAINER_EDGE_SIZE + 1) * (GOOD_Y + SPACE_BETWEEN_GOOD);
            final float dx = Planet3DRenderer.PLANET_3D_SIZE / 2 - (CONTAINER_EDGE_SIZE) * (GOOD_X + SPACE_BETWEEN_GOOD);
            renderTextOnTop(aX, aY, aZ, renderEngine, dy, 0, dx, good.type.getName(), GOOD_X);
            //			final float size = 8;
            //			final float x = aX;
            //			final float y = aY;
            //			final float z = aZ;
            //			//draw text
            //			final PolygonSpriteBatch batch = sceneManager.batch2D;
            //			final BitmapFont font = sceneManager.getAtlasManager().modelFont;
            //			{
            //				final Matrix4 m = new Matrix4();
            //				final float fontSize = font.getLineHeight();
            //				final float scaling = size / fontSize;
            //				m.setToTranslation(x - Planet3DRenderer.PLANET_SIZE / 2 + (CONTAINER_EDGE_SIZE) * (GOOD_X + SPACE_BETWEEN_GOOD) + size, y + 1, z + Planet3DRenderer.PLANET_SIZE / 2 - index * (CONTAINER_EDGE_SIZE + 1) * (GOOD_Y + SPACE_BETWEEN_GOOD) - size / 5);
            //				final Vector3 xVector = new Vector3(1, 0, 0);
            //				final Vector3 yVector = new Vector3(0, 1, 0);
            //				m.rotate(yVector, 90);
            //				m.rotate(xVector, -90);
            //				m.scale(scaling, scaling, 1f);
            //				batch.setTransformMatrix(m);
            //				font.setColor(GOOD_NAME_COLOR);
            //				font.draw(batch, good.type.getName(), 0, 0);
            //			}

        }

    }

    @Override
    public void update(final float x, final float y, final float z, final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
        updateGood(x, y, z, renderEngine, currentTime, index, false);
    }

    private void renderTextOnTop(final float aX, final float aY, final float aZ, final RenderEngine3D<GameEngine3D> renderEngine, final float dx, final float dy, final float dz, final String text, final float size) {
        //draw text
        final PolygonSpriteBatch batch = renderEngine.renderEngine25D.batch;
        final BitmapFont         font  = renderEngine.getGameEngine().getAtlasManager().modelFont;
        {
            final Matrix4     m        = new Matrix4();
            final float       fontSize = font.getLineHeight();
            final float       scaling  = size / fontSize;
            final GlyphLayout layout   = new GlyphLayout();
            layout.setText(font, text);
            final float width  = layout.width;// contains the width of the current set text
            final float height = layout.height; // contains the height of the current set text
            //on top
            {
                final Vector3 xVector = new Vector3(1, 0, 0);
                final Vector3 yVector = new Vector3(0, 1, 0);
                m.setToTranslation(aX - height * scaling / 2.0f + size - dz, aY + 0.2f, aZ + width * scaling / 2.0f - size - dx);
                m.rotate(yVector, 90);
                m.rotate(xVector, -90);
                m.scale(scaling, scaling, 1f);

            }
            batch.setTransformMatrix(m);
            font.setColor(GOOD_NAME_COLOR);
            font.draw(batch, text, 0, 0);
        }
    }

    private void updateGood(final float aX, final float aY, final float aZ, final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final int index, final boolean selected) {
        //		Color color;
        //		if (selected) {
        //			color = SELECTED_GOOD_COLOR;
        //		} else if (good.isTraded(renderMaster.universe.currentTime)) {
        //			color = GOOD_COLOR;
        //		} else {
        //			color = NOT_TRADED_GOOD_COLOR;
        //		}
        // if ( good.type == GoodType.Food && !good.isTraded(
        // universe.currentTime ) )
        // System.out.printf( "planet %s last time %d now %d\n", planet.name,
        // good.lastBuyInterest, universe.currentTime );
        final int delta = usedMls.size() - good.getAmount() / GOOD_AMOUNT_DRAWING_FACTOR;
        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                final GameObject<GameEngine3D> scene = usedMls.remove(usedMls.size() - 1);
                unusedMls.add(scene);
                if (!renderEngine.removeStatic(scene))
                    logger.error("Game engine logic error: Expected dynamic GameObject to exist.");
            }
        } else if (delta < 0) {
            final int addNr    = -delta;
            final int reuseNr  = Math.min(addNr, unusedMls.size());// reuse from unused
            final int createNr = addNr - reuseNr;// create the rest
            for (int i = 0; i < reuseNr; i++) {
                final GameObject<GameEngine3D> scene = unusedMls.remove(unusedMls.size() - 1);
                usedMls.add(scene);
                renderEngine.addStatic(scene);
            }
            for (int i = 0; i < createNr; i++) {
                final int        edgeSize   = CONTAINER_EDGE_SIZE;
                final int        xEdgeSize  = edgeSize;
                final int        yEdgeSize  = edgeSize;
                final int        xContainer = usedMls.size() % xEdgeSize;
                final int        zContainer = (int) Math.floor(usedMls.size() / xEdgeSize) % yEdgeSize;
                final int        yContainer = (int) Math.floor(usedMls.size() / (xEdgeSize * yEdgeSize));
                final float      x          = aX - Planet3DRenderer.PLANET_3D_SIZE / 2 + GOOD_X / 2 + xContainer * (GOOD_X + 1);
                final float      z          = aZ + Planet3DRenderer.PLANET_3D_SIZE / 2 - GOOD_Z / 2 - zContainer * (GOOD_Z) + 1 - index * (edgeSize + 1) * (GOOD_Z + 1);
                final float      y          = aY + GOOD_Y / 2 + yContainer * (GOOD_Y);
                final GameObject go         = instanciateGoodGameObject(good, renderEngine);
                go.instance.transform.setToTranslationAndScaling(x, y, z, GOOD_X - SPACE_BETWEEN_GOOD, GOOD_Y - SPACE_BETWEEN_GOOD, GOOD_Z - SPACE_BETWEEN_GOOD);
                go.update();
                renderEngine.addStatic(go);
                usedMls.add(go);
            }

        } else {
            // everything is good
        }
        // renderMaster.bar( renderMaster.goodTexture, x, y, x + Screen.GOOD_WIDTH - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
        // Screen.SPACE_BETWEEN_OBJECTS, color );
        // updateLight( good, x + GOOD_WIDTH / 2, y, new Color( 0.8f, 0.8f, 0.8f, 0.8f
        // ), GOOD_WIDTH );
        // light = renderMaster.updatePointLight( light, renderMaster, x + (
        // Screen.GOOD_WIDTH - Screen.SPACE_BETWEEN_OBJECTS ) / 2, y + (
        // Screen.GOOD_HEIGHT - Screen.SPACE_BETWEEN_OBJECTS ) / 2, lightColor,
        // Screen.GOOD_WIDTH );
        switch (renderEngine.getGameEngine().assetManager.showGood) {
            case Price:
                //				renderMaster.text( x, y, color, Screen.TEXT_COLOR, String.format( "%.0f", good.price ) );
                //				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
                //						(int)( ( Screen.GOOD_HEIGHT * good.price ) / good.getMaxPrice() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
                //						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.priceColor( good ) );
                break;
            case Name:
                //				renderMaster.text( x, y, color, Screen.TEXT_COLOR, good.type.getName() );
                //				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
                //						(int)( ( Screen.GOOD_HEIGHT * good.amount ) / good.getMaxAmount() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
                //						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.amountColor( good ) );
                break;
            case Volume:
                //				renderMaster.text( x, y, color, Screen.TEXT_COLOR, String.format( "%.0f", good.amount ) );
                //				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
                //						(int)( ( Screen.GOOD_HEIGHT * good.amount ) / good.getMaxAmount() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
                //						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.amountColor( good ) );
                break;
        }
    }
//    @Override
//    public boolean withinBounds(final float x, final float y) {
//        return annulusSegment.contains(x, y);
//    }

}
