/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.core.ui;

import java.util.function.UnaryOperator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.text.FontWeight.NORMAL;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.display.entity.course.LegEnding;
import uk.theretiredprogrammer.sketch.display.entity.flows.Gradient;

public class FieldBuilder {

    public static TextField getIntegerField(int size, SimpleIntegerProperty property) {
        TextField intfield = new TextField(Integer.toString(property.get()));
        intfield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return intfield;
    }

    public static TextField getDoubleField(int size, SimpleDoubleProperty property) {
        TextField doublefield = new TextField(Double.toString(property.doubleValue()));
        doublefield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return doublefield;
    }

    public static CheckBox getBooleanField(SimpleBooleanProperty property) {
        CheckBox booleanfield = new CheckBox();
        booleanfield.setSelected(property.get());
        booleanfield.selectedProperty().bindBidirectional(property);
        return booleanfield;
    }

    public static TextField getStringField(SimpleStringProperty property) {
        TextField stringfield = new TextField(property.get());
        stringfield.textProperty().bindBidirectional(property);
        return stringfield;
    }

    public static ComboBox getConstrainedStringField(SimpleStringProperty property, ObservableList<String> constraints) {
        ComboBox combofield = new ComboBox(constraints);
        combofield.valueProperty().bindBidirectional(property);
        return combofield;
    }

    public static ColorPicker getColourField(SimpleObjectProperty<Color> property) {
        ColorPicker picker = new ColorPicker();
        picker.setValue(property.get());
        picker.setOnAction(actionEvent -> {
            property.set(picker.getValue());
        });
        return picker;
    }

    public static TextFlow getLocationField(int size, SimpleObjectProperty<Location> property) {
        return new TextFlow(
                createTextFor("["),
                getDoubleField(size, property.get().getXProperty()),
                createTextFor(","),
                getDoubleField(size, property.get().getYProperty()),
                createTextFor("]")
        );
    }

    public static TextFlow getSpeedPolarField(int speedsize, int directionsize, SimpleObjectProperty<SpeedPolar> property) {
        return new TextFlow(
                getDoubleField(speedsize, property.get().getSpeedProperty()),
                createTextFor("@"),
                getDoubleField(directionsize, property.get().getDegreesProperty()),
                createTextFor("˚"));
    }

    public static HBox getAreaField(int size, SimpleObjectProperty<Area> property) {
        PropertyLocation bottomleft = property.get().getBottomLeftProperty();
        return new HBox(
                createTextFor("["),
                getDoubleField(size, bottomleft.get().getXProperty()),
                createTextFor(","),
                getDoubleField(size, bottomleft.get().getYProperty()),
                createTextFor("] "),
                getDoubleField(size, property.get().getWidthProperty()),
                createTextFor("x"),
                getDoubleField(size, property.get().getHeightProperty())
        );
    }

    public static HBox getLegEndingField(SimpleObjectProperty<LegEnding> property,
            ObservableList<String> marknames, ObservableList<String> roundings) {
        return new HBox(getConstrainedStringField(property.get().getMarknameProperty(), marknames),
                getConstrainedStringField(property.get().getRoundingdirectionProperty(), roundings)
        );
    }

    public static HBox getGradientField(int size, SimpleObjectProperty<Gradient> property, ObservableList<String> typeconstraints) {
        HBox hbox = new HBox(getConstrainedStringField(property.get().getTypeProperty(), typeconstraints));
        property.get().getSpeeds().forEach(speedproperty -> hbox.getChildren().add(getDoubleField(size, speedproperty)));
        return hbox;
    }

    static UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)(\\.[0-9]*)?")) {
            return change;
        }
        return null;
    };

    static UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)?")) {
            return change;
        }
        return null;
    };

    static Text createTextFor(String input) {
        Text text = new Text(input);
        text.setFont(Font.font("System", NORMAL, 28));
        return text;
    }
}
