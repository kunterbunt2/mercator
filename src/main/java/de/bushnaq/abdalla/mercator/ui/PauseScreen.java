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
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visual keyboard overlay pause screen that shows all keyboard commands
 * with connecting lines to their descriptions.
 */
public class PauseScreen {

    /**
     * Represents a physical key on the keyboard
     */
    private static class KeyboardKey {
        final String label;
        final float relativeX;
        final float relativeY;
        final float width;
        final float height;

        KeyboardKey(String label, float relativeX, float relativeY, float width, float height) {
            this.label = label;
            this.relativeX = relativeX;
            this.relativeY = relativeY;
            this.width = width;
            this.height = height;
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

    private static final Color                        DESCRIPTION_COLOR = new Color(0.9f, 0.9f, 0.9f, 1.0f);
    private static final Color                        KEY_BORDER_COLOR  = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    private static final Color                        KEY_COLOR         = new Color(0.2f, 0.2f, 0.2f, 0.9f);
    private static final Color                        ASSIGNED_KEY_COLOR = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private static final float                        KEY_HEIGHT        = 60f;  // 2x bigger (was 30f, reduced from 120f)
    private static final float                        KEY_SPACING       = 4f;   // 2x bigger (was 2f, reduced from 8f)
    private static final float                        KEY_WIDTH         = 80f;  // 2x bigger (was 40f, reduced from 160f)
    private static final float                        FUNCTION_KEY_WIDTH = 70f;  // 2x bigger (was 35f, reduced from 140f)
    private static final Color                        LINE_COLOR        = new Color(0.5f, 0.5f, 0.8f, 0.8f);
    private static final Color                        OVERLAY_COLOR     = new Color(0.0f, 0.0f, 0.0f, 0.7f);
    private static final float                        ROW_SPACING       = 20f;  // 2x bigger (was 10f, reduced from 40f)
    private static final Color                        TITLE_COLOR       = Color.WHITE;

    private final        AtlasManager                 atlasManager;
    private final        Map<String, KeyboardCommand> commands;
    private final        GameEngine3D                 gameEngine;
    private final        List<KeyboardKey>            allKeys;
    private              RichLabel                    resumeLabel;
    private final        ShapeRenderer                shapeRenderer;
    private final        Stage                        stage;
    private              RichLabel                    titleLabel;
    private              float                        keyboardStartX;
    private              float                        keyboardStartY;

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

    private void initializeKeyboardLayout() {
        allKeys.clear();

        // Scale factor to convert SVG coordinates to our coordinate system
        // SVG uses larger coordinates, so we'll scale them down
        float scaleFactor = 1f; // Adjust this to make keyboard bigger/smaller

        // Function keys row (F1-F12)
        addKeyFromSVG("F1", 145.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F2", 217.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F3", 289.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F4", 361.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F5", 469.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F6", 541.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F7", 613.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F8", 685.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F9", 793.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F10", 865.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F11", 937.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F12", 1009.5f, 1.5f, 69f, 69f, scaleFactor);

        // System keys
        addKeyFromSVG("PRNT", 1117.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("ROLL", 1189.5f, 1.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("PAUSE", 1261.5f, 1.5f, 69f, 69f, scaleFactor);

        // Number row
        addKeyFromSVG("`", 1.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("1", 73.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("2", 145.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("3", 217.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("4", 289.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("5", 361.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("6", 433.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("7", 505.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("8", 577.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("9", 649.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("0", 721.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("-", 793.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("=", 865.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Backspace", 937.5f, 109.5f, 141f, 69f, scaleFactor);

        // QWERTY row
        addKeyFromSVG("Tab", 1.5f, 181.5f, 105f, 69f, scaleFactor);
        addKeyFromSVG("Q", 109.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("W", 181.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("E", 253.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("R", 325.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("T", 397.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Y", 469.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("U", 541.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("I", 613.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("O", 685.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("P", 757.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("[", 829.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("]", 901.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("\\", 973.5f, 181.5f, 105f, 69f, scaleFactor);

        // ASDF row
        addKeyFromSVG("CapsLock", 1.5f, 253.5f, 123f, 69f, scaleFactor);
        addKeyFromSVG("A", 127.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("S", 199.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("D", 271.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("F", 343.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("G", 415.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("H", 487.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("J", 559.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("K", 631.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("L", 703.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG(";", 775.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("'", 847.5f, 253.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Enter", 919.5f, 253.5f, 159f, 69f, scaleFactor);

        // ZXCV row
        addKeyFromSVG("LShift", 1.5f, 325.5f, 159f, 69f, scaleFactor);
        addKeyFromSVG("Z", 163.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("X", 235.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("C", 307.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("V", 379.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("B", 451.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("N", 523.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("M", 595.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG(",", 667.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG(".", 739.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("/", 811.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("RShift", 883.5f, 325.5f, 195f, 69f, scaleFactor);

        // Bottom row
        addKeyFromSVG("LCtrl", 1.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("LWin", 91.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("LAlt", 181.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("Space", 271.5f, 397.5f, 447f, 69f, scaleFactor);
        addKeyFromSVG("RAlt", 721.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("RWin", 811.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("Menu", 901.5f, 397.5f, 87f, 69f, scaleFactor);
        addKeyFromSVG("RCtrl", 991.5f, 397.5f, 87f, 69f, scaleFactor);

        // Arrow keys and navigation cluster
        addKeyFromSVG("Insert", 1117.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Home", 1189.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("PageUp", 1261.5f, 109.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Delete", 1117.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("End", 1189.5f, 181.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("PageDown", 1261.5f, 181.5f, 69f, 69f, scaleFactor);

        // Arrow keys
        addKeyFromSVG("Up", 1189.5f, 325.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Left", 1117.5f, 397.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Down", 1189.5f, 397.5f, 69f, 69f, scaleFactor);
        addKeyFromSVG("Right", 1261.5f, 397.5f, 69f, 69f, scaleFactor);
    }

    private void addKeyFromSVG(String label, float svgX, float svgY, float svgWidth, float svgHeight, float scaleFactor) {
        // Convert SVG coordinates to our coordinate system
        // SVG Y coordinates are from top, but we want from bottom, so we need to flip Y
        float x = svgX * scaleFactor;
        float y = -svgY * scaleFactor; // Negative to flip Y coordinate
        float width = svgWidth * scaleFactor;
        float height = svgHeight * scaleFactor;

        allKeys.add(new KeyboardKey(label, x, y, width, height));
    }

    private void calculateLayout() {
        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Position title at top center
        titleLabel.setPosition(screenWidth / 2 - titleLabel.getWidth() / 2, screenHeight - 80);

        // Position resume instruction at bottom center
        resumeLabel.setPosition(screenWidth / 2 - resumeLabel.getWidth() / 2, 50);

        // Center the keyboard on screen
        float totalKeyboardWidth = calculateKeyboardWidth();
        float totalKeyboardHeight = calculateKeyboardHeight();

        keyboardStartX = (screenWidth - totalKeyboardWidth) / 2;
        keyboardStartY = (screenHeight + totalKeyboardHeight) / 2 - 50; // Slight offset from center
    }

    private float calculateKeyboardWidth() {
        float maxWidth = 0;
        float currentRowWidth = 0;
        float currentY = Float.MAX_VALUE;

        for (KeyboardKey key : allKeys) {
            if (key.relativeY != currentY) {
                maxWidth = Math.max(maxWidth, currentRowWidth);
                currentRowWidth = key.relativeX + key.width;
                currentY = key.relativeY;
            } else {
                currentRowWidth = Math.max(currentRowWidth, key.relativeX + key.width);
            }
        }
        maxWidth = Math.max(maxWidth, currentRowWidth);
        return maxWidth;
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

    private void drawKeyboardLayout() {
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

        // Draw filled rectangles for keys
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (KeyboardKey key : allKeys) {
            // Check if this key has an assigned command
            boolean hasCommand = commands.containsKey(key.label);

            // Draw key background
            shapeRenderer.setColor(hasCommand ? ASSIGNED_KEY_COLOR : KEY_COLOR);
            shapeRenderer.rect(
                    keyboardStartX + key.relativeX,
                    keyboardStartY + key.relativeY,
                    key.width,
                    key.height
            );
        }
        shapeRenderer.end();

        // Draw key borders only (no connecting lines)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(KEY_BORDER_COLOR);

        for (KeyboardKey key : allKeys) {
            // Draw key border
            shapeRenderer.rect(
                    keyboardStartX + key.relativeX,
                    keyboardStartY + key.relativeY,
                    key.width,
                    key.height
            );
        }
        shapeRenderer.end();

        // Draw text on keys and descriptions
        stage.getBatch().begin();

        for (KeyboardKey key : allKeys) {
            // Draw key label in top-left corner
            atlasManager.menuBoldFont.setColor(Color.WHITE);
            atlasManager.menuBoldFont.getData().setScale(1.2f); // Slightly smaller for better fit

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

                float descX = keyboardStartX + key.relativeX + 4; // Same padding as label
                float lineHeight = 14f;

                // First, determine how many lines we need
                String[] words = command.description.split(" ");
                StringBuilder currentLine = new StringBuilder();
                int totalLines = 0;
                float maxLines = Math.max(1, (key.height - 20) / lineHeight); // Dynamic max lines based on key height

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
                int lineCount = 0;
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

    private void addCommand(String key, String description, float descX, float descY) {
        KeyboardCommand command = new KeyboardCommand(key, description);
        command.descriptionPosition.set(descX, descY);
        commands.put(key, command);
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
}

