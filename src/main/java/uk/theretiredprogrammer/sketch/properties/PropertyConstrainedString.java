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

import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.function.Supplier;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import uk.theretiredprogrammer.sketch.controller.Controller;
import uk.theretiredprogrammer.sketch.core.IllegalStateFailure;

/**
 *
 * @author richard
 */
public class PropertyConstrainedString extends PropertyElement<String> {

    private final PropertyString constrainedproperty;
    private final ObservableList<String> constraints;
    private final Supplier<ObservableList<String>> constraintslookup;

    public PropertyConstrainedString(String key, ObservableList<String> constraints) {
        setKey(key);
        constrainedproperty = new PropertyString(null);
        this.constraints = constraints;
        constraintslookup = () -> constraints;
    }

    public PropertyConstrainedString(ObservableList<String> constraints) {
        this(null, constraints);
    }

    public PropertyConstrainedString(ObservableList<String> constraints, String defaultvalue) {
        this(null, constraints, defaultvalue);
    }

    public PropertyConstrainedString(String key, ObservableList<String> constraints, String defaultvalue) {
        setKey(key);
        this.constraints = constraints;
        constraintslookup = () -> constraints;
        if (defaultvalue != null && (!constraints.contains(defaultvalue))) {
            throw new IllegalStateFailure("Bad default value - not in constraints list");
        }
        constrainedproperty = new PropertyString(defaultvalue);
    }

    public PropertyConstrainedString(String key, Supplier<ObservableList<String>> constraintslookup) {
        setKey(key);
        constrainedproperty = new PropertyString(null);
        this.constraints = null;
        this.constraintslookup = constraintslookup;
    }

    public PropertyConstrainedString(Supplier<ObservableList<String>> constraintslookup) {
        this(null, constraintslookup);
    }

    public PropertyConstrainedString(Supplier<ObservableList<String>> constraintslookup, String defaultvalue) {
        this(null, constraintslookup, defaultvalue);
    }

    public PropertyConstrainedString(String key, Supplier<ObservableList<String>> constraintslookup, String defaultvalue) {
        setKey(key);
        this.constraints = null;
        this.constraintslookup = constraintslookup;
        if (defaultvalue != null && (!constraints.contains(defaultvalue))) {
            throw new IllegalStateFailure("Bad default value - not in constraints list");
        }
        constrainedproperty = new PropertyString(defaultvalue);
    }

    @Override
    public final String get() {
        return constrainedproperty.get();
    }

    @Override
    public final void set(String newvalue) throws IOException {
            if (constraintslookup.get().stream().anyMatch(v -> newvalue.equals(v))) {
                constrainedproperty.set(newvalue);
            } else {
                throw new IOException("Constrained String - value not in constrained set");
            }
    }

    @Override
    public String parsevalue(JsonValue value) throws IOException {
        if (value != null && value.getValueType() == JsonValue.ValueType.STRING) {
            String val = ((JsonString) value).getString();
            if (constraintslookup.get().stream().anyMatch(v -> val.equals(v))) {
                return val;
            }
        }
        throw new IOException("Malformed Definition file - value not in constrained set");
    }

    @Override
    public JsonValue toJson() {
        return constrainedproperty.toJson();
    }

    @Override
    public ComboBox getField(Controller controller) {
        return getField(controller, 0);
    }

    @Override
    public ComboBox getField(Controller controller, int size) {
        ComboBox combofield = new ComboBox(constraints);
        combofield.valueProperty().bindBidirectional(constrainedproperty.propertyString());
        return combofield;
    }

    @Override
    public final void parse(JsonValue jvalue) throws IOException {
        constrainedproperty.set(parsevalue(jvalue));
    }
}
