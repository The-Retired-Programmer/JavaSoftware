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

public class PropertyArea implements ModelProperty<PropertyArea> {

    public static final PropertyArea AREAZERO = new PropertyArea(new PropertyLocation(0, 0), 0, 0);

    private final PropertyLocation bottomleft = new PropertyLocation();
    private final SimpleDoubleProperty width = new SimpleDoubleProperty();
    private final SimpleDoubleProperty height = new SimpleDoubleProperty();

    public PropertyArea() {
        set(AREAZERO);
    }

    public PropertyArea(PropertyArea area) {
        set(area);
    }

    public PropertyArea(PropertyLocation bottomleft, double width, double height) {
        set(bottomleft, width, height);
    }

    public PropertyArea(double x, double y, double width, double height) {
        set(x, y, width, height);
    }

    public final void set(PropertyArea area) {
        set(area.bottomleft, area.getWidth(), area.getHeight());
    }

    public final void set(PropertyLocation bottomleft, double width, double height) {
        this.bottomleft.set(bottomleft);
        this.width.set(width);
        this.height.set(height);
    }

    public final void set(double x, double y, double width, double height) {
        set(new PropertyLocation(x, y), width, height);
    }

    public PropertyLocation getLocationProperty() {
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

    @Override
    public void setOnChange(Runnable onchange) {
        bottomleft.setOnChange(onchange);
        width.addListener((o, oldval, newval) -> onchange.run());
        height.addListener((o, oldval, newval) -> onchange.run());
    }

    @Override
    public final PropertyArea parsevalue(JsonValue value) {
        return FromJson.areaProperty(value);
    }

    @Override
    public JsonValue toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return getControl(7);
    }

    @Override
    public Node getControl(int size) {
        return UI.control(size, this);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
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
        final PropertyArea other = (PropertyArea) obj;
        if (!this.bottomleft.equals(other.bottomleft)) {
            return false;
        }
        if (Double.doubleToLongBits(this.width.get()) != Double.doubleToLongBits(other.width.get())) {
            return false;
        }
        return Double.doubleToLongBits(this.height.get()) == Double.doubleToLongBits(other.height.get());
    }
}
