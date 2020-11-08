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

import javafx.beans.property.SimpleDoubleProperty;

public class Area {

    public static final Area AREAZERO = new Area(new PropertyLocation(0, 0), 0, 0);

    private final PropertyLocation bottomleft = new PropertyLocation();
    private final SimpleDoubleProperty width = new SimpleDoubleProperty();
    private final SimpleDoubleProperty height = new SimpleDoubleProperty();

    public Area(PropertyLocation bottomleft, double width, double height) {
        this.bottomleft.set(bottomleft);
        this.width.set(width);
        this.height.set(height);
    }

    public Area(double left, double bottom, double width, double height) {
        this(new PropertyLocation(left, bottom), width, height);
    }

    public PropertyLocation getBottomleft() {
        return bottomleft;
    }

    public PropertyLocation getBottomLeftProperty() {
        return bottomleft;
    }

    public double getWidth() {
        return width.get();
    }

    public SimpleDoubleProperty getWidthProperty() {
        return width;
    }

    public double getHeight() {
        return height.get();
    }

    public SimpleDoubleProperty getHeightProperty() {
        return height;
    }

    public boolean isWithinArea(PropertyLocation location) {
        return location.getX() >= bottomleft.getX() && location.getX() <= bottomleft.getX() + width.get()
                && location.getY() >= bottomleft.getY() && location.getY() <= bottomleft.getY() + height.get();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + bottomleft.hashCode();
        hash = 67 * hash + (int) (Double.doubleToLongBits(width.get()) ^ (Double.doubleToLongBits(width.get()) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(height.get()) ^ (Double.doubleToLongBits(height.get()) >>> 32));
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
        if (Double.doubleToLongBits(this.width.get()) != Double.doubleToLongBits(other.width.get())) {
            return false;
        }
        return Double.doubleToLongBits(this.height.get()) == Double.doubleToLongBits(other.height.get());
    }
}
