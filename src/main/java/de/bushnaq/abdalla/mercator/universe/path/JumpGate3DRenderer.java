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

package de.bushnaq.abdalla.mercator.universe.path;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine2D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader3DRenderer;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class JumpGate3DRenderer extends ObjectRenderer<GameEngine3D> {
    public static final  float JUMP_GATE_WIDTH  = 16 / Universe.WORLD_SCALE;
    static final         Color JUMPGATE_COLOR   = new Color(0.275f, 0.314f, 0.314f, 1.0f);
    private static final float JUMP_GATE_DEPTH  = 0 / Universe.WORLD_SCALE /*+ Planet3DRenderer.WATER_Y*/;
    private static final float JUMP_GATE_HEIGHT = 16 / Universe.WORLD_SCALE;
    private static final Color PATH_NAME_COLOR  = Color.BLUE;
    private final        Path  jumpGate;
    float   directionLength;
    //	private void renderTextOnTop(final SceneManager sceneManager, final String text1) {
    //		final float x = jumpGate.target.x;
    //		final float y = jumpGate.target.y - 10 / Universe.WORLD_SCALE;
    //		final float z = jumpGate.target.z;
    //
    //		final float size = JUMP_GATE_SIZE * 2;
    //		//draw text
    //		final PolygonSpriteBatch batch = sceneManager.batch2D;
    //		final BitmapFont font = sceneManager.getAtlasManager().modelFont;
    //		{
    //			final Matrix4 m = new Matrix4();
    //			final float fontSize = font.getLineHeight();
    //			final float scaling = size / fontSize;
    //			final GlyphLayout layout = new GlyphLayout();
    //			layout.setText(font, text1);
    //			final float width = layout.width;// contains the width of the current set text
    //			final float height = layout.height; // contains the height of the current set text
    //			m.setToTranslation(x - height * scaling / 2.0f, y + 0.2f, z + width * scaling / 2.0f);
    //			//			m.setToTranslation(x - size/2, y + 0.1f, z - size / 2);
    //			m.rotate(Vector3.Y, 90);
    //			m.rotate(Vector3.X, -90);
    //			m.scale(scaling, scaling, 1f);
    //			batch.setTransformMatrix(m);
    //			font.setColor(PATH_NAME_COLOR);
    //			font.draw(batch, text1, 0, 0);
    //		}
    //	}
    boolean instance2AddedToEngine = false;
    //	Matrix4 rotationMatrix = new Matrix4();
    //	protected Quaternion rotation = new Quaternion();
    boolean lastSelected           = false;
    float   r                      = 45;
    Vector3 targetVector;
    //	private static final Color SELECTED_JUMPGATE_COLOR = Color.WHITE;
    //	private static final Color JUMPGATE_COLOR = new Color(0.3f, 0.3f, 0.3f, 1.0f); // 0xff5555cc
    //	static final Color JUMPGATE_COLOR = new Color(0.275f, 0.314f, 0.314f, 1.0f);
    //	private static final float JUMP_GATE_MIN_RADIUS = Planet3DRenderer.PLANET_SIZE * 4.0f + 3;
    // static final Color SELECTED_JUMPGATE_COLOR = Color.ORANGE;
    private GameObject<GameEngine3D> instance;
    private GameObject<GameEngine3D> instanceOfSelected;//visible if path is selected
    private Trader                   lastTrader;

    public JumpGate3DRenderer(final Path jumpGate) {
        this.jumpGate = jumpGate;
    }

    @Override
    public void create(final float x, final float y, final float z, final RenderEngine3D<GameEngine3D> renderEngine) {
        createJumpGate(x, y, z, renderEngine);
    }

    public void render2D(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final Vector2 target    = new Vector2(jumpGate.target.x, jumpGate.target.z);
        final Vector2 start     = new Vector2(jumpGate.source.x, jumpGate.source.z);
        Color         color;
        float         thickness = 1.3f /* renderEngine.getGameEngine().renderEngine.camera.zoom*/;
        if (jumpGate.closed) {
            color = Color.RED;
        } else if (jumpGate.selected) {
            color     = GameEngine2D.SELECTED_COLOR;
            thickness = 3.3f /* renderEngine.getGameEngine().renderEngine.camera.zoom*/;
        } else {
            color = JUMPGATE_COLOR;
        }
        final Color c = new Color(color);
        c.a = 0.45f;
        renderEngine.renderutils2Dxz.line(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, start.x, 0, start.y, target.x, 0, target.y, c, thickness);
    }

    @Override
    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        if (renderEngine.isDebugMode()) {
            final String text1 = jumpGate.target.getName();
            renderTextOnTop(renderEngine, 0f, 0f, text1, JUMP_GATE_WIDTH / 4);
//		if (jumpGate.source.trader != null) {
//			final String text2 = jumpGate.source.trader.getName();
//			renderTextOnTop(sceneManager, 0f, 10f, text2, JUMP_GATE_SIZE/2);
//		}
        }
    }

    @Override
    public void update(final float x, final float y, final float z, final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
        drawJumpGate(x, y, z, renderEngine, currentTime, selected);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return false;
    }

    private void createJumpGate(final float x, final float y, final float z, final RenderEngine3D<GameEngine3D> renderEngine) {
        //jump gate
        final float tx     = jumpGate.target.x;
        final float ty     = jumpGate.target.y + JUMP_GATE_DEPTH;
        final float tz     = jumpGate.target.z;
        final float scalex = (tx - x);
        final float scaley = (ty - y - JUMP_GATE_DEPTH);
        final float scalez = (tz - z);

        final Vector3 direction = new Vector3(scalex, scaley, scalez);

        instance        = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.jumpGate), null, this);
        directionLength = direction.len() + GameEngine3D.SPACE_BETWEEN_OBJECTS;
        targetVector    = new Vector3(tx, ty, tz /*- sign * Planet3DRenderer.PLANET_SIZE / 2*/);

        instance.instance.transform.setToTranslation(x/* + shift.x*/, y/* + shift.y*/ + JUMP_GATE_DEPTH, z/* + shift.z*//*+sign * Planet3DRenderer.PLANET_SIZE / 2*/);
        instance.instance.transform.rotateTowardTarget(targetVector, Vector3.Y);
        instance.instance.transform.translate(0, /*-sign **/ -JUMP_GATE_HEIGHT / 2 - 1, -directionLength / 2);
        instance.instance.transform.scale(JUMP_GATE_WIDTH, JUMP_GATE_HEIGHT - GameEngine3D.SPACE_BETWEEN_OBJECTS, directionLength);
        instance.update();
        renderEngine.addStatic(instance);

        instanceOfSelected = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.jumpGateArrow), null, this);
        instanceOfSelected.instance.transform.setToTranslation(x/* + shift.x*/, y/* + shift.y*/ + JUMP_GATE_DEPTH, z/* + shift.z*//*+sign * Planet3DRenderer.PLANET_SIZE / 2*/);
        instanceOfSelected.instance.transform.rotateTowardTarget(targetVector, Vector3.Y);
        instanceOfSelected.instance.transform.translate(0, /*-sign **/ Trader3DRenderer.TRADER_SIZE_Y, -directionLength / 2);
        instanceOfSelected.instance.transform.scale(JUMP_GATE_WIDTH / 2, 1, directionLength);
        instanceOfSelected.update();
//        renderEngine.addStatic(instance2);
    }

    private void drawJumpGate(final float x, final float y, final float z, final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final boolean selected) {
        if (selected && !instance2AddedToEngine) {
            renderEngine.addStatic(instanceOfSelected);
            instance2AddedToEngine = true;
        } else if (!selected && instance2AddedToEngine) {
            renderEngine.removeStatic(instanceOfSelected);
            instance2AddedToEngine = false;
        }
//        instanceOfSelected.instance.transform.translate(0, /*-sign **/ JUMP_GATE_HEIGHT / 2 + 3 + (float) Math.sin((currentTime % 100) * Math.PI), -directionLength / 2);
//        instanceOfSelected.instance.transform.rotateTowardTarget(targetVector, Vector3.Y);
//        instanceOfSelected.instance.transform.translate(0, /*-sign **/ JUMP_GATE_HEIGHT / 2 + 3, -directionLength / 2);
//        instanceOfSelected.instance.transform.scale(JUMP_GATE_WIDTH, 1, directionLength);
//        instanceOfSelected.update();
//        instanceOfSelected.instance.transform.translate(0, , 0);
//        if (instance != null && (selected != lastSelected || jumpGate.source.trader != lastTrader)) {
//            if (selected) {
////				if (jumpGate.source.trader != null) {
////					instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.RED));
////				} else {
//                instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
////				}
//                instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
//            } else {
////				if (jumpGate.source.trader != null) {
////					instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.RED));
////					instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
////				} else
//                {
//                    instance.instance.materials.get(0).remove(ColorAttribute.Emissive);
//                    final PBRColorAttribute ca = (PBRColorAttribute) renderEngine.getGameEngine().assetManager.cubeBase1.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
//                    instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, ca.color));
//                }
//            }
//            lastSelected = selected;
//            lastTrader   = jumpGate.source.trader;
//        }
    }

    public void render2Da(final RenderEngine2D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final Vector2 target    = new Vector2(jumpGate.target.x, jumpGate.target.z);
        final Vector2 start     = new Vector2(jumpGate.source.x, jumpGate.source.z);
        Color         color;
        float         thickness = 1.3f /* renderEngine.getGameEngine().renderEngine.camera.zoom*/;
        if (jumpGate.closed) {
            color = Color.RED;
        } else if (jumpGate.selected) {
            color     = GameEngine2D.SELECTED_COLOR;
            thickness = 3.3f /* renderEngine.getGameEngine().renderEngine.camera.zoom*/;
        } else {
            color = JUMPGATE_COLOR;
        }
        final Color c = new Color(color);
        c.a = 0.45f;
        renderEngine.batch.line(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, start.x, 0, start.y, target.x, 0, target.y, c, thickness);
    }

    private void renderTextOnTop(final RenderEngine3D<GameEngine3D> renderEngine, final float dx, final float dy, final String text, final float size) {
        final float x = jumpGate.target.x;
        final float y = jumpGate.target.y + JUMP_GATE_HEIGHT / 2 + 10;
        final float z = jumpGate.target.z;
        //draw text
        final PolygonSpriteBatch batch = renderEngine.renderEngine2D.batch;
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
                m.setToTranslation(x - height * scaling / 2.0f - dy, y + 0.2f, z + width * scaling / 2.0f - dx);
                m.rotate(Vector3.Y, 90);
                m.rotate(Vector3.X, -90);
                m.scale(scaling, scaling, 1f);

            }
            batch.setTransformMatrix(m);
            font.setColor(PATH_NAME_COLOR);
            font.draw(batch, text, 0, 0);
        }
    }
}
