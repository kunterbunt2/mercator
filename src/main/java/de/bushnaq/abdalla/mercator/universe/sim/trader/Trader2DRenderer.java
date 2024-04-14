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

package de.bushnaq.abdalla.mercator.universe.sim.trader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;

public class Trader2DRenderer extends ObjectRenderer<GameEngine2D> {
    // static final Color SELECTED_TRADER_COLOR = Color.ORANGE; //0xffff0000;
    public static final  Color   TADER_COLOR_IS_GOOD     = Color.LIGHT_GRAY; // 0xaaaaaa
    public static final  Color   TRADER_COLOR            = new Color(.7f, .7f, .7f, 0.45f); // 0xffcc5555;
    public static final  float   TRADER_HEIGHT           = 17;
    public static final  float   TRADER_SIZE_Z           = 32 * 2 / Universe.WORLD_SCALE;
    public static final  float   TRADER_WIDTH            = 17f;
    private static final float   RADIUS                  = Planet2DRenderer.PLANET_SIZE * 2.0f;
    private static final float   TRADER_SIZE_X           = 16 * 2 / Universe.WORLD_SCALE;
    private static final float   TRADER_SIZE_Y           = 8 * 2 / Universe.WORLD_SCALE;
    private static final float   TRADER_TRAVELING_HEIGHT = -TRADER_SIZE_Y / 2 + Planet3DRenderer.WATER_Y;
    private final        Vector3 direction               = new Vector3();//intermediate value
    private final        Vector3 scaling                 = new Vector3();//intermediate value
    private final        Vector3 target                  = new Vector3();//intermediate value
    private final        Trader  trader;
    //	public static final Color TRADER_OF_SELECTED_PLANET_COLOR1 = Color.RED; // 0xffff0000;
//	public static final Color TRADER_OF_SELECTED_PLANET_COLOR2 = new Color(1f, .5f, 0f, 1f); // 0xffff8800;
    private final        Vector3 translation             = new Vector3();//intermediate value
    Circle  circle;
    float[] lastVelocity = new float[3];
    float[] position     = new float[3];
    Vector3 speed        = new Vector3(0, 0, 0);
    float[] velocity     = new float[3];
    private boolean lastSelected = false;

    public Trader2DRenderer(final Trader trader) {
        this.trader = trader;
        circle      = new Circle(0, 0, TRADER_WIDTH + 1);
    }

    private void drawTrader(final Trader trader, final RenderEngine2D<GameEngine2D> renderEngine, final int index, final boolean selected) {
//		Color color;
//		if (selected) {
//			color = Screen2D.SELECTED_COLOR;
//		}
//		else {
//			color = TRADER_COLOR;
//		}
//		if (!trader.traderStatus.isGood()) {
//			color = TADER_COLOR_IS_GOOD;
//		}
//		float x = 0;
//		float y = 0;
//		final float deltaAngle = (float) (Math.PI / 8);
//		if (trader.targetWaypoint != null) {
//			// ---Traveling
//			if (trader.destinationWaypointDistance != 0) {
//				x = (trader.planet.x + (trader.targetWaypoint.x - trader.planet.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance);
//				y = (trader.planet.z + (trader.targetWaypoint.z - trader.planet.z) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance);
//			} else {
//				x = trader.planet.x;
//				y = trader.planet.z;
//			}
//		} else {
//			x = (float) (trader.planet.x + RADIUS * Math.sin(trader.planet.orbitAngle + deltaAngle * index));
//			y = (float) (trader.planet.z + RADIUS * Math.cos(trader.planet.orbitAngle + deltaAngle * index));
//		}
//		final float hps = TRADER_WIDTH / 2;
//		renderMaster.fillCircle(renderMaster.atlasManager.planetTextureRegion, x, y, hps + 1, 32, color);
//		// renderMaster.bar( renderMaster.fillCircle.get( TRADER_WIDTH, TRADER_HEIGHT ),
//		// x - hps, y - hps, x + hps, y + hps, color );
//		if (renderMaster.camera.zoom < 3.0f) {
//			renderMaster.lable(x - hps, y - hps, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderMaster.atlasManager.defaultFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderMaster.queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));
//		}
//		circle.setPosition(x, y);
        if (trader.targetWaypoint != null) {

            position[0] = translation.x;
            position[1] = translation.y;
            position[2] = translation.z;
//            trader.getEngine().calculateEngineSpeed();
            if (trader.sourceWaypoint != null)
                speed.set(trader.targetWaypoint.x - trader.sourceWaypoint.x, 0, trader.targetWaypoint.z - trader.sourceWaypoint.z);
            else
                speed.set(1, 0, 1);

            speed.nor();
            //			final float engineSpeed = trader.getMaxEngineSpeed();
            speed.scl(trader.getEngine().getEngineSpeed());
            velocity[0] = speed.x;
            velocity[1] = 0;
            velocity[2] = speed.z;

            boolean update = false;
            for (int i = 0; i < 3; i++) {
                if (Math.abs(lastVelocity[i] - velocity[i]) > 0.001f) {
                    update = true;
                }
            }
            if (update) {
//			synth.setPositionAndVelocity(position, velocity);
                //				if (Debug.isFilter(trader.getName()))
                //					logger.info(String.format("%f %f  %f %f  %f %f", lastVelocity[0], velocity[0], lastVelocity[1], velocity[1], lastVelocity[2], velocity[2]));
                for (int i = 0; i < 3; i++)
                     lastVelocity[i] = velocity[i];
            }

            //			if (Debug.isFilter(trader.getName()))
//		synth.play();
            // ---Traveling
            if (trader.destinationWaypointDistance != 0) {
                final float scalex = (trader.targetWaypoint.x - trader.sourceWaypoint.x);
                final float scaley = (trader.targetWaypoint.y - trader.sourceWaypoint.y);
                final float scalez = (trader.targetWaypoint.z - trader.sourceWaypoint.z);
                direction.set(scalex, scaley, scalez);
                //				shift.set(-direction.z, direction.y, direction.x);
                //				shift.nor();
                //				shift.scl(Planet.CHANNEL_SIZE / 2);
                translation.x = (trader.sourceWaypoint.x + (trader.targetWaypoint.x - trader.sourceWaypoint.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.x*/;
                translation.y = (trader.sourceWaypoint.y + (trader.targetWaypoint.y - trader.sourceWaypoint.y) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.y*/ + TRADER_TRAVELING_HEIGHT;
                translation.z = (trader.sourceWaypoint.z + (trader.targetWaypoint.z - trader.sourceWaypoint.z) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.z*/;
            } else {
                translation.x = trader.planet.x /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2*/;
                translation.y = trader.planet.y + TRADER_TRAVELING_HEIGHT;
                translation.z = trader.planet.z /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_SIZE_Z*/;
            }
        } else {
//		synth.pause();
            // in port
            translation.x = trader.planet.x /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2*/;
            translation.y = trader.planet.y + TRADER_TRAVELING_HEIGHT;
            translation.z = trader.planet.z /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_SIZE_Z*/;
        }
        translation.add(0, TRADER_SIZE_Y / 2, 0);
        scaling.set(TRADER_SIZE_X, TRADER_SIZE_Y, TRADER_SIZE_Z);
//		instance.instance.transform.setToTranslation(translation);

        //		pole.instance.transform.setToTranslation(translation);

        if (trader.targetWaypoint != null) {
            target.set(trader.targetWaypoint.x/* + shift.x*/, Planet3DRenderer.WATER_Y, trader.targetWaypoint.z/* + shift.z*/);
            //			instance.instance.transform.rotateTowardTarget(target, Vector3.Y);
//		instance.instance.transform.rotateTowardDirection(direction, Vector3.Y);
//		instance.instance.transform.scale(scaling.x, scaling.y, scaling.z);
            //			pole.instance.transform.rotateTowardDirection(direction, Vector3.Y);
            //			pole.instance.transform.translate(-TRADER_X_SIZE / 2, POLE_Y_SIZE / 2, -TRADER_Z_SIZE / 2);
            //			pole.instance.transform.scale(1f, POLE_Y_SIZE, 1f);
        }

        //		if (Debug.isFilter(trader.getName())) {
        //						System.out.println("x=" + position[0] + " y=" + position[1] + " z=" + position[2]);
        //		}

//        trader.x = translation.x;
//        trader.y = translation.y;
//        trader.z = translation.z;
//		instance.update();
        //		pole.update();
        if (selected != lastSelected) {
            if (selected) {
//			instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
//			instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
            } else {
                //				instance.instance.materials.get(0).remove(ColorAttribute.Emissive);
                //				final PBRColorAttribute ca = (PBRColorAttribute) renderMaster.trader.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
                //				instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, ca.color));
            }
            lastSelected = selected;
        }
        Color color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else {
            color = TRADER_COLOR;
        }
        if (!trader.getTraderStatus().isGood()) {
            color = TADER_COLOR_IS_GOOD;
        }
        final float hps = TRADER_WIDTH / 2;
        renderEngine.getGameEngine().renderEngine.fillCircle(renderEngine.getGameEngine().atlasManager.planetTextureRegion, translation.x, translation.z, hps + 1, 32, color);
        // renderMaster.bar( renderMaster.fillCircle.get( TRADER_WIDTH, TRADER_HEIGHT ),
        // x - hps, y - hps, x + hps, y + hps, color );
        if (renderEngine.getGameEngine().renderEngine.camera.zoom < 3.0f) {
            renderEngine.getGameEngine().renderEngine.lable(renderEngine.getGameEngine().atlasManager.dottedLineTextureRegion, translation.x - hps, translation.z - hps, Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderEngine.getGameEngine().atlasManager.defaultFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderEngine.getGameEngine().queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));
        }
        circle.setPosition(translation.x, translation.z);
//        if (Debug.isFilter(trader.getName())) {
//            System.out.println(String.format("%f %f", trader.x, trader.z));
//        }
    }

    @Override
    public void render(final float px, final float py, final RenderEngine2D<GameEngine2D> renderEngine, final int index, final boolean selected) {
        drawTrader(trader, renderEngine, index, selected);
    }

    @Override
    public boolean withinBounds(final float x, final float y) {
        return circle.contains(x, y);
    }
}
