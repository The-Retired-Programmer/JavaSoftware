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
package uk.theretiredprogrammer.sketch.properties.entity;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonValue;
import java.io.IOException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.display.control.Controller;

/**
 *
 * @author richard
 */
public class PropertyInteger extends PropertyElement<Integer> {

    private final SimpleIntegerProperty integerproperty;

    public PropertyInteger(int defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyInteger(String key, int defaultvalue) {
        setKey(key);
        integerproperty = new SimpleIntegerProperty(defaultvalue);
    }

    @Override
    public final Integer get() {
        return integerproperty.get();
    }

    @Override
    public final void set(Integer newint) {
        integerproperty.set(newint);
    }

    public SimpleIntegerProperty propertyInteger() {
        return integerproperty;
    }

    @Override
    public Integer parsevalue(JsonValue value) throws IOException {
        if (value != null & value.getValueType() == JsonValue.ValueType.NUMBER) {
            try {
                return ((JsonNumber) value).intValueExact();
            } catch (ArithmeticException ex) {
            }
        }
        throw new IOException("Malformed Definition file - Integer expected");
    }

    @Override
    public JsonValue toJson() {
        return Json.createValue(get());
    }

    @Override
    public Node getField(Controller controller) {
        return getField(controller, 5);
    }

    @Override
    public Node getField(Controller controller, int size) {
        TextField intfield = new TextField(Integer.toString(integerproperty.get()));
        intfield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(integerproperty);
        return intfield;
    }

    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }

}
