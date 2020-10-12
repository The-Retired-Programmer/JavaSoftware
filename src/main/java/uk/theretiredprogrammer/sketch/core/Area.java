/*
 * Copyright 2020 Richard Linsdale
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Area {

    public static final Area AREAZERO = new Area(new Location(0, 0), 0, 0);

    public static Optional<Area> parse(JsonObject jobj, String key) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            if (value.getValueType() == JsonValue.ValueType.ARRAY) {
                JsonArray values = (JsonArray) value;
                if (values.size() == 4) {
                    return Optional.of(new Area(
                            values.getJsonNumber(0).doubleValue(),
                            values.getJsonNumber(1).doubleValue(),
                            values.getJsonNumber(2).doubleValue(),
                            values.getJsonNumber(3).doubleValue()
                    ));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - List of 4 numbers expected with " + key);
    }

    private final Location bottomleft;
    private final double width;
    private final double height;

    public Area(Location bottomleft, double width, double height) {
        this.bottomleft = bottomleft;
        this.width = width;
        this.height = height;
    }

    public Area(double left, double bottom, double width, double height) {
        this(new Location(left, bottom), width, height);
    }

    public Location getBottomleft() {
        return bottomleft;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isWithinArea(Location location) {
        return location.getX() >= bottomleft.getX() && location.getX() <= bottomleft.getX() + width
                && location.getY() >= bottomleft.getY() && location.getY() <= bottomleft.getY() + height;
    }

    public JsonArray toJson() {
        return Json.createArrayBuilder().add(bottomleft.getX())
                .add(bottomleft.getY()).add(width).add(height).build();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + bottomleft.hashCode();
        hash = 67 * hash + (int) (Double.doubleToLongBits(width) ^ (Double.doubleToLongBits(width) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(height) ^ (Double.doubleToLongBits(height) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Area other = (Area) obj;
        if (!this.bottomleft.equals(other.bottomleft)) {
            return false;
        }
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) {
            return false;
        }
        return Double.doubleToLongBits(this.height) == Double.doubleToLongBits(other.height);
    }
}
