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

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class Shapes2D {

    private final double zoom;

    public Shapes2D(double zoom) {
        this.zoom = zoom;
    }

    public Shape[] drawfieldofplay(Area canvasarea, Area sailingarea) {
        return new Shape[]{
            drawrectangle(canvasarea, Color.OLIVEDRAB),
            drawrectangle(sailingarea, Color.LIGHTSEAGREEN)
        };
    }

    public Shape[] drawmark(Location location, double diameter, Color fill) {
        Shape mark = drawcircle(location, (diameter < 8 / zoom ? 8 / zoom : diameter) / 2, fill);
        return new Shape[]{mark};
    }

    public Shape[] drawwindwardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
        final Angle WINDWARDLAYLINEANGLE = new Angle(135);
        return new Shape[]{
            drawLine(location, laylinelength, windAngle.plus(WINDWARDLAYLINEANGLE), 1, laylinecolour),
            drawLine(location, laylinelength, windAngle.sub(WINDWARDLAYLINEANGLE), 1, laylinecolour)
        };
    }

    public Shape[] drawleewardlaylines(Location location, Angle windAngle, double laylinelength, Color laylinecolour) {
        final Angle LEEWARDLAYLINEANGLE = new Angle(45);
        return new Shape[]{
            drawLine(location, laylinelength, windAngle.plus(LEEWARDLAYLINEANGLE), 1, laylinecolour),
            drawLine(location, laylinelength, windAngle.sub(LEEWARDLAYLINEANGLE), 1, laylinecolour)
        };
    }

    public Shape[] drawwindwardladder(Location location, Angle windAngle, double ladderspacing, Color laylinecolour, int numberofsteps) {
        if (numberofsteps != 0) {
            Shape[] ladderlines = new Shape[numberofsteps * 2];
            for (int i = 0; i < numberofsteps; i++) {
                double offsetdistance = ladderspacing * (i + 1);
                DistanceVector offset = new DistanceVector(offsetdistance, windAngle.plus(180));
                Location offsetlocation = offset.toLocation(location);
                double halflinelength = offsetdistance * 1.2;
                ladderlines[i * 2] = drawLine(offsetlocation, halflinelength, windAngle.plus(90), 1, laylinecolour);
                ladderlines[i * 2 + 1] = drawLine(offsetlocation, halflinelength, windAngle.sub(90), 1, laylinecolour);
            }
            return ladderlines;
        }
        return new Shape[]{};
    }

    public Shape[] drawleewardladder(Location location, Angle windAngle, double ladderspacing, Color laylinecolour, int numberofsteps) {
        if (numberofsteps != 0) {
            Shape[] ladderlines = new Shape[numberofsteps * 2];
            for (int i = 0; i < numberofsteps; i++) {
                double offsetdistance = ladderspacing * (i + 1);
                DistanceVector offset = new DistanceVector(offsetdistance, windAngle);
                Location offsetlocation = offset.toLocation(location);
                double halflinelength = offsetdistance * 1.2;
                ladderlines[i * 2] = drawLine(offsetlocation, halflinelength, windAngle.plus(90), 1, laylinecolour);
                ladderlines[i * 2 + 1] = drawLine(offsetlocation, halflinelength, windAngle.sub(90), 1, laylinecolour);
            }
            return ladderlines;
        }
        return new Shape[]{};
    }

    public Shape[] drawboat(Location location, Angle direction, Color fill, Angle winddirection,
            double length, double width, Color sailcolour) {
        if (length < 17 / zoom) {
            length = 17 / zoom;
            width = 7 / zoom;
        }
        Angle relative = direction.degreesDiff(winddirection);
        boolean onStarboard = relative.lt(0);
        Angle absrelative = relative.abs();
        Angle sailRotation = absrelative.lteq(45) ? new Angle(0) : absrelative.sub(45).mult(2.0 / 3);
        sailRotation = sailRotation.negateif(!onStarboard);
        Translate positiontranslate = new Translate(location.getX(), location.getY());
        Rotate directionrotation = new Rotate(-direction.get());
        Rotate sailreachrotation = new Rotate(sailRotation.get());
        //
        MoveTo move = new MoveTo();
        move.setY(-length * 0.5);
        move.setX(width * 0.45); //go to transom quarter
        //
        CubicCurveTo curve1 = new CubicCurveTo();
        curve1.setControlY1(-length * 0.2);
        curve1.setControlX1(width * 0.55);
        curve1.setControlY2(length * 0.2);
        curve1.setControlX2(width * 0.3);
        curve1.setY(length * 0.5);
        curve1.setX(0);
        //
        CubicCurveTo curve2 = new CubicCurveTo();
        curve2.setControlY1(length * 0.2);
        curve2.setControlX1(-width * 0.3);
        curve2.setControlY2(-length * 0.2);
        curve2.setControlX2(-width * 0.55);
        curve2.setY(-length * 0.5);
        curve2.setX(-width * 0.45);
//
        Path boat = new Path();
        boat.getElements().addAll(move, curve1, curve2, new ClosePath());
        boat.setStrokeWidth(0);
        boat.setFill(fill);
        boat.getTransforms().addAll(positiontranslate, directionrotation);
        //
        MoveTo movesail = new MoveTo();
        movesail.setX(0.0);
        movesail.setY(0.00);
        //
        CubicCurveTo sailcurve = new CubicCurveTo();
        if (!onStarboard) {
            sailcurve.setControlY1(-length * 0.2);
            sailcurve.setControlX1(-width * 0.4);
            sailcurve.setControlY2(-length * 0.4);
            sailcurve.setControlX2(-width * 0.4);
            sailcurve.setY(-length * 0.7);
            sailcurve.setX(-width * 0.4);
        } else {
            sailcurve.setControlY1(-length * 0.2);
            sailcurve.setControlX1(width * 0.4);
            sailcurve.setControlY2(-length * 0.4);
            sailcurve.setControlX2(width * 0.4);
            sailcurve.setY(-length * 0.7);
            sailcurve.setX(width * 0.4);
        }
        Path sail = new Path();
        sail.getElements().addAll(movesail, sailcurve);
        sail.setStroke(sailcolour);
        sail.setStrokeWidth(2 / zoom);
        sail.getTransforms().addAll(positiontranslate, directionrotation, sailreachrotation);
        return new Shape[]{boat, sail};
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

    public Shape[] displayWindGraphic(Location location, SpeedVector flow, double size, Color colour) {
        return new Shape[]{drawLine(location, size / zoom, flow.getAngle(), 1, colour)};
    }

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
//        SpeedVector flow = getFlow(new Location(x, y));
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
    // drawing primatives
    private Shape drawrectangle(Area area, Color fill) {
        Rectangle rect = new Rectangle(area.getLocationProperty().getX(), area.getLocationProperty().getY(),
                area.getWidth(), area.getHeight());
        rect.setFill(fill);
        return rect;
    }

    private Shape drawcircle(Location pt, double radius, Color fill) {
        Circle circle = new Circle(pt.getX(), pt.getY(), radius, fill);
        return circle;
    }

    private Shape drawLine(Location fromPoint, Location toPoint, double width, Color colour) {
        Line line = new Line(fromPoint.getX(), fromPoint.getY(), toPoint.getX(), toPoint.getY());
        line.setStroke(colour);
        line.setStrokeWidth(width / zoom); // ??? was gc.setLineWidth(width / zoom);
        return line;
    }

    private Shape drawLine(Location fromPoint, double linelength, Angle degrees, int width, Color colour) {
        DistanceVector line = new DistanceVector(linelength, degrees);
        return drawLine(fromPoint, line.toLocation(fromPoint), width, colour);
    }
}
