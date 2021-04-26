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

public class BoatHull3D extends MeshView {

    public BoatHull3D(HullDimensions3D dimensions) {

        // now build the mesh
        TriangleMesh mesh = new TriangleMesh();
        var facesmoothinggroups = mesh.getFaceSmoothingGroups();
        mesh.getTexCoords().addAll(0, 0);
        // all hull points
        for (int section = 0; section < dimensions.getSectionlocations().length; section++) {
            float x = -dimensions.getSectionlocations()[section];
            for (int point = 0; point < dimensions.getSectiondimensions()[section].length / 2; point++) {
                float y = dimensions.getSectiondimensions()[section][point * 2];
                float z = dimensions.getSectiondimensions()[section][point * 2 + 1];
                mesh.getPoints().addAll(x, -y, z);
                mesh.getPoints().addAll(x, y, z);
            }
        }
        int deckclindex = mesh.getPoints().size() / 3;
        for (int section = 0; section < dimensions.getSectionlocations().length; section++) {
            mesh.getPoints().addAll(-dimensions.getSectionlocations()[section], 0f, dimensions.getDeckclheights()[section]);
        }
        // build hull mesh
        for (int surface = 0; surface < dimensions.getNumsurfaces(); surface++) {
            for (int section = 0; section < dimensions.getSectionlocations().length - 1; section++) {
                // port
                int lowerforward = section * (dimensions.getNumsurfaces() + 1) * 2 + surface * 2;
                int upperforward = lowerforward + 2;
                int lowerbackward = lowerforward + (dimensions.getNumsurfaces() + 1) * 2;
                int upperbackwards = lowerbackward + 2;
                mesh.getFaces().addAll(
                        //port
                        lowerforward, 0, lowerbackward, 0, upperforward, 0,
                        upperforward, 0, lowerbackward, 0, upperbackwards, 0,
                        //starboard
                        lowerforward + 1, 0, upperforward + 1, 0, lowerbackward + 1, 0,
                        upperforward + 1, 0, upperbackwards + 1, 0, lowerbackward + 1, 0
                );
                int pgroup = dimensions.isHardchines() ? 4 << (surface * 2) : 4;
                int sgroup = dimensions.isHardchines() ? 8 << (surface * 2) : 8;
                facesmoothinggroups.addAll(pgroup, pgroup, sgroup, sgroup);
            }
            // add the face smoothing groups (one port and one startboard
        }
        // build transom mesh
        int lastsection = dimensions.getSectionlocations().length - 1;
        for (int surface = 0; surface < dimensions.getNumsurfaces(); surface++) {
            int lower = lastsection * (dimensions.getNumsurfaces() + 1) * 2 + surface * 2;
            int upper = lower + 2;
            mesh.getFaces().addAll(
                    //port
                    upper, 0, lower, 0, deckclindex + lastsection, 0,
                    //starboard
                    lower + 1, 0, upper + 1, 0, deckclindex + lastsection, 0
            );
            facesmoothinggroups.addAll(2, 2);
        }
        // build deck mesh
        for (int section = 0; section < dimensions.getSectionlocations().length - 1; section++) {
            int outerforward = (section + 1) * (dimensions.getNumsurfaces() + 1) * 2 - 2;
            int innerforward = deckclindex + section;
            int outerbackward = outerforward + (dimensions.getNumsurfaces() + 1) * 2;
            int innerbackward = innerforward + 1;
            mesh.getFaces().addAll(
                    //port
                    outerforward, 0, outerbackward, 0, innerbackward, 0,
                    outerforward, 0, innerbackward, 0, innerforward, 0,
                    //starboard
                    outerforward + 1, 0, innerbackward, 0, outerbackward + 1, 0,
                    outerforward + 1, 0, innerforward, 0, innerbackward, 0
            );
            facesmoothinggroups.addAll(1, 1, 1, 1);
        }
        PhongMaterial material = new PhongMaterial(Color.LIGHTGRAY);
        setMesh(mesh);
        setDrawMode(DrawMode.FILL);
        setMaterial(material);
    }
}
