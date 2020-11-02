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
package uk.theretiredprogrammer.sketch.core.entity;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

/**
 *
 * @author richard
 */
public class PropertyDouble extends PropertyElement<Double> {

    private final SimpleDoubleProperty doubleproperty;

    public PropertyDouble(double defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyDouble(String key, Double defaultvalue) {
        setKey(key);
        doubleproperty = new SimpleDoubleProperty(defaultvalue);
    }
    
    public void setOnChange(Runnable onchange) {
        //setOnChange((c) -> onchange.run());
    }

    public void setOnChange(ChangeListener cl) {
        doubleproperty.addListener(cl);
    }

    @Override
    public final Double get() {
        return doubleproperty.get();
    }

    @Override
    public final void set(Double newdouble) {
        doubleproperty.set(newdouble);
    }

    public SimpleDoubleProperty propertyDouble() {
        return doubleproperty;
    }

    @Override
    public Double parsevalue(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) jvalue).doubleValue();
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    @Override
    public JsonValue toJson() {
        return Json.createValue(get());
    }

    @Override
    public TextField getField() {
        return getField(10);
    }

    @Override
    public TextField getField(int size) {
        TextField doublefield = new TextField(Double.toString(doubleproperty.get()));
        doublefield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(doubleproperty);
        return doublefield;
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
