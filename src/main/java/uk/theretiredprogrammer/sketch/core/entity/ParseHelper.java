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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.lang.reflect.Field;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.display.entity.course.LegEnding;
import uk.theretiredprogrammer.sketch.display.entity.flows.Gradient;

public class ParseHelper {

    public static JsonValue integerToJson(Integer value) {
        return Json.createValue(value);
    }

    public static Integer integerParse(JsonValue jvalue) {
        if (jvalue != null & jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            try {
                return ((JsonNumber) jvalue).intValueExact();
            } catch (ArithmeticException ex) {
            }
        }
        throw new ParseFailure("Malformed Definition file - Integer expected");
    }

    public static JsonValue doubleToJson(Double value) {
        return Json.createValue(value);
    }

    public static Double doubleParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) jvalue).doubleValue();
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static JsonValue booleanToJson(Boolean value) {
        return value ? JsonValue.TRUE : JsonValue.FALSE;
    }

    public static Boolean booleanParse(JsonValue jvalue) {
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

    public static JsonValue stringToJson(String string) {
        return Json.createValue(string);
    }

    public static String stringParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) jvalue).getString();
        }
        throw new ParseFailure("Malformed Definition file - String expected");
    }

    public static JsonValue constrainedStringToJson(String string) {
        return stringToJson(string);
    }

    public static String constrainedStringParse(JsonValue jvalue, ObservableList<String> constraints) {
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

    public static JsonValue colourToJson(Color colour) {
        return Json.createValue(color2String(colour));
    }

    public static Color colourParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.STRING) {
            Color color = string2color(((JsonString) jvalue).getString());
            if (color != null) {
                return color;
            }
        }
        throw new ParseFailure("Malformed Definition file - Colour name or hex string expected");
    }

    public static JsonValue degreesToJson(PropertyDegrees value) {
        return Json.createValue(value.get());
    }

    public static PropertyDegrees degreesParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.NUMBER) {
            return new PropertyDegrees(((JsonNumber) jvalue).doubleValue());
        }
        throw new ParseFailure("Malformed Definition file - Decimal expected");
    }

    public static JsonArray locationToJson(Location location) {
        return Json.createArrayBuilder()
                .add(location.getX())
                .add(location.getY())
                .build();
    }

    public static Location locationParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new Location(
                        doubleParse(values.get(0)),
                        doubleParse(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 numbers expected");
    }

    public static JsonArray areaToJson(Area value) {
        return Json.createArrayBuilder()
                .add(value.getBottomleft().getX())
                .add(value.getBottomleft().getY())
                .add(value.getWidth())
                .add(value.getHeight())
                .build();
    }

    public static Area areaParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 4) {
                return new Area(
                        doubleParse(values.get(0)),
                        doubleParse(values.get(1)),
                        doubleParse(values.get(2)),
                        doubleParse(values.get(3))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - Area expects a list of 4 numbers");
    }

    public static JsonArray speedpolarToJson(SpeedPolar value) {
        return Json.createArrayBuilder()
                .add(value.getSpeed())
                .add(value.getDegrees())
                .build();
    }

    public static SpeedPolar speedpolarParse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new SpeedPolar(
                        doubleParse(values.get(0)),
                        degreesParse(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }

    public static JsonArray legEndingToJson(LegEnding value) {
        return Json.createArrayBuilder()
                .add(value.getMarkname())
                .add(value.getRoundingdirection())
                .build();
    }

    public static LegEnding legEndingParse(JsonValue jvalue, ObservableList<String> markconstraints, ObservableList<String> roundingconstraints) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                return new LegEnding(
                        constrainedStringParse(values.get(0), markconstraints),
                        constrainedStringParse(values.get(1), roundingconstraints),
                        markconstraints
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - List of 2 Strings expected");
    }

    public static JsonArray gradientToJson(Gradient value) {
        JsonArrayBuilder jab = Json.createArrayBuilder().add(value.getType());
        value.getSpeeds().forEach(speed -> jab.add(speed.get()));
        return jab.build();
    }

    public static Gradient gradientParse(JsonValue jvalue) {
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
            return new Gradient(newtype, enteredspeeds);
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

    private static String color2String(Color color) {
        final Field[] fields = Color.class.getFields(); // only want public
        for (final Field field : fields) {
            if (field.getType() == Color.class) {
                try {
                    final Color clr = (Color) field.get(null);
                    if (color.equals(clr)) {
                        return field.getName();
                    }
                } catch (IllegalAccessException ex) {
                    return "Securty Manager does not allow access to field '" + field.getName() + "'.";
                }
            }
        }
        return color.toString();
    }
}
