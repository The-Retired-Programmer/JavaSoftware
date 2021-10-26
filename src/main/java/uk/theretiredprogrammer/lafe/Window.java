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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.DARKGREY;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.lafe.ProbeStateWatchDog.ProbeState;

public class Window {

    private final Class clazz;
    private final Stage stage;
    private Rectangle2D windowsize;
    private final ProbeConfiguration config;
    private final Controller controller;

    public Window(Stage stage, Controller controller) {
        this.clazz = Window.class;
        this.stage = stage;
        this.controller = controller;
        this.config = controller.getProbeConfiguration();
        setDefaultWindowWidth(400);
        LafePreferences.applyWindowSizePreferences(stage, clazz, windowsize);
        stage.setScene(buildScene());
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Logic Analyser");
        stage.setOnHiding(e -> ExecuteAndCatch.run(() -> saveWindowSizePreferences()));
        stage.show();
    }

    private Scene buildScene() {
        BorderPane borderpane = new BorderPane();
        borderpane.setCenter(new ScrollPane(buildEmptySampleDisplay()));
        borderpane.setRight(buildConfiguration());
        borderpane.setTop(buildControls());
        borderpane.setBottom(buildStatus());
        return new Scene(borderpane);
    }

    public void close() {
        stage.close();
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

    // -------------------------------------------------------------------------
    //
    //  CommPort selection dialogs
    //
    // -------------------------------------------------------------------------
    public static String buildAndShowNoPicoProbeDialog() {
        ButtonType doneButtonType = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(doneButtonType);
        dialog.setTitle("Missing Probe");
        dialog.setContentText("Cannot find a PICO Probe connected to this machine\n\nPlease connect one and then press the Done button\n\n");
        dialog.getDialogPane().setMinWidth(500);
        dialog.setResizable(false);
        dialog.showAndWait();
        return null; // after connecting device then asked to try again
    }

    public static String buildAndShowManyPicoProbeDialog(List<String> commPorts, Consumer<String> setLocate, Consumer<USBSerialDevice> clearLocate) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, commPorts);
        dialog.setTitle("Multiple Probes Available");
        dialog.setHeaderText("More than one PICO probe appear to be connected to this machine\nPlease select one and then press the OK button\n");
        dialog.setGraphic(null);
        Optional<String> selected = dialog.showAndWait();
        return selected.orElse(null);
    }

    // -------------------------------------------------------------------------
    //
    //  status area
    //
    // -------------------------------------------------------------------------
    private Text statusnode;

    private final ObjectProperty<Paint> connectedProperty = new SimpleObjectProperty<>(RED);
    private final ObjectProperty<Paint> samplingProperty = new SimpleObjectProperty<>(RED);
    private final StringProperty samplingstatustext = new SimpleStringProperty("Unknown");
    private final StringProperty connectedprobetext = new SimpleStringProperty("No Probe connected");

    private Node buildStatus() {
        return new VBox(10,
                buildSamplingStatus(),
                buildStatusReporting()
        );
    }

    private Node buildSamplingStatus() {
        return new HBox(10,
                new Lamp(connectedProperty),
                new ConnectedProbe(connectedprobetext),
                new Lamp(samplingProperty),
                new SamplingStatus(samplingstatustext),
                statusnode = new Text()
        );
    }

    private Node buildStatusReporting() {
        return statusnode = new Text();
    }

    public void setConnected(boolean isconnected) {
        connectedProperty.set(isconnected ? GREEN : RED);
    }

    public void probeStateChanged(ProbeState newstate) {
        samplingstatustext.set(newstate.toString());
        switch (newstate) {
            case STATE_SAMPLING:
                samplingProperty.set(GREEN);
                break;
            case STATE_STOPPING_SAMPLING:
            case STATE_SAMPLING_DONE:
                samplingProperty.set(ORANGE);
                break;
            case STATE_IDLE:
            default:
                samplingProperty.set(RED);
        }
    }

    public void displayStatus(String message) {
        if (!message.isBlank()) {
            statusnode.setText(message);
        }
    }

    public class SamplingStatus extends Text {

        public SamplingStatus(StringProperty bind2statustext) {
            this.textProperty().bind(bind2statustext);
        }
    }

    public class ConnectedProbe extends Text {

        public ConnectedProbe(StringProperty bind2connectedprobetext) {
            this.textProperty().bind(bind2connectedprobetext);
        }
    }

    public void displayConnectedProbe(String probetype) {
        connectedprobetext.set(probetype);
    }

    public class Lamp extends Group {

        public Lamp(ObjectProperty<Paint> bind2Colour) {
            Circle colourlens = new Circle(40, 20, 10);
            colourlens.fillProperty().bind(bind2Colour);
            this.getChildren().addAll(
                    new Circle(40, 20, 15, DARKGREY),
                    colourlens
            );
        }
    }

    // -------------------------------------------------------------------------
    //
    // controls panel
    //
    // -------------------------------------------------------------------------
    public Node buildControls() {
        return new HBox(10,
                new ControlButton("Start Sampling", (ev) -> onStartSamplingRequest(ev)),
                new ControlButton("End Sampling", (ev) -> onStopSamplingRequest(ev)),
                new ControlButton("Reset Probe", (ev) -> onResetProbeRequest(ev)),
                new ControlButton("Start Probe Waveform Generator", (ev) -> onStartSQW(ev)),
                new ControlButton("Stop Probe Waveform Generator", (ev) -> onStopSQW(ev))
        );
    }

    public void onStartSamplingRequest(Event ev) {
        try {
            controller.start();
        } catch (IOException ex) {
        }
    }

    public void onStopSamplingRequest(Event ev) {
        try {
            controller.stop();
        } catch (IOException ex) {
        }
    }

    public void onResetProbeRequest(Event ev) {
        controller.resetProbe();
    }

    public void onStartSQW(Event ev) {
        controller.squareWaveGenerator(true);
    }

    public void onStopSQW(Event ev) {
        controller.squareWaveGenerator(false);
    }

    public class ControlButton extends Button {

        public ControlButton(String caption, EventHandler<ActionEvent> buttonPressedAction) {
            super(caption);
            this.setOnAction(buttonPressedAction);
        }
    }

    // -------------------------------------------------------------------------
    //
    //    the configuration panel
    //
    // -------------------------------------------------------------------------
    private Node buildConfiguration() {
        return new Accordion(
                new TitledPane("WaveForm Generator Configuration", buildWaveFormGeneratorForm()),
                new TitledPane("Sampling Configuration", buildSamplingConfigurationForm())
        );
    }

    private int row;

    private Node buildSamplingConfigurationForm() {
        GridPane pane = new GridPane();
        row = 0;
        insertSubtitle(pane, "Sample Source");
        insertField(pane, "First Pin", integerField(config.firstpin, 3));
        insertField(pane, "Pins", integerField(config.pins, 3));
        //
        insertSubtitle(pane, "Sampling Speed");
        insertField(pane, "speed", integerField(config.speed, 10));
        insertField(pane, "units", hzunitsSelectionField(config.speedunit));
        insertField(pane, "multiplier", integerField(config.speedmultiplier, 3));
        //
        insertSubtitle(pane, "Start Trigger");
        insertField(pane, "Enable", checkboxField(config.st_enabled));
        insertField(pane, "Pin", integerField(config.st_pin, 3));
        insertField(pane, "Pin Level", triggerSelectionField(config.st_level));
        //
        insertSubtitle(pane, "Event Trigger");
        insertField(pane, "Enable", checkboxField(config.et_enabled));
        insertField(pane, "Pin", integerField(config.et_pin, 3));
        insertField(pane, "Pin Level", triggerSelectionField(config.et_level));
        //
        insertSubtitle(pane, "Sampling Mode");
        insertField(pane, "Sample Size (bits)", integerField(config.samplesize, 7));
        insertField(pane, "Sample End Mode", sampleendmodeSelectionField(config.sampleendmode));
        return new ScrollPane(pane);
    }

    private Node buildWaveFormGeneratorForm() {
        GridPane pane = new GridPane();
        row = 0;
        insertField(pane, "First Pin", integerField(config.sqw_firstpin, 3));
        //
        insertField(pane, "Speed", integerField(config.sqw_speed, 10));
        insertField(pane, "Units", hzunitsSelectionField(config.sqw_speedunit));
        return new ScrollPane(pane);
    }

    private void insertSubtitle(GridPane pane, String subtitle) {
        pane.add(new Label(subtitle), 0, row++, 1, 1);
    }

    private void insertField(GridPane pane, String label, Node field) {
        pane.add(new Label(label), 0, row, 1, 1);
        pane.add(field, 1, row++, 1, 1);
    }

    private TextField integerField(IntegerProperty property, int size) {
        TextField intfield = new TextField();
        intfield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return intfield;
    }

    static UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9|,]*)?")) {
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

    // -------------------------------------------------------------------------
    //
    //  the sample display canvas
    //
    // -------------------------------------------------------------------------
    private Canvas sampledisplaycanvas;

    private Canvas buildEmptySampleDisplay() {
        //pane.setContextMenu(contextmenu);
        return sampledisplaycanvas = new Canvas(500.0, 500.0);
    }

    public void refreshSampleDisplay(Map<Integer, List<String>> samples) {
        int numbersamples = samples.size();
        expectedsamplesize = config.samplesize.get();
        // calculate layout
        int margin = 20;
        int height = 500;
        int maxsampleheight = 220;
        int calcsampleheight = (height - margin) / numbersamples;
        if (calcsampleheight > maxsampleheight) {
            calcsampleheight = maxsampleheight;
        }
        hscale = 5;
        int width = expectedsamplesize * hscale + 2 * margin;
        // set the required dimensions for the canvas and clear it
        sampledisplaycanvas.setWidth(width);
        sampledisplaycanvas.setHeight(height);
        sampledisplaycanvas.getGraphicsContext2D().clearRect(0, 0, sampledisplaycanvas.getWidth(), sampledisplaycanvas.getHeight());
        int count = 0;
        for (var es : samples.entrySet()) {
            int topofsample = calcsampleheight * count++ + margin;
            drawSample(es.getKey(), es.getValue(), margin, topofsample, topofsample + calcsampleheight - margin);
        }
    }

    // sample drawing variables
    private int hstart;
    private int hscale;
    private int highpos;
    private int lowpos;
    private int expectedsamplesize;
    private double[] xpos;
    private double[] ypos;
    private int insertat;

    private void drawSample(int pin, List<String> sample, int hstart, int highpos, int lowpos) {
        xpos = new double[expectedsamplesize];
        ypos = new double[expectedsamplesize];
        insertat = 0;
        this.hstart = hstart;
        this.highpos = highpos;
        this.lowpos = lowpos;
        sample.forEach(segment -> buildSamplesegment(segment));
        GraphicsContext gc = sampledisplaycanvas.getGraphicsContext2D();
        gc.setStroke(RED);
        gc.setLineWidth(2.0);
        gc.strokePolyline(xpos, ypos, insertat);
    }

    private void buildSamplesegment(String samplesegment) {
        for (int cptr = 0; cptr < samplesegment.length(); cptr++) {
            switch (samplesegment.charAt(cptr)) {
                case 'H' -> {
                    insertHigh();
                }
                case 'L' -> {
                    insertLow();
                }
                default ->
                    cptr = decoderle(cptr, samplesegment);
            }
        }
    }

    private int decoderle(int cptr, String s) {
        char c = s.charAt(cptr);
        int count = 0;
        while ('0' <= c && c <= '9') {
            count = count * 10 + (int) (c - '0');
            c = s.charAt(++cptr);
        }
        switch (c) {
            case 'H' ->
                insertHigh(count);
            case 'L' ->
                insertLow(count);
            default ->
                throw new Failure("Badly encoded RLE data: " + c);
        }
        return cptr;
    }

    private void insertHigh() {
        insertHigh(1);
    }

    private void insertHigh(int width) {
        insert(highpos, width);
    }

    private void insertLow() {
        insertLow(1);
    }

    private void insertLow(int width) {
        insert(lowpos, width);
    }

    private void insert(int vpos, int width) {
        xpos[insertat] = hstart;
        ypos[insertat++] = vpos;
        hstart += hscale * width;
        xpos[insertat] = hstart;
        ypos[insertat++] = vpos;
    }
}
