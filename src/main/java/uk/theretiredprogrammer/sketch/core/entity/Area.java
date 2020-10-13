/*
 * Copyright 2020 Richard Linsdale
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

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Area {

    public static final Area AREAZERO = new Area(new Location(0, 0), 0, 0);

    private final Location bottomleft;
    private final double width;
    private final double height;

    public Area(Location bottomleft, double width, double height) {
        this.bottomleft = bottomleft;
        this.width = width;
        this.height = height;
    }

    public Area(double left, double bottom, double width, double height) {
        this(new Location(left, bottom), width, height);
    }

    public Location getBottomleft() {
        return bottomleft;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isWithinArea(Location location) {
        return location.getX() >= bottomleft.getX() && location.getX() <= bottomleft.getX() + width
                && location.getY() >= bottomleft.getY() && location.getY() <= bottomleft.getY() + height;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + bottomleft.hashCode();
        hash = 67 * hash + (int) (Double.doubleToLongBits(width) ^ (Double.doubleToLongBits(width) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(height) ^ (Double.doubleToLongBits(height) >>> 32));
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
        final Area other = (Area) obj;
        if (!this.bottomleft.equals(other.bottomleft)) {
            return false;
        }
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) {
            return false;
        }
        return Double.doubleToLongBits(this.height) == Double.doubleToLongBits(other.height);
    }
}
