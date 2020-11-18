/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import uk.theretiredprogrammer.sketch.core.ui.DisplayContextMenu;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatFactory;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.Mark;
import uk.theretiredprogrammer.sketch.properties.ui.PropertyMapDialog;
import uk.theretiredprogrammer.sketch.properties.ui.PropertyMapPane;

public class DisplayPane extends Group {

    private final DisplayController controller;
    private double zoom;
    private Shapes2D shapebuilder;
    private Scale mainscale;
    private Translate maintranslate;

    public DisplayPane(DisplayController controller) {
        this.controller = controller;
        refreshParameters();
    }

    public final void refreshParameters() {
        SketchModel sketchproperty = controller.getProperty();
        this.zoom = sketchproperty.getDisplay().getZoom();
        mainscale = new Scale(zoom, -zoom);
        PropertyArea displayarea = sketchproperty.getDisplayArea();
        maintranslate = new Translate(-displayarea.getLocationProperty().getX(), -displayarea.getHeight() - displayarea.getLocationProperty().getY());
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
        PropertyArea displayarea = controller.getProperty().getDisplayArea();
        PropertyArea sailingarea = controller.getProperty().getDisplay().getSailingarea();
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
            Mark newmark = new Mark(new PropertyLocation(x, y));
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
            SketchModel model = controller.getProperty();
            Boat newboat = BoatFactory.createBoat("laser2", new PropertyLocation(x, y),
                    new CurrentLeg(model.getCourse()),
                    model.getWindFlow(),
                    model.getWaterFlow());
            if (PropertyMapDialog.showAndWait("Configure New Boat", new PropertyMapPane(newboat, "Boat"))) {
                controller.getProperty().getBoats().add(newboat);
            }
        }
    }

    private void windflowdraw() {
        PropertyArea area = controller.getProperty().getDisplayArea();
        PropertyLocation sw = area.getLocationProperty();
        double westedge = sw.getX();
        double eastedge = westedge + area.getWidth();
        double southedge = sw.getY();
        double northedge = southedge + area.getHeight();
        if (controller.getProperty().getWindFlow().getShiftsproperty().isShowflow()) {
            double showwindflowinterval = controller.getProperty().getWindFlow().getShiftsproperty().getShowflowinterval();
            double x = westedge + showwindflowinterval;
            while (x < eastedge) {
                double y = southedge + showwindflowinterval;
                while (y < northedge) {
                    PropertyLocation here = new PropertyLocation(x, y);
                    getChildren().addAll(
                            Wrap.globalTransform(
                                    shapebuilder.displayWindGraphic(here, controller.windflow.getFlow(here), 10, controller.getProperty().getWindFlow().getShiftsproperty().getShowflowcolour()),
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
//        PropertySpeedVector flow = getFlow(new PropertyLocation(x, y));
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
        controller.getProperty().getMarks().stream().forEach(mark -> markdraw(mark));
    }

    private void laylinesdraw() {
        controller.getProperty().getMarks().stream().forEach(mark -> laylinesdraw(mark));
    }

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private void markdraw(Mark mark) {
        getChildren().addAll(
                Wrap.globalTransform(
                        shapebuilder.drawmark(mark.getLocation(), SIZE, mark.getColour()),
                        maintranslate,
                        mainscale
                )
        );
    }

    private void laylinesdraw(Mark mark) {
        PropertyDegrees windAngle = controller.windflow.getFlow(mark.getLocation()).getDegreesProperty();
        if (mark.isWindwardlaylines()) {
            getChildren().addAll(
                    Wrap.globalTransform(
                            shapebuilder.drawwindwardlaylines(mark.getLocation(), windAngle, mark.getLaylinelength(), mark.getLaylinecolour()),
                            maintranslate,
                            mainscale
                    )
            );
        }
        if (mark.isDownwindlaylines()) {
            getChildren().addAll(
                    Wrap.globalTransform(
                            shapebuilder.drawleewardlaylines(mark.getLocation(), windAngle, mark.getLaylinelength(), mark.getLaylinecolour()),
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
                                shapebuilder.drawboat(boat.getLocation(), boat.getDirection(), boat.getColour(),
                                        controller.windflow.getFlow(boat.getLocation()).getDegreesProperty(),
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
        CurrentLeg leg = boat.getCurrentLeg();
        PropertyLocation position = boat.getLocation();
        PropertySpeedVector wind = controller.windflow.getFlow(position);
        PropertyDegrees delta = wind.degreesDiff(boat.getDirection());
        if (delta.gt(0)) {
            // anti clockwise to starboard tack
            PropertyDegrees target = boat.getStarboardCloseHauledCourse(wind.getDegreesProperty());
            Decision decision = leg.getDecision();
            decision.setTURN(target, PORT);
        } else {
            // clockwise to port tack
            PropertyDegrees target = boat.getPortCloseHauledCourse(wind.getDegreesProperty());
            Decision decision = leg.getDecision();
            decision.setTURN(target, STARBOARD);
        }
    }

    private void gybe(Boat boat) {
        CurrentLeg leg = boat.getCurrentLeg();
        PropertyLocation position = boat.getLocation();
        PropertySpeedVector wind = controller.windflow.getFlow(position);
        PropertyDegrees delta = wind.degreesDiff(boat.getDirection());
        if (delta.gt(0)) {
            // clockwise to starboard gybe
            PropertyDegrees target = boat.getStarboardReachingCourse(wind.getDegreesProperty());
            Decision decision = leg.getDecision();
            decision.setTURN(target, STARBOARD);
        } else {
            // anticlockwise to port gybe
            PropertyDegrees target = boat.getPortReachingCourse(wind.getDegreesProperty());
            Decision decision = leg.getDecision();
            decision.setTURN(target, PORT);
        }
    }

    private void duplicatetack(Boat boat) {
        Boat newboat = BoatFactory.cloneBoat(boat.getName() + "-1", boat);
        controller.getProperty().getBoats().add(newboat);
        tack(newboat);
    }

    private void duplicategybe(Boat boat) {
        Boat newboat = BoatFactory.cloneBoat(boat.getName() + "-1", boat);
        controller.getProperty().getBoats().add(newboat);
        gybe(newboat);
    }
}
