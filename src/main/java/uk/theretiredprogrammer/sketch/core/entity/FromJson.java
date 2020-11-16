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
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.display.entity.course.Marks;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLegEnding;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyGradient;

public class FromJson {

    public static Integer integerProperty(JsonValue jvalue) {
        if (jvalue != null & jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            try {
                return ((JsonNumber) jvalue).intValueExact();
            } catch (ArithmeticException ex) {
            }
        }
        throw new ParseFailure("Malformed Definition file - Integer expected");
    }

    public static Double doubleProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) jvalue).doubleValue();
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static Boolean booleanProperty(JsonValue jvalue) {
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

    public static String stringProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) jvalue).getString();
        }
        throw new ParseFailure("Malformed Definition file - String expected");
    }

    public static String constrainedStringProperty(JsonValue jvalue, ObservableList<String> constraints) {
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

    public static Color colourProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            Color color = string2color(((JsonString) jvalue).getString());
            if (color != null) {
                return color;
            }
        }
        throw new ParseFailure("Malformed Definition file - Colour name or hex string expected");
    }

    public static PropertyDegrees degreesProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return new PropertyDegrees(((JsonNumber) jvalue).doubleValue());
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static PropertyLocation locationProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new PropertyLocation(
                        doubleProperty(values.get(0)),
                        doubleProperty(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 numbers expected");
    }

    public static PropertyArea areaProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 4) {
                return new PropertyArea(
                        doubleProperty(values.get(0)),
                        doubleProperty(values.get(1)),
                        doubleProperty(values.get(2)),
                        doubleProperty(values.get(3))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - Area expects a list of 4 numbers");
    }

    public static PropertyDistanceVector distanceVectorProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new PropertyDistanceVector(
                        doubleProperty(values.get(0)),
                        degreesProperty(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }
    
    public static PropertySpeedVector speedVectorProperty(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new PropertySpeedVector(
                        doubleProperty(values.get(0)),
                        degreesProperty(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }

    public static PropertyLegEnding legEndingProperty(JsonValue jvalue, Marks marks, ObservableList<String> markconstraints, ObservableList<String> roundingconstraints) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new PropertyLegEnding(
                        constrainedStringProperty(values.get(0), markconstraints),
                        constrainedStringProperty(values.get(1), roundingconstraints),
                        marks,
                        markconstraints
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 Strings expected");
    }

    public static PropertyGradient gradientProperty(JsonValue jvalue) {
        String newtype = "north";
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            int count = -1;
            ObservableList<PropertyDouble> enteredspeeds = new SimpleListProperty<>();
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
                        enteredspeeds.add(new PropertyDouble(((JsonNumber) val).doubleValue()));
                    }
                    default ->
                        throw new ParseFailure("Illegal parameter in gradient definition");
                }
                count++;
            }
            return new PropertyGradient(newtype, enteredspeeds);
        }
        throw new ParseFailure("Illegal number in gradient definition");
    }

    private static Color string2color(String value) {
        try {
            return Color.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
