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
package uk.theretiredprogrammer.sketch.boats;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import uk.theretiredprogrammer.sketch.strategy.Decision;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.core.BooleanParser;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.properties.PropertyAngle;
import uk.theretiredprogrammer.sketch.core.Channel;
import static uk.theretiredprogrammer.sketch.core.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.properties.PropertyBoolean;
import uk.theretiredprogrammer.sketch.properties.PropertyColor;
import uk.theretiredprogrammer.sketch.properties.PropertyItem;
import uk.theretiredprogrammer.sketch.properties.PropertyLocation;
import uk.theretiredprogrammer.sketch.properties.PropertyString;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.StringParser;
import uk.theretiredprogrammer.sketch.flows.Flow;
import uk.theretiredprogrammer.sketch.jfx.sketchdisplay.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * The Abstract Boat class - implements the core capabilities of a boat.
 * Subclass to generate the concrete classes for particular boats provides the
 * metrics information.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class Boat {

    private final Supplier<Controller> controllersupplier;
    //
    private final PropertyString nameproperty = new PropertyString();

    public final String getName() {
        return nameproperty.get();
    }
    
    private final PropertyString typeproperty = new PropertyString();
    
    //
    private final PropertyAngle directionproperty = new PropertyAngle();

    public final Angle getDirection() {
        return directionproperty.getValue();
    }

    public final void setDirection(Angle newangle) {
        directionproperty.setValue(newangle);
    }
    //
    private final PropertyLocation locationproperty = new PropertyLocation();

    public final Location getLocation() {
        return locationproperty.getValue();
    }

    public final void setLocation(Location newlocation) {
        locationproperty.setValue(newlocation);
    }

    //
    private final PropertyColor colourproperty = new PropertyColor();
    private final PropertyColor trackcolorproperty = new PropertyColor();
    //
    private final PropertyBoolean upwindsailonbesttackproperty = new PropertyBoolean();

    public final boolean getUpwindsailonbesttack() {
        return upwindsailonbesttackproperty.get();
    }
    private final PropertyBoolean upwindtackifheadedproperty = new PropertyBoolean();

    public final boolean getUpwindtackifheaded() {
        return upwindtackifheadedproperty.get();
    }
    private final PropertyBoolean upwindbearawayifheadedproperty = new PropertyBoolean();

    public final boolean getUpwindbearawayifheaded() {
        return upwindbearawayifheadedproperty.get();
    }
    private final PropertyBoolean upwindluffupifliftedproperty = new PropertyBoolean();

    public final boolean getUpwindluffupiflifted() {
        return upwindluffupifliftedproperty.get();
    }
    private final PropertyBoolean reachdownwindproperty = new PropertyBoolean();

    public final boolean getReachdownwind() {
        return reachdownwindproperty.get();
    }
    private final PropertyBoolean downwindsailonbestgybeproperty = new PropertyBoolean();

    public final boolean getDownwindsailonbestgybe() {
        return downwindsailonbestgybeproperty.get();
    }
    private final PropertyBoolean downwindbearawayifheadedproperty = new PropertyBoolean();

    public final boolean getDownwindbearawayifheaded() {
        return downwindbearawayifheadedproperty.get();
    }
    private final PropertyBoolean downwindgybeifliftedproperty = new PropertyBoolean();

    public final boolean getDownwindgybeiflifted() {
        return downwindgybeifliftedproperty.get();
    }
    private final PropertyBoolean downwindluffupifliftedproperty = new PropertyBoolean();

    public final boolean getDownwindluffupiflifted() {
        return downwindluffupifliftedproperty.get();
    }
    public Channel upwindchannel;
    public Channel downwindchannel;
    private final Color sailcolor = Color.WHITE;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());

    public Boat(Supplier<Controller> controllersupplier, JsonObject paramsobj, BoatMetrics metrics) throws IOException {
        this.controllersupplier = controllersupplier;
        nameproperty.set(StringParser.parse(paramsobj, "name")
                .orElseThrow(() -> new IOException("Malformed Definition file - <name> is a mandatory parameter")));
        typeproperty.set(StringParser.parse(paramsobj, "type")
                .orElseThrow(() -> new IOException("Malformed Definition file - <type> is a mandatory parameter")));
        directionproperty.setValue(Angle.parse(paramsobj, "heading").orElse(ANGLE0));
        setLocation(Location.parse(paramsobj, "location").orElse(new Location(0, 0)));
        colourproperty.set(ColorParser.parse(paramsobj, "colour").orElse(Color.BLACK));
        trackcolorproperty.set(ColorParser.parse(paramsobj, "trackcolour").orElse(Color.BLACK));
        upwindsailonbesttackproperty.set(BooleanParser.parse(paramsobj, "upwindsailonbesttack").orElse(false));
        upwindtackifheadedproperty.set(BooleanParser.parse(paramsobj, "upwindtackifheaded").orElse(false));
        upwindbearawayifheadedproperty.set(BooleanParser.parse(paramsobj, "upwindbearawayifheaded").orElse(false));
        upwindluffupifliftedproperty.set(BooleanParser.parse(paramsobj, "upwindluffupiflifted").orElse(false));
        reachdownwindproperty.set(BooleanParser.parse(paramsobj, "reachdownwind").orElse(false));
        downwindsailonbestgybeproperty.set(BooleanParser.parse(paramsobj, "downwindsailonbestgybe").orElse(false));
        downwindbearawayifheadedproperty.set(BooleanParser.parse(paramsobj, "downwindbearawayifheaded").orElse(false));
        downwindgybeifliftedproperty.set(BooleanParser.parse(paramsobj, "downwindgybeiflifted").orElse(false));
        downwindluffupifliftedproperty.set(BooleanParser.parse(paramsobj, "downwindluffupiflifted").orElse(false));
        upwindchannel = Channel.parse(paramsobj, "upwindchannel").orElse(CHANNELOFF);
        downwindchannel = Channel.parse(paramsobj, "downwindchannel").orElse(CHANNELOFF);
        this.metrics = metrics;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
    }

    public void change(JsonObject paramsobj) throws IOException {
        directionproperty.setValue(Angle.parse(paramsobj, "heading").orElse(directionproperty.getValue()));
        setLocation(Location.parse(paramsobj, "location").orElse(locationproperty.getValue()));
        colourproperty.set(ColorParser.parse(paramsobj, "colour").orElse(colourproperty.get()));
        trackcolorproperty.set(ColorParser.parse(paramsobj, "trackcolour").orElse(trackcolorproperty.get()));
        upwindsailonbesttackproperty.set(BooleanParser.parse(paramsobj, "upwindsailonbesttack").orElse(getUpwindsailonbesttack()));
        upwindtackifheadedproperty.set(BooleanParser.parse(paramsobj, "upwindtackifheaded").orElse(getUpwindtackifheaded()));
        upwindbearawayifheadedproperty.set(BooleanParser.parse(paramsobj, "upwindbearawayifheaded").orElse(getUpwindbearawayifheaded()));
        upwindluffupifliftedproperty.set(BooleanParser.parse(paramsobj, "upwindluffupiflifted").orElse(getUpwindluffupiflifted()));
        reachdownwindproperty.set(BooleanParser.parse(paramsobj, "reachdownwind").orElse(getReachdownwind()));
        downwindsailonbestgybeproperty.set(BooleanParser.parse(paramsobj, "downwindsailonbestgybe").orElse(getDownwindsailonbestgybe()));
        downwindbearawayifheadedproperty.set(BooleanParser.parse(paramsobj, "downwindbearawayifheaded").orElse(getDownwindbearawayifheaded()));
        downwindgybeifliftedproperty.set(BooleanParser.parse(paramsobj, "downwindgybeiflifted").orElse(getDownwindgybeiflifted()));
        downwindluffupifliftedproperty.set(BooleanParser.parse(paramsobj, "downwindluffupiflifted").orElse(getDownwindluffupiflifted()));
        upwindchannel = Channel.parse(paramsobj, "upwindchannel").orElse(upwindchannel);
        downwindchannel = Channel.parse(paramsobj, "downwindchannel").orElse(downwindchannel);
    }

    public Map<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        map.put("name", nameproperty);
        map.put("type", typeproperty);
        map.put("heading", directionproperty);
        map.put("location", locationproperty);
        map.put("colour", colourproperty);
        map.put("trackcolour", trackcolorproperty);
        map.put("upwindsailonbesttack", upwindsailonbesttackproperty);
        map.put("upwindtackifheaded", upwindtackifheadedproperty);
        map.put("upwindbearawayifheaded", upwindbearawayifheadedproperty);
        map.put("upwindluffupiflifted", upwindluffupifliftedproperty);
        map.put("reachdownwind", reachdownwindproperty);
        map.put("downwindsailonbestgybe", downwindsailonbestgybeproperty);
        map.put("downwindbearawayifheaded", downwindbearawayifheadedproperty);
        map.put("downwindgybeiflifted", downwindgybeifliftedproperty);
        map.put("downwindluffupiflifted", downwindluffupifliftedproperty);
        //map.put("upwindchannel", upwindchannel);
        //map.put("downwindchannel", downwindchannel);
        return map;
    }
    
    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        properties().entrySet().forEach( e -> job.add(e.getKey(), e.getValue().toJson()));
        return job.build();
    }

    public boolean isPort(Angle winddirection) {
        return directionproperty.getValue().gteq(winddirection);
    }

    public Angle getStarboardCloseHauledCourse(Angle winddirection) {
        return winddirection.sub(metrics.upwindrelative);
    }

    public Angle getPortCloseHauledCourse(Angle winddirection) {
        return winddirection.add(metrics.upwindrelative);
    }

    public Angle getStarboardReachingCourse(Angle winddirection) {
        return winddirection.sub(metrics.downwindrelative);
    }

    public Angle getPortReachingCourse(Angle winddirection) {
        return winddirection.add(metrics.downwindrelative);
    }

    public boolean isPortRear90Quadrant(Location location) {
        return isQuadrant(location, this.directionproperty.getValue().inverse(), this.directionproperty.getValue().sub(ANGLE90));
    }

    public boolean isStarboardRear90Quadrant(Location location) {
        return isQuadrant(location, this.directionproperty.getValue().add(ANGLE90), this.directionproperty.getValue().inverse());
    }

    public boolean isPortTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, this.directionproperty.getValue().inverse(), getStarboardCloseHauledCourse(winddirection));
    }

    public boolean isStarboardTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getPortCloseHauledCourse(winddirection), this.directionproperty.getValue().inverse());
    }

    public boolean isPortGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, this.directionproperty.getValue().inverse(), getPortReachingCourse(winddirection));
    }

    public boolean isStarboardGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getStarboardReachingCourse(winddirection), this.directionproperty.getValue().inverse());
    }

    private boolean isQuadrant(Location location, Angle min, Angle max) {
        return getLocation().angleto(location).between(min, max);
    }

    public boolean moveUsingDecision() throws IOException {
        Controller controller = controllersupplier.get();
        SpeedPolar windflow = controller.windflow.getFlow(getLocation());
        Flow waterFlow = controller.waterflow;
        SpeedPolar waterflow = waterFlow.getFlow(getLocation());
        Decision decision = controller.boatstrategies.getStrategy(this).decision;
        switch (decision.getAction()) {
            case SAILON -> {
                moveBoat(directionproperty.getValue(), windflow, waterflow);
                return false;
            }
            case STOP -> {
                return false;
            }
            case MARKROUNDING -> {
                return turn(decision, windflow, waterflow);
            }
            case TURN -> {
                turn(decision, windflow, waterflow);
                return false;
            }
            default ->
                throw new IOException("Illegal sailing Mode when moving boat");
        }
    }

    private boolean turn(Decision decision, SpeedPolar windflow, SpeedPolar waterflow) {
        Angle newdirection = decision.getAngle();
        if (directionproperty.getValue().absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON();
            return true;
        }
        moveBoat(directionproperty.getValue().add(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
        return false;
    }

    /**
     * Move the boat in the required directionproperty.
     *
     * @param nextdirection the required directionproperty
     */
    void moveBoat(Angle nextdirection, SpeedPolar windflow, SpeedPolar waterflow) {
        // calculate the potential boat speed - based on wind speed and relative angle 
        double potentialBoatspeed = SpeedPolar.convertKnots2MetresPerSecond(
                metrics.getPotentialBoatSpeed(nextdirection.absAngleDiff(windflow.getAngle()),
                        windflow.getSpeed()));
        boatspeed += metrics.getInertia() * (potentialBoatspeed - boatspeed);
        // start by calculating the vector components of the boats movement
        DistancePolar move = new DistancePolar(boatspeed, nextdirection)
                .subtract(new DistancePolar(waterflow.getSpeedMetresPerSecond(), waterflow.getAngle()));
        setLocation(move.polar2Location(getLocation())); // updated position calculated
        track.add(getLocation()); // record it in track
        directionproperty.setValue(nextdirection); // and update the directionproperty
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }

    public void draw(SketchPane canvas) throws IOException {
        canvas.drawboat(getLocation(), directionproperty.getValue(), colourproperty.get(),
                controllersupplier.get().windflow.getFlow(getLocation()).getAngle(),
                metrics.length, metrics.width, sailcolor);
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
