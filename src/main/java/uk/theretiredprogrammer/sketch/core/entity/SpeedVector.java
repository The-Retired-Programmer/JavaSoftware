/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class SpeedVector implements ModelProperty<SpeedVector> {

    private static final double KNOTSTOMETRESPERSECOND = (double) 1853 / 3600; // multiply knots to get m/s
    // source NASA - 1 knot = 1.853 km/hour

    public static double convertKnots2MetresPerSecond(double knots) {
        return knots * KNOTSTOMETRESPERSECOND;
    }

    private final SimpleDoubleProperty speedproperty = new SimpleDoubleProperty();
    private final Angle degreesproperty = new Angle();

    public SpeedVector() {
        set(0, 0);
    }

    public SpeedVector(SpeedVector value) {
        set(value);
    }

    public SpeedVector(double speed, Angle degrees) {
        set(speed, degrees);
    }

    public SpeedVector(double speed, double degrees) {
        set(speed, degrees);
    }

    @Override
    public void setOnChange(Runnable onchange) {
        degreesproperty.addListener((o, oldval, newval) -> onchange.run());
        speedproperty.addListener((o, oldval, newval) -> onchange.run());
    }

    public final void set(SpeedVector value) {
        set(value.getSpeed(), value.getDegrees());
    }

    public final void set(double speed, Angle degrees) {
        set(speed, degrees.get());
    }

    public final void set(double speed, double degrees) {
        speedproperty.set(speed);
        degreesproperty.set(degrees);
    }

    public double getSpeed() {
        return speedproperty.get();
    }

    public SimpleDoubleProperty getSpeedProperty() {
        return speedproperty;
    }

    public double getDegrees() {
        return degreesproperty.get();
    }

    public Angle getAngle() {
        return degreesproperty;
    }

    @Override
    public SpeedVector parsevalue(JsonValue jvalue) {
        return FromJson.speedVector(jvalue);
    }

    @Override
    public Node getControl() {
        return getControl(6, 7);
    }

    @Override
    public Node getControl(int size) {
        return getControl(size, size);
    }

    private Node getControl(int speedsize, int directionsize) {
        return UI.control(speedsize, directionsize, this);
    }

    @Override
    public JsonValue toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }

    public SpeedVector plus(SpeedVector other) {
        double speed = speedproperty.get();
        double radians = degreesproperty.getRadians();
        double otherspeed = other.speedproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = speed * Math.cos(radians) + otherspeed * Math.cos(otherradians);
        double y = speed * Math.sin(radians) + otherspeed * Math.sin(otherradians);
        //
        return new SpeedVector(Math.sqrt(x * x + y * y), Math.toDegrees(Math.atan2(y, x)));
    }

    public SpeedVector sub(SpeedVector other) {
        double speed = speedproperty.get();
        double radians = degreesproperty.getRadians();
        double otherspeed = other.speedproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = speed * Math.cos(radians) - otherspeed * Math.cos(otherradians);
        double y = speed * Math.sin(radians) - otherspeed * Math.sin(otherradians);
        //
        return new SpeedVector(Math.sqrt(x * x + y * y), Math.toDegrees(Math.atan2(y, x)));
    }

    public SpeedVector mult(double multiplier) {
        double speed = speedproperty.get();
        return multiplier < 0 ? new SpeedVector(-speed * multiplier, degreesproperty.inverse()) : new SpeedVector(speed * multiplier, degreesproperty);
    }

    public Angle degreesDiff(SpeedVector p) {
        return degreesproperty.degreesDiff(p.getAngle());
    }

    public Angle degreesDiff(Angle p) {
        return degreesproperty.degreesDiff(p);
    }
    
    public Angle degreesDiff(double p) {
        return degreesproperty.degreesDiff(p);
    }

    public double getSpeedMetresPerSecond() {
        return speedproperty.get() * KNOTSTOMETRESPERSECOND;
    }

    private Angle extrapolateAngle(Angle other, double fraction) {
        return new SpeedVector(1, getAngle())
                .mult(1.0 - fraction)
                .plus(new SpeedVector(1, other).mult(fraction))
                .getAngle();
    }

    private double extrapolateSpeed(double other, double fraction) {
        return speedproperty.get() * (1.0 - fraction) + other * fraction;
    }

    private SpeedVector extrapolate(SpeedVector other, double fraction) {
        return new SpeedVector(extrapolateSpeed(other.getSpeed(), fraction),
                extrapolateAngle(other.getAngle(), fraction));
    }

    public SpeedVector extrapolate(SpeedVector nw, SpeedVector ne, SpeedVector se, Location fractions) {
        SpeedVector w = this.extrapolate(nw, fractions.getY());
        SpeedVector e = se.extrapolate(ne, fractions.getY());
        return w.extrapolate(e, fractions.getX());
    }
    
    @Override
    public String toString() {
        return speedproperty.get() + "kts @" + degreesproperty.get()+ "˚";
    }
}
