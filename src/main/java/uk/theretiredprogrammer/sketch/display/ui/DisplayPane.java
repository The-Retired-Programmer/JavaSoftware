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

import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.ui.DisplayContextMenu;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.control.strategy.BoatStrategies;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyBoat;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMark;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;
import uk.theretiredprogrammer.sketch.properties.ui.PropertyMapDialog;
import uk.theretiredprogrammer.sketch.properties.ui.PropertyMapPane;

public class DisplayPane extends Group {

    private final DisplayController controller;
    private double zoom;
    private Shapes2D shapebuilder;
    private Scale mainscale;
    private Translate maintranslate;
    

    public DisplayPane(DisplayController controller) { //PropertySketch sketchproperty, WindFlow windflow, WaterFlow waterflow, Boats boats, BoatStrategies strategies) {
        this.controller = controller;
        refreshParameters();
    }

    public final void refreshParameters() {
        PropertySketch sketchproperty = controller.getProperty();
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
        Area displayarea = controller.getProperty().getDisplayArea();
        Area sailingarea = controller.getProperty().getDisplay().getSailingarea();
        getChildren().addAll(
                Wrap.globalTransform(
                        Wrap.displayContextMenu(
                                shapebuilder.drawfieldofplay(displayarea, sailingarea),
                                UI.displayContextMenu(
                                        UI.contextMenuitem("Add Mark", (ev, contextmenu) -> addMark(ev, contextmenu)),
                                        UI.contextMenuitem("Add boat", (ev, contextmenu) -> addBoat(ev, contextmenu))
                                )
                        ),
                        maintranslate,
                        mainscale
                )
        );
    }

    private void addMark(ActionEvent ev, ContextMenu contextmenu) {
        if (contextmenu instanceof DisplayContextMenu displaycontextmenu) {
            double x = displaycontextmenu.getDisplayX();
            double y = displaycontextmenu.getDisplayY();
            PropertyMark newmark = new PropertyMark(new Location(x, y));
            if (PropertyMapDialog.showAndWait("Configure New Mark", new PropertyMapPane(newmark, "Mark"))) {
                // insert new property into sketchproperty
                controller.getProperty().getMarks().add(newmark);
            }
        }
    }

    private void addBoat(ActionEvent ev, ContextMenu contextmenu) {
        if (contextmenu instanceof DisplayContextMenu displaycontextmenu) {
            double x = displaycontextmenu.getDisplayX();
            double y = displaycontextmenu.getDisplayY();
            PropertyBoat newboat = new PropertyBoat(new Location(x, y));
            if (PropertyMapDialog.showAndWait("Configure New Boat", new PropertyMapPane(newboat, "Boat"))) {
                controller.getProperty().getBoats().add(newboat);
            }
        }
    }

    private void windflowdraw() {
        Area area = controller.getProperty().getDisplayArea();
        Location sw = area.getBottomleft();
        double westedge = sw.getX();
        double eastedge = westedge + area.getWidth();
        double southedge = sw.getY();
        double northedge = southedge + area.getHeight();
        if (controller.getProperty().getWindshifts().isShowflow()) {
            double showwindflowinterval = controller.getProperty().getWindshifts().getShowflowinterval();
            double x = westedge + showwindflowinterval;
            while (x < eastedge) {
                double y = southedge + showwindflowinterval;
                while (y < northedge) {
                    Location here = new Location(x, y);
                    getChildren().addAll(
                            Wrap.globalTransform(
                                    shapebuilder.displayWindGraphic(here, controller.windflow.getFlow(here), 10, controller.getProperty().getWindshifts().getShowflowcolour()),
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
        controller.getProperty().getMarks().getList().forEach(markproperty -> markdraw(markproperty));
    }

    private void laylinesdraw() {
        controller.getProperty().getMarks().getList().forEach(markproperty -> laylinesdraw(markproperty));
    }

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private void markdraw(PropertyMark markproperty) {
        getChildren().addAll(
                Wrap.globalTransform(
                        shapebuilder.drawmark(markproperty.getLocation(), SIZE, markproperty.getColour()),
                        maintranslate,
                        mainscale
                )
        );
    }

    private void laylinesdraw(PropertyMark markproperty) {
        Angle windAngle = controller.windflow.getFlow(markproperty.getLocation()).getAngle();
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
        controller.boats.stream().forEach(boat -> boatdraw(boat));
    }

    private void boatdraw(Boat boat) {
        getChildren().addAll(
                Wrap.globalTransform(
                        Wrap.contextMenu(
                                shapebuilder.drawboat(boat.getProperty().getLocation(), boat.getProperty().getDirection(), boat.getProperty().getColour(),
                                        controller.windflow.getFlow(boat.getProperty().getLocation()).getAngle(),
                                        boat.metrics.length, boat.metrics.width, boat.sailcolor),
                                UI.contextMenu(
                                        UI.menuitem("tack", ev -> tack(boat)),
                                        UI.menuitem("gybe", ev -> gybe(boat)),
                                        UI.menuitem("duplicate on opposite tack", ev -> duplicatetack(boat)),
                                        UI.menuitem("duplicate on opposite gybe", ev -> duplicategybe(boat))
                                ),
                                Cursor.CROSSHAIR
                        ),
                        maintranslate,
                        mainscale
                )
        );
    }

    private void tack(Boat boat) {
        PropertyBoat boatproperty = boat.getProperty();
        Location position = boatproperty.getLocation();
        SpeedPolar wind = controller.windflow.getFlow(position);
        Angle delta = wind.angleDiff(boatproperty.getDirection());
        if (delta.gt(ANGLE0)) {
            // anti clockwise to starboard tack
            Angle target = boat.getStarboardCloseHauledCourse(wind.getAngle());
            Decision decision = controller.boatstrategies.getStrategy(boat).decision;
            decision.setTURN(target, PORT);
        } else {
            // clockwise to port tack
            Angle target = boat.getPortCloseHauledCourse(wind.getAngle());
            Decision decision = controller.boatstrategies.getStrategy(boat).decision;
            decision.setTURN(target, STARBOARD);
        }
    }

    private void gybe(Boat boat) {
        PropertyBoat boatproperty = boat.getProperty();
        Location position = boatproperty.getLocation();
        SpeedPolar wind = controller.windflow.getFlow(position);
        Angle delta = wind.angleDiff(boatproperty.getDirection());
        if (delta.gt(ANGLE0)) {
            // clockwise to starboard gybe
            Angle target = boat.getStarboardReachingCourse(wind.getAngle());
            Decision decision = controller.boatstrategies.getStrategy(boat).decision;
            decision.setTURN(target, STARBOARD);
        } else {
            // anticlockwise to port gybe
            Angle target = boat.getPortReachingCourse(wind.getAngle());
            Decision decision = controller.boatstrategies.getStrategy(boat).decision;
            decision.setTURN(target, PORT);
        }
    }
    
    private void duplicatetack(Boat boat) {
        String newname = boat.getName()+"-1";
        PropertyBoat newboatproperty = new PropertyBoat(boat.getName()+"-1", boat.getProperty());
        controller.getProperty().getBoats().add(newboatproperty);
        Boat newboat = controller.boats.getBoat(newname);
        tack(newboat);
    }
    
    private void duplicategybe(Boat boat) {
        String newname = boat.getName()+"-1";
        PropertyBoat newboatproperty = new PropertyBoat(boat.getName()+"-1", boat.getProperty());
        controller.getProperty().getBoats().add(newboatproperty);
        Boat newboat = controller.boats.getBoat(newname);
        gybe(newboat);
    }
}
