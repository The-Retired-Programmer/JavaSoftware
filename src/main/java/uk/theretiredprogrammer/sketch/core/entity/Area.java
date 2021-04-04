/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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

public class Area implements ModelProperty<Area> {

    public static final Area AREAZERO = new Area(new Location(0, 0), 0, 0);

    private final Location topleft = new Location();
    private final SimpleDoubleProperty width = new SimpleDoubleProperty();
    private final SimpleDoubleProperty height = new SimpleDoubleProperty();

    public Area() {
        set(AREAZERO);
    }

    public Area(Area area) {
        set(area);
    }

    public Area(Location topleft, double width, double height) {
        set(topleft, width, height);
    }

    public Area(double x, double y, double width, double height) {
        set(x, y, width, height);
    }

    public final void set(Area area) {
        set(area.topleft, area.getWidth(), area.getHeight());
    }

    public final void set(Location topleft, double width, double height) {
        this.topleft.set(topleft);
        this.width.set(width);
        this.height.set(height);
    }

    public final void set(double x, double y, double width, double height) {
        set(new Location(x, y), width, height);
    }

    public Location getLocationProperty() {
        return topleft;
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
        topleft.setOnChange(onchange);
        width.addListener((o, oldval, newval) -> onchange.run());
        height.addListener((o, oldval, newval) -> onchange.run());
    }

    @Override
    public final Area parsevalue(JsonValue value) {
        return FromJson.area(value);
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

    public boolean isWithinArea(Location location) {
        return location.getX() >= topleft.getX() && location.getX() <= topleft.getX() + width.get()
                && location.getY() >= topleft.getY() && location.getY() <= topleft.getY() + height.get();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + topleft.hashCode();
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
        if (!this.topleft.equals(other.topleft)) {
            return false;
        }
        if (Double.doubleToLongBits(this.width.get()) != Double.doubleToLongBits(other.width.get())) {
            return false;
        }
        return Double.doubleToLongBits(this.height.get()) == Double.doubleToLongBits(other.height.get());
    }
}
