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

import java.awt.Color;
import java.io.IOException;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

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
                    int a = values.size() == 4
                            ? values.getJsonNumber(3).intValueExact()
                            : 255;
                    return Optional.of(new Color(r, g, b, a));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - Colour (RGBA) expected with " + key);
    }

    private static Color string2color(String value) throws IOException {
        switch (value.toLowerCase()) {
            case "black":
                return Color.BLACK;
            case "blue":
                return Color.BLUE;
            case "cyan":
                return Color.cyan;
            case "darkgrey":
                return Color.DARK_GRAY;
            case "grey":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "lightgrey":
                return Color.LIGHT_GRAY;
            case "magenta":
                return Color.MAGENTA;
            case "orange":
                return Color.ORANGE;
            case "pink":
                return Color.PINK;
            case "red":
                return Color.RED;
            case "white":
                return Color.WHITE;
            case "yellow":
                return Color.YELLOW;
            default:
                return null;
        }
    }
}
