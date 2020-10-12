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
package uk.theretiredprogrammer.sketch.controller;

import java.io.IOException;
import static java.lang.Double.SIZE;
import java.util.function.Consumer;
import uk.theretiredprogrammer.sketch.boats.Boat;
import uk.theretiredprogrammer.sketch.boats.Boats;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.course.Course;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.jfx.sketchdisplay.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.properties.PropertyMark;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;
import uk.theretiredprogrammer.sketch.strategy.BoatStrategies;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Painter {

    private final BoatStrategies boatstrategies;
    private final PropertySketch sketchproperty;
    private final WindFlow windflow;
    private final WaterFlow waterflow;
    private final Course course;
    private final Boats boats;
    private Consumer<String> writetostatusline = (m) -> {
    };

    public Painter(BoatStrategies boatstrategies, PropertySketch sketchproperty, WindFlow windflow, WaterFlow waterflow, Course course, Boats boats, Consumer<String> writetostatusline) {
        this.boatstrategies = boatstrategies;
        this.sketchproperty = sketchproperty;
        this.windflow = windflow;
        this.waterflow = waterflow;
        this.course = course;
        this.boats = boats;
        this.writetostatusline = writetostatusline;
    }

    public void paint(SketchPane canvas) {
        canvas.clear();
        try {
            displaydraw(canvas);
            windflowdraw(canvas);
            //waterflow.draw(canvas);
            coursedraw(canvas);
            boatsdraw(canvas);
        } catch (IOException ex) {
            writetostatusline.accept(ex.getLocalizedMessage());
        }
    }

    // displayparameters
    private void displaydraw(SketchPane canvas) throws IOException {
        canvas.drawfieldofplay(sketchproperty.getDisplay().getSailingarea());
    }

    //flow _ repeat for water and wind
    private void windflowdraw(SketchPane canvas) throws IOException {
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
                    canvas.displayWindGraphic(here, windflow.getFlow(here), sketchproperty.getWindshifts().getShowflowcolour());
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
    /// course
    private void coursedraw(SketchPane canvas) throws IOException {
        for (PropertyMark markproperty : sketchproperty.getMarks().getList()) {
            markdraw(canvas, markproperty);
        }
    }

    // mark
    private void markdraw(SketchPane canvas, PropertyMark markproperty) throws IOException {
        canvas.drawmark(markproperty.getLocation(), SIZE, 6, markproperty.getColour());
        Angle windAngle = windflow.getFlow(markproperty.getLocation()).getAngle();
        if (markproperty.isWindwardlaylines()) {
            canvas.drawwindwardlaylines(markproperty.getLocation(), windAngle,
                    markproperty.getLaylinelength(), markproperty.getLaylinecolour());
        }
        if (markproperty.isDownwindlaylines()) {
            canvas.drawleewardlaylines(markproperty.getLocation(), windAngle,
                    markproperty.getLaylinelength(), markproperty.getLaylinecolour());
        }
    }

    // boats
    private void boatsdraw(SketchPane canvas) throws IOException {
        boats.stream().forEach(boat -> boatdraw(canvas, boat));
    }

    //boat
    private void boatdraw(SketchPane canvas, Boat boat) {
        canvas.drawboat(boat.getProperty().getLocation(), boat.getProperty().getDirection(), boat.getProperty().getColour(),
                windflow.getFlow(boat.getProperty().getLocation()).getAngle(),
                boat.metrics.length, boat.metrics.width, boat.sailcolor);
    }
//        Angle relative = directionproperty.angleDiff(controllersupplier.getValue().windflow.getFlow(location).getAngle());
//        boolean onStarboard = relative.gt(ANGLE0);
//        Angle absrelative = relative.abs();
//        Angle sailRotation = absrelative.lteq(new Angle(45)) ? ANGLE0 : absrelative.sub(new Angle(45)).mult(2.0 / 3);
//        double l;
//        double w;
//        if (zoom > 4) {
//            l = metrics.getLength() * zoom;
//            w = metrics.getWidth() * zoom;
//        } else {
//            l = 13; // create a visible object
//            w = 5; // create a visible object
//        }
//        GeneralPath p = new GeneralPath();
//        p.moveTo(-l * 0.5, w * 0.45); //go to transom quarter
//        p.curveTo(-l * 0.2, w * 0.55, l * 0.2, l * 0.1, l * 0.5, 0);
//        p.curveTo(l * 0.2, -l * 0.1, -l * 0.2, -w * 0.55, -l * 0.5, -w * 0.45);
//        p.closePath(); // and the port side
//        GeneralPath sail = new GeneralPath();
//        if (onStarboard) {
//            sail.moveTo(0.0, 0);
//            sail.curveTo(-l * 0.2, l * 0.1, -l * 0.4, w * 0.4, -l * 0.7, w * 0.4);
//            sailRotation = sailRotation.negate();
//        } else {
//            sail.moveTo(0, 0);
//            sail.curveTo(-l * 0.2, -l * 0.1, -l * 0.4, -w * 0.4, -l * 0.7, -w * 0.4);
//        }
//        //
//        AffineTransform xform = gc.getTransform();
//        gc.translate(location.getX(), location.getY());
//        gc.scale(1 / zoom, 1 / zoom);
//        gc.rotate(ANGLE90.sub(directionproperty).getRadians());
//        gc.set(color);
//        gc.draw(p);
//        gc.fill(p);
//        gc.translate(l * 0.2, 0);
//        gc.rotate(sailRotation.getRadians());
//        gc.set(sailcolor);
//        gc.setStroke(new BasicStroke(2));
//        gc.draw(sail);
//        gc.setTransform(xform);
//
//        double MetresPerPixel = 1 / zoom;
//        BasicStroke stroke = new BasicStroke((float) (MetresPerPixel));
//        BasicStroke heavystroke = new BasicStroke((float) (MetresPerPixel * 3));
//        gc.set(trackcolor);
//        int count = 0;
//        synchronized (track) {
//            for (Location tp : track) {
//                gc.setStroke(count % 60 == 0 ? heavystroke : stroke);
//                Shape s = new Line2D.Double(tp.getX(), tp.getY(), tp.getX(), tp.getY());
//                gc.draw(s);
//                count++;
//            }
//        }
//    }
}
