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
import jakarta.json.JsonValue;
import java.lang.reflect.Field;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.display.entity.course.LegEnding;
import uk.theretiredprogrammer.sketch.display.entity.flows.Gradient;

public class ToJson {

    public static JsonValue serialise(Integer value) {
        return Json.createValue(value);
    }

    public static JsonValue serialise(Double value) {
        return Json.createValue(value);
    }

    public static JsonValue serialise(Boolean value) {
        return value ? JsonValue.TRUE : JsonValue.FALSE;
    }

    public static JsonValue serialise(String string) {
        return Json.createValue(string);
    }

    public static JsonValue serialise(Color colour) {
        return Json.createValue(color2String(colour));
    }

    public static JsonValue serialise(PropertyDegrees value) {
        return Json.createValue(value.get());
    }

    public static JsonArray serialise(PropertyLocation location) {
        return Json.createArrayBuilder()
                .add(location.getX())
                .add(location.getY())
                .build();
    }

    public static JsonArray serialise(PropertyArea value) {
        return Json.createArrayBuilder()
                .add(value.getLocationProperty().getX())
                .add(value.getLocationProperty().getY())
                .add(value.getWidth())
                .add(value.getHeight())
                .build();
    }

    public static JsonArray serialise(SpeedPolar value) {
        return Json.createArrayBuilder()
                .add(value.getSpeed())
                .add(value.getDegrees())
                .build();
    }

    public static JsonArray serialise(LegEnding value) {
        return Json.createArrayBuilder()
                .add(value.getMarkname())
                .add(value.getRoundingdirection())
                .build();
    }

    public static JsonArray serialise(Gradient value) {
        JsonArrayBuilder jab = Json.createArrayBuilder().add(value.getType());
        value.getSpeeds().forEach(speed -> jab.add(speed.get()));
        return jab.build();
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
