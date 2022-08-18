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

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class BoatHull3D extends MeshView {

    public BoatHull3D(HullDimensions3D dimensions) {
        TriangleMesh mesh = new TriangleMesh();
        createColourReferences(mesh);
        int numberofPlanks = dimensions.sections[0].sectionpoints.length;
        boolean hasHardchines = dimensions.hashardchines;
        HullSection3D[] sections = dimensions.sections;
        int startpoint = createBowPoints(numberofPlanks, sections[0], mesh);
        int portindex = createPorthullMesh(dimensions.getPortHullColour(), dimensions.getPortDeckColour(), startpoint, numberofPlanks, hasHardchines, sections, mesh);
        int starboardindex = createStarboardhullMesh(dimensions.getStarboardHullColour(), dimensions.getStarboardDeckColour(), startpoint, numberofPlanks, hasHardchines, sections, mesh);
        createTransomHullMesh(dimensions.getTransomColour(), numberofPlanks, portindex, starboardindex, mesh);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream("colours.png")));
        setMesh(mesh);
        setDrawMode(DrawMode.FILL);
        setMaterial(material);
    }

    private void createColourReferences(TriangleMesh mesh) {
        for (var colour : Colour.values()) {
            mesh.getTexCoords().addAll(colour.getTexCoords());
        }
    }

    private int createBowPoints(int numberofplanks, HullSection3D bowsection, TriangleMesh mesh) {
        var meshpoints = mesh.getPoints();
        int startpoint = meshpoints.size() / 3;
        meshpoints.addAll(-bowsection.distancefrombow, 0, -bowsection.depthatkeel);
        meshpoints.addAll(-bowsection.distancefrombow, 0, -bowsection.depthatdeckcentreline);
        for (int ptindex = 0; ptindex < numberofplanks; ptindex++) {
            Point point = bowsection.sectionpoints[ptindex];
            meshpoints.addAll(-bowsection.distancefrombow, 0, -point.height);
        }
        return startpoint;
    }

    private int createPorthullMesh(Colour hullcolour, Colour deckcolour, int startpoint, int numberofplanks, boolean hasHardchines, HullSection3D[] sections, TriangleMesh mesh) {
        int hulltexcoord = hullcolour.ordinal();
        int decktexcoord = deckcolour.ordinal();
        var meshpoints = mesh.getPoints();
        var meshfaces = mesh.getFaces();
        var meshsmoothinggroups = mesh.getFaceSmoothingGroups();
        int leading = startpoint;
        int trailing;
        int smoothinggroup;
        for (int sectionindex = 1; sectionindex < sections.length; sectionindex++) {
            HullSection3D section = sections[sectionindex];
            int pl;
            int pt;
            int pnl = 0;
            int pnt = 0;
            trailing = meshpoints.size() / 3;
            meshpoints.addAll(-section.distancefrombow, 0, -section.depthatkeel);
            meshpoints.addAll(-section.distancefrombow, 0, -section.depthatdeckcentreline);
            pl = leading;
            pt = trailing;
            for (int ptindex = 0; ptindex < numberofplanks; ptindex++) {
                Point point = section.sectionpoints[ptindex];
                meshpoints.addAll(-section.distancefrombow, -point.width, -point.height);
                smoothinggroup = hasHardchines ? 4 << (ptindex * 2) : 4;
                pnl = leading + 2 + ptindex;
                pnt = trailing + 2 + ptindex;
                meshfaces.addAll(
                        pl, hulltexcoord, pt, hulltexcoord, pnt, hulltexcoord,
                        pl, hulltexcoord, pnt, hulltexcoord, pnl, hulltexcoord
                );
                meshsmoothinggroups.addAll(smoothinggroup, smoothinggroup);
                pl = pnl;
                pt = pnt;
            }
            // deck
            meshfaces.addAll(
                    pnl, decktexcoord, pnt, decktexcoord, trailing + 1, decktexcoord,
                    pnl, decktexcoord, trailing + 1, decktexcoord, leading + 1, decktexcoord
            );
            mesh.getFaceSmoothingGroups().addAll(1, 1);
            leading = trailing;
        }
        return leading;
    }

    private int createStarboardhullMesh(Colour hullcolour, Colour deckcolour, int startpoint, int numberofplanks, boolean hasHardchines, HullSection3D[] sections, TriangleMesh mesh) {
        int hulltexcoord = hullcolour.ordinal();
        int decktexcoord = deckcolour.ordinal();
        var meshpoints = mesh.getPoints();
        var meshfaces = mesh.getFaces();
        var meshsmoothinggroups = mesh.getFaceSmoothingGroups();
        int leading = startpoint;
        int trailing;
        int smoothinggroup;
        for (int sectionindex = 1; sectionindex < sections.length; sectionindex++) {
            HullSection3D section = sections[sectionindex];
            int pl;
            int pt;
            int pnl;
            int pnt;
            trailing = meshpoints.size() / 3;
            meshpoints.addAll(-section.distancefrombow, 0, -section.depthatkeel);
            meshpoints.addAll(-section.distancefrombow, 0, -section.depthatdeckcentreline);
            pl = leading;
            pt = trailing;
            for (int ptindex = 0; ptindex < numberofplanks; ptindex++) {
                Point point = section.sectionpoints[ptindex];
                meshpoints.addAll(-section.distancefrombow, point.width, -point.height);
                smoothinggroup = hasHardchines ? 8 << (ptindex * 2) : 8;
                pnl = leading + 2 + ptindex;
                pnt = trailing + 2 + ptindex;
                meshfaces.addAll(
                        pl, hulltexcoord, pnl, hulltexcoord, pnt, hulltexcoord,
                        pl, hulltexcoord, pnt, hulltexcoord, pt, hulltexcoord
                );
                meshsmoothinggroups.addAll(smoothinggroup, smoothinggroup);
                pl = pnl;
                pt = pnt;
            }
            // deck
            meshfaces.addAll(
                    pt, decktexcoord, pl, decktexcoord, trailing + 1, decktexcoord,
                    trailing + 1, decktexcoord, pl, decktexcoord, leading + 1, decktexcoord
            );
            meshsmoothinggroups.addAll(1, 1);
            leading = trailing;
        }
        return leading;
    }

    private void createTransomHullMesh(Colour colour, int numberofplanks, int portindex, int starboardindex, TriangleMesh mesh) {
        int texcoord = colour.ordinal();
        int pl = portindex;
        int pd = portindex + 1;
        for (int ptindex = 0; ptindex < numberofplanks; ptindex++) {
            int ph = portindex + 2 + ptindex;
            mesh.getFaces().addAll(pl, texcoord, pd, texcoord, ph, texcoord);
            mesh.getFaceSmoothingGroups().addAll(2);
            pl = ph;
        }
        pl = starboardindex;
        pd = starboardindex + 1;
        for (int ptindex = 0; ptindex < numberofplanks; ptindex++) {
            int ph = starboardindex + 2 + ptindex;
            mesh.getFaces().addAll(pd, texcoord, pl, texcoord, ph, texcoord);
            mesh.getFaceSmoothingGroups().addAll(2);
            pl = ph;
        }
    }

}
