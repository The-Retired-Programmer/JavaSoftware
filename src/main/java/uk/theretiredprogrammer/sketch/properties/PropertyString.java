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
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import uk.theretiredprogrammer.sketch.controller.Controller;

/**
 *
 * @author richard
 */
public class PropertyString extends PropertyElement<String> {

    private final SimpleStringProperty stringproperty;

    public PropertyString(String defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyString(String key, String defaultvalue) {
        setKey(key);
        stringproperty = new SimpleStringProperty(defaultvalue);
    }

    @Override
    public final String get() {
        return stringproperty.get();
    }

    @Override
    public final void set(String newvalue) {
        stringproperty.set(newvalue);
    }

    public SimpleStringProperty propertyString() {
        return stringproperty;
    }

    @Override
    public final String parsevalue(JsonValue value) throws IOException {
        if (value != null && value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        }
        throw new IOException("Malformed Definition file - String expected");
    }

    @Override
    public JsonValue toJson() {
        return Json.createValue(stringproperty.get());
    }

    @Override
    public Node getField(Controller controller) {
        return getField(controller, 0);
    }

    @Override
    public Node getField(Controller controller, int size) {
        TextField stringfield = new TextField(stringproperty.get());
        stringfield.textProperty().bindBidirectional(stringproperty);
        return stringfield;
    }

    @Override
    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }
}
