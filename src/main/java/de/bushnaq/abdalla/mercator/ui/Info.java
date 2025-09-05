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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.engine.AtlasManager;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.factory.Factory;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.SimNeed;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Engine;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.TimeStatistic;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

import java.util.ArrayList;
import java.util.List;

public class Info {
    private static final String                       CAPTION_LABEL  = "captionLabel";
    private static final String                       NAME_LABEL     = "nameLabel";
    private static final String                       STATIC_LABEL   = "staticLabel";
    private static final String                       VARIABLE_LABEL = "variableLabel";
    private final        AtlasManager                 atlasManager;
    private final        Batch                        batch;
    private final        OrthographicCamera           camera;
    private final        TimeStatistic                debugTimer;
    private final        InputMultiplexer             inputMultiplexer;
    // private TextButton closeButton;
    private              int                          labelIndex     = 0;
    private final        List<LabelData>              labels         = new ArrayList<LabelData>();
    private              RenderEngine3D<GameEngine3D> renderEngine;
    private              float                        screenHeight   = 0;
    private              float                        screenWidth;
    private              Skin                         skin;
    private              Stage                        stage;
    private final        StringBuilder                stringBuilder  = new StringBuilder();
    private final        String                       title          = "info";
    private              Class<?>                     type;
    private              Window                       window;

    //	public Info(Render2DMaster renderMaster, InputMultiplexer inputMultiplexer) {
    //		this.renderMaster = renderMaster;
    //		this.inputMultiplexer = inputMultiplexer;
    //	}

    public Info(RenderEngine3D<GameEngine3D> renderEngine, final AtlasManager atlasManager, OrthographicCamera camera, final Batch batch, final InputMultiplexer inputMultiplexer) throws Exception {
        this.renderEngine = renderEngine;
        //		this.universe = universe;
        this.atlasManager     = atlasManager;
        this.camera           = camera;
        this.batch            = batch;
        this.inputMultiplexer = inputMultiplexer;
        debugTimer            = new TimeStatistic();
    }

    public void act(final float deltaTime) {
        stage.act(deltaTime);
    }

    private void checkSize(final int size) {
        if (labels.size() != size)
            System.err.printf("size mismatch, expected %d but was %d\n", size, labels.size());
    }

    private void clearUnmatchedSizeAndType(final int size, final Class<?> type) {
        if (labels.size() != size || this.type != type) {
            // window.clearChildren();
            // labels.clear();
            labels.clear();
            labelIndex = 0;
            disposeWindow();
            createStage();
            // System.err.print( "+" );
            this.type = type;
        }
    }

    public void createStage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(camera), batch);
            inputMultiplexer.addProcessor(stage);
            skin = new Skin();
            skin.addRegions(atlasManager.atlas);
            skin.add("default-font", atlasManager.menuFont);
            skin.add("graph-font", atlasManager.chartFont);
            skin.load(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/uiskin.json"));
        }
        window = new Window(title, skin);
        // window.setDebug( true );
//        final TextButton closeButton = new TextButton("X", skin);
//        window.getTitleTable().add(closeButton).height(window.getPadTop());
        window.setMovable(true);
//        closeButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(final InputEvent event, final float x, final float y) {
        //				universe.selected = null;
        // System.err.println(
        // "------------------------------------------------------------" );
//            }
//        });
        stage.addActor(window);
        window.pack();
    }

    public void dispose() {
        inputMultiplexer.removeProcessor(stage);
        stage.dispose();
        skin.dispose();
    }

    public void disposeWindow() {
        stage.clear();
    }

    public void draw() {
        stage.draw();
    }

    public Viewport getViewport() {
        return stage.getViewport();
    }
    //	public Info(String title) {
    //		this.title = title;
    //	}

    private void positionWindow() {
        if (window != null)
            window.setPosition(screenWidth - window.getWidth(), screenHeight - window.getHeight()/* - GameEngine2D.FONT_SIZE - 2*/);
    }

    public void resize(final float w, final float h) {
        screenHeight = h;
        screenWidth  = w;
        positionWindow();
    }

    private void update(final Universe universe) {
        clearUnmatchedSizeAndType(6, Universe.class);
        updateCake(universe.statisticsPieChart, universe.creditPieChart, universe.amountPieChart, universe.satisfactionPieChart);
        updateCake(universe.simDeadReasonPieChart, universe.traderDeadReasonPieChart, universe.planetDeadReasonPieChart);
        updateGraph(universe.deadSimStatistics, universe.deadTraderStatistics);
        window.getTitleLabel().setText("Statistics");
        window.pack();
        positionWindow();
        labelIndex = 0;
    }

    private void update(final Universe universe, final GLProfiler profiler) {
        if (profiler != null && profiler.isEnabled() && debugTimer.getTime() > 1000) {
            int size = 10;
            if (renderEngine != null)
                size = 14;
            clearUnmatchedSizeAndType(size, GLProfiler.class);
            updateNameAndValue("textureBindings", profiler.getTextureBindings(), VARIABLE_LABEL);
            updateNameAndValue("drawCalls", profiler.getDrawCalls(), VARIABLE_LABEL);

            updateNameAndValue("shaderSwitches", profiler.getShaderSwitches(), VARIABLE_LABEL);
            updateNameAndValue("vertexCount.min", profiler.getVertexCount().min, VARIABLE_LABEL);
            updateNameAndValue("vertexCount.average", profiler.getVertexCount().average, VARIABLE_LABEL);
            updateNameAndValue("vertexCount.max", profiler.getVertexCount().max, VARIABLE_LABEL);
            updateNameAndValue("calls", profiler.getCalls(), VARIABLE_LABEL);
            updateNameAndValue("Texture.getNumManagedTextures()", Texture.getNumManagedTextures(), VARIABLE_LABEL);
            updateNameAndValue("delta time", Gdx.graphics.getDeltaTime(), VARIABLE_LABEL);
            updateNameAndValue("fps", Gdx.graphics.getFramesPerSecond(), VARIABLE_LABEL);

            if (renderEngine != null) {
                {
                    final int count      = renderEngine.visibleStaticGameObjectCount;
                    final int totalCount = renderEngine.staticGameObjects.size;
                    stringBuilder.setLength(0);
                    stringBuilder.append(count).append(" / ").append(totalCount);
                    updateNameAndValue("Static Models", stringBuilder.toString(), VARIABLE_LABEL);
                }
                {
                    stringBuilder.setLength(0);
                    final int count      = renderEngine.visibleDynamicGameObjectCount;
                    final int totalCount = renderEngine.dynamicGameObjects.size;
                    stringBuilder.append(count).append(" / ").append(totalCount);
                    updateNameAndValue("Dynamic Models", stringBuilder.toString(), VARIABLE_LABEL);
                }
                //			 light
                PointLightsAttribute pointLightsAttribute = renderEngine.environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
                if (pointLightsAttribute != null) {
                    Array<PointLight> lights = pointLightsAttribute.lights;
                    {
                        stringBuilder.setLength(0);
                        final int count      = renderEngine.visibleStaticLightCount;
                        final int totalCount = lights.size;
                        stringBuilder.append(count).append(" / ").append(totalCount);
                        updateNameAndValue("Static PointLights", stringBuilder.toString(), VARIABLE_LABEL);
                    }
                    {
                        stringBuilder.setLength(0);
                        final int count      = renderEngine.visibleDynamicLightCount;
                        final int totalCount = lights.size;
                        stringBuilder.append(count).append(" / ").append(totalCount);
                        updateNameAndValue("Dynamic PointLights", stringBuilder.toString(), VARIABLE_LABEL);
                    }
                }
            }

            updateWidgets("Profiler");
            checkSize(size);
        }
    }

    private void update(final Universe universe, final Good good) {
        final int size = 7;
        if (good != null) {
            clearUnmatchedSizeAndType(size, Good.class);
            updateNameAndValue("name", good.type.getName(), NAME_LABEL);
            updateNameAndValue("amount", good.getAmount(), VARIABLE_LABEL);
            updateNameAndValue("AverageAmount", good.averageAmount, STATIC_LABEL);
            updateNameAndValue("price", good.price, VARIABLE_LABEL);
            updateNameAndValue("AveragePrice", good.averagePrice, STATIC_LABEL);
            updateNameAndValue("traded", good.isTraded(universe.currentTime), VARIABLE_LABEL);
            updateNameAndValue("lastBuyInterest", String.format("%s", TimeUnit.toString(good.lastBuyInterest)), VARIABLE_LABEL);
        }
        updateWidgets("good");
        checkSize(size);
    }

    public void update(final Universe universe, final Object selected, final RenderEngine3D<GameEngine3D> renderEngine) throws Exception {
        this.renderEngine = renderEngine;
        if (selected instanceof Planet) {
            update(universe, (Planet) selected);
        } else if (selected instanceof Trader) {
            update(universe, (Trader) selected);
        } else if (selected instanceof Sim) {
            update(universe, (Sim) selected);
        } else if (selected instanceof ProductionFacility) {
            update(universe, (ProductionFacility) selected);
        } else if (selected instanceof Good) {
            update(universe, (Good) selected);
        } else if (selected instanceof GLProfiler) {
            update(universe, (GLProfiler) selected);
        } else {
            update(universe);
        }
        if (debugTimer.getTime() > 1000) {
            debugTimer.restart();
        }
    }

    private void update(final Universe universe, final Planet planet) {
        if (planet != null) {
            final int size = 13;
            clearUnmatchedSizeAndType(size, Planet.class);
            updateNameAndValue("name", planet.getName(), NAME_LABEL);
            updateNameAndValue("credits", planet.getCredits(), VARIABLE_LABEL);
            updateNameAndValue("docking door state", planet.dockingDoors.getDockingDoorStatus().name(), VARIABLE_LABEL);
            updateNameAndValue("in dock", planet.inDock != null ? planet.inDock.getName() : "-", VARIABLE_LABEL);
            updateNameAndValue("status", planet.status.name(), VARIABLE_LABEL);
            updateNameAndValue("sector", planet.sector.name, NAME_LABEL);
            updateNameAndValue("satisfaction", planet.getSatisfactionFactor(universe.currentTime), VARIABLE_LABEL);
            updateNameAndValue("anual export amount", planet.getHistoryManager().getAnualExportAmountOfGoods(), VARIABLE_LABEL);
            updateNameAndValue("anual import amount", planet.getHistoryManager().getAnualImportAmountOfGoods(), VARIABLE_LABEL);
            updateNameAndValue("anual import credits", planet.getHistoryManager().getAnualImportedCredits(), VARIABLE_LABEL);
            updateNameAndValue("anual export credits", planet.getHistoryManager().getAnualExportCredits(), VARIABLE_LABEL);
            updateNameAndValue("anual local credits earned", planet.getHistoryManager().getAnualLocalCreditsEarned(), VARIABLE_LABEL);
            updateNameAndValue("anual local credits spent", planet.getHistoryManager().getAnualLocalCreditsSpent(), VARIABLE_LABEL);
            updateWidgets("Station");
            checkSize(size);
        }
    }

    private void update(final Universe universe, final ProductionFacility productionFacility) {
        if (productionFacility != null) {
            if (productionFacility instanceof Factory factory) {
                final int size = 10 + 1 + factory.inputGood.size();
                clearUnmatchedSizeAndType(size, ProductionFacility.class);
                updateNameAndValue("name", productionFacility.getName(), NAME_LABEL);
                updateNameAndValue("planet", productionFacility.planet.getName(), NAME_LABEL);
                updateNameAndValue("status", productionFacility.getStatusName(), VARIABLE_LABEL);
                updateNameAndValue("profit", productionFacility.queryProfit(), VARIABLE_LABEL);
                updateNameAndValue("produces", productionFacility.producedGood.type.getName(), NAME_LABEL);
                updateNameAndValue("produced", productionFacility.producedGood.statistic.produced, NAME_LABEL);
                updateNameAndValue("consumed", productionFacility.producedGood.statistic.consumed, NAME_LABEL);
                updateNameAndValue("bought", productionFacility.producedGood.statistic.bought, NAME_LABEL);
                updateNameAndValue("sold", productionFacility.producedGood.statistic.sold, NAME_LABEL);
                updateNameAndValue("", "", VARIABLE_LABEL);
                updateGood("good", CAPTION_LABEL, "price", CAPTION_LABEL, "average", CAPTION_LABEL, "amount", CAPTION_LABEL, "average", CAPTION_LABEL);
                for (final Good good : factory.inputGood) {
                    updateGood(good.type.getName(), NAME_LABEL, good.price, VARIABLE_LABEL, good.averagePrice, STATIC_LABEL, good.getAmount(), VARIABLE_LABEL, good.averageAmount, STATIC_LABEL);
                }
                updateWidgets("production facility");
                checkSize(size);
            }
        }
    }

    private void update(final Universe universe, final Sim sim) {
        if (sim != null) {
            final int size = 11 + 1 + sim.simNeedsList.size();
            clearUnmatchedSizeAndType(size, Sim.class);
            updateNameAndValue("name", sim.getName(), NAME_LABEL);
            updateNameAndValue("status", sim.status.getName(), VARIABLE_LABEL);
            updateNameAndValue("cost", sim.cost, STATIC_LABEL);
            updateNameAndValue("planet", sim.planet.getName(), NAME_LABEL);
            updateNameAndValue("credits", sim.getCredits(), VARIABLE_LABEL);
            updateNameAndValue("creditsToSave", sim.creditsToSave, VARIABLE_LABEL);
            updateNameAndValue("start credits", Sim.SIM_START_CREDITS, VARIABLE_LABEL);
            updateNameAndValue("factory", sim.productionFacility != null ? sim.productionFacility.getName() : "-", NAME_LABEL);
            updateNameAndValue("profession", sim.profession.name(), VARIABLE_LABEL);
            updateNameAndValue("satisfaction", sim.getSatisfactionFactor(universe.currentTime), VARIABLE_LABEL);
            updateNameAndValue("consumed", sim.lastYearConsumedAmount, VARIABLE_LABEL);
            updateGood("good", CAPTION_LABEL, "every", CAPTION_LABEL, "last time", CAPTION_LABEL, "consumed", CAPTION_LABEL, null, null);
            for (final SimNeed needs : sim.simNeedsList) {
                updateGood(needs.type.getName(), NAME_LABEL, TimeUnit.toString(needs.consumeEvery), STATIC_LABEL, TimeUnit.toString(needs.lastConsumed), VARIABLE_LABEL, "" + needs.totalConsumed, VARIABLE_LABEL, null, null);
            }
            updateWidgets("Sim");
            checkSize(size);
        }
    }

    private void update(final Universe universe, final Trader trader) {
        if (trader != null) {
            int numberOfGoods = 0;
            for (final Good good : trader.getGoodList()) {
                if (good.getAmount() != 0f) {
                    numberOfGoods++;
                }
            }
            final int size = 20 + 1 + 1 + numberOfGoods + 1 + 1 + trader.simNeedsList.size();
            clearUnmatchedSizeAndType(size, Trader.class);
            updateNameAndValue("Name", trader.getName(), NAME_LABEL);
            updateNameAndValue("Sim Status", trader.status.getName(), VARIABLE_LABEL);
            updateNameAndValue("Trader Status", trader.getTraderStatus().getDisplayName(), VARIABLE_LABEL);
            updateNameAndValue("Trader Substatus", trader.getTraderSubStatus().getDisplayName(), VARIABLE_LABEL);
            updateNameAndValue("Start Credits", Sim.SIM_START_CREDITS, STATIC_LABEL);
            updateNameAndValue("Cargo Capacity", trader.goodSpace, STATIC_LABEL);
            updateNameAndValue("Engine Speed", trader.getEngine().getEngineSpeed() * Engine.ENGINE_TO_REALITY_FACTOR, STATIC_LABEL);
            updateNameAndValue("Engine Acceleration", trader.getEngine().getEngineSpeed(), STATIC_LABEL);
            updateNameAndValue("Rotation Speed", trader.getManeuveringSystem().rotationSpeed, STATIC_LABEL);
            updateNameAndValue("Credits", String.format("%.2f", trader.getCredits()), VARIABLE_LABEL);
            updateNameAndValue("Credits to Save", String.format("%.2f", trader.creditsToSave), VARIABLE_LABEL);
            updateNameAndValue("Factory", trader.productionFacility != null ? trader.productionFacility.getName() : "-", NAME_LABEL);
            updateNameAndValue("Profession", trader.profession.name(), VARIABLE_LABEL);
            updateNameAndValue("Satisfaction", trader.getSatisfactionFactor(universe.currentTime), VARIABLE_LABEL);
            updateNameAndValue("Resting", trader.portRestingTime, VARIABLE_LABEL);
            updateNameAndValue("Source Station", trader.navigator.sourcePlanet != null ? trader.navigator.sourcePlanet.getName() : "-", NAME_LABEL);
            updateNameAndValue("Current Station", trader.planet != null ? trader.planet.getName() : "-", NAME_LABEL);
            updateNameAndValue("Destination Station", trader.navigator.destinationPlanet != null ? trader.navigator.destinationPlanet.getName() : "-", NAME_LABEL);
            updateNameAndValue("Previous Waypoint", trader.navigator.previousWaypoint != null ? trader.navigator.previousWaypoint.getName() : "-", NAME_LABEL);
            updateNameAndValue("Next Waypoint", trader.navigator.nextWaypoint != null ? trader.navigator.nextWaypoint.getName() : "-", NAME_LABEL);
            updateNameAndValue("", "", VARIABLE_LABEL);
            updateGood("good", CAPTION_LABEL, "price", CAPTION_LABEL, "average", CAPTION_LABEL, "amount", CAPTION_LABEL, "average", CAPTION_LABEL);
            for (final Good good : trader.getGoodList()) {
                if (good.getAmount() != 0f) {
                    updateGood(good.type.getName(), NAME_LABEL, good.price, VARIABLE_LABEL, good.averagePrice, STATIC_LABEL, good.getAmount(), VARIABLE_LABEL, good.averageAmount, STATIC_LABEL);
                }
            }
            updateNameAndValue("consumed", trader.lastYearConsumedAmount, VARIABLE_LABEL);
            updateGood("good", CAPTION_LABEL, "every", CAPTION_LABEL, "last time", CAPTION_LABEL, "consumed", CAPTION_LABEL, null, null);
            for (final SimNeed needs : trader.simNeedsList) {
                updateGood(needs.type.getName(), NAME_LABEL, TimeUnit.toString(needs.consumeEvery), STATIC_LABEL, TimeUnit.toString(needs.lastConsumed), VARIABLE_LABEL, "" + needs.totalConsumed, VARIABLE_LABEL, null, null);
            }
            updateWidgets("Trader");
            checkSize(size);
        }
    }

    private void updateCake(final PieChartData... creditPieCharts) {
        if (labelIndex >= labels.size()) {
            final LabelData data = new LabelData();
            labels.add(data);
            for (final PieChartData pieChartData : creditPieCharts) {
                final PieChart pieChart = new PieChart(pieChartData, skin, "factory");
                data.pieCharts.add(pieChart);
                window.add(pieChart);
            }
            window.row();
        }
        final LabelData data  = labels.get(labelIndex++);
        int             index = 0;
        for (final PieChartData pieChartData : creditPieCharts) {
            data.pieCharts.get(index++).pieChart = pieChartData;
        }
    }

    private void updateGood(final String name, final String nameStyle, final float value1, final String style1, final float value2, final String style2, final float value3, final String style3, final float value4, final String style4) {
        final LabelData data = updateName(name, nameStyle);
        data.nameLabel.setStyle(skin.get(nameStyle, LabelStyle.class));
        data.valueLabel.setText(String.valueOf(value1));
        data.valueLabel.setStyle(skin.get(style1, LabelStyle.class));
        data.value2Label.setText(String.valueOf(value2));
        data.value2Label.setStyle(skin.get(style2, LabelStyle.class));
        data.value3Label.setText(String.valueOf(value3));
        data.value3Label.setStyle(skin.get(style3, LabelStyle.class));
        data.value4Label.setText(String.valueOf(value4));
        data.value4Label.setStyle(skin.get(style4, LabelStyle.class));
    }

    private void updateGood(final String name, final String nameStyle, final String value1, final String style1, final String value2, final String style2, final String value3, final String style3, final String value4, final String style4) {
        final LabelData data = updateName(name, nameStyle);
        data.nameLabel.setStyle(skin.get(nameStyle, LabelStyle.class));
        data.valueLabel.setText(value1);
        data.valueLabel.setStyle(skin.get(style1, LabelStyle.class));
        data.value2Label.setText(value2);
        data.value2Label.setStyle(skin.get(style2, LabelStyle.class));
        if (value3 != null) {
            data.value3Label.setText(value3);
            data.value3Label.setStyle(skin.get(style3, LabelStyle.class));
        }
        if (value4 != null) {
            data.value4Label.setText(value4);
            data.value4Label.setStyle(skin.get(style4, LabelStyle.class));
        }
    }

    private void updateGraph(final GraphChartData... graphChartDataList) {
        if (labelIndex >= labels.size()) {
            final LabelData data = new LabelData();
            labels.add(data);
            for (final GraphChartData graphChartData : graphChartDataList) {
                final GraphChart graph = new GraphChart(graphChartData, skin, "factory");
                data.graphCharts.add(graph);
                window.add(graph);
            }
            window.row();
        }
        final LabelData data  = labels.get(labelIndex++);
        int             index = 0;
        for (final GraphChartData graphChartData : graphChartDataList) {
            data.graphCharts.get(index++).graphChartData = graphChartData;
        }
    }

    private LabelData updateName(final String name, final String style) {
        if (labelIndex >= labels.size()) {
            final LabelData data = new LabelData();
            data.nameLabel   = new Label("", skin, "captionLabel");
            data.valueLabel  = new Label("", skin);
            data.value2Label = new Label("", skin);
            data.value3Label = new Label("", skin);
            data.value4Label = new Label("", skin);
            labels.add(data);
            window.add(data.nameLabel).right();
            window.add("");
            window.add(data.valueLabel).left();
            window.add(data.value2Label).left();
            window.add(data.value3Label).left();
            window.add(data.value4Label).left();
            window.row();
        }
        final LabelData data = labels.get(labelIndex++);
        data.nameLabel.setText(name);
        data.valueLabel.setStyle(skin.get(style, LabelStyle.class));
        return data;
    }

    private void updateNameAndValue(final String name, final boolean traded, final String style) {
        final LabelData data = updateName(name, style);
        data.valueLabel.setText(traded ? "true" : "false");
    }

    private void updateNameAndValue(final String name, final float value, final String style) {
        final LabelData data = updateName(name, style);
        data.valueLabel.setText(String.valueOf(value));
    }

    private void updateNameAndValue(final String name, final String value, final String style) {
        final LabelData data = updateName(name, style);
        data.valueLabel.setText(value);
    }

    private void updateWidgets(final String title) {
        window.getTitleLabel().setText(title);
        final Array<Cell> cells = window.getCells();
        if (cells.size < labels.size() * 6 + 6) {
            window.add("").minWidth(100);
            window.add("").minWidth(10f);
            window.add("").minWidth(75);
            window.add("").minWidth(75);
            window.add("").minWidth(75);
            window.add("").minWidth(75);
            window.add("");
            window.row();
            window.pack();
            positionWindow();
        }
        labelIndex = 0;
    }
}
