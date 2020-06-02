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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
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
import java.util.List;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class BoatBasics {

    private final BoatMetrics metrics;
    private Location location = new Location(0,0);
    private Angle direction = new Angle();
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final double close;
    private final double clearance;
    //
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());
    
    public BoatBasics(BoatMetrics metrics, Location initialposition, Angle initialdirection) {
        this.metrics = metrics;
        this.location = initialposition;
        this.direction = initialdirection;
        this.close = metrics.getLength() * 3;
        this.clearance = metrics.getWidth() * 2;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
    }
    
    public Angle getDirection() {
        return direction;
    }

    public Location getLocation() {
        return location;
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
    
    public void doNextMovement(SpeedPolar windflow, SpeedPolar waterflow, Decision decision) throws IOException {
        switch (decision.getAction()) {
            case SAILON:
                moveBoat(direction, windflow, waterflow);
                return;
            case STOP:
                return;
            case MARKROUNDING:
            case TURN:
                turn(windflow, waterflow, decision);
                return;
            default:
                throw new IOException("Illegal sailing Mode when moving boat");
        }
    }

    private void turn(SpeedPolar windflow, SpeedPolar waterflow, Decision decision) throws IOException {
        Angle newdirection = decision.getAngle();
        if (direction.absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(decision.getAngle(), windflow, waterflow);
            decision.setSAILON();
        } else {
            moveBoat(direction.add(rotationAnglePerSecond.negateif(!decision.isClockwise())), windflow, waterflow);
        }
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
    public void draw(ScenarioElement context, Graphics2D g2D, double pixelsPerMetre,
            Color boatcolour, Color sailcolour, Color trackcolour) throws IOException {
        Angle relative = direction.angleDiff(context.getWindflow(location).getAngle());
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
        g2D.setColor(boatcolour);
        g2D.draw(p);
        g2D.fill(p);
        g2D.translate(l * 0.2, 0);
        g2D.rotate(sailRotation.getRadians());
        g2D.setColor(sailcolour);
        g2D.setStroke(new BasicStroke(2));
        g2D.draw(sail);
        g2D.setTransform(xform);

        double MetresPerPixel = 1 / pixelsPerMetre;
        BasicStroke stroke = new BasicStroke((float) (MetresPerPixel));
        BasicStroke heavystroke = new BasicStroke((float) (MetresPerPixel * 3));
        g2D.setColor(trackcolour);
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
