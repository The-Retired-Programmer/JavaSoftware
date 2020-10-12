/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.jfx.sketchdisplay;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.jfx.AbstractWindow;
import uk.theretiredprogrammer.sketch.jfx.UI;
import uk.theretiredprogrammer.sketch.jfx.decisiondisplay.DecisionDisplayWindow;
import uk.theretiredprogrammer.sketch.jfx.propertiesdisplay.PropertiesWindow;
import uk.theretiredprogrammer.sketch.jfx.sketchdisplay.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.controller.Controller;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author richard
 */
public class SketchWindow extends AbstractWindow {

    public static SketchWindow create(String fn, Controller controller, PropertySketch sketchproperty, AbstractWindow parent) {
        return new SketchWindow(fn, controller, sketchproperty, parent);
    }

    private final SketchPane canvas;
    private final Text timetext;
    private DecisionDisplayWindow decisiondisplaywindow = null;

    private SketchWindow(String fn, Controller controller, PropertySketch sketchproperty, AbstractWindow parent) {
        super(SketchWindow.class, parent);
        setTitle("SKETCH Scenario Viewer - " + fn);
        setDefaultWindow();
        addtoMenubar(
                UI.menu("Control",
                        UI.menuitem("Start", actionEvent -> controller.start()),
                        UI.menuitem("Pause", actionEvent -> controller.stop()),
                        UI.menuitem("Reset", actionEvent -> controller.reset())
                ),
                UI.menu("Window Management",
                        UI.menuitem("Show Properties", actionEvent -> PropertiesWindow.create(fn, controller, controller.getProperty(), SketchWindow.this)),
                        UI.menuitem("Show Decision Log", actionEvent -> showDecisionLog(controller, fn)),
                        UI.menuitem("Show Filtered Decision Log", actionEvent -> showFilteredDecisionLog(controller, fn))
                )
        );
        addtoToolbar(
                UI.toolbarButton("control_play_blue.png", "Start", actionEvent -> controller.start()),
                UI.toolbarButton("control_pause_blue.png", "Pause", actionEvent -> controller.stop()),
                UI.toolbarButton("control_rewind_blue.png", "Reset", actionEvent -> controller.reset()),
                UI.toolbarButton("table_save.png", "Save Properties", actionEvent -> save(controller,
                "/Users/richard/SKETCHSAVE/save.json")),
                UI.toolbarButton("table.png", "Show Properties", actionEvent -> PropertiesWindow.create(fn, controller, controller.getProperty(), SketchWindow.this)),
                UI.toolbarButton("script.png", "Show Decision Log", actionEvent -> showDecisionLog(controller, fn)),
                UI.toolbarButton("script_code.png", "Show Filtered Decision Log", actionEvent -> showFilteredDecisionLog(controller, fn)),
                timetext = new Text("      ")
        );
        Group group = new Group();
        canvas = new SketchPane(sketchproperty.getDisplay().getDisplayarea(), sketchproperty.getDisplay().getZoom());
        controller
                .setOnSketchChange(() -> Platform.runLater(() -> controller.paint(canvas)))
                .setOnTimeChange((seconds) -> Platform.runLater(() -> updateTimeField(seconds)))
                .setWritetoStatusLine((s) -> Platform.runLater(() -> statusbarDisplay(s)));
        group.getChildren().add(canvas);
        controller.paint(canvas);
        setContent(group, SCROLLABLE);
        show();
    }

    private boolean save(Controller controller, String fn) {
        Path path = Path.of(fn);
        JsonObject jobj = controller.getProperty().toJson();
        if (path != null) {
            //Files.move(path, path.resolveSibling(path.getFileName() + ".v" + fromversion));
            try ( JsonWriter jsonWriter = Json.createWriter(Files.newOutputStream(path))) {
                jsonWriter.write(jobj);
            } catch (IOException ex) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void showDecisionLog(Controller controller, String fn) {
        setupDecisionLogWindow(controller, fn);
        controller.displaylog();
    }

    private void showFilteredDecisionLog(Controller controller, String fn) {
        setupDecisionLogWindow(controller, fn);
        controller.displayfilteredlog("SELECTED");
    }

    private void setupDecisionLogWindow(Controller controller, String fn) {
        if (decisiondisplaywindow == null) {
            decisiondisplaywindow = DecisionDisplayWindow.create(fn, SketchWindow.this);
            controller.setShowDecisionLine((l) -> decisiondisplaywindow.writeline(l));
        }
        decisiondisplaywindow.clear();
    }

    private void updateTimeField(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        timetext.setText(Integer.toString(mins) + ":" + ss);
    }

    public class SketchPane extends Canvas {

        private final double zoom;
        private final GraphicsContext gc;

        public SketchPane(Area canvasarea, double zoom) {
            this.zoom = zoom;
            setWidth(canvasarea.getWidth() * zoom);
            setHeight(canvasarea.getHeight() * zoom);
            gc = getGraphicsContext2D();
            gc.scale(zoom, -zoom);
            gc.translate(-canvasarea.getBottomleft().getX(), -canvasarea.getHeight() - canvasarea.getBottomleft().getY());
            drawrectangle(canvasarea, Color.OLIVEDRAB);
        }

        public void clear() {
            getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        }

        public void drawfieldofplay(Area sailingarea) {
            drawrectangle(sailingarea, Color.LIGHTSEAGREEN);
        }

        public void drawmark(Location location, double diameter, double minimumDiameterPixels, Color fill) {
            double minimumDiameter = minimumDiameterPixels / zoom;
            if (diameter < minimumDiameter) {
                diameter = minimumDiameter;
            }
            drawcircle(location, diameter, fill);
        }

        public void drawwindwardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
            final Angle WINDWARDLAYLINEANGLE = new Angle(135);
            drawLine(location, laylinelength, windAngle.add(WINDWARDLAYLINEANGLE), 1, laylinecolour);
            drawLine(location, laylinelength, windAngle.sub(WINDWARDLAYLINEANGLE), 1, laylinecolour);
        }

        public void drawleewardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
            final Angle LEEWARDLAYLINEANGLE = new Angle(45);
            drawLine(location, laylinelength, windAngle.add(LEEWARDLAYLINEANGLE), 1, laylinecolour);
            drawLine(location, laylinelength, windAngle.sub(LEEWARDLAYLINEANGLE), 1, laylinecolour);
        }

        public void drawboat(Location location, Angle direction, Color fill, Angle winddirection,
                double length, double width, Color sailcolour) {
            Angle relative = direction.angleDiff(winddirection);
            boolean onStarboard = relative.lt(ANGLE0);
            Angle absrelative = relative.abs();
            Angle sailRotation = absrelative.lteq(new Angle(45)) ? ANGLE0 : absrelative.sub(new Angle(45)).mult(2.0 / 3);
            sailRotation = sailRotation.negateif(!onStarboard);
            if (zoom < 5) { // create a visible object
                length = 17 / zoom;
                width = 7 / zoom;
            }
            //
            gc.save();
            gc.translate(location.getX(), location.getY());
            gc.rotate(ANGLE90.sub(direction).getDegrees());
            //
            gc.setFill(fill);
            gc.beginPath();
            gc.moveTo(-length * 0.5, width * 0.45); //go to transom quarter
            gc.bezierCurveTo(-length * 0.2, width * 0.55, length * 0.2, length * 0.1, length * 0.5, 0);
            gc.bezierCurveTo(length * 0.2, -length * 0.1, -length * 0.2, -width * 0.55, -length * 0.5, -width * 0.45);
            gc.closePath(); // and the port side
            gc.fill();
            //
            gc.setStroke(sailcolour);
            gc.setLineWidth(2 / zoom);
            gc.translate(length * 0.2, 0);
            gc.rotate(sailRotation.getDegrees());
            gc.beginPath();
            if (onStarboard) {
                gc.moveTo(0, 0);
                gc.bezierCurveTo(-length * 0.2, -length * 0.1, -length * 0.4, -width * 0.4, -length * 0.7, -width * 0.4);
            } else {
                gc.moveTo(0.0, 0);
                gc.bezierCurveTo(-length * 0.2, length * 0.1, -length * 0.4, width * 0.4, -length * 0.7, width * 0.4);
            }
            gc.stroke();
            gc.restore();
//
//        double MetresPerPixel = 1 / zoom;
//        BasicStroke stroke = new BasicStroke((float) (MetresPerPixel));
//        BasicStroke heavystroke = new BasicStroke((float) (MetresPerPixel * 3));
//        gc.setColor(trackcolor);
//        int count = 0;
//        synchronized (track) {
//            for (Location tp : track) {
//                gc.setStroke(count % 60 == 0 ? heavystroke : stroke);
//                Shape s = new Line2D.Double(tp.getX(), tp.getY(), tp.getX(), tp.getY());
//                gc.draw(s);
//                count++;
//            }
//        }
        }

        public void displayWindGraphic(Location location, SpeedPolar flow, Color colour) {
            drawLine(location, 10 / zoom, flow.getAngle(), 1, colour);

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
        }

        // drawing primatives
        private void drawrectangle(Area area, Color fill) {
            gc.setFill(fill);
            gc.fillRect(area.getBottomleft().getX(), area.getBottomleft().getY(),
                    area.getWidth(), area.getHeight());
        }

        private void drawcircle(Location pt, double diameter, Color fill) {
            gc.setFill(fill);
            gc.fillOval(pt.getX() - diameter / 2, pt.getY() - diameter / 2,
                    diameter, diameter);
        }

        private void drawLine(Location fromPoint, Location toPoint, int width, Color colour) {
            gc.setStroke(colour);
            gc.setLineWidth(width / zoom);
            gc.strokeLine(fromPoint.getX(), fromPoint.getY(), toPoint.getX(), toPoint.getY());
        }

        private void drawLine(Location fromPoint, double linelength, Angle angle, int width, Color colour) {
            DistancePolar line = new DistancePolar(linelength, angle);
            drawLine(fromPoint, line.polar2Location(fromPoint), width, colour);
        }
    }
}
