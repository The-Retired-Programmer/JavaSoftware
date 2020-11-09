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

public class PropertyDistanceVector implements ModelProperty<PropertyDistanceVector> {

    private final SimpleDoubleProperty distanceproperty = new SimpleDoubleProperty();
    private final PropertyDegrees degreesproperty = new PropertyDegrees();

    public PropertyDistanceVector() {
        set(0,0);
    }
    
    public PropertyDistanceVector(PropertyDistanceVector value) {
        set(value);
    }

    public PropertyDistanceVector(double distance, PropertyDegrees degrees) {
        set(distance, degrees);
    }

    public PropertyDistanceVector(double distance, double degrees) {
        set(distance, degrees);
    }

    public PropertyDistanceVector(PropertyLocation origin, PropertyLocation pos) {
        double deltax = (pos.getX() - origin.getX());
        double deltay = (pos.getY() - origin.getY());
        set(
                Math.sqrt(deltax * deltax + deltay * deltay),
                Math.toDegrees(Math.atan2(pos.getX() - origin.getX(), pos.getY() - origin.getY()))
        );
    }

    @Override
    public void setOnChange(Runnable onchange) {
        //
    }

    public final void set(PropertyDistanceVector value) {
        set(value.getDistance(), value.getDegrees());
    }

    public final void set(double distance, PropertyDegrees degrees) {
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

    public PropertyDegrees getDegreesProperty() {
        return degreesproperty;
    }

    @Override
    public PropertyDistanceVector parsevalue(JsonValue jvalue) {
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

    public PropertyLocation toLocation(PropertyLocation origin) {
        double distance = distanceproperty.get();
        double radians = getDegreesProperty().getRadians();
        return new PropertyLocation(origin.getX() + distance * Math.sin(radians),
                origin.getY() + distance * Math.cos(radians));
    }

    public PropertyDistanceVector plus(PropertyDistanceVector other) {
        double distance = distanceproperty.get();
        double radians = degreesproperty.getRadians();
        double otherdistance = other.distanceproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = distance * Math.sin(radians) + otherdistance * Math.sin(otherradians);
        double y = distance * Math.cos(radians) + otherdistance * Math.cos(otherradians);
        //
        return new PropertyDistanceVector(Math.sqrt(x * x + y * y), new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public PropertyDistanceVector sub(PropertyDistanceVector other) {
        double distance = distanceproperty.get();
        double radians = degreesproperty.getRadians();
        double otherdistance = other.distanceproperty.get();
        double otherradians = other.degreesproperty.getRadians();
        double x = distance * Math.sin(radians) - otherdistance * Math.sin(otherradians);
        double y = distance * Math.cos(radians) - otherdistance * Math.cos(otherradians);
        //
        return new PropertyDistanceVector(Math.sqrt(x * x + y * y), new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public PropertyDistanceVector mult(double multiplier) {
        double distance = distanceproperty.get();
        return multiplier < 0 ? new PropertyDistanceVector(-distance * multiplier, degreesproperty.inverse()) : new PropertyDistanceVector(distance * multiplier, degreesproperty);
    }

    public PropertyDegrees degreesDiff(PropertyDistanceVector p) {
        return degreesproperty.degreesDiff(p.getDegreesProperty());
    }
    
    public PropertyDegrees degreesDiff(PropertyDegrees p) {
        return degreesproperty.degreesDiff(p);
    }
}
