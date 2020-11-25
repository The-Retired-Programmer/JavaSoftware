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

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class DistanceVector implements ModelProperty<DistanceVector> {

    private final SimpleDoubleProperty distanceproperty = new SimpleDoubleProperty();
    private final Angle degreesproperty = new Angle();

    public DistanceVector() {
        set(0, 0);
    }

    public DistanceVector(DistanceVector value) {
        set(value);
    }

    public DistanceVector(double distance, Angle degrees) {
        set(distance, degrees);
    }

    public DistanceVector(double distance, double degrees) {
        set(distance, degrees);
    }

    public DistanceVector(Location origin, Location pos) {
        double deltax = (pos.getX() - origin.getX());
        double deltay = (pos.getY() - origin.getY());
        set(
                Math.sqrt(deltax * deltax + deltay * deltay),
                Math.toDegrees(Math.atan2(pos.getX() - origin.getX(), pos.getY() - origin.getY()))
        );
    }

    @Override
    public void setOnChange(Runnable onchange) {
        distanceproperty.addListener((o, oldval, newval) -> onchange.run());
        degreesproperty.addListener((o, oldval, newval) -> onchange.run());
    }

    public final void set(DistanceVector value) {
        set(value.getDistance(), value.getDegrees());
    }

    public final void set(double distance, Angle degrees) {
        set(distance, degrees.get());
    }

    public final void set(double distance, double degrees) {
        distanceproperty.set(distance);
        degreesproperty.set(degrees);
    }

    public double getDistance() {
        return distanceproperty.get();
    }

    public SimpleDoubleProperty getDistanceProperty() {
        return distanceproperty;
    }

    public double getDegrees() {
        return degreesproperty.get();
    }

    public Angle getAngle() {
        return degreesproperty;
    }

    @Override
    public DistanceVector parsevalue(JsonValue jvalue) {
        return FromJson.distanceVectorProperty(jvalue);
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

    public Location toLocation(Location origin) {
        double distance = distanceproperty.get();
        double radians = getAngle().getRadians();
        return new Location(origin.getX() + distance * Math.sin(radians),
                origin.getY() + distance * Math.cos(radians));
    }

    public DistanceVector plus(DistanceVector other) {
        double distance = distanceproperty.get();
        double radians = degreesproperty.getRadians();
        double otherdistance = other.distanceproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = distance * Math.sin(radians) + otherdistance * Math.sin(otherradians);
        double y = distance * Math.cos(radians) + otherdistance * Math.cos(otherradians);
        //
        return new DistanceVector(Math.sqrt(x * x + y * y), new Angle(Math.toDegrees(Math.atan2(x, y))));
    }

    public DistanceVector sub(DistanceVector other) {
        double distance = distanceproperty.get();
        double radians = degreesproperty.getRadians();
        double otherdistance = other.distanceproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = distance * Math.sin(radians) - otherdistance * Math.sin(otherradians);
        double y = distance * Math.cos(radians) - otherdistance * Math.cos(otherradians);
        //
        return new DistanceVector(Math.sqrt(x * x + y * y), new Angle(Math.toDegrees(Math.atan2(x, y))));
    }

    public DistanceVector mult(double multiplier) {
        double distance = distanceproperty.get();
        return multiplier < 0 ? new DistanceVector(-distance * multiplier, degreesproperty.inverse()) : new DistanceVector(distance * multiplier, degreesproperty);
    }

    public Angle degreesDiff(DistanceVector p) {
        return degreesproperty.degreesDiff(p.getAngle());
    }

    public Angle degreesDiff(Angle p) {
        return degreesproperty.degreesDiff(p);
    }

    public Angle degreesDiff(double p) {
        return degreesproperty.degreesDiff(p);
    }
}
