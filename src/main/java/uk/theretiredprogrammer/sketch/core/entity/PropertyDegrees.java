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
import javafx.scene.control.TextField;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class PropertyDegrees extends SimpleDoubleProperty implements ModelProperty<PropertyDegrees> {

    public static final PropertyDegrees DEGREES0 = new PropertyDegrees(0);
    public static final PropertyDegrees DEGREES90 = new PropertyDegrees(90);
    public static final PropertyDegrees DEGREES180 = new PropertyDegrees(180);
    public static final PropertyDegrees DEGREESMINUS90 = new PropertyDegrees(-90);

    public PropertyDegrees() {
        set(0);
    }

    public PropertyDegrees(PropertyDegrees degrees) {
        set(degrees);
    }

    public PropertyDegrees(double degrees) {
        set(degrees);
    }

    @Override
    public final void set(double degrees) {
        super.set(normalise(degrees));
    }

    public final void set(PropertyDegrees degrees) {
        super.set(normalise(degrees.get()));
    }

    @Override
    public double get() {
        return super.get();
    }

    public double getRadians() {
        return Math.toRadians(get());
    }

    @Override
    public void setOnChange(Runnable onchange) {
        //setOnChange((c) -> onchange.run());
    }

    @Override
    public final PropertyDegrees parsevalue(JsonValue jvalue) {
        return FromJson.degreesProperty(jvalue);
    }

    @Override
    public JsonValue toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public TextField getControl() {
        return getControl(7);
    }

    @Override
    public TextField getControl(int size) {
        return UI.control(size, this);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }

    private double normalise(double angle) {
        while (angle > 180.0) {
            angle -= 360.0;
        }
        while (angle <= -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public final PropertyDegrees plus(PropertyDegrees degrees) {
        return new PropertyDegrees(get() + degrees.get());
    }

    public final PropertyDegrees sub(PropertyDegrees degrees) {
        return new PropertyDegrees(get() - degrees.get());
    }

    public final PropertyDegrees mult(double mult) {
        return new PropertyDegrees(get() * mult);
    }

    public final PropertyDegrees div(double div) {
        return new PropertyDegrees(get() / div);
    }

    public final double div(PropertyDegrees div) {
        return get() / div.get();
    }

    public final PropertyDegrees abs() {
        return new PropertyDegrees(Math.abs(get()));
    }

    public final PropertyDegrees negative() {
        return new PropertyDegrees(-get());
    }

    public final PropertyDegrees negateif(boolean test) {
        return new PropertyDegrees(test ? -get() : get());
    }

    public final boolean eq(PropertyDegrees degrees) {
        return get() == degrees.get();
    }

    public final boolean neq(PropertyDegrees degrees) {
        return get() != degrees.get();
    }

    public final boolean gt(PropertyDegrees degrees) {
        return sub(degrees).get() > 0;
    }

    public final boolean gteq(PropertyDegrees degrees) {
        return sub(degrees).get() >= 0;
    }

    public final boolean lt(PropertyDegrees degrees) {
        return sub(degrees).get() < 0;
    }

    public final boolean lteq(PropertyDegrees degrees) {
        return sub(degrees).get() <= 0;
    }

    public final boolean between(PropertyDegrees min, PropertyDegrees max) {
        return gteq(min) && lteq(max);
    }

    public final PropertyDegrees degreesDiff(PropertyDegrees degrees) {
        return new PropertyDegrees(degrees.get() - get());
    }

    public final PropertyDegrees absDegreesDiff(PropertyDegrees degrees) {
        return new PropertyDegrees(Math.abs(normalise(degrees.get() - get())));
    }

    public final PropertyDegrees inverse() {
        return new PropertyDegrees(get() + 180);
    }

    public final PropertyDegrees inverseif(boolean test) {
        return new PropertyDegrees(test ? get() + 180 : get());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Double.hashCode(get());
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
        final PropertyDegrees other = (PropertyDegrees) obj;
        return this.get() == other.get();
    }

    @Override
    public String toString() {
        return Double.toString(get());
    }
}
