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

        KeyboardKey(String label, float relativeX, float relativeY, float width) {
            this.label = label;
            this.relativeX = relativeX;
            this.relativeY = relativeY;
            this.width = width;
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

        // Function key row: F1-F12, Print, Scroll Lock, Pause
        String[] functionKeys = {"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"};
        String[] systemKeys = {"PRNT", "ROLL", "PAUSE"};

        float currentX = 0;
        float currentY = 0;

        // F1-F4 group
        for (int i = 0; i < 4; i++) {
            allKeys.add(new KeyboardKey(functionKeys[i], currentX, currentY, FUNCTION_KEY_WIDTH));
            currentX += FUNCTION_KEY_WIDTH + KEY_SPACING;
        }

        currentX += KEY_SPACING; // Gap between F4 and F5

        // F5-F8 group
        for (int i = 4; i < 8; i++) {
            allKeys.add(new KeyboardKey(functionKeys[i], currentX, currentY, FUNCTION_KEY_WIDTH));
            currentX += FUNCTION_KEY_WIDTH + KEY_SPACING;
        }

        currentX += KEY_SPACING; // Gap between F8 and F9

        // F9-F12 group
        for (int i = 8; i < 12; i++) {
            allKeys.add(new KeyboardKey(functionKeys[i], currentX, currentY, FUNCTION_KEY_WIDTH));
            currentX += FUNCTION_KEY_WIDTH + KEY_SPACING;
        }

        currentX += KEY_SPACING; // Gap before system keys

        // System keys (Print, Scroll Lock, Pause) - positioned about one key width to the right
        currentX += KEY_WIDTH;
        for (String key : systemKeys) {
            allKeys.add(new KeyboardKey(key, currentX, currentY, FUNCTION_KEY_WIDTH));
            currentX += FUNCTION_KEY_WIDTH + KEY_SPACING;
        }

        // Number row: 1-0 with additional keys
        currentY -= KEY_HEIGHT + ROW_SPACING;
        currentX = 0;
        String[] numberKeys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "ß", "´"};
        for (String key : numberKeys) {
            allKeys.add(new KeyboardKey(key, currentX, currentY, KEY_WIDTH));
            currentX += KEY_WIDTH + KEY_SPACING;
        }

        // QWERTZ row (half a key to the right of 1)
        currentY -= KEY_HEIGHT + ROW_SPACING;
        currentX = KEY_WIDTH / 2;
        String[] qwertzKeys = {"Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "Ü", "+"};
        for (String key : qwertzKeys) {
            allKeys.add(new KeyboardKey(key, currentX, currentY, KEY_WIDTH));
            currentX += KEY_WIDTH + KEY_SPACING;
        }

        // ASDF row (quarter a key width to the right of Q)
        currentY -= KEY_HEIGHT + ROW_SPACING;
        currentX = KEY_WIDTH / 2 + KEY_WIDTH / 4;
        String[] asdfKeys = {"A", "S", "D", "F", "G", "H", "J", "K", "L", "Ö", "Ä", "#"};
        for (String key : asdfKeys) {
            allKeys.add(new KeyboardKey(key, currentX, currentY, KEY_WIDTH));
            currentX += KEY_WIDTH + KEY_SPACING;
        }

        // YXCV row (3/4 a key width to the right of A)
        currentY -= KEY_HEIGHT + ROW_SPACING;
        currentX = KEY_WIDTH / 2 + KEY_WIDTH / 4 + KEY_WIDTH * 3 / 4;
        String[] yxcvKeys = {"Y", "X", "C", "V", "B", "N", "M", ",", ".", "-"};
        for (String key : yxcvKeys) {
            allKeys.add(new KeyboardKey(key, currentX, currentY, KEY_WIDTH));
            currentX += KEY_WIDTH + KEY_SPACING;
        }

        // Space bar (below X until after M)
        currentY -= KEY_HEIGHT + ROW_SPACING;
        float spaceStartX = KEY_WIDTH / 2 + KEY_WIDTH / 4 + KEY_WIDTH * 3 / 4 + KEY_WIDTH + KEY_SPACING; // Position after Y
        float spaceEndX = KEY_WIDTH / 2 + KEY_WIDTH / 4 + KEY_WIDTH * 3 / 4 + (KEY_WIDTH + KEY_SPACING) * 7; // Position after M
        float spaceWidth = spaceEndX - spaceStartX;
        allKeys.add(new KeyboardKey("Space", spaceStartX, currentY, spaceWidth));
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
                    KEY_HEIGHT
            );
        }
        shapeRenderer.end();

        // Draw key borders and connecting lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(KEY_BORDER_COLOR);

        for (KeyboardKey key : allKeys) {
            // Draw key border
            shapeRenderer.rect(
                    keyboardStartX + key.relativeX,
                    keyboardStartY + key.relativeY,
                    key.width,
                    KEY_HEIGHT
            );

            // Draw connecting line if this key has a command
            KeyboardCommand command = commands.get(key.label);
            if (command != null) {
                shapeRenderer.setColor(LINE_COLOR);
                float keyCenter = keyboardStartX + key.relativeX + key.width;
                float keyCenterY = keyboardStartY + key.relativeY + KEY_HEIGHT / 2;

                shapeRenderer.line(
                        keyCenter,
                        keyCenterY,
                        command.descriptionPosition.x - 5,
                        command.descriptionPosition.y
                );

                shapeRenderer.setColor(KEY_BORDER_COLOR);
            }
        }
        shapeRenderer.end();

        // Draw text on keys and descriptions
        stage.getBatch().begin();

        for (KeyboardKey key : allKeys) {
            // Draw key text with larger font
            atlasManager.menuBoldFont.setColor(Color.WHITE);
            atlasManager.menuBoldFont.getData().setScale(1.5f); // Make font 1.5x larger

            // Calculate proper text positioning using GlyphLayout for accurate centering
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
            layout.setText(atlasManager.menuBoldFont, key.label);

            float textX = keyboardStartX + key.relativeX + (key.width - layout.width) / 2;
            float textY = keyboardStartY + key.relativeY + (KEY_HEIGHT + layout.height) / 2;

            atlasManager.menuBoldFont.draw(
                    stage.getBatch(),
                    key.label,
                    textX,
                    textY
            );

            // Reset font scale for descriptions
            atlasManager.menuBoldFont.getData().setScale(1.0f);

            // Draw description if this key has a command
            KeyboardCommand command = commands.get(key.label);
            if (command != null) {
                atlasManager.menuFont.setColor(DESCRIPTION_COLOR);
                atlasManager.menuFont.draw(
                        stage.getBatch(),
                        command.description,
                        command.descriptionPosition.x,
                        command.descriptionPosition.y
                );
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
