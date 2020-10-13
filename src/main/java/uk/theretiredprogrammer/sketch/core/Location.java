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

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Location {

    public final static Location LOCATIONZERO = new Location(0.0, 0.0);

    private final double x;
    private final double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Location(Location pos) {
        x = pos.x;
        y = pos.y;
    }

    public double to(Location target) {
        double deltax = (target.getX() - this.getX());
        double deltay = (target.getY() - this.getY());
        return Math.sqrt(deltax * deltax + deltay * deltay);
    }

    public Angle angleto(Location target) {
        return new Angle(Math.round(Math.toDegrees(Math.atan2(target.getX() - this.getX(), target.getY() - this.getY()))));
    }

    public Location getFractionalLocation(Location lowerleft, double width, double height) {
        // note this location uses fractional units - not metres
        return new Location(
                (this.x - lowerleft.x) / (width),
                (this.y - lowerleft.y) / (height)
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
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
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
