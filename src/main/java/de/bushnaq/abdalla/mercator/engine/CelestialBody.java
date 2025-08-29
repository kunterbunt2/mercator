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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CelestialBody {
    Color color;
    private Vector3 direction = new Vector3();
    float exponent;

    public CelestialBody() {
        float xAngle = (float) Math.random() * 360;
        float yAngle = (float) Math.random() * 360;
//        float         zAngle = (float) Math.random() * 360;
        final Matrix4 m = new Matrix4();
        m.rotate(Vector3.X, xAngle);
        m.rotate(Vector3.Y, yAngle);
        m.rotate(Vector3.Z, 0);
        m.translate(0, 0, -1);
        m.getTranslation(direction);
        {
            float max = 1000000 * 5;
            float min = 100000;
            exponent = (float) (min + Math.random() * (max - min));
        }
        {
            float minAlpha   = .1f;
            float brightness = (float) Math.random();
            float r          = brightness + (float) Math.random() * (1f - brightness);
            float g          = brightness + (float) Math.random() * (1f - brightness);
            float b          = brightness + (float) Math.random() * (1f - brightness);
            float a          = minAlpha + (float) Math.random() * (1f - minAlpha);
            color = new Color(r, g, b, a);
        }
    }

    public CelestialBody(Vector3 direction, Color color, float exponent) {
        this.direction = direction;
        this.color     = color;
        this.exponent  = exponent;
    }

    public Color getColor() {
        return color;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public float getExponent() {
        return exponent;
    }
}
