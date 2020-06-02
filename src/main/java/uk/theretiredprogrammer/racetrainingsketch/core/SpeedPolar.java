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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.io.IOException;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * A Polar location is the relative value (distance, flow etc from a logical
 * origin with direction
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SpeedPolar extends Polar<SpeedPolar> {

    public static Optional<SpeedPolar> parse(JsonObject jobj, String key) throws IOException {
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
                    return Optional.of(new SpeedPolar(
                            values.getJsonNumber(1).doubleValue(),
                            new Angle(values.getJsonNumber(0).intValueExact())
                    ));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - integer and decimal expected with " + key);
    }

    private static final double KNOTSTOMETRESPERSECOND = (double) 1853 / 3600; // multiply knots to get m/s
    // source NASA - 1 knot = 1.853 km/hour

    private final double speed; // knots

    public SpeedPolar(double speed, Angle angle) {
        super(angle);
        this.speed = speed;
    }

    @Override
    public SpeedPolar create(double speed, Angle angle) {
        return new SpeedPolar(speed, angle);
    }

    @Override
    double getDimension() {
        return speed;
    }

    public double getSpeed() {
        return speed;
    }

    public double getSpeedMetresPerSecond() {
        return speed * KNOTSTOMETRESPERSECOND;
    }

    public static double convertKnots2MetresPerSecond(double knots) {
        return knots * KNOTSTOMETRESPERSECOND;
    }

    public SpeedPolar extrapolate(SpeedPolar other, double fraction) {
        SpeedPolar thisweightedpolar = mult(fraction);
        SpeedPolar otherweightedpolar = other.mult(1.0 - fraction);
        return thisweightedpolar.add(otherweightedpolar);
    }
}
