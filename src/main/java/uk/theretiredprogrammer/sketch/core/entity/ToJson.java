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
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLegEnding;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyGradient;

public class ToJson {

    public static JsonValue serialise(PropertyInteger property) {
        return Json.createValue(property.get());
    }

    public static JsonValue serialise(PropertyDouble property) {
        return Json.createValue(property.get());
    }

    public static JsonValue serialise(PropertyBoolean property) {
        return property.get() ? JsonValue.TRUE : JsonValue.FALSE;
    }

    public static JsonValue serialise(PropertyString property) {
        return Json.createValue(property.get());
    }
    
    public static JsonValue serialise(PropertyConstrainedString property) {
        return Json.createValue(property.get());
    }

    public static JsonValue serialise(PropertyColour property) {
        return Json.createValue(color2String(property.get()));
    }

    public static JsonValue serialise(PropertyDegrees property) {
        return Json.createValue(property.get());
    }

    public static JsonArray serialise(PropertyLocation property) {
        return Json.createArrayBuilder()
                .add(property.getX())
                .add(property.getY())
                .build();
    }

    public static JsonArray serialise(PropertyArea property) {
        return Json.createArrayBuilder()
                .add(property.getLocationProperty().getX())
                .add(property.getLocationProperty().getY())
                .add(property.getWidth())
                .add(property.getHeight())
                .build();
    }
    
    public static JsonArray serialise(PropertyDistanceVector property) {
        return Json.createArrayBuilder()
                .add(property.getDistance())
                .add(property.getDegrees())
                .build();
    }
    
    public static JsonArray serialise(PropertySpeedVector property) {
        return Json.createArrayBuilder()
                .add(property.getSpeed())
                .add(property.getDegrees())
                .build();
    }

    public static JsonArray serialise(PropertyLegEnding property) {
        return Json.createArrayBuilder()
                .add(property.getMarkname())
                .add(property.getRoundingdirection())
                .build();
    }

    public static JsonArray serialise(PropertyGradient property) {
        JsonArrayBuilder jab = Json.createArrayBuilder().add(property.getType());
        property.getSpeeds().forEach(speed -> jab.add(speed.get()));
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
