/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BoatCoordinates {

    private final DoubleProperty boatangle;
    private final DoubleProperty boatxpos;
    private final DoubleProperty boatypos;
    private final DoubleProperty boomangle;

    public BoatCoordinates(double x, double y, double angle, double boomangle) {
        boatxpos = new SimpleDoubleProperty(x);
        boatypos = new SimpleDoubleProperty(y);
        boatangle = new SimpleDoubleProperty(angle);
        this.boomangle = new SimpleDoubleProperty(boomangle);
    }

    public BoatCoordinates() {
        this(0,0,0,0);
    }

    public void setPosition(double x, double y) {
        boatxpos.set(x);
        boatypos.set(y);
    }

    DoubleProperty getXProperty() {
        return boatxpos;
    }

    DoubleProperty getYProperty() {
        return boatypos;
    }

    public void setAngle(double angle) {
        boatangle.set(angle);
    }

    DoubleProperty getAngleProperty() {
        return boatangle;
    }

    public void setBoomAngle(double angle) {
        boomangle.set(angle);
    }

    DoubleProperty getBoomAngleProperty() {
        return boomangle;
    }

}
