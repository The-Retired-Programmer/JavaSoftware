/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.display.ui;

import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMark;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DisplayPainter extends DisplayPainter2D {

    private final PropertySketch sketchproperty;
    private final WindFlow windflow;
    //private final WaterFlow waterflow;
    private final Boats boats;

    public DisplayPainter(PropertySketch sketchproperty, WindFlow windflow, WaterFlow waterflow, Boats boats) {
        this.sketchproperty = sketchproperty;
        this.windflow = windflow;
        //this.waterflow = waterflow;
        this.boats = boats;
        initialise(sketchproperty.getDisplayArea(), sketchproperty.getDisplay().getZoom());
        repaint();
    }

    public final void repaint() {
        clear();
        displaydraw();
        windflowdraw();
        //waterflow.draw();
        coursedraw();
        boatsdraw();
    }

    private void displaydraw() {
        drawfieldofplay(sketchproperty.getDisplay().getSailingarea());
    }

    private void windflowdraw() {
        Area area = sketchproperty.getDisplay().getDisplayarea();
        Location sw = area.getBottomleft();
        double westedge = sw.getX();
        double eastedge = westedge + area.getWidth();
        double southedge = sw.getY();
        double northedge = southedge + area.getHeight();
        if (sketchproperty.getWindshifts().isShowflow()) {
            double showwindflowinterval = sketchproperty.getWindshifts().getShowflowinterval();
            double x = westedge + showwindflowinterval;
            while (x < eastedge) {
                double y = southedge + showwindflowinterval;
                while (y < northedge) {
                    Location here = new Location(x, y);
                    displayWindGraphic(here, windflow.getFlow(here), sketchproperty.getWindshifts().getShowflowcolour());
                    y += showwindflowinterval;
                }
                x += showwindflowinterval;
            }
        }
    }

//    private void displayWindGraphic(SketchWindow canvas, double zoom, double x, double y) throws IOException {
//        GeneralPath p = new GeneralPath();
//        p.moveTo(0, 15);
//        p.lineTo(0, -15);
//        p.moveTo(4, 7);
//        p.lineTo(0, 15);
//        p.lineTo(-4, 7);
//        //
//        AffineTransform xform = gc.getTransform();
//        gc.translate(x, y);
//        gc.scale(1 / pixelsPerMetre, -1 / pixelsPerMetre);
//        SpeedPolar flow = getFlow(new Location(x, y));
//        gc.rotate(flow.getAngle().getRadians());
//        gc.setColor(showflowcolor);
//        gc.setStroke(new BasicStroke(1));
//        gc.draw(p);
//        //
//        gc.setFont(new Font("Sans Serif", Font.PLAIN, 10));
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(1);
//        nf.setMinimumFractionDigits(1);
//        String windspeedText = nf.format(flow.getSpeed());
//        if (flow.getAngle().isPositive()) {
//            gc.translate(-2, 4);
//            gc.rotate(-Math.PI / 2);
//        } else {
//            gc.translate(+2, -15);
//            gc.rotate(Math.PI / 2);
//        }
//        gc.drawString(windspeedText, 0, 0);
//        gc.setTransform(xform);
//    }
    private void coursedraw() {
        sketchproperty.getMarks().getList().forEach(markproperty -> markdraw(markproperty));
    }

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private void markdraw(PropertyMark markproperty) {
        drawmark(markproperty.getLocation(), SIZE, 6, markproperty.getColour());
        Angle windAngle = windflow.getFlow(markproperty.getLocation()).getAngle();
        if (markproperty.isWindwardlaylines()) {
            drawwindwardlaylines(markproperty.getLocation(), windAngle,
                    markproperty.getLaylinelength(), markproperty.getLaylinecolour());
        }
        if (markproperty.isDownwindlaylines()) {
            drawleewardlaylines(markproperty.getLocation(), windAngle,
                    markproperty.getLaylinelength(), markproperty.getLaylinecolour());
        }
    }

    private void boatsdraw() {
        boats.stream().forEach(boat -> boatdraw(boat));
    }

    private void boatdraw(Boat boat) {
        drawboat(boat.getProperty().getLocation(), boat.getProperty().getDirection(), boat.getProperty().getColour(),
                windflow.getFlow(boat.getProperty().getLocation()).getAngle(),
                boat.metrics.length, boat.metrics.width, boat.sailcolor);
    }
}
