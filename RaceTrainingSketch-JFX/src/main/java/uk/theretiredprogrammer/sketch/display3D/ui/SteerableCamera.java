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
package uk.theretiredprogrammer.sketch.display3D.ui;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import static javafx.scene.transform.Rotate.X_AXIS;
import javafx.scene.transform.Translate;

public class SteerableCamera extends Group {

    private final Rotate angle = new Rotate(0f, 0, 0, 0);
    private final Rotate elevation = new Rotate(0f, X_AXIS);
    private final Translate distance = new Translate(0, 0, -1);
    private final Translate pointto = new Translate(0, 0, 0);
    private final PerspectiveCamera camera;

    public SteerableCamera(double farclip) {
        camera = new PerspectiveCamera(true);
        camera.setVerticalFieldOfView(true);
        camera.setNearClip(0f);
        camera.setFarClip(farclip);
        camera.getTransforms().add(distance);
        Group cameragroup = new Group();
        cameragroup.getChildren().add(camera);
        cameragroup.getTransforms().addAll(angle, elevation);
        getChildren().add(cameragroup);
        getTransforms().add(pointto);
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void setPointTo(DoubleProperty xcontrol, DoubleProperty ycontrol, DoubleProperty zcontrol) {
        pointto.xProperty().bind(xcontrol);
        pointto.yProperty().bind(ycontrol);
        pointto.zProperty().bind(zcontrol);
    }

    public void setAngle(DoubleProperty control) {
        angle.angleProperty().bind(control);
    }

    public void setElevation(DoubleProperty control) {
        elevation.angleProperty().bind(control.subtract(90).negate());
    }

    public void setDistance(DoubleProperty control) {
        distance.zProperty().bind(control.negate());
    }

    public void setFieldOfView(DoubleProperty control) {
        camera.fieldOfViewProperty().bind(control);
    }
}
