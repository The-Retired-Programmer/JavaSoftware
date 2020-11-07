/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import javafx.beans.property.SimpleDoubleProperty;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;

public class SpeedPolar extends Polar<SpeedPolar> {

    public final static SpeedPolar FLOWZERO = new SpeedPolar();

    private static final double KNOTSTOMETRESPERSECOND = (double) 1853 / 3600; // multiply knots to get m/s
    // source NASA - 1 knot = 1.853 km/hour

    public static double convertKnots2MetresPerSecond(double knots) {
        return knots * KNOTSTOMETRESPERSECOND;
    }

    private final SimpleDoubleProperty speed = new SimpleDoubleProperty(); // knots

    public SpeedPolar(double speed, PropertyDegrees degrees) {
        super(degrees);
        this.speed.set(speed);
    }

    public SpeedPolar() {
        this(0.0, DEGREES0);
    }

    @Override
    public SpeedPolar create(double speed, PropertyDegrees degrees) {
        return new SpeedPolar(speed, degrees);
    }

    @Override
    double getDimension() {
        return speed.get();
    }

    public double getSpeed() {
        return speed.get();
    }

    public SimpleDoubleProperty getSpeedProperty() {
        return speed;
    }

    public double getSpeedMetresPerSecond() {
        return speed.get() * KNOTSTOMETRESPERSECOND;
    }

    private PropertyDegrees extrapolateAngle(PropertyDegrees other, double fraction) {
        return new SpeedPolar(1, getDegreesProperty())
                .mult(1.0 - fraction)
                .plus(new SpeedPolar(1, other).mult(fraction))
                .getDegreesProperty();
    }

    private double extrapolateSpeed(double other, double fraction) {
        return speed.get() * (1.0 - fraction) + other * fraction;
    }

    private SpeedPolar extrapolate(SpeedPolar other, double fraction) {
        return new SpeedPolar(extrapolateSpeed(other.getSpeed(), fraction),
                extrapolateAngle(other.getDegreesProperty(), fraction));
    }

    public SpeedPolar extrapolate(SpeedPolar nw, SpeedPolar ne, SpeedPolar se, Location fractions) {
        SpeedPolar w = this.extrapolate(nw, fractions.getY());
        SpeedPolar e = se.extrapolate(ne, fractions.getY());
        return w.extrapolate(e, fractions.getX());
    }

    public static PropertyDegrees meanAngle(SpeedPolar[][] array) {
        return Polar.meanAngle(array);
    }
}
