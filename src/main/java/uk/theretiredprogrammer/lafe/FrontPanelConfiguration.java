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
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.lafe.ProbeConfiguration.HzUnits;
import uk.theretiredprogrammer.lafe.ProbeConfiguration.SampleEndMode;
import uk.theretiredprogrammer.lafe.ProbeConfiguration.Trigger;

public class FrontPanelConfiguration extends HBox {

    public FrontPanelConfiguration(ProbeConfiguration config) {
        super(10);
        this.getChildren().addAll(
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

//    private TextField doubleField(DoubleProperty value) {
//        TextField dblefield = new TextField(Double.toString(value.doubleValue()));
//        dblefield.setPrefColumnCount(10);
//        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
//        dblefield.setTextFormatter(textformatter);
//        textformatter.valueProperty().bindBidirectional(value);
//        return dblefield;
//    }
//
//    static UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
//        if (change.getControlNewText().matches("-?([0-9,]*)(\\.[0-9]*)?")) {
//            return change;
//        }
//        return null;
//    };
    private ComboBox<HzUnits> hzunitsSelectionField(ObjectProperty<HzUnits> value) {
        ComboBox<HzUnits> cbox = new ComboBox<>();
        cbox.getItems().addAll(HzUnits.values());
        cbox.valueProperty().bindBidirectional(value);
        return cbox;
    }

    private ComboBox<Trigger> triggerSelectionField(ObjectProperty<Trigger> value) {
        ComboBox<Trigger> cbox = new ComboBox<>();
        cbox.getItems().addAll(Trigger.values());
        cbox.valueProperty().bindBidirectional(value);
        return cbox;
    }

    private ComboBox<SampleEndMode> sampleendmodeSelectionField(ObjectProperty<SampleEndMode> value) {
        ComboBox<SampleEndMode> cbox = new ComboBox<>();
        cbox.getItems().addAll(SampleEndMode.values());
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
