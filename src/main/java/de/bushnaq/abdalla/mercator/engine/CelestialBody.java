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
import com.badlogic.gdx.math.Vector3;

public class CelestialBody {
    Color color;
    private Vector3 direction = new Vector3();
    float exponent;

    public CelestialBody() {
        // Generate uniform random point on sphere surface
        // Use proper spherical coordinate distribution to avoid pole clustering
        float u = (float) Math.random(); // [0, 1)
        float v = (float) Math.random(); // [0, 1)

        // Convert to spherical coordinates with uniform distribution
        float theta = (float) (2 * Math.PI * u); // azimuthal angle [0, 2π)
        float phi   = (float) Math.acos(2 * v - 1); // polar angle from uniform cosine distribution

        // Convert spherical coordinates to Cartesian
        float x = (float) (Math.sin(phi) * Math.cos(theta));
        float y = (float) (Math.sin(phi) * Math.sin(theta));
        float z = (float) Math.cos(phi);

        direction.set(x, y, z);
        {
            float max = 1000000000;
            float min = 1000000;
            exponent = (float) (min + Math.random() * (max - min));
        }
        {
            float minAlpha    = .1f;
            float colorFactor = 0.1f;
            float brightness  = (float) Math.random() * (1f - colorFactor);
            float r           = brightness + (float) Math.random() * (colorFactor);
            float g           = brightness + (float) Math.random() * (colorFactor);
            float b           = brightness + (float) Math.random() * (colorFactor);
            float a           = minAlpha + (float) Math.random() * (0.7f - minAlpha);
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
