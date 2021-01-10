/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class BoatHull3D extends MeshView {

    public BoatHull3D() {

        // testing the potential metrics
        // made-up for testing
//        int numsurfaces = 2;
//        float[] sectionlocations = new float[] { 0f, 1.2f, 2.5f, 3f};
//        float[][] sectiondimensions = new float[][]{
//            { 0f, 0f, 0f, 0f, 0f, 0.5f},
//            { 0f, -0.05f, 0.3f, 0f, 0.6f, 0.4f},
//            { 0f, -0.1f, 0.4f, 0f, 0.75f, 0.35f},
//            { 0f, 0f, 0.4f, 0.1f, 0.6f, 0.3f}
//        };
//        float[] deckclheights = new float[]{ 0.5f, 0f ,  0f ,0.15f};
//        boolean hardchines = false;
        // START SOLO
        int numsurfaces = 3;
        float[] sectionlocations = new float[]{0f, 0.728f, 1.338f, 1.948f, 2.558f, 3.168f, 3.778f};
        float[][] sectiondimensions = new float[][]{
            {0f, 00.0f, 0f, -00.1f, 0f, -00.2f, 0f, -00.31f},
            {0f, 0.061f, 0.241f, -0.038f, 0.363f, -0.159f, 0.469f, -0.324f},
            {0f, 0.105f, 0.398f, 0.014f, 0.506f, -0.097f, 00.619f, -00.334f},
            {0f, 0.115f, 0.489f, 0.045f, 0.641f, -0.076f, 0.749f, -0.345f},
            {0f, 0.096f, 0.503f, 0.025f, 00.640f, -00.096f, 0.744f, -00.345f},// need estimates for upper chine and sheerline
            {0f, 0.051f, 0.458f, -0.013f, 00.550f, -00.116f, 00.630f, -00.305f}, // need estimates for upper chine and sheerline
            {0f, 0f, 0.33f, -0.05f, 0.448f, -0.136f, 0.505f, -0.28f}
        };
        float[] deckclheights = new float[]{-00.31f, -00.36f, -00.39f, -00.3f, -00.3f, -000.3f, -0.3f};
        boolean hardchines = true;
        // END SOLO
        // now build the mesh
        TriangleMesh mesh = new TriangleMesh();
        var facesmoothinggroups = mesh.getFaceSmoothingGroups();
        mesh.getTexCoords().addAll(0, 0);
        // all hull points
        for (int section = 0; section < sectionlocations.length; section++) {
            float y = sectionlocations[section];
            for (int point = 0; point < sectiondimensions[section].length / 2; point++) {
                float x = sectiondimensions[section][point * 2];
                float z = sectiondimensions[section][point * 2 + 1];
                mesh.getPoints().addAll(-x, y, z);
                mesh.getPoints().addAll(x, y, z);
            }
        }
        int deckclindex = mesh.getPoints().size() / 3;
        for (int section = 0; section < sectionlocations.length; section++) {
            mesh.getPoints().addAll(0f, sectionlocations[section], deckclheights[section]);
        }
        // build hull mesh
        for (int surface = 0; surface < numsurfaces; surface++) {
            for (int section = 0; section < sectionlocations.length - 1; section++) {
                // port
                int lowerforward = section * (numsurfaces + 1) * 2 + surface * 2;
                int upperforward = lowerforward + 2;
                int lowerbackward = lowerforward + (numsurfaces + 1) * 2;
                int upperbackwards = lowerbackward + 2;
                mesh.getFaces().addAll(
                        //port
                        lowerforward, 0, lowerbackward, 0, upperforward, 0,
                        upperforward, 0, lowerbackward, 0, upperbackwards, 0,
                        //starboard
                        lowerforward + 1, 0, upperforward + 1, 0, lowerbackward + 1, 0,
                        upperforward + 1, 0, upperbackwards + 1, 0, lowerbackward + 1, 0
                );
                int pgroup = hardchines ? 4<<(surface * 2) : 4;
                int sgroup = hardchines ? 8<<(surface * 2) : 8;
                facesmoothinggroups.addAll(pgroup,pgroup, sgroup, sgroup);
            }
            // add the face smoothing groups (one port and one startboard
        }
        // build transom mesh
        int lastsection = sectionlocations.length - 1;
        for (int surface = 0; surface < numsurfaces; surface++) {
            int lower = lastsection * (numsurfaces + 1) * 2 + surface * 2;
            int upper = lower + 2;
            mesh.getFaces().addAll(
                    //port
                    upper, 0, lower, 0, deckclindex + lastsection, 0,
                    //starboard
                    lower + 1, 0, upper + 1, 0, deckclindex + lastsection, 0
            );
            facesmoothinggroups.addAll(2,2);
        }
        // build deck mesh
        for (int section = 0; section < sectionlocations.length - 1; section++) {
            int outerforward = (section + 1) * (numsurfaces + 1) * 2 - 2;
            int innerforward = deckclindex + section;
            int outerbackward = outerforward + (numsurfaces + 1) * 2;
            int innerbackward = innerforward + 1;
            mesh.getFaces().addAll(
                    //port
                    outerforward, 0, outerbackward, 0, innerbackward, 0,
                    outerforward, 0, innerbackward, 0, innerforward, 0,
                    //starboard
                    outerforward + 1, 0, innerbackward, 0, outerbackward + 1, 0,
                    outerforward + 1, 0, innerforward, 0, innerbackward, 0
            );
            facesmoothinggroups.addAll(1,1, 1,1);
        }
        PhongMaterial material = new PhongMaterial(Color.LIGHTGRAY);
        setMesh(mesh);
        setDrawMode(DrawMode.FILL);
        setMaterial(material);
        this.getTransforms().addAll(
                new Rotate(180, Rotate.Y_AXIS),
                new Rotate(-90, Rotate.Z_AXIS)
        );

    }
}
