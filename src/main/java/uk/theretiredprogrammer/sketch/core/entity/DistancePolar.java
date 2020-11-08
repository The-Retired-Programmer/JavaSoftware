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

public class DistancePolar extends Polar<DistancePolar> {

    private final SimpleDoubleProperty distanceproperty = new SimpleDoubleProperty(0);

    public DistancePolar(double distance, PropertyDegrees degrees) {
        super(degrees);
        distanceproperty.set(distance);
    }

    public DistancePolar(PropertyLocation origin, PropertyLocation pos) {
        super(new PropertyDegrees(Math.toDegrees(Math.atan2(pos.getX() - origin.getX(), pos.getY() - origin.getY()))));
        double deltax = (pos.getX() - origin.getX());
        double deltay = (pos.getY() - origin.getY());
        distanceproperty.set(Math.sqrt(deltax * deltax + deltay * deltay));
    }

    public PropertyLocation polar2Location(PropertyLocation origin) {
        return new PropertyLocation(origin.getX() + distanceproperty.get() * Math.sin(getDegreesProperty().getRadians()),
                origin.getY() + distanceproperty.get() * Math.cos(getDegreesProperty().getRadians()));
    }

    public double getDistance() {
        return distanceproperty.get();
    }

    @Override
    double getDimension() {
        return distanceproperty.get();
    }

    @Override
    DistancePolar create(double dimension, PropertyDegrees degrees) {
        return new DistancePolar(dimension, degrees);
    }
}
