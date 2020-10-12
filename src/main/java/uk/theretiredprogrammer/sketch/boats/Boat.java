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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.properties.PropertyBoat;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 * The Abstract Boat class - implements the core capabilities of a boat.
 * Subclass to generate the concrete classes for particular boats provides the
 * metrics information.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class Boat {

    private final PropertySketch sketchproperty;
    private final PropertyBoat boatproperty;
    public final Color sailcolor = Color.WHITE;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());

    public Boat(PropertyBoat boatproperty, PropertySketch sketchproperty, BoatMetrics metrics) {
        this.boatproperty = boatproperty;
        this.sketchproperty = sketchproperty;
        this.metrics = metrics;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
    }

    public PropertyBoat getProperty() {
        return boatproperty;
    }

    public String getName() {
        return boatproperty.getName();
    }

    public boolean isPort(Angle winddirection) {
        return boatproperty.getDirection().gteq(winddirection);
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
        return isQuadrant(location, boatproperty.getDirection().inverse(), boatproperty.getDirection().sub(ANGLE90));
    }

    public boolean isStarboardRear90Quadrant(Location location) {
        return isQuadrant(location, boatproperty.getDirection().add(ANGLE90), boatproperty.getDirection().inverse());
    }

    public boolean isPortTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, boatproperty.getDirection().inverse(), getStarboardCloseHauledCourse(winddirection));
    }

    public boolean isStarboardTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getPortCloseHauledCourse(winddirection), boatproperty.getDirection().inverse());
    }

    public boolean isPortGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, boatproperty.getDirection().inverse(), getPortReachingCourse(winddirection));
    }

    public boolean isStarboardGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getStarboardReachingCourse(winddirection), boatproperty.getDirection().inverse());
    }

    private boolean isQuadrant(Location location, Angle min, Angle max) {
        return boatproperty.getLocation().angleto(location).between(min, max);
    }

    public boolean moveUsingDecision(WindFlow windflow, WaterFlow waterflow, Decision decision) {
        SpeedPolar windpolar = windflow.getFlow(boatproperty.getLocation());
        SpeedPolar waterpolar = waterflow.getFlow(boatproperty.getLocation());
        switch (decision.getAction()) {
            case SAILON -> {
                moveBoat(boatproperty.getDirection(), windpolar, waterpolar);
                return false;
            }
            case STOP -> {
                return false;
            }
            case MARKROUNDING -> {
                return turn(decision, windpolar, waterpolar);
            }
            case TURN -> {
                turn(decision, windpolar, waterpolar);
                return false;
            }
            default ->
                throw new IllegalStateFailure("Illegal sailing Action when moving boat");
        }
    }

    private boolean turn(Decision decision, SpeedPolar windflow, SpeedPolar waterflow) {
        Angle newdirection = decision.getAngle();
        if (boatproperty.getDirection().absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON();
            return true;
        }
        moveBoat(boatproperty.getDirection().add(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
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
        boatproperty.setLocation(move.polar2Location(boatproperty.getLocation())); // updated position calculated
        track.add(boatproperty.getLocation()); // record it in track
        boatproperty.setDirection(nextdirection); // and update the directionproperty
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }
}
