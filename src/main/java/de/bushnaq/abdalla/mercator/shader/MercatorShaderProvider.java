package de.bushnaq.abdalla.mercator.shader;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Plane;

import net.mgsx.gltf.scene3d.shaders.PBRShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class MercatorShaderProvider extends PBRShaderProvider {

	public static MercatorShaderProvider createDefault(final PBRShaderConfig config, final FrameBuffer waterRefractionFbo, final FrameBuffer waterReflectionFbo, final FrameBuffer postFbo, final int universeSize) {
		return new MercatorShaderProvider(config, waterRefractionFbo, waterReflectionFbo, postFbo, universeSize);
	}

	private Plane clippingPlane;
	public MyPBRShader pbrShader;
	private final int universeSize;
	private final FrameBuffer waterReflectionFbo;
	private final FrameBuffer waterRefractionFbo;
	public WaterShader waterShader;

	public MercatorShaderProvider(final PBRShaderConfig config, final FrameBuffer waterRefractionFbo, final FrameBuffer waterReflectionFbo, final FrameBuffer postFbo, final int universeSize) {
		super(config);
		this.waterRefractionFbo = waterRefractionFbo;
		this.waterReflectionFbo = waterReflectionFbo;
		this.universeSize = universeSize;
	}

	private MyPBRShader createPBRShader(final Renderable renderable) {
		pbrShader = (MyPBRShader) super.createShader(renderable);
		pbrShader.setClippingPlane(clippingPlane);
		return pbrShader;
	}

	public String createPrefixBase(final Renderable renderable, final Config config) {

		final String defaultPrefix = DefaultShader.createPrefix(renderable, config);
		String version = null;
		if (isGL3()) {
			if (Gdx.app.getType() == ApplicationType.Desktop) {
				if (version == null)
					version = "#version 130\n" + "#define GLSL3\n";
			} else if (Gdx.app.getType() == ApplicationType.Android) {
				if (version == null)
					version = "#version 300 es\n" + "#define GLSL3\n";
			}
		}
		String prefix = "";
		if (version != null)
			prefix += version;
		//		if (config.prefix != null)
		//			prefix += config.prefix;
		prefix += defaultPrefix;

		return prefix;
	}

	@Override
	protected Shader createShader(final Renderable renderable) {

		if (renderable.material.id.equals("water")) {
			return createWaterShader(renderable);
		} else
			return createPBRShader(renderable);
	}

	@Override
	protected PBRShader createShader(final Renderable renderable, final PBRShaderConfig config, final String prefix) {
		return new MyPBRShader(renderable, config, prefix);
	}

	private Shader createWaterShader(final Renderable renderable) {
		final String prefix = createPrefixBase(renderable, config);
		final Config config = new Config();
		config.vertexShader = Gdx.files.internal("shader/water.vertex.glsl").readString();
		config.fragmentShader = Gdx.files.internal("shader/water.fragment.glsl").readString();
		waterShader = new WaterShader(renderable, config, prefix, waterRefractionFbo, waterReflectionFbo);
		waterShader.setTiling(universeSize * 2 * 4 * 2);
		waterShader.setClippingPlane(clippingPlane);
		return waterShader;

	}

	@Override
	public void dispose() {
		//		pbrShader.dispose();
		//		waterShader.dispose();
		super.dispose();
	}

	@Override
	protected boolean isGL3() {
		return Gdx.graphics.getGLVersion().isVersionEqualToOrHigher(3, 0);
	}

	public void setClippingPlane(final Plane clippingPlane) {
		this.clippingPlane = clippingPlane;
		if (waterShader != null) {
			waterShader.setClippingPlane(clippingPlane);
		}
		if (waterShader != null) {
			waterShader.setClippingPlane(clippingPlane);
		}
		if (pbrShader != null) {
			pbrShader.setClippingPlane(clippingPlane);
		}
	}

}
