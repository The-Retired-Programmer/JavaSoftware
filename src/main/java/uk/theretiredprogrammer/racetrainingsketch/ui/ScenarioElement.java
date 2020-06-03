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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElement;
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElementFactory;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Element;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.IntegerParser;
import uk.theretiredprogrammer.racetrainingsketch.flows.ConstantFlowElement;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.course.Course;
import uk.theretiredprogrammer.racetrainingsketch.flows.FlowElement;
import uk.theretiredprogrammer.racetrainingsketch.flows.FlowElementFactory;
import uk.theretiredprogrammer.racetrainingsketch.course.MarkElement;

/**
 * The Information to describe the Simulation "Field of play
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ScenarioElement extends Element {

    private FlowElement wind = new ConstantFlowElement(this);
    private FlowElement water = new ConstantFlowElement(this);
    private Course course;
    private final Map<String, MarkElement> marks = new HashMap<>();
    private final List<BoatElement> boats = new ArrayList<>();

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
    

    public ScenarioElement(JsonObject paramsobj) throws IOException {
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
    }

    public void setWater(JsonObject waterparamsobj) throws IOException {
        water = FlowElementFactory.createflowelement(waterparamsobj, this);
    }
    
    public FlowElement getWater() {
        return water;
    }
    
    public void setWind(JsonObject windparamsobj) throws IOException {
        wind = FlowElementFactory.createflowelement(windparamsobj, this);
    }
    
    public FlowElement getWind() {
        return wind;
    }
    
    public void setCourse(JsonObject courseparmsobj) throws IOException {
        course = new Course(courseparmsobj,this);
    }
    
    public Course getCourse() {
        return course;
    }

    public void addMark(JsonObject markparamsobj) throws IOException {
        MarkElement mark = new MarkElement(markparamsobj, this);
        marks.put(mark.getName(), mark);
    }

    public void addBoat(JsonObject boatparamsobj) throws IOException {
        boats.add(BoatElementFactory.createboatelement(boatparamsobj, this, course.getFirstCourseLeg()));
    }
    
    public BoatElement getBoat(int index){
        return boats.get(index);
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

    public SpeedPolar getWindflow(Location pos) throws IOException {
        return wind.getFlow(pos);
    }

    public Angle getWindmeanflowangle() throws IOException {
        return wind.getMeanFlowAngle();
    }

    public SpeedPolar getWaterflow(Location pos) throws IOException {
        return water.getFlow(pos);
    }

    public MarkElement getMark(String name) {
        return marks.get(name);
    }

    @Override
    public void finish() {
        wind.finish();
        water.finish();
        marks.values().stream().forEach(mark->mark.finish());
        boats.stream().forEach(boat -> boat.finish());
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
    public void timerAdvance(int simulationtime) throws IOException {
        wind.timerAdvance(simulationtime);
        water.timerAdvance(simulationtime);
        for (BoatElement boat : boats) {
            boat.timerAdvance(simulationtime);
        }
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
        wind.draw(g2D, scale);
        water.draw(g2D, scale);
        for (MarkElement mark: marks.values()) {
            mark.draw(g2D, scale);
        }
        for (BoatElement boat : boats) {
            boat.draw(g2D, scale);
        }
    }

    @Override
    public void draw(Graphics2D g2D, double pixelsPerMetre) throws IOException {
        draw(g2D);
    }
}
