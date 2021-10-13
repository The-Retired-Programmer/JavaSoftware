/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.lafe;

import java.util.function.UnaryOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

public class FrontPanelWindow {

    private final Class clazz;
    private final Stage stage;
    private Rectangle2D windowsize;
    private FrontPanelDisplay displaypane;
    private Text messagenode;
    private FrontPanelControls frontpanelcontrols;

    public FrontPanelWindow(Stage stage, FrontPanelController controller) {
        this.clazz = FrontPanelWindow.class;
        this.stage = stage;
        setDefaultWindowWidth(400);
        LafePreferences.applyWindowSizePreferences(stage, clazz, windowsize);
        stage.setScene(buildScene(controller));
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Logic Analyser");
        stage.setOnHiding(e -> ExecuteAndCatch.run(() -> saveWindowSizePreferences()));
        stage.show();
    }
    
    private Scene buildScene(FrontPanelController controller) {
        BorderPane borderpane = new BorderPane();
        //pane.setContextMenu(contextmenu);
        borderpane.setCenter(new ScrollPane(displaypane = new FrontPanelDisplay(controller)));
        borderpane.setBottom(buildConfiguration(controller.getProbeConfiguration()));
        borderpane.setLeft(frontpanelcontrols= new FrontPanelControls(controller));
        borderpane.setTop(messagenode = new Text());
        return new Scene(borderpane);
    }

    public void close() {
        stage.close();
    }

    public void refreshDisplay() {
        displaypane.refresh();
    }
    
    public void writestatusmessage(String message) {
        messagenode.setText(message);
    }
    
    public final void checkifprobeconnected() {
        messagenode.setText(frontpanelcontrols.checkifprobeconnected()? "Probe Connected": "Probe connection failed");
    }

    public void reset() {
        LafePreferences.clearWindowSizePreferences(clazz);
        LafePreferences.applyWindowSizePreferences(stage, clazz, windowsize);
    }

    private void setDefaultWindowWidth(double width) {
        Rectangle2D screenbounds = Screen.getPrimary().getVisualBounds();
        if (width > screenbounds.getWidth()) {
            windowsize = screenbounds;
            return;
        }
        windowsize = new Rectangle2D(
                screenbounds.getMinX(),
                screenbounds.getMinY(),
                width,
                screenbounds.getHeight()
        );
    }

    private void saveWindowSizePreferences() {
        LafePreferences.saveWindowSizePreferences(stage, clazz);
    }
    
    
    //    the configuration panel
    
    private HBox buildConfiguration(ProbeConfiguration config) {
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(
                labelledNode("Sample Source", combineFields(
                        labelledNode("First Pin", integerField(config.firstpin, 3)),
                        labelledNode("Pins", integerField(config.pins, 3)))),
                labelledNode("Sampling Speed", combineFields(
                        labelledNode("speed", integerField(config.speed, 10)),
                        labelledNode("units", hzunitsSelectionField(config.speedunit)),
                        labelledNode("multiplier", integerField(config.speedmultiplier, 3)))),
                labelledNode("Start Trigger", combineFields(
                        labelledNode("Enable", checkboxField(config.st_enabled)),
                        labelledNode("Pin", integerField(config.st_pin, 3)),
                        labelledNode("Pin Level", triggerSelectionField(config.st_level)))),
                labelledNode("Sample Size (bits)", integerField(config.samplesize, 7)),
                labelledNode("Sample End Mode", sampleendmodeSelectionField(config.sampleendmode)),
                labelledNode("Event Trigger", combineFields(
                        labelledNode("Enable", checkboxField(config.et_enabled)),
                        labelledNode("Pin", integerField(config.et_pin, 3)),
                        labelledNode("Pin Level", triggerSelectionField(config.et_level))))
        );
        return hbox;
    }

    private VBox labelledNode(String label, Node node) {
        VBox display = new VBox(2);
        display.setAlignment(Pos.CENTER);
        display.getChildren().addAll(new Label(label), node);
        return display;
    }

    private Node combineFields(Node... fields) {
        HBox display = new HBox(0);
        display.setAlignment(Pos.CENTER);
        display.getChildren().addAll(fields);
        return display;
    }

    private TextField integerField(IntegerProperty property, int size) {
        TextField intfield = new TextField();
        intfield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return intfield;
    }

    static UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)?")) {
            return change;
        }
        return null;
    };

    private ComboBox<ProbeConfiguration.HzUnits> hzunitsSelectionField(ObjectProperty<ProbeConfiguration.HzUnits> value) {
        ComboBox<ProbeConfiguration.HzUnits> cbox = new ComboBox<>();
        cbox.getItems().addAll(ProbeConfiguration.HzUnits.values());
        cbox.valueProperty().bindBidirectional(value);
        return cbox;
    }

    private ComboBox<ProbeConfiguration.Trigger> triggerSelectionField(ObjectProperty<ProbeConfiguration.Trigger> value) {
        ComboBox<ProbeConfiguration.Trigger> cbox = new ComboBox<>();
        cbox.getItems().addAll(ProbeConfiguration.Trigger.values());
        cbox.valueProperty().bindBidirectional(value);
        return cbox;
    }

    private ComboBox<ProbeConfiguration.SampleEndMode> sampleendmodeSelectionField(ObjectProperty<ProbeConfiguration.SampleEndMode> value) {
        ComboBox<ProbeConfiguration.SampleEndMode> cbox = new ComboBox<>();
        cbox.getItems().addAll(ProbeConfiguration.SampleEndMode.values());
        cbox.valueProperty().bindBidirectional(value);
        return cbox;
    }

    private CheckBox checkboxField(BooleanProperty value) {
        CheckBox booleanfield = new CheckBox();
        booleanfield.setSelected(value.get());
        booleanfield.selectedProperty().bindBidirectional(value);
        return booleanfield;
    }
}
