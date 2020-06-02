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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.io.IOException;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE180;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90MINUS;

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
                double[] enteredspeeds = new double[values.size() - 1];
                for (JsonValue val : values) {
                    switch (val.getValueType()) {
                        case STRING:
                            if (count >= 0) {
                                throw new IOException("Illegal parameter in gradient definition");
                            }
                            newtype = ((JsonString) val).getString();
                            break;
                        case NUMBER:
                            if (count < 0) {
                                throw new IOException("Illegal parameter in gradient definition");
                            }
                            enteredspeeds[count] = ((JsonNumber) val).intValueExact();
                            break;
                        default:
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
    private final double[] speeds;

    public Gradient() {
        this.type = "north";
        this.speeds = new double[]{};
    }

    public Gradient(String type, double... speeds) {
        this.type = type;
        this.speeds = speeds;
    }

    public SpeedPolar getFlow(Location pos) {
        return null;  //TODO - getFlow not yet implemented - return null
    }

    public Angle getMeanFlowDirection() throws IOException {
        switch (type) {
            case "north":
                return ANGLE0;
            case "south":
                return ANGLE180;
            case "east":
                return ANGLE90;
            case "west":
                return ANGLE90MINUS;
            default:
                throw new IOException("Illegal gradient direction");
        }
    }
}
