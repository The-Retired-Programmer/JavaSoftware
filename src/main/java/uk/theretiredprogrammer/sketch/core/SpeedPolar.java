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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Optional;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;

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
                            new Angle(values.getJsonNumber(0).doubleValue())
                    ));
                }
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - decimal and decimal expected with " + key);
    }

    private static final double KNOTSTOMETRESPERSECOND = (double) 1853 / 3600; // multiply knots to get m/s
    // source NASA - 1 knot = 1.853 km/hour

    private final double speed; // knots

    public SpeedPolar(double speed, Angle angle) {
        super(angle);
        this.speed = speed;
    }
    
    public SpeedPolar() {
        super(ANGLE0);
        this.speed = 0.0;
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

    private Angle extrapolateAngle(Angle other, double fraction) {
        return new SpeedPolar(1,this.getAngle()).mult(1.0 - fraction)
                .add(new SpeedPolar(1,other).mult(fraction))
                .getAngle();
    }
    
    private double extrapolateSpeed(double other, double fraction) {
        return speed*(1.0 - fraction)+other*fraction;
    }
    
    private SpeedPolar extrapolate(SpeedPolar other, double fraction) {
        return new SpeedPolar(extrapolateSpeed(other.getSpeed(), fraction),
                extrapolateAngle(other.getAngle(), fraction));
    } 
    
    public SpeedPolar extrapolate(SpeedPolar nw, SpeedPolar ne, SpeedPolar se, Location fractions) {
        SpeedPolar w = this.extrapolate(nw, fractions.getY());
        SpeedPolar e = se.extrapolate(ne, fractions.getY());
        return w.extrapolate(e, fractions.getX());
    }
    
    public static Angle meanAngle(SpeedPolar[][] array) {
        return Polar.meanAngle(array);
    }
    
    public JsonArray toJson() {
        return Json.createArrayBuilder().add(speed).add(getAngle().getDegrees()).build();
    }
}
