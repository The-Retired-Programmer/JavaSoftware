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

abstract class Polar<P extends Polar> {

    final PropertyDegrees degreesproperty = new PropertyDegrees(0);

    abstract P create(double dimension, PropertyDegrees angle);

    abstract double getDimension();

    Polar(PropertyDegrees degrees) {
        this.degreesproperty.set(degrees);
    }

    public P plus(P p) {
        double dimension = getDimension();
        double pdimension = p.getDimension();
        double x = dimension * Math.sin(degreesproperty.getRadians()) + pdimension * Math.sin(p.degreesproperty.getRadians());
        double y = dimension * Math.cos(degreesproperty.getRadians()) + pdimension * Math.cos(p.degreesproperty.getRadians());
        //
        return create(
                Math.sqrt(x * x + y * y),
                new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public P sub(P p) {
        double dimension = getDimension();
        double pdimension = p.getDimension();
        double x = dimension * Math.sin(degreesproperty.getRadians()) - pdimension * Math.sin(p.degreesproperty.getRadians());
        double y = dimension * Math.cos(degreesproperty.getRadians()) - pdimension * Math.cos(p.degreesproperty.getRadians());
        //
        return create(
                Math.sqrt(x * x + y * y),
                new PropertyDegrees(Math.toDegrees(Math.atan2(x, y))));
    }

    public P mult(double multiplier) {
        return multiplier < 0
                ? create(-getDimension() * multiplier, degreesproperty.inverse())
                : create(getDimension() * multiplier, degreesproperty);
    }

    public PropertyDegrees degreesDiff(PropertyDegrees degrees) {
        return this.degreesproperty.degreesDiff(degrees);
    }

    public PropertyDegrees degreesDiff(Polar p) {
        return degreesDiff(p.degreesproperty);
    }

    public double getDegrees() {
        return degreesproperty.get();
    }

    public PropertyDegrees getDegreesProperty() {
        return degreesproperty;
    }

    public static PropertyDegrees meanAngle(Polar[][] array) {
        double x = 0;
        double y = 0;
        for (Polar[] column : array) {
            for (Polar cell : column) {
                double r = cell.degreesproperty.getRadians();
                x += Math.sin(r);
                y += Math.cos(r);
            }
        }
        return new PropertyDegrees(Math.toDegrees(Math.atan2(x, y)));
    }
}
