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
    
    private DoubleProperty camerarotation = new SimpleDoubleProperty(0);
    private DoubleProperty camerazoom = new SimpleDoubleProperty(1);
    private DoubleProperty cameraxlocation = new SimpleDoubleProperty(0); 
    private DoubleProperty cameraylocation = new SimpleDoubleProperty(0);
    private DoubleProperty cameraverticalrotation = new SimpleDoubleProperty(45);
    private DoubleProperty camerafieldofview = new SimpleDoubleProperty(90);

    public Display3DPane(Display3DController controller) {
        this(controller, controller.getModel().getDisplayArea());
    }

    private Display3DPane(Display3DController controller, Area displayarea) {
        this(controller, displayarea.getLocationProperty()  ,displayarea.getWidth(), displayarea.getHeight());
    }

    private Display3DPane(Display3DController controller, Location origin, double width, double height) {
        super(new Display3DGroup(controller), width, height, true, BALANCED);
        Area displayarea = controller.getModel().getDisplay().getDisplayarea();
        double arearadius = Math.max(displayarea.getWidth(), displayarea.getHeight()) / 2;
//        setCamerazoom(arearadius);
//        setCameraviewlocation(origin.getX()+width/2, origin.getY()+height/2);
//        SteerableCamera steerablecamera = new SteerableCamera();
//        AmbientLight lighting = new AmbientLight(Color.WHITE);
//        Group group = (Group) getRoot();
//        group.getChildren().addAll(lighting, steerablecamera);
//        //
//        steerablecamera.bindtoCameraViewRotation(camerarotation);
//        steerablecamera.bindtoCameraViewScale(camerazoom);
//        steerablecamera.bindtoCameraViewLocation(cameraxlocation, cameraylocation);
//        steerablecamera.bindtoCameraHeightRotation(cameraverticalrotation);
//        steerablecamera.bindtoCameraFieldOfView(camerafieldofview);
        //
//        setCamera(steerablecamera.getCamera());
    }
    
    public void setRotatecameraview(double newvalue){
        camerarotation.set(newvalue);
    }
    
    public void setCamerazoom(double newvalue){
        camerazoom.set(newvalue);
    }
    
    public void setCameraviewlocation(double newxvalue, double newyvalue){
        cameraxlocation.set(newxvalue);
        cameraylocation.set(newyvalue);
    }
    
    public void setCameraverticalrotation(double newvalue){
        cameraverticalrotation.set(newvalue);
    }
    
     public void setCamerafieldofview(double newvalue){
        camerafieldofview.set(newvalue);
    }
}
