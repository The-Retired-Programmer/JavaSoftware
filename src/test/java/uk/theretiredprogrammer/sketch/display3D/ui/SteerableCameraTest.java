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
import javafx.scene.control.CheckBox;
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
    private Slider sizeslider;
    private Slider rotationslider;
    private Slider xlocationslider;
    private Slider ylocationslider;
    private Slider heightrotationslider;
    private Slider heightscaleslider;
    private Slider fieldofviewslider;
    private CheckBox verticalfieldofviewswitch;

//    @Test
    public void Camera3D() {
        System.out.println("SteerableCamera test");
        launch(new String[]{});
    }

    @Override
    public void start(Stage stage) {
        createUIcontrols();
        HBox hcontrols = new HBox(10,
                new Label("Rotation"), rotationslider,
                new Label("Size"), sizeslider,
                new Label("X"), xlocationslider,
                new Label("Field of View"), fieldofviewslider,
                new Label("Vertical Field of View"), verticalfieldofviewswitch
        );
        BorderPane borderpane = new BorderPane();
        borderpane.setBottom(hcontrols);
        VBox vcontrols = new VBox(10,
                new Label("Y"), ylocationslider,
                new Label("Height Rotation"), heightrotationslider,
                new Label("Height Scale"), heightscaleslider
        );
        borderpane.setRight(vcontrols);
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
                steerablecamera = new SteerableCamera()
        );
        SubScene subscene = new SubScene(centrenode, DISPLAYSIZE, DISPLAYSIZE, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(steerablecamera.getCamera());
        steerablecamera.bindBidirectionaltoCameraViewRotation(rotationslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraViewScale(sizeslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraViewLocation(xlocationslider.valueProperty(), ylocationslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraHeightRotation(heightrotationslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraHeightScale(heightscaleslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraFieldOfView(fieldofviewslider.valueProperty());
        steerablecamera.bindBidirectionaltoCameraVerticalFieldofView(verticalfieldofviewswitch.selectedProperty());

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
        sizeslider = createHslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
        rotationslider = createHslider(-180, 180, 10, 30, 0);
        xlocationslider = createHslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
        ylocationslider = createVslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
        heightrotationslider = createVslider(0, 90, 10, 30, 0);
        heightscaleslider = createVslider(0, 1, 0.1, 0.2, 1);
        fieldofviewslider = createHslider(0, 180, 10, 30, 90);
        verticalfieldofviewswitch = createcheckbox(true);
    }

    private Slider createHslider(double min, double max, double tickunit, double majortickunit, double initialvalue) {
        Slider slider = new Slider(min, max, initialvalue);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(majortickunit);
        slider.setBlockIncrement(tickunit);
        slider.setSnapToTicks(true);
        return slider;
    }

    private Slider createVslider(double min, double max, double tickunit, double majortickunit, double initialvalue) {
        Slider slider = new Slider(min, max, initialvalue);
        slider.setOrientation(VERTICAL);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(majortickunit);
        slider.setBlockIncrement(tickunit);
        slider.setSnapToTicks(true);
        return slider;
    }

    private CheckBox createcheckbox(boolean initialvalue) {
        CheckBox cbox = new CheckBox();
        cbox.setSelected(initialvalue);
        return cbox;
    }
}
