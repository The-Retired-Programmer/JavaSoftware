/*
 * Copyright 2014-2012 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import uk.theretiredprogrammer.racetrainingsketch.strategy.Decision;
import uk.theretiredprogrammer.racetrainingsketch.strategy.CourseLegWithStrategy;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.JsonObject;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.racetrainingsketch.core.BooleanParser;
import uk.theretiredprogrammer.racetrainingsketch.core.ColorParser;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.Channel;
import static uk.theretiredprogrammer.racetrainingsketch.core.Channel.CHANNELOFF;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.Element;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;

/**
 * The Abstract Boat class - implements the core capabilities of a boat.
 * Subclass to generate the concrete classes for particular boats provides the
 * metrics information.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class BoatElement extends Element {

    private final ScenarioElement scenario;
    private final Decision decision;
    private CourseLegWithStrategy leg;

    private Angle direction;
    private Location location;
    private Color color;
    private Color trackcolor;
    private boolean upwindsailonbesttack;
    private boolean upwindtackifheaded;
    private boolean upwindbearawayifheaded;
    private boolean upwindluffupiflifted;
    private boolean reachdownwind;
    private boolean downwindsailonbestgybe;
    private boolean downwindbearawayifheaded;
    private boolean downwindgybeiflifted;
    private boolean downwindluffupiflifted;
    private Channel upwindchannel;
    private Channel downwindchannel;

    private final Color sailcolor = Color.white;

    private final BoatMetrics metrics;
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final double close;
    private final double clearance;
    //
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());

    /**
     * Constructor
     *
     * @param name the flow name - either wind or water
     * @param scenario the scenario to be applied
     * @param wind the wind flow
     * @param water the water flow
     * @param marks the set of marks
     */
    public BoatElement(JsonObject paramsobj, ScenarioElement scenario, CourseLeg cleg, BoatMetrics metrics) throws IOException {
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
        this.scenario = scenario;
        this.metrics = metrics;
        this.close = metrics.getLength() * 3;
        this.clearance = metrics.getWidth() * 2;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
        leg = new CourseLegWithStrategy(cleg, scenario.getWindmeanflowangle(), this);
        decision = new Decision(this);
    }

    private class decisionActionChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ("ACTION".equals(propertyName)) {
                if (e.getOldValue() == MARKROUNDING) {
                    try {
                        leg = new CourseLegWithStrategy(leg.getFollowingLeg(), scenario.getWindmeanflowangle(), BoatElement.this);
                    } catch (IOException ex) {
                        // TODO sort out the squashed IOException here!
                    }
                }
            }
        }
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

    public Location getLocation() {
        return location;
    }

    public Angle getDirection() {
        return direction;
    }

    public Channel getUpwindchannel() {
        return upwindchannel;
    }

    public Channel getDownwindchannel() {
        return downwindchannel;
    }

    public boolean isUpwindsailonbesttack() {
        return upwindsailonbesttack;
    }

    public boolean isUpwindtackifheaded() {
        return upwindtackifheaded;
    }

    public boolean isUpwindbearawayifheaded() {
        return upwindbearawayifheaded;
    }

    public boolean isUpwindluffupiflifted() {
        return upwindluffupiflifted;
    }

    public boolean isReachdownwind() {
        return reachdownwind;
    }

    public boolean isDownwindsailonbestgybe() {
        return downwindsailonbestgybe;
    }

    public boolean isDownwindbearawayifheaded() {
        return downwindbearawayifheaded;
    }

    public boolean isDownwindgybeiflifted() {
        return downwindgybeiflifted;
    }

    public boolean isDownwindluffupiflifted() {
        return downwindluffupiflifted;
    }

    public double getTurndistance() {
        return boatspeed * 4;
    }

    public Angle getClosehauled() {
        return metrics.getUpwindrelative();
    }

    public Angle getDownwind() {
        return metrics.getDownwindrelative();
    }

    public BoatMetrics getMetrics() {
        return metrics;
    }

    public double close() {
        return close;
    }

    public double clearance() {
        return clearance;
    }

    public Angle closeAngle() {
        return ANGLE90.sub(rotationAnglePerSecond.div(2));
    }

    /**
     * Advance time. Recalculate the boat position and other parameters.
     *
     * @param time the new time
     */
    @Override
    public void timerAdvance(int time) throws IOException {
        if (decision.getAction() == SAILON) {
            leg.nextTimeInterval(decision, this, scenario.getWindflow(location).getAngle());
        }
        SpeedPolar windflow = scenario.getWindflow(location);
        SpeedPolar waterflow = scenario.getWaterflow(location);
        switch (decision.getAction()) {
            case SAILON:
                moveBoat(direction, windflow, waterflow);
                return;
            case STOP:
                return;
            case MARKROUNDING:
                if (turn(windflow, waterflow)) {
                    leg = new CourseLegWithStrategy(leg.getFollowingLeg(), scenario.getWindmeanflowangle(), this);
                }
                return;
            case TURN:
                turn(windflow, waterflow);
                return;
            default:
                throw new IOException("Illegal sailing Mode when moving boat");
        }
    }

    private boolean turn(SpeedPolar windflow, SpeedPolar waterflow) throws IOException {
        Angle newdirection = decision.getAngle();
        if (direction.absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(decision.getAngle(), windflow, waterflow);
            decision.setSAILON();
            return true;
        } 
        moveBoat(direction.add(rotationAnglePerSecond.negateif(!decision.isClockwise())), windflow, waterflow);
        return false;
    }

    /**
     * Move the boat in the required direction.
     *
     * @param nextdirection the required direction
     */
    private void moveBoat(Angle nextdirection, SpeedPolar windflow, SpeedPolar waterflow) throws IOException {
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
     * @param pixelsPerMetre the scale factor
     */
    @Override
    public void draw(Graphics2D g2D, double pixelsPerMetre) throws IOException {
        Angle relative = direction.angleDiff(scenario.getWindflow(location).getAngle());
        boolean onStarboard = relative.gt(ANGLE0);
        Angle absrelative = relative.abs();
        Angle sailRotation = absrelative.lteq(new Angle(45)) ? ANGLE0 : absrelative.sub(new Angle(45)).mult(2.0 / 3);
        double l;
        double w;
        if (pixelsPerMetre > 4) {
            l = metrics.getLength() * pixelsPerMetre;
            w = metrics.getWidth() * pixelsPerMetre;
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
        g2D.scale(1 / pixelsPerMetre, 1 / pixelsPerMetre);
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

        double MetresPerPixel = 1 / pixelsPerMetre;
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
