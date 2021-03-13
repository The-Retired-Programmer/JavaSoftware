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
import javafx.scene.control.TextField;
import uk.theretiredprogrammer.sketch.core.control.MissingFeatureFailure;
import uk.theretiredprogrammer.sketch.core.control.ReplacedMethodFailure;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class Angle extends SimpleDoubleProperty implements ModelProperty<Angle> {

    public Angle() {
        set(0);
    }

    public Angle(Angle degrees) {
        set(degrees);
    }

    public Angle(double degrees) {
        set(degrees);
    }

    @Override
    public final void set(double degrees) {
        super.set(normalise(degrees));
    }

    public final void set(Angle degrees) {
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
        addListener((o, oldval, newval) -> onchange.run());
    }

    @Override
    public final Angle parsevalue(JsonValue jvalue) {
        return FromJson.angle(jvalue);
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
        while (angle >= 360.0) {
            angle -= 360.0;
        }
        while (angle < 0) {
            angle += 360.0;
        }
        return angle;
    }

    public final Angle plus(Angle degrees) {
        return new Angle(get() + degrees.get());
    }

    public final Angle plus(double degrees) {
        return new Angle(get() + degrees);
    }

    public final Angle sub(Angle degrees) {
        return new Angle(get() - degrees.get());
    }

    public final Angle sub(double degrees) {
        return new Angle(get() - degrees);
    }

    public final Angle mult(double mult) {
        return new Angle(get() * mult);
    }

    public final Angle div(double div) {
        return new Angle(get() / div);
    }

    public final double div(Angle div) {
        return get() / div.get();
    }

    public final Angle abs() {
        throw new MissingFeatureFailure("Angle:abs");
//        return new Angle(Math.abs(get()));
    }

    public final Angle opposite() {
        return new Angle(-get());
    }

    public final Angle negative() {
        throw new ReplacedMethodFailure("Angle:negative use Angle:opposite");
    }

    public final Angle oppositeif(boolean test) {
        return new Angle(test ? -get() : get());
    }

    public final Angle negateif(boolean test) {
        throw new ReplacedMethodFailure("Angle:negativeif use Angle:oppositeif");
    }

    public final boolean eq(Angle degrees) {
        return get() == degrees.get();
    }

    public final boolean eq(double degrees) {
        return get() == degrees;
    }

    public final boolean neq(Angle degrees) {
        return get() != degrees.get();
    }

    public final boolean neq(double degrees) {
        return get() != degrees;
    }

    public final boolean gt(Angle degrees) {
        double diff = sub(degrees).get();
        return diff > 0 && diff < 180;
    }

    public final boolean gt(double degrees) {
        double diff = sub(degrees).get();
        return diff > 0 && diff < 180;
    }

    public final boolean gteq(Angle degrees) {
        double diff = sub(degrees).get();
        return diff >= 0 && diff < 180;
    }

    public final boolean gteq(double degrees) {
        double diff = sub(degrees).get();
        return diff >= 0 && diff < 180;
    }

    public final boolean lt(Angle degrees) {
        return !gteq(degrees);
    }

    public final boolean lt(double degrees) {
        return !gteq(degrees);
    }

    public final boolean lteq(Angle degrees) {
        return !gt(degrees);
    }

    public final boolean lteq(double degrees) {
        return !gteq(degrees);
    }

    public final boolean between(Angle min, Angle max) {
        return gteq(min) && lteq(max);
    }

    public final boolean between(double min, double max) {
        return gteq(min) && lteq(max);
    }

    public final Angle degreesDiff(Angle degrees) {
        return new Angle(degrees.get() - get());
    }

    public final Angle degreesDiff(double degrees) {
        return new Angle(degrees - get());
    }

    public final Angle absDegreesDiff(Angle degrees) {
        throw new MissingFeatureFailure("Angle:absDegreesDiff(Angle)");
//        return new Angle(Math.abs(normalise(degrees.get() - get())));
    }

    public final Angle absDegreesDiff(double degrees) {
        throw new MissingFeatureFailure("Angle:absDegreesDiff(double)");
//        return new Angle(Math.abs(normalise(degrees - get())));
    }

    public final Angle inverse() {
        return new Angle(get() + 180);
    }

    public final Angle inverseif(boolean test) {
        return new Angle(test ? get() + 180 : get());
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
        final Angle other = (Angle) obj;
        return this.get() == other.get();
    }

    @Override
    public String toString() {
        return Double.toString(get());
    }
}
