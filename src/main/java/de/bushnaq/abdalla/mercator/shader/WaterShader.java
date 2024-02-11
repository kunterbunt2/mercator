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

package de.bushnaq.abdalla.mercator.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Plane;

public class WaterShader extends DefaultShader {
    private static final String      DUDV_MAP_FILE_NAME   = "shader/texture/waterDUDV.png";
    private static final String      NORMAL_MAP_FILE_NAME = "shader/texture/normal.png";
    private static final float       WAVE_SPEED           = 0.03f;
    private static       Plane       clippingPlane;
    private static       FrameBuffer waterReflectionFbo;
    private static       FrameBuffer waterRefractionFbo;
    private final        Texture     normalMap;
    private final        int         u_clippingPlane      = register("u_clippingPlane");
    private final        int         u_depthMap           = register("u_depthMap");
    private final        int         u_dudvMapTexture     = register("u_dudvMapTexture");
    private final        int         u_moveFactor         = register("u_moveFactor");
    private final        int         u_normalMap          = register("u_normalMap");
    private final        int         u_reflectionTexture  = register("u_reflectionTexture");
    private final        int         u_refractionTexture  = register("u_refractionTexture");
    private final        int         u_tiling             = register("u_tiling");
    private final        int         u_waveStrength       = register("u_waveStrength");
    private final        Texture     waterDuDv;
    private              float       moveFactor           = 0f;
    private              float       tiling;
    private              float       waveStrength         = 0.01f;

    public WaterShader(final Renderable renderable, final Config config, final String prefix, final FrameBuffer waterRefractionFbo, final FrameBuffer waterReflectionFbo) {
        super(renderable, config, prefix);
        WaterShader.waterRefractionFbo = waterRefractionFbo;
        WaterShader.waterReflectionFbo = waterReflectionFbo;
        //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 4.0f);
        waterDuDv = new Texture(Gdx.files.internal(DUDV_MAP_FILE_NAME));
        waterDuDv.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        normalMap = new Texture(Gdx.files.internal(NORMAL_MAP_FILE_NAME));
        normalMap.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }

    @Override
    public boolean canRender(final Renderable renderable) {
        if (renderable.material.id.equals("water"))
            return true;
        else
            return false;
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        set(u_clippingPlane, clippingPlane.normal.x, clippingPlane.normal.y, clippingPlane.normal.z, clippingPlane.d);
        set(u_refractionTexture, waterRefractionFbo.getColorBufferTexture());
        set(u_reflectionTexture, waterReflectionFbo.getColorBufferTexture());
        set(u_depthMap, waterRefractionFbo.getTextureAttachments().get(1));
        set(u_dudvMapTexture, waterDuDv);
        set(u_normalMap, normalMap);
        set(u_tiling, tiling);
        moveFactor += WAVE_SPEED * Gdx.graphics.getDeltaTime();
        moveFactor %= 1.0;
        set(u_moveFactor, moveFactor);
        set(u_waveStrength, waveStrength);

    }

    public String getLog() {
        return program.getLog();
    }

    @Override
    public void render(final Renderable renderable) {
        super.render(renderable);
    }

    public void setClippingPlane(final Plane clippingPlane) {
        WaterShader.clippingPlane = clippingPlane;
    }

    public void setTiling(final float tiling) {
        this.tiling = tiling;
    }

    public void setWaveStrength(final float waveStrength) {
        this.waveStrength = waveStrength;
    }

}
