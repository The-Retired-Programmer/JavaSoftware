/*
 * Copyright 2020 Richard Linsdale.
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
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;
import javafx.scene.paint.Color;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ColorParser {

    public static Optional<Color> parse(JsonObject jobj, String key) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value.getValueType() == JsonValue.ValueType.STRING) {
            Color color = string2color(((JsonString) value).getString());
            if (color == null) {
                throw new IOException("Malformed Definition file - Colour name expected with " + key);
            }
            return Optional.of(color);
        }
        try {
            if (value.getValueType() == JsonValue.ValueType.ARRAY) {
                JsonArray values = (JsonArray) value;
                if (values.size() == 3 || values.size() == 4) {
                    int r = values.getJsonNumber(0).intValueExact();
                    int g = values.getJsonNumber(1).intValueExact();
                    int b = values.getJsonNumber(2).intValueExact();
                    double a = values.size() == 4
                            ? values.getJsonNumber(3).doubleValue()
                            : 1.0;
                    return Optional.of(Color.rgb(r, g, b, a));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - Colour (RGBA) expected with " + key);
    }

    private static Color string2color(String value) {
        try {
        return Color.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

//        return switch (value.toLowerCase()) {
//            case "black" ->
//                Color.BLACK;
//            case "blue" ->
//                Color.BLUE;
//            case "cyan" ->
//                Color.cyan;
//            case "darkgrey" ->
//                Color.DARK_GRAY;
//            case "grey" ->
//                Color.GRAY;
//            case "green" ->
//                Color.GREEN;
//            case "lightgrey" ->
//                Color.LIGHT_GRAY;
//            case "magenta" ->
//                Color.MAGENTA;
//            case "orange" ->
//                Color.ORANGE;
//            case "pink" ->
//                Color.PINK;
//            case "red" ->
//                Color.RED;
//            case "white" ->
//                Color.WHITE;
//            case "yellow" ->
//                Color.YELLOW;
//            default ->
//                null;
//        };
//    }
//
    public static String color2String(Color color) {
        final Field[] fields = Color.class.getFields(); // only want public
        for (final Field field : fields) {
            if (field.getType() == Color.class) {
                try {
                    final Color clr = (Color) field.get(null);
                    if (color.equals(clr)) {
                        return field.getName();
                    }
                } catch (IllegalAccessException ex) {
                    return "Securty Manager does not allow access to field '"+ field.getName() + "'.";
                }
            }
        }
        return color.toString();
    }
//        if (clr == Color.BLACK) {
//            return "Black";
//        }
//        if (clr == Color.BLUE) {
//            return "Blue";
//        }
//        if (clr == Color.cyan) {
//            return "Cyan";
//        }
//        if (clr == Color.DARK_GRAY) {
//            return "DarkGrey";
//        }
//        if (clr == Color.GRAY) {
//            return "Grey";
//        }
//        if (clr == Color.GREEN) {
//            return "Green";
//        }
//        if (clr == Color.LIGHT_GRAY) {
//            return "LightGrey";
//        }
//        if (clr == Color.MAGENTA) {
//            return "Magenta";
//        }
//        if (clr == Color.ORANGE) {
//            return "Orange";
//        }
//        if (clr == Color.PINK) {
//            return "Pink";
//        }
//        if (clr == Color.RED) {
//            return "Red";
//        }
//        if (clr == Color.WHITE) {
//            return "White";
//        }
//        if (clr == Color.YELLOW) {
//            return "Yellow";
//        }
//        return clr.toString();
//    }
}
