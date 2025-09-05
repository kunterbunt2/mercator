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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.bushnaq.abdalla.engine.RichLabel;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visual keyboard overlay pause screen that shows all keyboard commands
 * with connecting lines to their descriptions.
 */
public class PauseScreen {

    private static final Color                        ASSIGNED_KEY_COLOR = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private static final Color                        DESCRIPTION_COLOR  = new Color(0.9f, 0.9f, 0.9f, 1.0f);
    private static final Color                        KEY_BORDER_COLOR   = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    private static final Color                        KEY_COLOR          = new Color(0.2f, 0.2f, 0.2f, 0.9f);
    private static final float                        KEY_HEIGHT         = 60f;  // 2x bigger (was 30f, reduced from 120f)
    private static final Color                        OVERLAY_COLOR      = new Color(0.0f, 0.0f, 0.0f, 0.7f);
    private static final Color                        TITLE_COLOR        = Color.WHITE;
    private final        List<KeyboardKey>            allKeys;
    private final        AtlasManager                 atlasManager;
    private final        Map<String, KeyboardCommand> commands;
    private final        GameEngine3D                 gameEngine;
    private              float                        keyboardStartX;
    private              float                        keyboardStartY;
    private              RichLabel                    resumeLabel;
    private final        ShapeRenderer                shapeRenderer;
    private final        Stage                        stage;
    private              RichLabel                    titleLabel;

    public PauseScreen(GameEngine3D gameEngine, AtlasManager atlasManager) {
        this.gameEngine    = gameEngine;
        this.atlasManager  = atlasManager;
        this.stage         = new Stage();
        this.shapeRenderer = new ShapeRenderer();
        this.commands      = new HashMap<>();
        this.allKeys       = new ArrayList<>();

        initializeKeyboardCommands();
        initializeKeyboardLayout();
        createUI();
    }

    private void addCommand(String key, String description, float descX, float descY) {
        KeyboardCommand command = new KeyboardCommand(key, description);
        command.descriptionPosition.set(descX, descY);
        commands.put(key, command);
    }

    private void addKeyFromSVG(String label, float svgX, float svgY, float svgWidth, float svgHeight, float scaleFactor) {
        // Convert SVG coordinates to our coordinate system
        // SVG Y coordinates are from top, but we want from bottom, so we need to flip Y
        float x      = svgX * scaleFactor;
        float y      = -svgY * scaleFactor; // Negative to flip Y coordinate
        float width  = svgWidth * scaleFactor;
        float height = svgHeight * scaleFactor;

        allKeys.add(new KeyboardKey(label, x, y, width, height));
    }

    private float calculateKeyboardHeight() {
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (KeyboardKey key : allKeys) {
            minY = Math.min(minY, key.relativeY);
            maxY = Math.max(maxY, key.relativeY);
        }

        return maxY - minY + KEY_HEIGHT;
    }

    private float calculateKeyboardWidth() {
        float maxWidth        = 0;
        float currentRowWidth = 0;
        float currentY        = Float.MAX_VALUE;

        for (KeyboardKey key : allKeys) {
            if (key.relativeY != currentY) {
                maxWidth        = Math.max(maxWidth, currentRowWidth);
                currentRowWidth = key.relativeX + key.width;
                currentY        = key.relativeY;
            } else {
                currentRowWidth = Math.max(currentRowWidth, key.relativeX + key.width);
            }
        }
        maxWidth = Math.max(maxWidth, currentRowWidth);
        return maxWidth;
    }

    private void calculateLayout() {
        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Position title at top center
        titleLabel.setPosition(screenWidth / 2 - titleLabel.getWidth() / 2, screenHeight - 80);

        // Position resume instruction at bottom center
        resumeLabel.setPosition(screenWidth / 2 - resumeLabel.getWidth() / 2, 50);

        // Center the keyboard on screen
        float totalKeyboardWidth  = calculateKeyboardWidth();
        float totalKeyboardHeight = calculateKeyboardHeight();

        keyboardStartX = (screenWidth - totalKeyboardWidth) / 2;
        keyboardStartY = (screenHeight + totalKeyboardHeight) / 2 - 50; // Slight offset from center
    }

    private void createUI() {
        // Create title label
        Label.LabelStyle titleStyle = new Label.LabelStyle(atlasManager.menuBoldFont, TITLE_COLOR);
        titleLabel = new RichLabel("PAUSED", titleStyle, atlasManager.systemTextureRegion);
        titleLabel.setBackgroundColor(new Color(0.1f, 0.1f, 0.3f, 0.8f));

        // Create resume instruction label
        Label.LabelStyle resumeStyle = new Label.LabelStyle(atlasManager.menuFont, DESCRIPTION_COLOR);
        resumeLabel = new RichLabel("Press ESC to Resume", resumeStyle, atlasManager.systemTextureRegion);
        resumeLabel.setBackgroundColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));

        stage.addActor(titleLabel);
        stage.addActor(resumeLabel);

        calculateLayout();
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

    /**
     * Draw an arc using line segments
     */
    private void drawArc(float centerX, float centerY, float radius, float startAngle, float arcAngle) {
        int   segments  = 8; // Number of line segments for the arc
        float angleStep = arcAngle / segments;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) Math.toRadians(startAngle + i * angleStep);
            float angle2 = (float) Math.toRadians(startAngle + (i + 1) * angleStep);

            float x1 = centerX + radius * (float) Math.cos(angle1);
            float y1 = centerY + radius * (float) Math.sin(angle1);
            float x2 = centerX + radius * (float) Math.cos(angle2);
            float y2 = centerY + radius * (float) Math.sin(angle2);

            shapeRenderer.line(x1, y1, x2, y2);
        }
    }

    private void drawKeyboardLayout() {
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

        // Draw filled rectangles for keys with rounded corners
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (KeyboardKey key : allKeys) {
            // Check if this key has an assigned command
            boolean hasCommand = commands.containsKey(key.label);

            // Draw key background with rounded corners (similar to SVG rx="4" ry="4")
            shapeRenderer.setColor(hasCommand ? ASSIGNED_KEY_COLOR : KEY_COLOR);
            drawRoundedRect(keyboardStartX + key.relativeX, keyboardStartY + key.relativeY, key.width, key.height, 4f);
        }
        shapeRenderer.end();

        // Draw key borders with rounded corners
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(KEY_BORDER_COLOR);

//        for (KeyboardKey key : allKeys) {
//            drawRoundedRectOutline(keyboardStartX + key.relativeX, keyboardStartY + key.relativeY, key.width, key.height, 4f);
//        }
        shapeRenderer.end();

        // Draw text on keys and descriptions
        stage.getBatch().begin();

        for (KeyboardKey key : allKeys) {
            // Draw key label in top-left corner
            atlasManager.menuBoldFont.setColor(Color.WHITE);
            atlasManager.menuBoldFont.getData().setScale(1f); // Slightly smaller for better fit

            float labelX = keyboardStartX + key.relativeX + 4; // Small padding from left edge
            float labelY = keyboardStartY + key.relativeY + key.height - 8; // Near top edge

            atlasManager.menuBoldFont.draw(
                    stage.getBatch(),
                    key.label,
                    labelX,
                    labelY
            );

            // Reset font scale
            atlasManager.menuBoldFont.getData().setScale(1.0f);

            // Draw command description in bottom-left corner if this key has a command
            KeyboardCommand command = commands.get(key.label);
            if (command != null) {
                atlasManager.menuFont.setColor(DESCRIPTION_COLOR);
                atlasManager.menuFont.getData().setScale(1.0f); // Larger font for descriptions

                float descX      = keyboardStartX + key.relativeX + 4; // Same padding as label
                float lineHeight = 14f;

                // First, determine how many lines we need
                String[]      words       = command.description.split(" ");
                StringBuilder currentLine = new StringBuilder();
                int           totalLines  = 0;
                float         maxLines    = Math.max(1, (key.height - 20) / lineHeight); // Dynamic max lines based on key height

                // Count total lines needed
                for (String word : words) {
                    String testLine = !currentLine.isEmpty() ? currentLine + " " + word : word;

                    // Check if the line would be too wide for the key
                    com.badlogic.gdx.graphics.g2d.GlyphLayout testLayout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
                    testLayout.setText(atlasManager.menuFont, testLine);

                    if (testLayout.width > key.width - 8 && !currentLine.isEmpty()) {
                        totalLines++;
                        currentLine = new StringBuilder(word);
                    } else {
                        currentLine = new StringBuilder(testLine);
                    }
                }
                if (!currentLine.isEmpty()) {
                    totalLines++;
                }

                // Now draw the lines, starting from bottom for single line, moving up for multi-line
                currentLine = new StringBuilder();
                int   lineCount = 0;
                float startY;

                if (totalLines == 1) {
                    // Single line: position at bottom of key
                    startY = keyboardStartY + key.relativeY + 16;
                } else {
                    // Multi-line: start higher up to fit all lines
                    startY = keyboardStartY + key.relativeY + 16 + (totalLines - 1) * lineHeight;
                }

                for (String word : words) {
                    String testLine = !currentLine.isEmpty() ? currentLine + " " + word : word;

                    // Check if the line would be too wide for the key
                    com.badlogic.gdx.graphics.g2d.GlyphLayout testLayout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
                    testLayout.setText(atlasManager.menuFont, testLine);

                    if (testLayout.width > key.width - 8 && !currentLine.isEmpty()) {
                        // Draw current line
                        if (lineCount < maxLines) {
                            float descY = startY - (lineCount * lineHeight);
                            atlasManager.menuFont.draw(
                                    stage.getBatch(),
                                    currentLine.toString(),
                                    descX,
                                    descY
                            );
                        }
                        currentLine = new StringBuilder(word);
                        lineCount++;
                    } else {
                        currentLine = new StringBuilder(testLine);
                    }
                }

                // Draw the last line
                if (!currentLine.isEmpty() && lineCount < maxLines) {
                    float descY = startY - (lineCount * lineHeight);
                    atlasManager.menuFont.draw(
                            stage.getBatch(),
                            currentLine.toString(),
                            descX,
                            descY
                    );
                }

                // Reset font scale
                atlasManager.menuFont.getData().setScale(1.0f);
            }
        }

        stage.getBatch().end();
    }

    /**
     * Draw a filled rounded rectangle using the ShapeRenderer
     */
    private void drawRoundedRect(float x, float y, float width, float height, float radius) {
        width -= 1;
        height -= 1;
        // Clamp radius to not exceed half of width or height
        float maxRadius = Math.min(width / 2, height / 2);
        radius = Math.min(radius, maxRadius);

        // Main rectangle (without corners)
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius);

        // Corner circles
        shapeRenderer.circle(x + radius, y + radius, radius, 8); // Bottom-left
        shapeRenderer.circle(x + width - radius, y + radius, radius, 8); // Bottom-right
        shapeRenderer.circle(x + radius, y + height - radius, radius, 8); // Top-left
        shapeRenderer.circle(x + width - radius, y + height - radius, radius, 8); // Top-right
    }

    /**
     * Draw a rounded rectangle outline using the ShapeRenderer
     */
//    private void drawRoundedRectOutline(float x, float y, float width, float height, float radius) {
//        width -= 1;
//        height -= 1;
//        // Clamp radius to not exceed half of width or height
//        float maxRadius = Math.min(width / 2, height / 2);
//        radius = Math.min(radius, maxRadius);
//
//        // Draw the four sides
//        shapeRenderer.line(x + radius, y, x + width - radius, y); // Bottom
//        shapeRenderer.line(x + radius, y + height, x + width - radius, y + height); // Top
//        shapeRenderer.line(x, y + radius, x, y + height - radius); // Left
//        shapeRenderer.line(x + width, y + radius, x + width, y + height - radius); // Right
//
//        // Draw corner arcs
//        drawArc(x + radius, y + radius, radius, 180, 90); // Bottom-left
//        drawArc(x + width - radius, y + radius, radius, 270, 90); // Bottom-right
//        drawArc(x + radius, y + height - radius, radius, 90, 90); // Top-left
//        drawArc(x + width - radius, y + height - radius, radius, 0, 90); // Top-right
//    }
    private void initializeKeyboardCommands() {
        // Initialize command descriptions and positions
        // Movement commands
        addCommand("W", "Move Forward", 100, 400);
        addCommand("A", "Move Left", 100, 380);
        addCommand("S", "Move Backward", 100, 360);
        addCommand("D", "Move Right", 100, 340);
        addCommand("Q", "Rotate Left", 100, 320);
        addCommand("E", "Rotate Right", 100, 300);

        // Function key commands
        addCommand("F1", "Gamma Correction", 300, 400);
        addCommand("F2", "Depth of Field", 300, 380);
        addCommand("F3", "Render Bokeh", 300, 360);
        addCommand("F4", "SSAO", 300, 340);
        addCommand("F5", "Always Day", 300, 320);
        addCommand("F6", "Cycle Demo Modes", 300, 300);
        addCommand("F9", "Show Graphs", 300, 280);
        addCommand("F10", "Debug Mode", 300, 260);

        // System commands
        addCommand("Space", "Toggle Time", 500, 400);
        addCommand("PRNT", "Screenshot", 500, 380);
        addCommand("Tab", "Profiler", 500, 360);
        addCommand("V", "VSync Toggle", 500, 340);
        addCommand("I", "Show Info", 500, 320);

        // Camera commands
        addCommand("F", "Follow Mode", 700, 400);
        addCommand("C", "Reset Camera", 700, 380);

        // Audio commands
        addCommand("H", "Toggle HRTF", 700, 360);

        // Arrow keys
        addCommand("Up", "Move Up", 900, 400);
        addCommand("Down", "Move Down", 900, 380);
        addCommand("Left", "Move Left", 900, 360);
        addCommand("Right", "Move Right", 900, 340);
    }

    private void initializeKeyboardLayout() {
        allKeys.clear();

        // Scale factor to convert SVG coordinates to our coordinate system
        // SVG uses larger coordinates, so we'll scale them down to fit screen better
        float scaleFactor = 1f; // Reduced from 1f to make keyboard smaller and show proper spacing

        // Function keys row (F1-F12) - using inner rect coordinates (outer + 0.5) and dimensions (68x68)
        addKeyFromSVG("F1", 146f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F2", 218f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F3", 290f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F4", 362f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F5", 470f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F6", 542f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F7", 614f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F8", 686f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F9", 794f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F10", 866f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F11", 938f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F12", 1010f, 2f, 68f, 68f, scaleFactor);

        // System keys
        addKeyFromSVG("PRNT", 1118f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("ROLL", 1190f, 2f, 68f, 68f, scaleFactor);
        addKeyFromSVG("PAUSE", 1262f, 2f, 68f, 68f, scaleFactor);

        // Number row
        addKeyFromSVG("`", 2f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("1", 74f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("2", 146f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("3", 218f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("4", 290f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("5", 362f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("6", 434f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("7", 506f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("8", 578f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("9", 650f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("0", 722f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("-", 794f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("=", 866f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Backspace", 938f, 110f, 140f, 68f, scaleFactor); // Width 141-1 = 140

        // QWERTY row
        addKeyFromSVG("Tab", 2f, 182f, 104f, 68f, scaleFactor); // Width 105-1 = 104
        addKeyFromSVG("Q", 110f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("W", 182f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("E", 254f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("R", 326f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("T", 398f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Y", 470f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("U", 542f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("I", 614f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("O", 686f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("P", 758f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("[", 830f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("]", 902f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("\\", 974f, 182f, 104f, 68f, scaleFactor); // Width 105-1 = 104

        // ASDF row
        addKeyFromSVG("CapsLock", 2f, 254f, 122f, 68f, scaleFactor); // Width 123-1 = 122
        addKeyFromSVG("A", 128f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("S", 200f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("D", 272f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("F", 344f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("G", 416f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("H", 488f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("J", 560f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("K", 632f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("L", 704f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG(";", 776f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("'", 848f, 254f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Enter", 920f, 254f, 158f, 68f, scaleFactor); // Width 159-1 = 158

        // ZXCV row
        addKeyFromSVG("LShift", 2f, 326f, 158f, 68f, scaleFactor); // Width 159-1 = 158
        addKeyFromSVG("Z", 164f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("X", 236f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("C", 308f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("V", 380f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("B", 452f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("N", 524f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("M", 596f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG(",", 668f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG(".", 740f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("/", 812f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("RShift", 884f, 326f, 194f, 68f, scaleFactor); // Width 195-1 = 194

        // Bottom row
        addKeyFromSVG("LCtrl", 2f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("LWin", 92f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("LAlt", 182f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("Space", 272f, 398f, 446f, 68f, scaleFactor); // Width 447-1 = 446
        addKeyFromSVG("RAlt", 722f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("RWin", 812f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("Menu", 902f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86
        addKeyFromSVG("RCtrl", 992f, 398f, 86f, 68f, scaleFactor); // Width 87-1 = 86

        // Arrow keys and navigation cluster
        addKeyFromSVG("Insert", 1118f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Home", 1190f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("PageUp", 1262f, 110f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Delete", 1118f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("End", 1190f, 182f, 68f, 68f, scaleFactor);
        addKeyFromSVG("PageDown", 1262f, 182f, 68f, 68f, scaleFactor);

        // Arrow keys
        addKeyFromSVG("Up", 1190f, 326f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Left", 1118f, 398f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Down", 1190f, 398f, 68f, 68f, scaleFactor);
        addKeyFromSVG("Right", 1262f, 398f, 68f, 68f, scaleFactor);
    }

    private boolean isPaused() {
        return !gameEngine.assetManager.universe.isEnableTime();
    }

    public void render(float deltaTime) {
        Batch batch = gameEngine.getRenderEngine().renderEngine2D.batch;
        if (!isPaused()) return;

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Draw semi-transparent overlay
        batch.setColor(OVERLAY_COLOR);
        batch.draw(atlasManager.systemTextureRegion, 0, 0, screenWidth, screenHeight);
        batch.setColor(Color.WHITE);

        // Draw UI labels
        stage.draw();

        // End batch to draw shapes
        batch.end();

        // Draw keyboard layout and connecting lines
        drawKeyboardLayout();

        // Restart batch for any additional rendering
        batch.begin();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (isPaused()) {
            calculateLayout();
        }
    }

    /**
     * Represents a single keyboard command
     */
    private static class KeyboardCommand {
        final String  description;
        final Vector2 descriptionPosition;
        final String  key;

        KeyboardCommand(String key, String description) {
            this.key                 = key;
            this.description         = description;
            this.descriptionPosition = new Vector2();
        }
    }

    /**
     * Represents a physical key on the keyboard
     */
    private record KeyboardKey(String label, float relativeX, float relativeY, float width, float height) {
    }
}

