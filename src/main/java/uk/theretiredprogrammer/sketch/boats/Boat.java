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

import uk.theretiredprogrammer.sketch.strategy.Decision;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.json.JsonObject;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.core.BooleanParser;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.core.Channel;
import static uk.theretiredprogrammer.sketch.core.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.StringParser;
import uk.theretiredprogrammer.sketch.flows.Flow;
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
    public final String name;
    public Angle direction;
    public  Location location;
    //
    private Color color;
    private Color trackcolor;
    public boolean upwindsailonbesttack;
    public boolean upwindtackifheaded;
    public boolean upwindbearawayifheaded;
    public boolean upwindluffupiflifted;
    public boolean reachdownwind;
    public boolean downwindsailonbestgybe;
    public boolean downwindbearawayifheaded;
    public boolean downwindgybeiflifted;
    public boolean downwindluffupiflifted;
    public Channel upwindchannel;
    public Channel downwindchannel;
    private final Color sailcolor = Color.white;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());

    public Boat(Supplier<Controller> controllersupplier, JsonObject paramsobj, BoatMetrics metrics) throws IOException {
        this.controllersupplier = controllersupplier;
        name = StringParser.parse(paramsobj, "name")
                .orElseThrow(() -> new IOException("Malformed Definition file - <name> is a mandatory parameter"));
        direction = Angle.parse(paramsobj, "heading").orElse(ANGLE0);
        location = Location.parse(paramsobj, "location").orElse(new Location(0, 0));
        color = ColorParser.parse(paramsobj, "colour").orElse(Color.black);
        trackcolor = ColorParser.parse(paramsobj, "trackcolour").orElse(Color.black);
        upwindsailonbesttack = BooleanParser.parse(paramsobj, "upwindsailonbesttack").orElse(false);
        upwindtackifheaded = BooleanParser.parse(paramsobj, "upwindtackifheaded").orElse(false);
        upwindbearawayifheaded = BooleanParser.parse(paramsobj, "upwindbearawayifheaded").orElse(false);
        upwindluffupiflifted = BooleanParser.parse(paramsobj, "upwindluffupiflifted").orElse(false);
        reachdownwind = BooleanParser.parse(paramsobj, "reachdownwind").orElse(false);
        downwindsailonbestgybe = BooleanParser.parse(paramsobj, "downwindsailonbestgybe").orElse(false);
        downwindbearawayifheaded = BooleanParser.parse(paramsobj, "downwindbearawayifheaded").orElse(false);
        downwindgybeiflifted = BooleanParser.parse(paramsobj, "downwindgybeiflifted").orElse(false);
        downwindluffupiflifted = BooleanParser.parse(paramsobj, "downwindluffupiflifted").orElse(false);
        upwindchannel = Channel.parse(paramsobj, "upwindchannel").orElse(CHANNELOFF);
        downwindchannel = Channel.parse(paramsobj, "downwindchannel").orElse(CHANNELOFF);
        this.metrics = metrics;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
    }

    public void change(JsonObject paramsobj) throws IOException {
        direction = Angle.parse(paramsobj, "heading").orElse(direction);
        location = Location.parse(paramsobj, "location").orElse(location);
        color = ColorParser.parse(paramsobj, "colour").orElse(color);
        trackcolor = ColorParser.parse(paramsobj, "trackcolour").orElse(trackcolor);
        upwindsailonbesttack = BooleanParser.parse(paramsobj, "upwindsailonbesttack").orElse(upwindsailonbesttack);
        upwindtackifheaded = BooleanParser.parse(paramsobj, "upwindtackifheaded").orElse(upwindtackifheaded);
        upwindbearawayifheaded = BooleanParser.parse(paramsobj, "upwindbearawayifheaded").orElse(upwindbearawayifheaded);
        upwindluffupiflifted = BooleanParser.parse(paramsobj, "upwindluffupiflifted").orElse(upwindluffupiflifted);
        reachdownwind = BooleanParser.parse(paramsobj, "reachdownwind").orElse(reachdownwind);
        downwindsailonbestgybe = BooleanParser.parse(paramsobj, "downwindsailonbestgybe").orElse(downwindsailonbestgybe);
        downwindbearawayifheaded = BooleanParser.parse(paramsobj, "downwindbearawayifheaded").orElse(downwindbearawayifheaded);
        downwindgybeiflifted = BooleanParser.parse(paramsobj, "downwindgybeiflifted").orElse(downwindgybeiflifted);
        downwindluffupiflifted = BooleanParser.parse(paramsobj, "downwindluffupiflifted").orElse(downwindluffupiflifted);
        upwindchannel = Channel.parse(paramsobj, "upwindchannel").orElse(upwindchannel);
        downwindchannel = Channel.parse(paramsobj, "downwindchannel").orElse(downwindchannel);
    }
    
    public Map<String, Object> properties() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("heading", direction);
        map.put("locataion", location);
        map.put("colour", color);
        map.put("trackcolour", trackcolor);
        map.put("upwindsailonbesttack", upwindsailonbesttack);
        map.put("upwindtackifheaded", upwindtackifheaded);
        map.put("upwindbearawayifheaded", upwindbearawayifheaded);
        map.put("upwindluffupiflifted", upwindluffupiflifted);
        map.put("reachdownwind", reachdownwind);
        map.put("downwindsailonbestgybe", downwindsailonbestgybe);
        map.put("downwindbearawayifheaded", downwindbearawayifheaded);
        map.put("downwindgybeiflifted", downwindgybeiflifted);
        map.put("downwindluffupiflifted", downwindluffupiflifted);
        map.put("upwindchannel", upwindchannel);
        map.put("downwindchannel", downwindchannel);
        return map;
    }   

    public boolean isPort(Angle winddirection) {
        return direction.gteq(winddirection);
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
        return isQuadrant(location, this.direction.inverse(), this.direction.sub(ANGLE90));
    }

    public boolean isStarboardRear90Quadrant(Location location) {
        return isQuadrant(location, this.direction.add(ANGLE90), this.direction.inverse());
    }

    public boolean isPortTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, this.direction.inverse(), getStarboardCloseHauledCourse(winddirection));
    }

    public boolean isStarboardTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getPortCloseHauledCourse(winddirection), this.direction.inverse());
    }

    public boolean isPortGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, this.direction.inverse(), getPortReachingCourse(winddirection));
    }

    public boolean isStarboardGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getStarboardReachingCourse(winddirection), this.direction.inverse());
    }

    private boolean isQuadrant(Location location, Angle min, Angle max) {
        return this.location.angleto(location).between(min, max);
    }

    public boolean moveUsingDecision() throws IOException {
        Controller controller = controllersupplier.get();
        SpeedPolar windflow = controller.windflow.getFlow(location);
        Flow waterFlow = controller.waterflow;
        SpeedPolar waterflow = waterFlow != null ? waterFlow.getFlow(location) : new SpeedPolar(0, ANGLE0);
        Decision decision = controller.boatstrategies.getStrategy(this).decision;
        switch (decision.getAction()) {
            case SAILON:
                moveBoat(direction, windflow, waterflow);
                return false;
            case STOP:
                return false;
            case MARKROUNDING:
                return turn(decision, windflow, waterflow);
            case TURN:
                turn(decision, windflow, waterflow);
                return false;
            default:
                throw new IOException("Illegal sailing Mode when moving boat");
        }
    }

    private boolean turn(Decision decision, SpeedPolar windflow, SpeedPolar waterflow) {
        Angle newdirection = decision.getAngle();
        if (direction.absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON();
            return true;
        }
        moveBoat(direction.add(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
        return false;
    }

    /**
     * Move the boat in the required direction.
     *
     * @param nextdirection the required direction
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
        location = move.polar2Location(location); // updated position calculated
        track.add(location); // record it in track
        direction = nextdirection; // and update the direction
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }

    /**
     * Draw the Boat on the display canvas.
     *
     * @param g2D the 2D graphics object
     * @param zoom the scale factor (pixelsPerMetre)
     */
    public void draw(Graphics2D g2D, double zoom) throws IOException {
        Angle relative = direction.angleDiff(controllersupplier.get().windflow.getFlow(location).getAngle());
        boolean onStarboard = relative.gt(ANGLE0);
        Angle absrelative = relative.abs();
        Angle sailRotation = absrelative.lteq(new Angle(45)) ? ANGLE0 : absrelative.sub(new Angle(45)).mult(2.0 / 3);
        double l;
        double w;
        if (zoom > 4) {
            l = metrics.getLength() * zoom;
            w = metrics.getWidth() * zoom;
        } else {
            l = 13; // create a visible object
            w = 5; // create a visible object
        }
        GeneralPath p = new GeneralPath();
        p.moveTo(-l * 0.5, w * 0.45); //go to transom quarter
        p.curveTo(-l * 0.2, w * 0.55, l * 0.2, l * 0.1, l * 0.5, 0);
        p.curveTo(l * 0.2, -l * 0.1, -l * 0.2, -w * 0.55, -l * 0.5, -w * 0.45);
        p.closePath(); // and the port side
        GeneralPath sail = new GeneralPath();
        if (onStarboard) {
            sail.moveTo(0.0, 0);
            sail.curveTo(-l * 0.2, l * 0.1, -l * 0.4, w * 0.4, -l * 0.7, w * 0.4);
            sailRotation = sailRotation.negate();
        } else {
            sail.moveTo(0, 0);
            sail.curveTo(-l * 0.2, -l * 0.1, -l * 0.4, -w * 0.4, -l * 0.7, -w * 0.4);
        }
        //
        AffineTransform xform = g2D.getTransform();
        g2D.translate(location.getX(), location.getY());
        g2D.scale(1 / zoom, 1 / zoom);
        g2D.rotate(ANGLE90.sub(direction).getRadians());
        g2D.setColor(color);
        g2D.draw(p);
        g2D.fill(p);
        g2D.translate(l * 0.2, 0);
        g2D.rotate(sailRotation.getRadians());
        g2D.setColor(sailcolor);
        g2D.setStroke(new BasicStroke(2));
        g2D.draw(sail);
        g2D.setTransform(xform);

        double MetresPerPixel = 1 / zoom;
        BasicStroke stroke = new BasicStroke((float) (MetresPerPixel));
        BasicStroke heavystroke = new BasicStroke((float) (MetresPerPixel * 3));
        g2D.setColor(trackcolor);
        int count = 0;
        synchronized (track) {
            for (Location tp : track) {
                g2D.setStroke(count % 60 == 0 ? heavystroke : stroke);
                Shape s = new Line2D.Double(tp.getX(), tp.getY(), tp.getX(), tp.getY());
                g2D.draw(s);
                count++;
            }
        }
    }
}
