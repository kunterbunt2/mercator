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

package de.bushnaq.abdalla.mercator.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.bushnaq.abdalla.engine.CustomizedSpriteBatch;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader2DRenderer;

public class PieChart extends Image {
    private static final int             MAX_RADIUS = 50;
    private static final int             MIN_RADIUS = 30;
    private final        BitmapFontCache cache;
    GlyphLayout  layout = new GlyphLayout();
    PieChartData pieChart;

    /**
     * Creates an image stretched, and aligned center.
     *
     * @param creditPieChart
     */
    public PieChart(final PieChartData creditPieChart, final Skin skin, final String drawableName) {
        super(skin.getRegion(drawableName));
        cache         = skin.getFont("graph-font").newFontCache();
        this.pieChart = creditPieChart;
        getDrawable().setMinWidth(MAX_RADIUS * 5);
        getDrawable().setMinHeight(MAX_RADIUS * 5);
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        validate();
        final float x      = getX();
        final float y      = getY();
        final float scaleX = getScaleX();
        final float scaleY = getScaleY();
        if (getDrawable() != null) {
            final TextureRegion region = ((TextureRegionDrawable) getDrawable()).getRegion();
            // batch.setColor( Color.BLACK );
            // batch.draw( region, x + getImageX(), y + getImageY(), getImageWidth() *
            // scaleX, getImageHeight() * scaleY );
            // getDrawable().draw( batch, x + getImageX(), y + getImageY(), getImageWidth()
            // * scaleX, getImageHeight() * scaleY );
            layout.setText(cache.getFont(), pieChart.caption);
            final Color textColor = Color.WHITE;
            cache.getFont().setColor(textColor.r, textColor.g, textColor.b, textColor.a * parentAlpha);
            cache.getFont().setColor(Color.WHITE);
            cache.getFont().draw(batch, pieChart.caption, x + getImageWidth() / 2 - layout.width / 2, y + getImageHeight() / 2 + layout.height / 2);
            float sum = 0;
            for (final PieChartSectionData pc : pieChart.pices) {
                final Color pcColor = new Color(pc.color.r, pc.color.g, pc.color.b, pc.color.a * parentAlpha);
                fillPie((CustomizedSpriteBatch) batch, region, x + getImageWidth() / 2, y + getImageHeight() / 2, MIN_RADIUS * scaleX, MAX_RADIUS * scaleY, (float) (sum * Math.PI * 2 / 100), (float) ((sum + pc.percentage - 2) * Math.PI * 2 / 100), pcColor, 32, textColor, pc.name, textColor, "" + pc.absolute);
                sum += pc.percentage;
            }
        }
    }

    public void fillPie(final CustomizedSpriteBatch batch, final TextureRegion region, final float x1, final float y1, final float startRadius, final float endRadius, final float startAngle, final float endAngle, final Color color, final int edges, final Color nameColor, final String name, final Color valueColor, final String value) {
        if (getStage().getCamera().frustum.sphereInFrustum(x1, y1, 0.0f, endRadius)) {
            batch.setColor(color);
            batch.fillPie(region, x1, y1, startRadius, endRadius, startAngle, endAngle, edges);
            if (name != null) {
                final float angle = startAngle + (endAngle - startAngle) / 2;
                // ---Center
                final float tx = x1 + (float) Math.sin(angle) * endRadius;
                final float ty = y1 + (float) Math.cos(angle) * endRadius;
                // cache.getFont().setColor( nameColor );
                // cache.getFont().draw( batch, name, tx, ty );
                float yOrientation = 1;
                float xOrientation = 1;
                if (angle > Math.PI / 2 && angle < (Math.PI / 2) * 3) {
                    yOrientation = -1;
                }
                if (angle > Math.PI && angle < Math.PI * 2) {
                    xOrientation = -1;
                }
                lable(batch, tx, ty, xOrientation, yOrientation, 0, 30, nameColor, name, nameColor, value, nameColor);
            }
        }
    }

    public void lable(final CustomizedSpriteBatch batch, final float x, final float y, final float xOrientation, final float yOrientation, final float start, final float end, final Color lableColor, final String name, final Color nameColor, final String value, final Color valueColor) {
        layout.setText(cache.getFont(), name);
        final float         angle    = (float) (Math.PI / 6.0);
        final float         x1       = (float) (x + start * Math.sin(angle));
        final float         y1       = (float) (y - start * Math.cos(angle));
        final float         x2       = (float) (x + xOrientation * end * Math.sin(angle));
        final float         y2       = (float) (y + yOrientation * end * Math.cos(angle));
        final float         x3       = x2 + xOrientation * Trader2DRenderer.TRADER_WIDTH * 3;
        final float         thicknes = 2.0f;
        final TextureRegion region   = ((TextureRegionDrawable) getDrawable()).getRegion();
        line(batch, region, x1, y1, x2, y2, lableColor, thicknes);
        line(batch, region, x2, y2, x3, y2, lableColor, thicknes);
        cache.getFont().setColor(nameColor);
        if (xOrientation > 0) {
            cache.getFont().draw(batch, name, x2, y2 - layout.height * 1.1f);
            cache.getFont().setColor(valueColor);
            cache.getFont().draw(batch, value, x2, y2 + layout.height * 1.5f);
        } else {
            cache.getFont().draw(batch, name, x3, y2 - layout.height * 1.1f);
            cache.getFont().setColor(valueColor);
            cache.getFont().draw(batch, value, x3, y2 + layout.height * 1.5f);
        }
        // text( x2, y2 - fontSize * 1.1f, nameColor, nameColor, name );
        // text( x2, y2 + fontSize * 0.1f, valueColor, valueColor, value );
    }

    public void line(final CustomizedSpriteBatch batch, final TextureRegion texture, final float x1, final float y1, final float x2, final float y2, final Color color, final float aThickness) {
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
}
