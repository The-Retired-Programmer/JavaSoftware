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
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class BoatCoordinates {

    private final Angle boatangle;
    private final Location position;
    private final Angle boomangle = new Angle(0);
    private final Angle winddirection;
    private final WindFlow windflow;

    public BoatCoordinates(Location position, Angle boatangle, Angle winddirection) {
        this.position = position;
        this.boatangle = boatangle;
        this.winddirection = new Angle(winddirection);
        windflow = null;
        setBoomangle();
    }

    public BoatCoordinates(Location position, Angle boatangle, WindFlow windflow) {
        this.position = position;
        this.boatangle = boatangle;
        this.windflow = windflow;
        winddirection = null;
        setBoomangle();
    }
    
    public BoatCoordinates() {
        this(new Location(0, 0), new Angle(0), new Angle(0));
    }

    private void setBoomangle() {
        Angle wdirection = windflow == null ? winddirection : windflow.getFlow(position).getAngle();
        double relative = wdirection.degreesDiff(boatangle).get();
        boolean onStarboard = relative > 180;
        double sailRotation;
        if (onStarboard) {
            sailRotation = relative > 320 ? 0 : (360 - relative) / 2;

        } else {
            sailRotation = relative < 40 ? 0 : -relative / 2;
        }
        boomangle.set(sailRotation);
    }

    public void setPosition(double x, double y) {
        position.getXProperty().set(x);
        position.getYProperty().set(y);
    }

    DoubleProperty getXProperty() {
        return position.getXProperty();
    }

    DoubleProperty getYProperty() {
        return position.getYProperty();
    }

    public void setAngle(double angle) {
        boatangle.set(angle);
        setBoomangle();
    }

    DoubleProperty getAngleProperty() {
        return boatangle;
    }

    DoubleProperty getBoomAngleProperty() {
        return boomangle;
    }

}
