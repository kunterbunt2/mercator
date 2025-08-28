package de.bushnaq.abdalla.mercator.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import de.bushnaq.abdalla.engine.ISubtitles;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.chronos.TextData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Subtitles implements ISubtitles {
    private final GameEngine3D   gameEngine;
    private final List<TextData> text  = new ArrayList<>();
    private final float          textX = 100;
    private final float          textY = 100;

    public Subtitles(GameEngine3D gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void add(String subtitle) {
        text.clear();
        text.add(new TextData(subtitle, gameEngine.getAtlasManager().demoMidFont, Color.WHITE));
    }

    public void render(float deltaTime) throws IOException, OpenAlException {

        final float lineHeightFactor = 2f;

        Color demoTextColor;
        demoTextColor = new Color(1f, 1f, 1f, 0.4f);
        float deltaY = 0;

        if (text.isEmpty())
            return; // no text to render
        final GlyphLayout layout = new GlyphLayout();
        layout.setText(text.get(0).font, text.get(0).text);
        final float width = layout.width;// contains the width of the current set text
        //		final float height = layout.height; // contains the height of the current set text

        final float topMargin    = 50f;
        final float bottomMargin = 200f;
        for (final TextData ds : text) {
            ds.font.setColor(demoTextColor);
            final float       y              = textY - deltaY;
            RenderEngine2D<?> renderEngine2D = gameEngine.getRenderEngine().renderEngine2D;
            {
                final float x1 = renderEngine2D.untransformX(-renderEngine2D.width / 2 + textX - 10);
                final float x2 = renderEngine2D.untransformX(x1 + width + 20);
                final float y1 = renderEngine2D.untransformY(renderEngine2D.height / 2 - 100 - 10);
                final float y2 = renderEngine2D.untransformY(y1 + layout.height * 1.5f + 10);
                gameEngine.getRenderEngine().renderEngine2D.bar(gameEngine.getAtlasManager().systemTextureRegion, x1, y1, x2, y2, new Color(0f, 0f, 0f, 0.7f));
            }
            final GlyphLayout lastLayout = ds.font.draw(renderEngine2D.batch, ds.text, textX, y, width, Align.left, true);
            deltaY += lastLayout.height * lineHeightFactor;
        }
//        textY += 100 * deltaTime;
//        if (textY - deltaY > gameEngine.getRenderEngine().renderEngine2D.height * lineHeightFactor) textY = 0;
    }
}
