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
package uk.theretiredprogrammer.sketch.core.entity;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.display.entity.course.LegValues;
import uk.theretiredprogrammer.sketch.display.entity.flows.Gradient;

public class FromJson {

    public static Integer intgr(JsonValue jvalue) {
        if (jvalue != null & jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            try {
                return ((JsonNumber) jvalue).intValueExact();
            } catch (ArithmeticException ex) {
            }
        }
        throw new ParseFailure("Malformed Definition file - Integer expected");
    }

    public static Double dble(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) jvalue).doubleValue();
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static Boolean booln(JsonValue jvalue) {
        if (jvalue != null) {
            switch (jvalue.getValueType()) {
                case TRUE -> {
                    return true;
                }
                case FALSE -> {
                    return false;
                }
            }
        }
        throw new ParseFailure("Malformed Definition file - Boolean expected");
    }

    public static String strg(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) jvalue).getString();
        }
        throw new ParseFailure("Malformed Definition file - String expected");
    }

    public static String constrainedString(JsonValue jvalue, ObservableList<String> constraints) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            String val = ((JsonString) jvalue).getString();
            for (var v : constraints) {
                if (val.equals(v)) {
                    return val;
                }
            }
        }
        throw new ParseFailure("Malformed Definition file - value not in constrained set");
    }

    public static Color colour(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            try {
                return Color.valueOf(((JsonString) jvalue).getString().toLowerCase());
            } catch (IllegalArgumentException ex) {
            }
        }
        throw new ParseFailure("Malformed Definition file - Colour name or hex string expected");
    }

    public static Angle angle(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return new Angle(((JsonNumber) jvalue).doubleValue());
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static Location location(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new Location(dble(values.get(0)),dble(values.get(1)));
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 numbers expected");
    }

    public static Area area(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 4) {
                return new Area(
                        dble(values.get(0)),
                        dble(values.get(1)),
                        dble(values.get(2)),
                        dble(values.get(3))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - Area expects a list of 4 numbers");
    }

    public static DistanceVector distanceVector(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new DistanceVector(dble(values.get(0)),angle(values.get(1)));
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }

    public static SpeedVector speedVector(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new SpeedVector(
                        dble(values.get(0)),
                        angle(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }

    public static LegValues legvalues(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new LegValues(strg(values.get(0)),strg(values.get(1)).toLowerCase());
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 Strings expected");
    }

    public static Gradient gradient(JsonValue jvalue) {
        String newtype = "north";
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            int count = -1;
            ObservableList<Dble> enteredspeeds = new SimpleListProperty<>();
            for (JsonValue val : values) {
                switch (val.getValueType()) {
                    case STRING -> {
                        if (count >= 0) {
                            throw new ParseFailure("Illegal parameter in gradient definition");
                        }
                        newtype = ((JsonString) val).getString();
                    }
                    case NUMBER -> {
                        if (count < 0) {
                            throw new ParseFailure("Illegal parameter in gradient definition");
                        }
                        enteredspeeds.add(new Dble(((JsonNumber) val).doubleValue()));
                    }
                    default ->
                        throw new ParseFailure("Illegal parameter in gradient definition");
                }
                count++;
            }
            return new Gradient(newtype, enteredspeeds);
        }
        throw new ParseFailure("Illegal number in gradient definition");
    }
}
