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
package uk.theretiredprogrammer.sketch.jfx;

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
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class SketchWindow extends AbstractWindow {

    public static SketchWindow create(String title, Controller controller) {
        return new SketchWindow(title, controller);
    }

    private final SketchPane canvas;
    private final Text timetext;
    private final Text statusbar;
    private DecisionDisplayWindow decisiondisplaywindow = null;

    private SketchWindow(String title, Controller controller) {
        timetext = new Text("      ");
        statusbar = new Text();
        Group group = new Group();
        canvas = new SketchPane(controller.displayparameters.getDisplayArea(), controller.displayparameters.getZoom());
        controller
                .setOnSketchChange(() -> Platform.runLater(() -> controller.paint(canvas)))
                .setOnTimeChange((seconds) -> Platform.runLater(() -> updteTime(seconds)))
                .setWritetoStatusLine((s) -> Platform.runLater(() -> statusbar.setText(s)));
        group.getChildren().add(canvas);
        controller.paint(canvas);
        //
        new WindowBuilder()
                .setTitle(title)
                .addtoToolbar(
                        toolbarButton("Start", actionEvent -> controller.start()),
                        toolbarButton("Pause", actionEvent -> controller.stop()),
                        toolbarButton("Rest", actionEvent -> controller.reset()),
                        toolbarButton("Show Properties", actionEvent -> PropertiesWindow.create(title, controller)),
                        toolbarButton("Show Decision Log", actionEvent -> {
                            if (decisiondisplaywindow == null) {
                                decisiondisplaywindow = DecisionDisplayWindow.create(title);
                                controller.setShowDecisionLine((l) -> decisiondisplaywindow.writeline(l));
                            }
                            decisiondisplaywindow.clear();
                            controller.displaylog();
                        }),
                        toolbarButton("Show Filtered Decision Log", actionEvent -> {
                            if (decisiondisplaywindow == null) {
                                decisiondisplaywindow = DecisionDisplayWindow.create(title);
                                controller.setShowDecisionLine((l) -> decisiondisplaywindow.writeline(l));
                            }
                            decisiondisplaywindow.clear();
                            controller.displayfilteredlog("SELECTED");
                        }),
                        timetext
                )
                .setScrollableContent(group)
                .setStatusbar(statusbar)
                .show();
    }

    private void updteTime(int seconds) {
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
        }

        public void clear() {
            GraphicsContext gc1 = getGraphicsContext2D();
            gc1.clearRect(0, 0, getWidth(), getHeight());
        }

        public void drawrectangle(Area area, Color fill) {
            gc.setFill(fill);
            gc.fillRect(area.getBottomleft().getX(), area.getBottomleft().getY(),
                    area.getWidth(), area.getHeight());
        }

        public void drawmark(Location location, double diameter, double minimumDiameterPixels, Color fill) {
            gc.setFill(fill);
            double minimumDiameter = minimumDiameterPixels / zoom;
            if (diameter < minimumDiameter) {
                diameter = minimumDiameter;
            }
//            double x = transformX(location.getX()) - d / 2;
//            double y = transformY(location.getY()) - d / 2;
            gc.fillOval(location.getX() - diameter / 2, location.getY() - diameter / 2,
                    diameter, diameter);
        }

        public void drawwindwardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
            final Angle WINDWARDLAYLINEANGLE = new Angle(135);
            pixelLine(location,
                    new DistancePolar(laylinelength, windAngle.add(WINDWARDLAYLINEANGLE)),
                    laylinecolour, zoom);
            pixelLine(location,
                    new DistancePolar(laylinelength, windAngle.sub(WINDWARDLAYLINEANGLE)),
                    laylinecolour, zoom);
        }

        public void drawleewardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
            final Angle LEEWARDLAYLINEANGLE = new Angle(45);
            pixelLine(location,
                    new DistancePolar(laylinelength, windAngle.add(LEEWARDLAYLINEANGLE)),
                    laylinecolour, zoom);
            pixelLine(location,
                    new DistancePolar(laylinelength, windAngle.sub(LEEWARDLAYLINEANGLE)),
                    laylinecolour, zoom);
        }

        private void pixelLine(Location laylineBase, DistancePolar line,
                Color laylinecolour, double zoom) {
            gc.setStroke(laylinecolour);
            Location pt = line.polar2Location(laylineBase);
            gc.strokeLine(laylineBase.getX(), laylineBase.getY(),
                    pt.getX(), pt.getY());
//            gc.strokeLine(transformX(laylineBase.getX()), transformY(laylineBase.getY()),
//                    transformX(pt.getX()), transformY(pt.getY()));
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
            gc.setStroke(colour);
            gc.setLineWidth(1 / zoom);
            DistancePolar directionstroke = new DistancePolar(10 / zoom, flow.getAngle());
            Location pt = directionstroke.polar2Location(location);
            gc.strokeLine(location.getX(), location.getY(),
                    pt.getX(), pt.getY());

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
    }
}
