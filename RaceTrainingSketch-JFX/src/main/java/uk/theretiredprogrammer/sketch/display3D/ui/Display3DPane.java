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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import static javafx.scene.SceneAntialiasing.BALANCED;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.control.Display3DController;

public class Display3DPane extends SubScene {

    private final DoubleProperty cameraangle = new SimpleDoubleProperty(0);
    private final DoubleProperty cameraxpointto = new SimpleDoubleProperty(0);
    private final DoubleProperty cameraypointto = new SimpleDoubleProperty(0);
    private final DoubleProperty camerazpointto = new SimpleDoubleProperty(0);
    private final DoubleProperty cameraelevation = new SimpleDoubleProperty(90);
    private final DoubleProperty cameradistance = new SimpleDoubleProperty(0);
    private final DoubleProperty camerafieldofview = new SimpleDoubleProperty(120);

    public Display3DPane(Display3DController controller) {
        this(controller, controller.getModel().getDisplayArea());
    }

    private Display3DPane(Display3DController controller, Area displayarea) {
        this(controller, displayarea.getLocationProperty(), displayarea.getWidth(), displayarea.getHeight());
    }

    private Display3DPane(Display3DController controller, Location origin, double width, double height) {
        super(new Display3DGroup(controller), width, height, true, BALANCED);
        Area displayarea = controller.getModel().getDisplay().getDisplayarea();
        double arearadius = Math.max(displayarea.getWidth(), displayarea.getHeight()) / 2;
        SteerableCamera steerablecamera = new SteerableCamera(arearadius * 3);
        setCameraPointTo(origin.getX() + width / 2, origin.getY() + height / 2, 0);
        setCameraDistance(arearadius);
        AmbientLight lighting = new AmbientLight(Color.WHITE);
        Group group = (Group) getRoot();
        group.getChildren().addAll(lighting, steerablecamera);
        //
        steerablecamera.setAngle(cameraangle);
        steerablecamera.setPointTo(cameraxpointto, cameraypointto, camerazpointto);
        steerablecamera.setElevation(cameraelevation);
        steerablecamera.setFieldOfView(camerafieldofview);
        steerablecamera.setDistance(cameradistance);
        //
        setCamera(steerablecamera.getCamera());
    }

    public final void setCameraAngle(double newvalue) {
        cameraangle.set(newvalue);
    }
    
    public final void setCameraAngle(DoubleProperty newvalue) {
        cameraangle.bindBidirectional(newvalue);
    }

    public final void setCameraDistance(double newvalue) {
        cameradistance.set(newvalue);
    }
    
    public final void setCameraDistance(DoubleProperty newvalue) {
        cameradistance.bindBidirectional(newvalue);
    }

    public final void setCameraPointTo(double newxvalue, double newyvalue, double newzvalue) {
        cameraxpointto.set(newxvalue);
        cameraypointto.set(newyvalue);
        camerazpointto.set(newzvalue);
    }

    public final void setCameraElevation(double newvalue) {
        cameraelevation.set(newvalue);
    }
    
     public final void setCameraElevation(DoubleProperty newvalue) {
        cameraelevation.bindBidirectional(newvalue);
    }

    public final void setCameraFieldOfView(double newvalue) {
        camerafieldofview.set(newvalue);
    }
    
    public final void setCameraFieldOfView(DoubleProperty newvalue) {
        camerafieldofview.bindBidirectional(newvalue);
    }
}
