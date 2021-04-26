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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import static javafx.scene.transform.Rotate.X_AXIS;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class SteerableCamera extends Group {

    private final Rotate rotationangle = new Rotate(0f, 0, 0, 0);
    private final Rotate heightangle = new Rotate(0f, X_AXIS);
    private final Translate distancetranslation = new Translate(0, 0, -1);
    private final Translate moveto = new Translate(0, 0);
    private final Scale zoom = new Scale(1, 1, 1);
    private final PerspectiveCamera camera;

    public SteerableCamera() {
        rotationangle.angleProperty().bind(cameraviewrotation);
        heightangle.angleProperty().bind(cameraheightrotation);
        moveto.xProperty().bind(cameraviewlocationx);
        moveto.yProperty().bind(cameraviewlocationy);
        zoom.xProperty().bind(cameraviewscale);
        zoom.yProperty().bind(cameraviewscale);
        zoom.zProperty().bind(cameraviewscale);
        camera = new PerspectiveCamera(true);
        camera.setVerticalFieldOfView(true);
        camera.verticalFieldOfViewProperty().bind(cameraverticalfieldofview);
        camera.fieldOfViewProperty().bind(camerafieldofview);
        camera.setNearClip(0f);
        camera.farClipProperty().bind(cameraviewscale.multiply(1.5f));
        camera.getTransforms().add(distancetranslation);
        Group cameragroup = new Group();
        cameragroup.getChildren().add(camera);
        cameragroup.getTransforms().addAll(zoom, rotationangle, heightangle);
        getChildren().add(cameragroup);
        getTransforms().add(moveto);
        useSphericalEnvelop();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    // Model
    private final DoubleProperty cameraviewlocationx = new SimpleDoubleProperty(this, "cameraviewlocationx", 0d);
    private final DoubleProperty cameraviewlocationy = new SimpleDoubleProperty(this, "cameraviewlocationy", 0d);
    private final DoubleProperty cameraviewrotation = new SimpleDoubleProperty(this, "cameraviewrotation", 0d);
    private final DoubleProperty cameraviewscale = new SimpleDoubleProperty(this, "cameraviewscale", 300d);
    private final DoubleProperty cameraheightrotation = new SimpleDoubleProperty(this, "cameraheightrotation", 0d);
    private final DoubleProperty cameraheightscale = new SimpleDoubleProperty(this, "cameraheightscale", 1d);
    private final DoubleProperty camerafieldofview = new SimpleDoubleProperty(this, "camerafieldofview", 120d);
    private final BooleanProperty cameraverticalfieldofview = new SimpleBooleanProperty(this, "cameraverticalfieldofview", true);

    public void bindBidirectionaltoCameraViewLocation(DoubleProperty xcontrol, DoubleProperty ycontrol) {
        xcontrol.bindBidirectional(cameraviewlocationx);
        ycontrol.bindBidirectional(cameraviewlocationy);
    }

    public void bindtoCameraViewLocation(DoubleProperty xcontrol, DoubleProperty ycontrol) {
        cameraviewlocationx.bind(xcontrol);
        cameraviewlocationy.bind(ycontrol);
    }

    public void bindBidirectionaltoCameraViewRotation(DoubleProperty control) {
        control.bindBidirectional(cameraviewrotation);
    }

    public void bindtoCameraViewRotation(DoubleProperty control) {
        cameraviewrotation.bind(control);
    }

    public void bindBidirectionaltoCameraViewScale(DoubleProperty control) {
        control.bindBidirectional(cameraviewscale);
    }

    public void bindtoCameraViewScale(DoubleProperty control) {
        cameraviewscale.bind(control);
    }

    // height angle
    public void bindBidirectionaltoCameraHeightRotation(DoubleProperty control) {
        control.bindBidirectional(cameraheightrotation);
    }

    public void bindtoCameraHeightRotation(DoubleProperty control) {
        cameraheightrotation.bind(control);
    }

    // height fraction (scale z)
    public void bindBidirectionaltoCameraHeightScale(DoubleProperty control) {
        control.bindBidirectional(cameraheightscale);
    }

    public void bindtoCameraHeightScale(DoubleProperty control) {
        cameraheightscale.bind(control);
    }

    public void bindBidirectionaltoCameraFieldOfView(DoubleProperty control) {
        control.bindBidirectional(camerafieldofview);
    }

    public void bindtoCameraFieldOfView(DoubleProperty control) {
        camerafieldofview.bind(control);
    }

    public void bindBidirectionaltoCameraVerticalFieldofView(BooleanProperty control) {
        control.bindBidirectional(cameraverticalfieldofview);
    }

    public void bindtoCameraVerticalFieldofView(BooleanProperty control) {
        cameraverticalfieldofview.bind(control);
    }

    public final void useSphericalEnvelop() {
        distancetranslation.zProperty().bind(new Spherical());
    }

    private class Spherical extends DoubleBinding {

        @Override
        protected double computeValue() {
            return -cameraheightscale.get() + (heightangle.getAngle() / 90) * (-1 + cameraheightscale.get());
        }
    }

    public final void useCylndricalEnvelop() {
        //distancetranslation.zProperty().bind(new Cylndrical());
        distancetranslation.zProperty().bind(cameraheightscale.negate());
    }

    private class Cylndrical extends DoubleBinding {

        @Override
        protected double computeValue() {
            double cornerangle = Math.toDegrees(Math.atan(-1 / cameraheightscale.get()));
            return heightangle.getAngle() < cornerangle ? cameraheightscale.get() / Math.cos(Math.toRadians(heightangle.getAngle())) : -1 / Math.sin(Math.toRadians(heightangle.getAngle()));
        }
    }

    public final void useCubicEnvelop() {
        distancetranslation.zProperty().bind(new Cubic());
    }

    private class Cubic extends DoubleBinding {

        @Override
        protected double computeValue() {
            double rangle = normalise(rotationangle.getAngle()) % 90d;
            double baselength = rangle < 45 ? 1 / Math.cos(Math.toRadians(rangle)) : 1 / Math.sin(Math.toRadians(rangle));
            double topcornerangle = Math.toDegrees(Math.atan(-baselength / cameraheightscale.get()));
            return heightangle.getAngle() < topcornerangle ? cameraheightscale.get() / Math.cos(Math.toRadians(heightangle.getAngle())) : -baselength / Math.sin(Math.toRadians(heightangle.getAngle()));
        }

        private double normalise(double angle) {
            while (angle >= 360) {
                angle -= 360;
            }
            while (angle < 0) {
                angle += 360;
            }
            return angle;
        }
    }
}
