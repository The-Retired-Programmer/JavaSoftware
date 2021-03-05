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
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Boat3D extends Group {

    private final Rotate boatrotate;
    private final DoubleProperty boatangle = new SimpleDoubleProperty(0);
    private final Translate boattranslate;
    private final DoubleProperty boatxpos = new SimpleDoubleProperty(0);
    private final DoubleProperty boatypos = new SimpleDoubleProperty(0);

    private final BoatRig3D rig;
    private final Group boat;

    public Boat3D(Dimensions3D dimensions) {
        boat = new Group();
        BoatHull3D hull = new BoatHull3D(dimensions.getHulldimensions());
        rig = new BoatRig3D(dimensions.getSpardimensions());
        boat.getChildren().addAll(hull, rig);
        boat.getTransforms().addAll(
                boatrotate = new Rotate(0, Rotate.Z_AXIS),
                new Translate(1.25f, 0f, 0f)
        );
        boatrotate.angleProperty().bind(boatangle);
        getChildren().add(boat);
        getTransforms().addAll(
                new Translate(-1.25f, 0f, 0f),
                boattranslate = new Translate(0f, 0f, 0f)
        );
        boattranslate.xProperty().bind(boatxpos);
        boattranslate.yProperty().bind(boatypos);
    }

    public void setBoatAngle(double angle) {
        boatangle.set(angle);
    }

    public void incBoatAngle(double delta) {
        boatangle.set(boatangle.get() + delta);
    }

    public void decBoatAngle(double delta) {
        boatangle.set(boatangle.get() - delta);
    }

    public void setBoomAngle(double angle) {
        rig.setBoomAngle(angle);
    }

    public void incBoomAngle(double delta) {
        rig.incBoomAngle(delta);
    }

    public void decBoomAngle(double delta) {
        rig.decBoomAngle(delta);
    }

    public void incBoatPos(double delta) {
        double radians = Math.toRadians(boatangle.get());
        boatxpos.set(boatxpos.get() + delta * Math.cos(radians));
        boatypos.set(boatypos.get() + delta * Math.sin(radians));
    }

}
