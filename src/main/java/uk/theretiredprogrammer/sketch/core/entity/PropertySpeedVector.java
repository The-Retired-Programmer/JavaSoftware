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

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class PropertySpeedVector implements ModelProperty<PropertySpeedVector> {
    
    public static PropertyDegrees meanAngle(PropertySpeedVector[][] array) {
        double x = 0;
        double y = 0;
        for (PropertySpeedVector[] column : array) {
            for (PropertySpeedVector cell : column) {
                double r = cell.degreesproperty.getRadians();
                x += Math.sin(r);
                y += Math.cos(r);
            }
        }
        return new PropertyDegrees(Math.toDegrees(Math.atan2(x, y)));
    }
    
    private static final double KNOTSTOMETRESPERSECOND = (double) 1853 / 3600; // multiply knots to get m/s
    // source NASA - 1 knot = 1.853 km/hour

    public static double convertKnots2MetresPerSecond(double knots) {
        return knots * KNOTSTOMETRESPERSECOND;
    }

    private final SimpleDoubleProperty speedproperty = new SimpleDoubleProperty();
    private final PropertyDegrees degreesproperty = new PropertyDegrees();

    public PropertySpeedVector() {
        set(0,0);
    }
    
    public PropertySpeedVector(PropertySpeedVector value) {
        set(value);
    }

    public PropertySpeedVector(double speed, PropertyDegrees degrees) {
        set(speed, degrees);
    }

    public PropertySpeedVector(double speed, double degrees) {
        set(speed, degrees);
    }

    @Override
    public void setOnChange(Runnable onchange) {
        //
    }

    public final void set(PropertySpeedVector value) {
        set(value.getSpeed(), value.getDegrees());
    }

    public final void set(double speed, PropertyDegrees degrees) {
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

    public PropertyDegrees getDegreesProperty() {
        return degreesproperty;
    }

    @Override
    public PropertySpeedVector parsevalue(JsonValue jvalue) {
        return FromJson.speedVectorProperty(jvalue);
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

    //

    public PropertySpeedVector plus(PropertySpeedVector other) {
        double speed = speedproperty.get();
        double radians = degreesproperty.getRadians();
        double otherspeed = other.speedproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = speed * Math.sin(radians) + otherspeed * Math.sin(otherradians);
        double y = speed * Math.cos(radians) + otherspeed * Math.cos(otherradians);
        //
        return new PropertySpeedVector(Math.sqrt(x * x + y * y), new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public PropertySpeedVector sub(PropertySpeedVector other) {
        double speed = speedproperty.get();
        double radians = degreesproperty.getRadians();
        double otherspeed = other.speedproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = speed * Math.sin(radians) - otherspeed * Math.sin(otherradians);
        double y = speed * Math.cos(radians) - otherspeed * Math.cos(otherradians);
        //
        return new PropertySpeedVector(Math.sqrt(x * x + y * y), new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public PropertySpeedVector mult(double multiplier) {
        double speed = speedproperty.get();
        return multiplier < 0 ? new PropertySpeedVector(-speed * multiplier, degreesproperty.inverse()) : new PropertySpeedVector(speed * multiplier, degreesproperty);
    }

    public PropertyDegrees degreesDiff(PropertySpeedVector p) {
        return degreesproperty.degreesDiff(p.getDegreesProperty());
    }
    
    public PropertyDegrees degreesDiff(PropertyDegrees p) {
        return degreesproperty.degreesDiff(p);
    }
    
    public double getSpeedMetresPerSecond() {
        return speedproperty.get() * KNOTSTOMETRESPERSECOND;
    }

    private PropertyDegrees extrapolateAngle(PropertyDegrees other, double fraction) {
        return new PropertySpeedVector(1, getDegreesProperty())
                .mult(1.0 - fraction)
                .plus(new PropertySpeedVector(1, other).mult(fraction))
                .getDegreesProperty();
    }

    private double extrapolateSpeed(double other, double fraction) {
        return speedproperty.get() * (1.0 - fraction) + other * fraction;
    }

    private PropertySpeedVector extrapolate(PropertySpeedVector other, double fraction) {
        return new PropertySpeedVector(extrapolateSpeed(other.getSpeed(), fraction),
                extrapolateAngle(other.getDegreesProperty(), fraction));
    }

    public PropertySpeedVector extrapolate(PropertySpeedVector nw, PropertySpeedVector ne, PropertySpeedVector se, PropertyLocation fractions) {
        PropertySpeedVector w = this.extrapolate(nw, fractions.getY());
        PropertySpeedVector e = se.extrapolate(ne, fractions.getY());
        return w.extrapolate(e, fractions.getX());
    }
}
