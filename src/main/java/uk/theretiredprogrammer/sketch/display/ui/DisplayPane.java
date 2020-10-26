/*
 * Copyright 2020 richard linsdale (richard at theretiredprogrammer.uk)
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

import javafx.scene.Group;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.strategy.BoatStrategies;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyBoat;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMark;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

public class DisplayPane extends Group {

    private final PropertySketch sketchproperty;
    private final WindFlow windflow;
    //private final WaterFlow waterflow;
    private final Boats boats;
    private double zoom;
    private Shapes2D shapebuilder;
    private Scale mainscale;
    private Translate maintranslate;
    private final BoatStrategies strategies;

    public DisplayPane(PropertySketch sketchproperty, WindFlow windflow, WaterFlow waterflow, Boats boats, BoatStrategies strategies) {
        this.sketchproperty = sketchproperty;
        this.windflow = windflow;
        //this.waterflow = waterflow;
        this.boats = boats;
        this.strategies = strategies;
        refreshrepaint();
    }

    public final void refreshrepaint() {
        this.zoom = sketchproperty.getDisplay().getZoom();
        mainscale = new Scale(zoom, -zoom);
        Area displayarea = sketchproperty.getDisplayArea();
        maintranslate = new Translate(-displayarea.getBottomleft().getX(), -displayarea.getHeight() - displayarea.getBottomleft().getY());
        shapebuilder = new Shapes2D(zoom);
        repaint();
    }

    public final void repaint() {
        getChildren().clear();
        displaydraw();
        marksdraw();
        windflowdraw();
        //waterflow.draw();
        laylinesdraw();
        boatsdraw();
    }

    private void displaydraw() {
        getChildren().addAll(
                Wrap.globalTransform(
                        shapebuilder.drawfieldofplay(sketchproperty.getDisplayArea(), sketchproperty.getDisplay().getSailingarea()),
                        maintranslate,
                        mainscale
                )
        );
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
                    getChildren().addAll(
                            Wrap.globalTransform(
                                    shapebuilder.displayWindGraphic(here, windflow.getFlow(here), 10, sketchproperty.getWindshifts().getShowflowcolour()),
                                    maintranslate,
                                    mainscale
                            )
                    );
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
    private void marksdraw() {
        sketchproperty.getMarks().getList().forEach(markproperty -> markdraw(markproperty));
    }

    private void laylinesdraw() {
        sketchproperty.getMarks().getList().forEach(markproperty -> laylinesdraw(markproperty));
    }

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private void markdraw(PropertyMark markproperty) {
        getChildren().addAll(
                Wrap.globalTransform(
                        Wrap.contextMenu(
                                shapebuilder.drawmark(markproperty.getLocation(), SIZE, markproperty.getColour()),
                                UI.contextMenu(
                                        UI.menuitem(markproperty.getName())
                                )
                        ),
                        maintranslate,
                        mainscale
                )
        );
    }

    private void laylinesdraw(PropertyMark markproperty) {
        Angle windAngle = windflow.getFlow(markproperty.getLocation()).getAngle();
        if (markproperty.isWindwardlaylines()) {
            getChildren().addAll(
                    Wrap.globalTransform(
                            shapebuilder.drawwindwardlaylines(markproperty.getLocation(), windAngle, markproperty.getLaylinelength(), markproperty.getLaylinecolour()),
                            maintranslate,
                            mainscale
                    )
            );
        }
        if (markproperty.isDownwindlaylines()) {
            getChildren().addAll(
                    Wrap.globalTransform(
                            shapebuilder.drawleewardlaylines(markproperty.getLocation(), windAngle, markproperty.getLaylinelength(), markproperty.getLaylinecolour()),
                            maintranslate,
                            mainscale
                    )
            );
        }
    }

    private void boatsdraw() {
        boats.stream().forEach(boat -> boatdraw(boat));
    }

    private void boatdraw(Boat boat) {
        getChildren().addAll(
                Wrap.globalTransform(
                        Wrap.contextMenu(
                                shapebuilder.drawboat(boat.getProperty().getLocation(), boat.getProperty().getDirection(), boat.getProperty().getColour(),
                                        windflow.getFlow(boat.getProperty().getLocation()).getAngle(),
                                        boat.metrics.length, boat.metrics.width, boat.sailcolor),
                                UI.contextMenu(
                                        UI.menuitem("tack", ev -> tack(boat)),
                                        UI.menuitem("gybe", ev -> gybe(boat))
                                )
                        ),
                        maintranslate,
                        mainscale
                )
        );
    }
    
    private void tack(Boat boat) {
        PropertyBoat boatproperty = boat.getProperty();
        Location position = boatproperty.getLocation();
        SpeedPolar wind = windflow.getFlow(position);
        Angle delta = wind.angleDiff(boatproperty.getDirection());
        if (delta.gt(ANGLE0)) {
            // anti clockwise to starboard tack
            Angle target = boat.getStarboardCloseHauledCourse(wind.getAngle());
            Decision decision = strategies.getStrategy(boat).decision;
            decision.setTURN(target, PORT);
        } else {
            // clockwise to port tack
            Angle target = boat.getPortCloseHauledCourse(wind.getAngle());
            Decision decision = strategies.getStrategy(boat).decision;
            decision.setTURN(target, STARBOARD);
        }
    }
    
     private void gybe(Boat boat) {
        PropertyBoat boatproperty = boat.getProperty();
        Location position = boatproperty.getLocation();
        SpeedPolar wind = windflow.getFlow(position);
        Angle delta = wind.angleDiff(boatproperty.getDirection());
        if (delta.gt(ANGLE0)) {
            // clockwise to starboard gybe
            Angle target = boat.getStarboardReachingCourse(wind.getAngle());
            Decision decision = strategies.getStrategy(boat).decision;
            decision.setTURN(target, STARBOARD);
        } else {
            // anticlockwise to port gybe
            Angle target = boat.getPortReachingCourse(wind.getAngle());
            Decision decision = strategies.getStrategy(boat).decision;
            decision.setTURN(target, PORT);
        }
    }
}
