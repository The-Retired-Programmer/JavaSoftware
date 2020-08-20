/*
 * Copyright 2014-2020 Richard Linsdale.
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

import java.io.IOException;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Channel {

    public final static Channel CHANNELOFF = new Channel();

    public static Optional<Channel> parse(JsonObject jobj, String key) throws IOException {
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
                if (values.size() == 2) {
                    return Optional.of(new Channel(
                            values.getJsonNumber(0).intValueExact(),
                            values.getJsonNumber(1).intValueExact()
                    ));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - List of 2 Integers expected with " + key);
    }

    private final double west;
    private final double east;
    private final boolean enabled;

    /**
     * Constructors.
     */
    private Channel() {
        this.west = 0;
        this.east = 0;
        this.enabled = false;
    }

    public Channel(double west, double east) {
        this.west = west;
        this.east = east;
        this.enabled = true;
    }

    public double getWest() {
        return west;
    }

    public double getEast() {
        return east;
    }

    public boolean isInchannel(Location location) {
        if (enabled) {
            double ew = location.getX();
            return ew > west && ew < east;
        }
        return true; // if no channel then OK
    }

    public double getInneroffset(Location mark) {
        double mx = mark.getX();
        return mx < west ? west - mx : (mx > east ? mx - east : 0);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.west) ^ (Double.doubleToLongBits(this.west) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.east) ^ (Double.doubleToLongBits(this.east) >>> 32));
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
        final Channel other = (Channel) obj;
        if (Double.doubleToLongBits(this.west) != Double.doubleToLongBits(other.west)) {
            return false;
        }
        return Double.doubleToLongBits(this.east) == Double.doubleToLongBits(other.east);
    }

    @Override
    public String toString() {
        return "[" + west + "," + east + "]";
    }
}
