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

public class Location {

    public final static Location LOCATIONZERO = new Location(0.0, 0.0);

    private final SimpleDoubleProperty x = new SimpleDoubleProperty();
    private final SimpleDoubleProperty y = new SimpleDoubleProperty();

    public Location(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    public Location(Location pos) {
        this.x.set(pos.getX());
        this.y.set(pos.getY());
    }

    public SimpleDoubleProperty getXProperty() {
        return x;
    }

    public SimpleDoubleProperty getYProperty() {
        return y;
    }

    public double to(Location target) {
        double deltax = (target.getX() - this.getX());
        double deltay = (target.getY() - this.getY());
        return Math.sqrt(deltax * deltax + deltay * deltay);
    }

    public PropertyDegrees angleto(Location target) {
        return new PropertyDegrees(Math.round(Math.toDegrees(Math.atan2(target.getX() - this.getX(), target.getY() - this.getY()))));
    }

    public Location getFractionalLocation(Location lowerleft, double width, double height) {
        // note this location uses fractional units - not metres
        return new Location(
                (getX() - lowerleft.getX()) / (width),
                (getY() - lowerleft.getY()) / (height)
        );
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(x.get()) ^ (Double.doubleToLongBits(x.get()) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(y.get()) ^ (Double.doubleToLongBits(y.get()) >>> 32));
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
        final Location other = (Location) obj;
        if (Double.doubleToLongBits(this.x.get()) != Double.doubleToLongBits(other.x.get())) {
            return false;
        }
        return Double.doubleToLongBits(this.y.get()) == Double.doubleToLongBits(other.y.get());
    }

    @Override
    public String toString() {
        return "[" + x.get() + "," + y.getName() + "]";
    }
}
