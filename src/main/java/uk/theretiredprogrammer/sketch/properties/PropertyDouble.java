/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.properties;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyDouble extends PropertyItem {

    private SimpleDoubleProperty doubleproperty = new SimpleDoubleProperty();

    public final Double getValue() {
        return doubleproperty.getValue();
    }

    public final void setValue(Double newvalue) {
        doubleproperty.setValue(newvalue);
    }

    public final double get() {
        return doubleproperty.get();
    }

    public final void set(double newdouble) {
        doubleproperty.set(newdouble);
    }

    public SimpleDoubleProperty PropertyDouble() {
        return doubleproperty;
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField doublefield = new TextField(Double.toString(doubleproperty.get()));
        doublefield.setPrefColumnCount(10);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(doubleproperty);
        return doublefield;
    }

    @Override
    public JsonValue toJson() {
        return Json.createValue(get());
    }
}
