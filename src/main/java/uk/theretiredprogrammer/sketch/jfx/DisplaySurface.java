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

import java.io.IOException;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;

/**
 *
 * @author richard
 */
public class DisplaySurface extends Canvas {

    private final double zoom;
    private final double offsetx;
    private final double offsety;

    public DisplaySurface(Area canvasarea, double zoom) {
        this.zoom = zoom;
        setWidth(canvasarea.getWidth() * zoom);
        setHeight(canvasarea.getHeight() * zoom);
        offsetx = canvasarea.getBottomleft().getX();
        offsety = canvasarea.getBottomleft().getY() + canvasarea.getHeight();
    }
    
    public void clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void drawrectangle(Area area, Color fill) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(fill);
        double x = transformX(area.getBottomleft().getX());
        double y = transformY(area.getBottomleft().getY()+area.getHeight());
        double w = scale(area.getWidth());
        double h = scale(area.getHeight());
        gc.fillRect(x, y, w, h);
    }
    
    public void drawmark(Location location, double diameter, double scaledminimum, Color fill) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(fill);
        double d = scale(diameter);
        if (d < scaledminimum) {
            d = scaledminimum;
        }
        double x = transformX(location.getX())-d/2;
        double y = transformY(location.getY())-d/2;
        gc.fillOval(x, y, d, d);
    }
    
    public void drawboat(Location location, Angle direction, Color fill) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(fill);
        double d = 6;
        double x = transformX(location.getX())-d/2;
        double y = transformY(location.getY())-d/2;
        gc.fillOval(x, y, d, d);
        gc.setStroke(fill);
        DistancePolar directionstroke = new DistancePolar(5, direction);
        Location pt = directionstroke.polar2Location(location);
        gc.strokeLine(transformX(location.getX()),transformY(location.getY()),
                transformX(pt.getX()),transformY(pt.getY()));
    }
    //        Angle relative = direction.angleDiff(controllersupplier.get().windflow.getFlow(location).getAngle());
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
//        gc.rotate(ANGLE90.sub(direction).getRadians());
//        gc.setColor(color);
//        gc.draw(p);
//        gc.fill(p);
//        gc.translate(l * 0.2, 0);
//        gc.rotate(sailRotation.getRadians());
//        gc.setColor(sailcolor);
//        gc.setStroke(new BasicStroke(2));
//        gc.draw(sail);
//        gc.setTransform(xform);
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
    
    
    public void displayWindGraphic(Location location, SpeedPolar flow, Color colour) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(colour);
        DistancePolar directionstroke = new DistancePolar(10/zoom, flow.getAngle());
        Location pt = directionstroke.polar2Location(location);
        gc.strokeLine(transformX(location.getX()),transformY(location.getY()),
                transformX(pt.getX()),transformY(pt.getY()));
        
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

    private double transformX(double x) {
        return scale(x - offsetx);
    }

    private double transformY(double y) {
        return scale(offsety - y);
    }

    private double scale(double value) {
        return value * zoom;
    }
}
