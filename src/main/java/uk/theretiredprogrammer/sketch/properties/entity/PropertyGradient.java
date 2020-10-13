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
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import uk.theretiredprogrammer.sketch.core.entity.Gradient;
import uk.theretiredprogrammer.sketch.display.control.Controller;

/**
 *
 * @author richard
 */
public class PropertyGradient extends PropertyElement<Gradient> {

    private static final ObservableList<String> types = FXCollections.observableArrayList();

    static {
        types.addAll("north", "south", "east", "west");
    }

    private final PropertyConstrainedString typeproperty;
    private ObservableList<PropertyDouble> speedsproperty;

    public PropertyGradient(Gradient defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyGradient(String key, Gradient defaultvalue) {
        setKey(key);
        typeproperty = new PropertyConstrainedString(types, defaultvalue == null ? null : defaultvalue.getType());
        speedsproperty = defaultvalue == null ? FXCollections.observableArrayList() : defaultvalue.getSpeeds();
    }

    @Override
    public final Gradient get() {
        String type = typeproperty.get();
        return type == null ? null : new Gradient(type, speedsproperty);
    }

    @Override
    public final void set(Gradient newgradient) throws IOException {
        typeproperty.setValue(newgradient == null ? null : newgradient.getType());
        speedsproperty = newgradient == null ? FXCollections.observableArrayList() : newgradient.getSpeeds();
    }

    @Override
    public Gradient parsevalue(JsonValue value) throws IOException {
        String newtype = "north";
        if (value != null && value.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) value;
            int count = -1;
            ObservableList<PropertyDouble> enteredspeeds = new SimpleListProperty<>();
            for (JsonValue val : values) {
                switch (val.getValueType()) {
                    case STRING -> {
                        if (count >= 0) {
                            throw new IOException("Illegal parameter in gradient definition");
                        }
                        newtype = ((JsonString) val).getString();
                    }
                    case NUMBER -> {
                        if (count < 0) {
                            throw new IOException("Illegal parameter in gradient definition");
                        }
                        enteredspeeds.add(new PropertyDouble(((JsonNumber) val).doubleValue()));
                    }
                    default ->
                        throw new IOException("Illegal parameter in gradient definition");
                }
                count++;
            }
            return new Gradient(newtype, enteredspeeds);
        }
        throw new IOException("Illegal number in gradient definition");
    }

    @Override
    public JsonValue toJson() {
        String type = typeproperty.get();
        if (type == null) {
            return JsonValue.NULL;
        } else {
            JsonArrayBuilder jab = Json.createArrayBuilder()
                    .add(typeproperty.get());
            speedsproperty.forEach(speed -> jab.add(speed.get()));
            return jab.build();
        }
    }

    @Override
    public Node getField(Controller controller) {
        HBox hbox = new HBox(typeproperty.getField(controller));
        speedsproperty.forEach(speed -> hbox.getChildren().add(speed.getField(controller)));
        return hbox;
    }

    @Override
    public Node getField(Controller controller, int size) {
        HBox hbox = new HBox(typeproperty.getField(controller));
        speedsproperty.forEach(speed -> hbox.getChildren().add(speed.getField(controller)));
        return hbox;
    }

    @Override
    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }
}
