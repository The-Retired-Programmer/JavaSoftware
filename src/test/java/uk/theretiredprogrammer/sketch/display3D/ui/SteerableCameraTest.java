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

import javafx.application.Application;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.WHITE;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat3D;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatCoordinates;
import uk.theretiredprogrammer.sketch.display.entity.boats.Dimensions3D;
import uk.theretiredprogrammer.sketch.display.entity.boats.HullDimensions3DBuilder;
import uk.theretiredprogrammer.sketch.display.entity.boats.SparDimensions3DBuilder;

public class SteerableCameraTest extends Application {

    private SteerableCamera steerablecamera;
    private Slider angleslider;
    private Slider xpointtoslider;
    private Slider ypointtoslider;
    private Slider zpointtoslider;
    private Slider elevationslider;
    private Slider distanceslider;
    private Slider fieldofviewslider;

    @Test
    public void Camera3D() {
        System.out.println("SteerableCamera test");
        launch(new String[]{});
    }

    @Override
    public void start(Stage stage) {
        createUIcontrols();
        HBox hcontrols = new HBox(10,
                new VBox(5, new Label("Point to X"), xpointtoslider),
                new VBox(5, new Label("Point to Y"), ypointtoslider),
                new VBox(5, new Label("Point to Z"), zpointtoslider),
                new VBox(5, new Label("Angle"), angleslider),
                new VBox(5, new Label("Elevation"), elevationslider),
                new VBox(5, new Label("Distance"), distanceslider),
                new VBox(5, new Label("Field of View"), fieldofviewslider)
        );
        BorderPane borderpane = new BorderPane();
        borderpane.setBottom(hcontrols);
        Dimensions3D dimensions = new Dimensions3D(
                new HullDimensions3DBuilder().build(),
                new SparDimensions3DBuilder().build());
        BoatCoordinates coordinates = new BoatCoordinates();
        Group centrenode = new Group(
                new Boat3D(dimensions, coordinates),
                new AmbientLight(WHITE),
                steerablecamera = new SteerableCamera(100)
        );
        SubScene subscene = new SubScene(centrenode, 600, 600, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(steerablecamera.getCamera());
        steerablecamera.setAngle(angleslider.valueProperty());
        steerablecamera.setPointTo(xpointtoslider.valueProperty(), ypointtoslider.valueProperty(), zpointtoslider.valueProperty());
        steerablecamera.setElevation(elevationslider.valueProperty());
        steerablecamera.setDistance(distanceslider.valueProperty());
        steerablecamera.setFieldOfView(fieldofviewslider.valueProperty());

        borderpane.setCenter(subscene);
        Scene scene = new Scene(borderpane, 1000, 1000);
        stage.setTitle("Testing STEERABLECAMERA");
        stage.setScene(scene);
        stage.show();
    }
    
    private void createUIcontrols() {
        angleslider = createslider(-180, 180, 10, 30, 0);
        xpointtoslider = createslider(-20, 20, 1, 5, 0);
        ypointtoslider = createslider(-20, 20, 1, 5, 0);
        zpointtoslider = createslider(-20, 0, 1, 5, 0);
        elevationslider = createslider(0, 90, 10, 30, 90);
        distanceslider = createslider(0, 40, 1, 5, 20);
        fieldofviewslider = createslider(0, 180, 10, 30, 120);
    }

    private Slider createslider(double min, double max, double tickunit, double majortickunit, double initialvalue) {
        Slider slider = new Slider(min, max, initialvalue);
        slider.setOrientation(VERTICAL);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(majortickunit);
        slider.setBlockIncrement(tickunit);
        slider.setSnapToTicks(true);
        return slider;
    }
}
