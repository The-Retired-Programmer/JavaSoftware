/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.core;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.NUMBER;
import static jakarta.json.JsonValue.ValueType.STRING;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE180;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90MINUS;
import uk.theretiredprogrammer.sketch.properties.PropertyDouble;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Gradient {

    public static Optional<Gradient> parse(JsonObject jobj, String key) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
        String newtype = "north";
        try {
            if (value.getValueType() == JsonValue.ValueType.ARRAY) {
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
                return Optional.of(new Gradient(newtype, enteredspeeds));
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Illegal number in gradient definition");
    }

    private final String type;
    private final ObservableList<PropertyDouble> speeds;

    public Gradient() {
        this.type = "north";
        this.speeds = new SimpleListProperty<>();
    }

    public Gradient(String type, ObservableList<PropertyDouble> speeds) {
        this.type = type;
        this.speeds = speeds;
    }

    public ObservableList<PropertyDouble> getSpeeds() {
        return speeds;
    }

    public String getType() {
        return type;
    }

    public ObservableList<String> getTypes() {
        ObservableList<String> types = new SimpleListProperty<>();
        types.addAll("north", "south", "east", "west");
        return types;
    }

    public SpeedPolar getFlow(Location pos) {
        return null;  //TODO - getFlow not yet implemented - return null
    }

    public Angle getMeanFlowDirection() throws IOException {
        switch (type) {
            case "north" -> {
                return ANGLE0;
            }
            case "south" -> {
                return ANGLE180;
            }
            case "east" -> {
                return ANGLE90;
            }
            case "west" -> {
                return ANGLE90MINUS;
            }
            default ->
                throw new IOException("Illegal gradient direction");
        }
    }
}
