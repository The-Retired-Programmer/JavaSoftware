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
import javafx.scene.Group;
import static javafx.scene.paint.Color.SILVER;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class BoatRig3D extends Group {

    private final Rotate boomrotate;

    public BoatRig3D(SparDimensions3D dimensions, DoubleProperty boomangle) {

        Cylinder mast = new Cylinder(dimensions.getDiameter() / 2, dimensions.getMastHeight());
        PhongMaterial rigmaterial = new PhongMaterial(SILVER);
        mast.setDrawMode(DrawMode.FILL);
        mast.setMaterial(rigmaterial);
        mast.getTransforms().addAll(
                new Rotate(90, Rotate.X_AXIS),
                new Translate(0f, dimensions.getMastHeight() / 2, 0f)
        );
        //
        Cylinder boom = new Cylinder(dimensions.getDiameter() / 2, dimensions.getBoomLength());
        boom.setDrawMode(DrawMode.FILL);
        boom.setMaterial(rigmaterial);
        boom.getTransforms().addAll(
                new Rotate(-90, Rotate.Z_AXIS),
                new Translate(0f, -(dimensions.getBoomLength() + dimensions.getDiameter()) / 2, dimensions.getGooseNeckHeight() - dimensions.getDiameter() / 2)
        );
        //
        this.getChildren().addAll(mast, boom);
        this.getTransforms().addAll(
                new Translate(-dimensions.getBowtoMast(), 0f, 0f),
                boomrotate = new Rotate(0, Rotate.Z_AXIS)
        );
        boomrotate.angleProperty().bind(boomangle);
    }

}
