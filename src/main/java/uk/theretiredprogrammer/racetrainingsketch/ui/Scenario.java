/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boats;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.IntegerParser;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.course.Course;
import uk.theretiredprogrammer.racetrainingsketch.flows.Flow;
import uk.theretiredprogrammer.racetrainingsketch.flows.FlowComponentSet;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;

/**
 * The Information to describe the Simulation "Field of play
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Scenario implements Displayable, Timerable {

    private Course course;
    private Boats boats;

    // dimensions of the visible "playing surface" in metres
    // (default is a 1km square with 0,0 in centre).
    public static final double EAST_DEFAULT = -500;
    public static final double WEST_DEFAULT = 500;
    public static final double NORTH_DEFAULT = 500;
    public static final double SOUTH_DEFAULT = -500;
    public static final double SCALE_DEFAULT = 1;

    private final double east;
    private final double west;
    private final double north;
    private final double south;
    private final double eastlimit;
    private final double westlimit;
    private final double northlimit;
    private final double southlimit;
    private final double zoom;
    private final int secondsperdisplay;
    private final double speedup;

    private Flow windflow;
    private Flow waterflow;

    public Scenario(JsonObject parsedjson) throws IOException {
        JsonObject paramsobj = parsedjson.getJsonObject("DISPLAY");
        if (paramsobj == null) {
            throw new IOException("Malformed Definition File - missing DISPLAY object");
        }
        east = DoubleParser.parse(paramsobj, "east").orElse(EAST_DEFAULT);
        eastlimit = DoubleParser.parse(paramsobj, "eastlimit").orElse(east);
        west = DoubleParser.parse(paramsobj, "west").orElse(WEST_DEFAULT);
        westlimit = DoubleParser.parse(paramsobj, "westlimit").orElse(west);
        north = DoubleParser.parse(paramsobj, "north").orElse(NORTH_DEFAULT);
        northlimit = DoubleParser.parse(paramsobj, "northlimit").orElse(north);
        south = DoubleParser.parse(paramsobj, "south").orElse(SOUTH_DEFAULT);
        southlimit = DoubleParser.parse(paramsobj, "southlimit").orElse(south);
        zoom = DoubleParser.parse(paramsobj, "zoom").orElse(SCALE_DEFAULT);
        secondsperdisplay = IntegerParser.parse(paramsobj, "secondsperdisplay").orElse(1);
        speedup = DoubleParser.parse(paramsobj, "speedup").orElse(1.0);
        //
    }

    public void setWindFlow(Flow windflow) {
        this.windflow = windflow;
    }

    public void setWaterFlow(Flow waterflow) {
        this.waterflow = waterflow;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setBoats(Boats boats) {
        this.boats = boats;
    }

    public Boats getBoats() {
        return boats;
    }

    public double getEastlimit() {
        return eastlimit;
    }

    public double getWestlimit() {
        return westlimit;
    }

    public double getNorthlimit() {
        return northlimit;
    }

    public double getSouthlimit() {
        return southlimit;
    }

    public double getEast() {
        return east;
    }

    public double getWest() {
        return west;
    }

    public double getNorth() {
        return north;
    }

    public double getSouth() {
        return south;
    }

    public int getSecondsperdisplay() {
        return secondsperdisplay;
    }

    public double getSpeedup() {
        return speedup;
    }

    public double getZoom() {
        return zoom;
    }

    public FlowComponentSet getWindFlowComponentSet() {
        return windflow.getFlowComponentSet();
    }

    public void recalculateWindFlow() throws IOException {
        windflow.setFlows();
    }

    public SpeedPolar getWindflow(Location pos) throws IOException {
        return windflow.getFlow(pos);
    }

    public Angle getWindmeanflowangle() throws IOException {
        return windflow.getMeanFlowAngle();
    }

    public SpeedPolar getWaterflow(Location pos) throws IOException {
        return waterflow != null ? waterflow.getFlow(pos): new SpeedPolar(0,ANGLE0);
    }

// TODO action Future Parameter disabled (due to disabled method in Element) - needs to be reworked later
//    @Override
//    public void actionFutureParameters(int simulationtime) throws IOException {
//        wind.actionFutureParameters(simulationtime);
//        water.actionFutureParameters(simulationtime);
//        for (MarkElement mark : marks.values()) {
//            mark.actionFutureParameters(simulationtime);
//        }
//        for (BoatElement boat : boats) {
//            boat.actionFutureParameters(simulationtime);
//        }
//    }
    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog ) throws IOException {
        windflow.timerAdvance(simulationtime, timerlog);
        if (waterflow != null) {
            waterflow.timerAdvance(simulationtime, timerlog);
        }
        boats.timerAdvance(simulationtime, timerlog);
    }

    /**
     * Draw the ScenarioElement on the display canvas.
     *
     * @param g2D the 2D graphics object
     */
    public void draw(Graphics2D g2D) throws IOException {
        // set transform
        double w = getWest();
        double n = getNorth();
        double e = getEast();
        double s = getSouth();
        double scale = getZoom();
        double wl = getWestlimit();
        double el = getEastlimit();
        double nl = getNorthlimit();
        double sl = getSouthlimit();
        g2D.transform(new AffineTransform(scale, 0, 0, -scale, -w * scale, n * scale));
        // if limits set then darken limit areas
        if (wl > w) {
            Shape shape = new Rectangle2D.Double(w, s,
                    wl - w, n - s);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (el < e) {
            Shape shape = new Rectangle2D.Double(el, s,
                    e - el, n - s);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (nl < n) {
            Shape shape = new Rectangle2D.Double(w, nl,
                    e - w, n - nl);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (sl > s) {
            Shape shape = new Rectangle2D.Double(w, s,
                    e - w, sl - s);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        windflow.draw(g2D, scale);
        if (waterflow != null) {
            waterflow.draw(g2D, scale);
        }
        course.draw(g2D, scale);
        boats.draw(g2D, scale);
    }

    @Override
    public void draw(Graphics2D g2D, double zoom) throws IOException {
        draw(g2D);
    }

    public Dimension getGraphicDimension() {
        double width = getEast() - getWest();
        double depth = getNorth() - getSouth();
        double scale = getZoom();
        return new Dimension((int) (width * scale), (int) (depth * scale));
    }
}
