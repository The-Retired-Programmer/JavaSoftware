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

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import static javafx.scene.paint.Color.STEELBLUE;
import static javafx.scene.paint.Color.YELLOW;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import static javafx.scene.transform.Rotate.X_AXIS;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

public class Boat3DTest extends Application {

    private final float WIDTH = 1400;
    private final float HEIGHT = 1000;
    private Boat3D boat;
    private Rotate camerarotate;
    private final DoubleProperty cameraangle = new SimpleDoubleProperty(225f);
    private final Dimensions3D dimensions = new Dimensions3D(
            new HullDimensions3DBuilder().build(),
            new SparDimensions3DBuilder().build());
    private final BoatCoordinates coordinates = new BoatCoordinates();

    @Test
    public void displayBoatRig3D() {
        System.out.println("draw 3D Boat");
        launch(new String[]{});
    }

    @Override
    public void start(Stage stage) {

        boat = new Boat3D(dimensions, coordinates);

        Camera camera = new PerspectiveCamera(true);
        //camera.setNearClip(10f);
        //camera.setFarClip(100f);
        camera.getTransforms().addAll(
                camerarotate = new Rotate(0f, X_AXIS),
                new Translate(0f, 0f, -30f)
        );
        camerarotate.angleProperty().bind(cameraangle);
        Box water = new Box(30, 30, 0.001);
        PhongMaterial watermaterial = new PhongMaterial(STEELBLUE);
        water.setDrawMode(DrawMode.FILL);
        water.setMaterial(watermaterial);
        //
        PhongMaterial markmaterial = new PhongMaterial(YELLOW);
        water.setDrawMode(DrawMode.FILL);
        water.setMaterial(watermaterial);
        Sphere mark = new Sphere(0.3);
        mark.setDrawMode(DrawMode.FILL);
        mark.setMaterial(markmaterial);
        Group waterg = new Group(water, boat, mark);
        Scene scene = new Scene(waterg, WIDTH, HEIGHT, true);
        scene.setCamera(camera);
        initMouseControl(boat, scene, stage);
        stage.setTitle("Testing 3D Boat");
        stage.setScene(scene);
        stage.show();
    }

    private void camerup(double delta) {
        cameraangle.set(cameraangle.get() - delta);
    }

    private void cameradown(double delta) {
        cameraangle.set(cameraangle.get() + delta);
    }

    private void initMouseControl(Group group, Scene scene, Stage stage) {

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case P ->
                    coordinates.setAngle(coordinates.getAngleProperty().get() - 10);
                case S ->
                    coordinates.setAngle(coordinates.getAngleProperty().get() + 10);
                case UP ->
                    camerup(10);
                case DOWN ->
                    cameradown(10);
                case F ->
                    move(2);
                case B ->
                    move(-2);
            }
        });
    }

    private void move(double delta) {
        double radians = Math.toRadians(coordinates.getAngleProperty().get());
        coordinates.setPosition(
                coordinates.getXProperty().get() + delta * Math.sin(radians),
                coordinates.getYProperty().get() + delta * Math.cos(radians));
    }
}
