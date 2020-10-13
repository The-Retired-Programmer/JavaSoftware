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
 * The Angle Class.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Angle {

    public final static Angle ANGLE0 = new Angle(0);
    public final static Angle ANGLE90 = new Angle(90);
    public final static Angle ANGLE180 = new Angle(180);
    public final static Angle ANGLE90MINUS = new Angle(-90);

    private final double angle_degrees; // Range +-179.999.. to +180.0 degrees

    public Angle() {
        angle_degrees = 0;
    }

    public Angle(Angle d) {
        angle_degrees = d.angle_degrees;
    }

    public Angle(double degrees) {
        angle_degrees = normalise(degrees);
    }

    public final Angle add(Angle angle) {
        return new Angle(normalise(angle_degrees + angle.angle_degrees));
    }

    public final Angle sub(Angle angle) {
        return new Angle(normalise(angle_degrees - angle.angle_degrees));
    }

    public final Angle mult(int mult) {
        return new Angle(normalise(angle_degrees * mult));
    }

    public final Angle mult(double mult) {
        return new Angle(normalise((mult * angle_degrees)));
    }

    public final Angle div(int div) {
        return new Angle(normalise(angle_degrees / div));
    }

    public final double div(Angle a) {
        return angle_degrees / a.angle_degrees;
    }

    public final Angle negate() {
        return new Angle(normalise(-angle_degrees));
    }

    public final Angle abs() {
        return new Angle(Math.abs(angle_degrees));
    }

    public final Angle negateif(boolean test) {
        return new Angle(normalise(test ? -angle_degrees : angle_degrees));
    }

    public final Angle reflectH() {
        return new Angle(normalise(180.0 - angle_degrees));
    }

    public final Angle reflectHif(boolean test) {
        return new Angle(normalise(test ? 180.0 - angle_degrees : angle_degrees));
    }

    public final Angle reflectV() {
        return new Angle(normalise(360.0 - angle_degrees));
    }

    public final Angle reflectVif(boolean test) {
        return new Angle(normalise(test ? 360.0 - angle_degrees : angle_degrees));
    }

    public final boolean isPositive() {
        return angle_degrees >= 0;
    }

    public final boolean isZero() {
        return angle_degrees == 0;
    }

    public final boolean isNegative() {
        return angle_degrees < 0;
    }

    public final boolean eq(Angle a) {
        return angle_degrees == a.angle_degrees;
    }

    public final boolean neq(Angle a) {
        return angle_degrees != a.angle_degrees;
    }

    public final boolean gt(Angle a) {
        return normalise(angle_degrees - a.angle_degrees) > 0;
    }

    public final boolean gteq(Angle a) {
        return normalise(angle_degrees - a.angle_degrees) >= 0;
    }

    public final boolean lt(Angle a) {
        return normalise(angle_degrees - a.angle_degrees) < 0;
    }

    public final boolean lteq(Angle a) {
        return normalise(angle_degrees - a.angle_degrees) <= 0;
    }

    public final boolean between(Angle min, Angle max) {
        return this.gteq(min) && this.lteq(max);
    }

    public final Angle angleDiff(Angle d) {
        return new Angle(normalise(d.angle_degrees - angle_degrees));
    }

    public final Angle absAngleDiff(Angle d) {
        return new Angle(Math.abs(normalise(d.angle_degrees - angle_degrees)));
    }

    public final Angle inverse() {
        return new Angle(angle_degrees + 180);
    }

    public final Angle inverseif(boolean test) {
        return new Angle(test ? angle_degrees + 180 : angle_degrees);
    }

    public final double getRadians() {
        return Math.toRadians(angle_degrees);
    }

    public final double getDegrees() {
        return angle_degrees;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Double.hashCode(angle_degrees);
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
        return this.angle_degrees == other.angle_degrees;
    }

    @Override
    public String toString() {
        return Double.toString(angle_degrees);
    }

}
