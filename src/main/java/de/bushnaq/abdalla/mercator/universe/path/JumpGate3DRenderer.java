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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class JumpGate3DRenderer extends ObjectRenderer<Screen3D> {
    public static final  float JUMP_GATE_SIZE  = 16 / Universe.WORLD_SCALE;
    private static final float JUMPGATE_DEPTH  = 0 / Universe.WORLD_SCALE /*+ Planet3DRenderer.WATER_Y*/;
    private static final float JUMP_GATE_HIGHT = 16 / Universe.WORLD_SCALE;
    private static final Color PATH_NAME_COLOR = Color.BLUE;
    private final        Path  jumpGate;
    //	Matrix4 rotationMatrix = new Matrix4();
    //	protected Quaternion rotation = new Quaternion();
    boolean lastSelected = false;
    float   r            = 45;
    //	private static final Color SELECTED_JUMPGATE_COLOR = Color.WHITE;
    //	private static final Color JUMPGATE_COLOR = new Color(0.3f, 0.3f, 0.3f, 1.0f); // 0xff5555cc
    //	static final Color JUMPGATE_COLOR = new Color(0.275f, 0.314f, 0.314f, 1.0f);
    //	private static final float JUMP_GATE_MIN_RADIUS = Planet3DRenderer.PLANET_SIZE * 4.0f + 3;
    // static final Color SELECTED_JUMPGATE_COLOR = Color.ORANGE;
    private GameObject instance;
    private Trader     lastTrader;

    public JumpGate3DRenderer(final Path jumpGate) {
        this.jumpGate = jumpGate;
    }

    @Override
    public void create(final float x, final float y, final float z, final RenderEngine3D<Screen3D> renderEngine) {
        createJumpGate(x, y, z, renderEngine);
    }

    @Override
    public void renderText(final RenderEngine3D<Screen3D> renderEngine, final int index, final boolean selected) {
        final String text1 = jumpGate.target.getName();
        renderTextOnTop(renderEngine, 0f, 0f, text1, JUMP_GATE_SIZE / 4);
//		if (jumpGate.source.trader != null) {
//			final String text2 = jumpGate.source.trader.getName();
//			renderTextOnTop(sceneManager, 0f, 10f, text2, JUMP_GATE_SIZE/2);
//		}
    }

    @Override
    public void update(final float x, final float y, final float z, final RenderEngine3D<Screen3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
        drawJumpGate(x, y, z, renderEngine, currentTime, selected);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return false;
    }

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

    private void createJumpGate(final float x, final float y, final float z, final RenderEngine3D<Screen3D> renderEngine) {
        //jump gate
        //			Vector2 target = new Vector2(jumpGate.targetPlanet.x, jumpGate.targetPlanet.y);
        //			Vector2 start = new Vector2(x, y);
        //			Vector2 line = target.sub(start);
        //			float length = line.len();
        final float tx = jumpGate.target.x;
        final float ty = jumpGate.target.y + JUMPGATE_DEPTH;
        final float tz = jumpGate.target.z;
        //			float x1 = x + (tx - x) * JUMP_GATE_MIN_RADIUS / length;
        //			float y1 = y + (ty - y) * JUMP_GATE_MIN_RADIUS / length;
        final float scalex = (tx - x);
        final float scaley = (ty - y - JUMPGATE_DEPTH);
        final float scalez = (tz - z);
        //lets start with north gateway, e.g. from planet to target in the north
        //		int ix = (int) (x / Planet.PLANET_DISTANCE);
        //		int iy = (int) (y / Planet.PLANET_DISTANCE);
        //		int iz = (int) (z / Planet.PLANET_DISTANCE);
        //		int itx = (int) (tx / Planet.PLANET_DISTANCE);
        //		int ity = (int) (ty / Planet.PLANET_DISTANCE);
        //		int itz = (int) (tz / Planet.PLANET_DISTANCE);
        //		float sign = Math.signum(scalez);

		/*if (ix == itx && iz > itz) {
			scalex = (tx - x);
			scaley = (ty - y);
			scalez = (tz - z + Planet3DRenderer.PLANET_SIZE);
		} else if (ix == itx && iz < itz) {
			scalex = (tx - x);
			scaley = (ty - y);
			scalez = (tz - z - Planet3DRenderer.PLANET_SIZE);
		} else {
			return;
		}*/

        //			Color color;
        //			if (jumpGate.closed) {
        //				color = Color.RED;
        //			} else if (jumpGate.selected) {
        //				color = Screen2D.SELECTED_COLOR;
        //			} else if (jumpGate.planet.sector == jumpGate.targetPlanet.sector) {
        //				color = renderMaster.distinctiveTransparentColorlist.get(jumpGate.planet.sector.type);
        //			} else {
        //				color = JUMPGATE_COLOR;
        //			}
        //			Color c = new Color(color);
        //			c.a = 0.45f;

        final Vector3 direction = new Vector3(scalex, scaley, scalez);
        //		Vector3 up = new Vector3(0, scalez, scaley);
        //		up.nor();

        instance = new GameObject(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.jumpGate), null, this);
        //			instance = new ModelInstance(renderMaster.boxModel2);
        //			instance.materials.get(0).set(ColorAttribute.createDiffuse(JUMPGATE_COLOR));
        //		final Color sectorColor = renderMaster.getDistinctiveColor(jumpGate.planet.sector.type);
        //		instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, sectorColor));
        renderEngine.addStatic(instance);
        final float directionLength = direction.len() + Screen3D.SPACE_BETWEEN_OBJECTS;

        //		final Vector3 shift = new Vector3(-direction.z, direction.y, direction.x);
        //		shift.nor();
        //		shift.scl(Planet.CHANNEL_SIZE/2);
        instance.instance.transform.setToTranslation(x/* + shift.x*/, y/* + shift.y*/ + JUMPGATE_DEPTH, z/* + shift.z*//*+sign * Planet3DRenderer.PLANET_SIZE / 2*/);
        //		instance.instance.transform.setToTranslation(x, y, z + sign * Planet3DRenderer.PLANET_SIZE / 2);
        //		direction.nor();
        final Vector3 targetVector = new Vector3(tx, ty, tz /*- sign * Planet3DRenderer.PLANET_SIZE / 2*/);
        instance.instance.transform.rotateTowardTarget(targetVector, Vector3.Y);
        instance.instance.transform.translate(0, /*-sign*JUMP_GATE_HIGHT / 2 -*/ Screen3D.SPACE_BETWEEN_OBJECTS, -directionLength / 2);
        instance.instance.transform.scale(JUMP_GATE_SIZE, JUMP_GATE_HIGHT, directionLength);
        instance.update();
    }

    private void drawJumpGate(final float x, final float y, final float z, final RenderEngine3D<Screen3D> renderEngine, final long currentTime, final boolean selected) {
        if (instance != null && (selected != lastSelected || jumpGate.source.trader != lastTrader)) {
            if (selected) {
//				if (jumpGate.source.trader != null) {
//					instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.RED));
//				} else {
                instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
//				}
                instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
            } else {
//				if (jumpGate.source.trader != null) {
//					instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.RED));
//					instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
//				} else
                {
                    instance.instance.materials.get(0).remove(ColorAttribute.Emissive);
                    final PBRColorAttribute ca = (PBRColorAttribute) renderEngine.getGameEngine().renderMaster.cubeBase1.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
                    instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, ca.color));
                }
            }
            lastSelected = selected;
            lastTrader   = jumpGate.source.trader;
        }
    }

    private void renderTextOnTop(final RenderEngine3D<Screen3D> renderEngine, final float dx, final float dy, final String text, final float size) {
        final float x = jumpGate.target.x;
        final float y = jumpGate.target.y + JUMP_GATE_HIGHT / 2/*- 10 / Universe.WORLD_SCALE*/;
        final float z = jumpGate.target.z;
        //draw text
        final PolygonSpriteBatch batch = renderEngine.batch2D;
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
