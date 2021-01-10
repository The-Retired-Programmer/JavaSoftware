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

import javafx.scene.Group;
import static javafx.scene.paint.Color.SILVER;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class BoatRig3D extends Group {

    public BoatRig3D() {

        // SOLO
        float[] spardimensions = new float[]{0.857f, 5.932f, 0.050f, 0.902f, 2.693f};

        Cylinder mast = new Cylinder(spardimensions[2] / 2, spardimensions[1]);
        PhongMaterial rigmaterial = new PhongMaterial(SILVER);
        mast.setDrawMode(DrawMode.FILL);
        mast.setMaterial(rigmaterial);
        mast.getTransforms().addAll(
                new Rotate(90, Rotate.X_AXIS),
                new Translate(0f, spardimensions[1]/2,0f )
        );
        //
        Cylinder boom = new Cylinder(spardimensions[2] / 2, spardimensions[4]);
        boom.setDrawMode(DrawMode.FILL);
        boom.setMaterial(rigmaterial);
        boom.getTransforms().addAll(
                new Rotate(-90, Rotate.Z_AXIS),
                new Translate( 0f, -(spardimensions[4] + spardimensions[2]) / 2, spardimensions[3] -spardimensions[2] / 2)
        );
        //
        this.getChildren().addAll(mast, boom);
        this.getTransforms().add(new Translate(-spardimensions[0], 0f, 0f));
    }
}
