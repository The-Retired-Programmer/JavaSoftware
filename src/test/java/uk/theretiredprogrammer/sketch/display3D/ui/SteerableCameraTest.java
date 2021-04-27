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
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.STEELBLUE;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.YELLOW;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat3D;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatCoordinates;
import uk.theretiredprogrammer.sketch.display.entity.boats.Dimensions3D;
import uk.theretiredprogrammer.sketch.display.entity.boats.HullDimensions3DBuilder;
import uk.theretiredprogrammer.sketch.display.entity.boats.SparDimensions3DBuilder;

public class SteerableCameraTest extends Application {

    private final float DISPLAYSIZE = 600;
    private final float BORDER = 25;
    //
    private SteerableCamera steerablecamera;
    //
    private Slider angleslider;
    private Slider xpointtoslider;
    private Slider ypointtoslider;
    private Slider zpointtoslider;
    private Slider elevationslider;
    private Slider distanceslider;
    private Slider fieldofviewslider;

//    @Test
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
        coordinates.setPosition(DISPLAYSIZE / 2, DISPLAYSIZE / 4);
        Boat3D boat = new Boat3D(dimensions, coordinates);
        boat.getTransforms().addAll(new Scale(25, 25, 25));
        //
        Group centrenode = new Group(
                createwater(),
                createmark(new PhongMaterial(YELLOW), 5, DISPLAYSIZE / 2, DISPLAYSIZE / 2),
                createmark(new PhongMaterial(RED), 5, BORDER, DISPLAYSIZE / 2),
                createmark(new PhongMaterial(GREEN), 5, DISPLAYSIZE - BORDER, DISPLAYSIZE / 2),
                createmark(new PhongMaterial(BLACK), 5, DISPLAYSIZE / 2, BORDER),
                createmark(new PhongMaterial(ORANGE), 5, DISPLAYSIZE / 2, DISPLAYSIZE - BORDER),
                boat,
                new AmbientLight(WHITE),
                steerablecamera = new SteerableCamera(DISPLAYSIZE * 2)
        );
        SubScene subscene = new SubScene(centrenode, DISPLAYSIZE, DISPLAYSIZE, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(steerablecamera.getCamera());
        steerablecamera.setAngle(angleslider.valueProperty());
        steerablecamera.setPointTo(xpointtoslider.valueProperty(), ypointtoslider.valueProperty(), zpointtoslider.valueProperty());
        steerablecamera.setElevation(elevationslider.valueProperty());
        steerablecamera.setDistance(distanceslider.valueProperty());
        steerablecamera.setFieldOfView(fieldofviewslider.valueProperty());

        borderpane.setCenter(subscene);
        Scene scene = new Scene(borderpane);
        stage.setTitle("Testing STEERABLECAMERA");
        stage.setScene(scene);
        stage.show();
    }

    private Box createwater() {
        Box water = new Box(DISPLAYSIZE, DISPLAYSIZE, 0.001);
        PhongMaterial watermaterial = new PhongMaterial(STEELBLUE);
        water.setDrawMode(DrawMode.FILL);
        water.setMaterial(watermaterial);
        water.setTranslateX(DISPLAYSIZE / 2);
        water.setTranslateY(DISPLAYSIZE / 2);
        return water;
    }

    private Sphere createmark(PhongMaterial markmaterial, double size, double xpos, double ypos) {
        Sphere mark = new Sphere(size);
        mark.setDrawMode(DrawMode.FILL);
        mark.setMaterial(markmaterial);
        mark.setTranslateX(xpos);
        mark.setTranslateY(ypos);
        return mark;
    }

    private void createUIcontrols() {
        angleslider = createslider(-180, 180, 10, 30, 0);
        xpointtoslider = createslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
        ypointtoslider = createslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
        zpointtoslider = createslider(-1000, 0, 100, 250, 0);
        elevationslider = createslider(0, 90, 10, 30, 90);
        distanceslider = createslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
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
