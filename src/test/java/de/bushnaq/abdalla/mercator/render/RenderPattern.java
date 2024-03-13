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

package de.bushnaq.abdalla.mercator.render;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RenderPattern {
    @Test
    public void renderCircle() throws IOException {
        renderCircle(128, 128, 12);
        renderCircle(512, 512, 24);
    }

    public void renderCircle(int width, int height, int sectors) throws IOException {
        String        fileName = String.format("pattern-circle-%d.png", sectors);
        BufferedImage bi       = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D    g2       = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = width / 2;
        int cy = height / 2;
        g2.setPaint(Color.white);
        float angleDelta = 360f / (width * height);
        float a          = 360f / sectors;
        float r          = (float) Math.sqrt(width * width + height * height);
        for (int i = 0; i < sectors; i++) {
            float startAngle = (i - .25f) * a;
            float endAngle   = (i + .25f) * a;
            for (float angle = startAngle; angle < endAngle; angle += angleDelta) {
                double rad = Math.toRadians(angle);
                double rx  = r * Math.cos(rad);
                double ry  = r * Math.sin(rad);
                g2.drawLine(cx, cy, (int) (cx + rx), (int) (cy + ry));
            }
        }

        ImageIO.write(bi, "PNG", new File(fileName));
    }
}
