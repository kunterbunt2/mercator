package de.bushnaq.abdalla.mercator.renderer;

import de.bushnaq.abdalla.mercator.renderer.generator.FontData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class AtlasManager {
	public static final int SOOMIN_FONT_SIZE = 10;
	public TextureAtlas atlas;
	public TextureRegion barTextureRegion;
	public BitmapFont chartFont;
	public BitmapFont defaultFont;
	public BitmapFont demoBigFont;
	public BitmapFont demoMidFont;
	public TextureRegion dottedLineTextureRegion;
	public TextureRegion factoryTextureRegion;
	public FontData[] fontDataList = { new FontData("default-font", "fonts/Roboto-Bold.ttf", Screen2D.FONT_SIZE), new FontData("zoomin-default-font", "fonts/Roboto-Bold.ttf", SOOMIN_FONT_SIZE), new FontData("time-machine-font", "fonts/Roboto-Bold.ttf", Screen2D.TIME_MACHINE_FONT_SIZE), new FontData("chart-font", "fonts/Roboto-bold.ttf", Screen2D.CHART_FONT_SIZE), new FontData("menu-font", "fonts/Roboto-Regular.ttf", Screen2D.MENU_FONT_SIZE),
			new FontData("model-font", "fonts/Roboto-Bold.ttf", 64), new FontData("demo-big-font", "fonts/Roboto-Bold.ttf", 128), new FontData("demo-mid-font", "fonts/Roboto-Regular.ttf", 32) };
	public TextureRegion gaugeTextureRegion;
	//	public Texture good_baseColor;
	//	public AtlasRegion good_baseColorRegion;
	//	public Texture good_normal;
	//	public AtlasRegion good_normalRegion;
	//	public Texture good_occlusionRoughnessMetallic;
	//	public AtlasRegion good_occlusionRoughnessMetallicRegion;
	public BitmapFont menuFont;
	public BitmapFont modelFont;
	public TextureRegion planetTextureRegion;
	public TextureRegion simTextureRegion;
	public TextureRegion systemTextureRegion;
	public BitmapFont timeMachineFont;
	public TextureRegion traderTextureRegion;
	public BitmapFont zoominDefaultFont;

	public AtlasManager() {
	}

	public void dispose() {
		for (final FontData fontData : fontDataList) {
			fontData.font.dispose();
		}
		atlas.dispose();
	}

	public void init() {
		initTextures();
		initFonts();
	}

	private void initFonts() {
		for (int index = 0; index < fontDataList.length; index++) {
			final FontData fontData = fontDataList[index];
			final AtlasRegion atlasRegion = atlas.findRegion(fontData.name);
			atlasRegion.getRegionWidth();
			atlasRegion.getRegionHeight();
			final PixmapPacker packer = new PixmapPacker(atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), Format.RGBA8888, 1, false);
			final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontData.file));
			final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = (fontData.fontSize);
			parameter.packer = packer;
			final BitmapFont generateFont = generator.generateFont(parameter);
			generator.dispose(); // don't forget to dispose to avoid memory leaks!
			fontData.font = new BitmapFont(generateFont.getData(), atlas.findRegion(fontData.name), true);
			packer.dispose();
			fontData.font.setUseIntegerPositions(false);
		}
		defaultFont = fontDataList[0].font;
		zoominDefaultFont = fontDataList[1].font;
		timeMachineFont = fontDataList[2].font;
		chartFont = fontDataList[3].font;
		menuFont = fontDataList[4].font;
		modelFont = fontDataList[5].font;
		demoBigFont = fontDataList[6].font;
		demoMidFont = fontDataList[7].font;
	}

	private void initTextures() {
		atlas = new TextureAtlas(Gdx.files.internal("atlas/atlas.atlas"));
		systemTextureRegion = atlas.findRegion("system");
		planetTextureRegion = atlas.findRegion("planet");
		simTextureRegion = atlas.findRegion("sim");
		factoryTextureRegion = atlas.findRegion("factory");
		gaugeTextureRegion = atlas.findRegion("gauge");
		traderTextureRegion = atlas.findRegion("trader");
		//		good_baseColorRegion = atlas.findRegion("good_baseColor");
		//		good_normalRegion = atlas.findRegion("good_normal");
		//		good_occlusionRoughnessMetallicRegion = atlas.findRegion("good_occlusionRoughnessMetallic");
		barTextureRegion = atlas.findRegion("bar");
		dottedLineTextureRegion = atlas.findRegion("dotted_line");
		//		good_baseColor = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_basecolor.jpg"));
		//		good_occlusionRoughnessMetallic = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_metallic-Metal_Floor_01_roughness.png"));
		//		good_normal = new Texture(Gdx.files.internal("models/glTF/Metal_Floor_01/Metal_Floor_01_normal.jpg"));
	}
}
