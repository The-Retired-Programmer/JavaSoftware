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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import java.util.Optional;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatMetrics;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.decisionslog.entity.BoatLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.DecisionLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.ReasonLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public abstract class Strategy {

    public static enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    public static Strategy get(Strategy clonefrom, Boat newboat) {
        if (clonefrom instanceof WindwardStrategy windwardstrategy) {
            return new WindwardStrategy(windwardstrategy, newboat);
        } else if (clonefrom instanceof OffwindStrategy offwindstrategy) {
            return new OffwindStrategy(offwindstrategy, newboat);
        } else if (clonefrom instanceof GybingDownwindStrategy gybingdownwindstrategy) {
            return new GybingDownwindStrategy(gybingdownwindstrategy, newboat);
        } else {
            throw new IllegalStateFailure("Illegal/unknown Strategy");
        }
    }

    public static Strategy get(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        LegType legtype = getLegType(boat, leg.getAngleofLeg(), windflow);
        switch (legtype) {
            case WINDWARD -> {
                return new WindwardStrategy(boat, leg, windflow, waterflow);
            }
            case OFFWIND -> {
                return new OffwindStrategy(boat, leg, windflow, waterflow);
            }
            case GYBINGDOWNWIND -> {
                return new GybingDownwindStrategy(boat, leg, windflow, waterflow);
            }
            default ->
                throw new IllegalStateFailure("Illegal/unknown LEGTYPE: " + legtype.toString());
        }
    }

    static LegType getLegType(Boat boat, PropertyDegrees legangle, WindFlow windflow) {
        if (legangle == null) {
            return LegType.NONE;
        }
        BoatMetrics metrics = boat.metrics;
        PropertyDegrees legtowind = legangle.absDegreesDiff(windflow.getMeanFlowAngle());
        if (legtowind.lteq(metrics.getUpwindrelative())) {
            return LegType.WINDWARD;
        }
        if (boat.isReachdownwind() && legtowind.gteq(metrics.getDownwindrelative())) {
            return LegType.GYBINGDOWNWIND;
        }
        return LegType.OFFWIND;
    }

    static Optional<Double> getRefDistance(PropertyLocation location, PropertyLocation marklocation, PropertyDegrees refangle) {
        return getRefDistance(location, marklocation, refangle.get());
    }

    static Optional<Double> getRefDistance(PropertyLocation location, PropertyLocation marklocation, double refangle) {
        PropertyDistanceVector tomark = new PropertyDistanceVector(location, marklocation);
        PropertyDegrees refangle2mark = tomark.getDegreesProperty().absDegreesDiff(refangle);
        if (refangle2mark.gt(90)) {
            return Optional.empty();
        }
        return Optional.of(refdistancetomark(tomark.getDistance(), refangle2mark));
    }

    private static double refdistancetomark(double distancetomark, PropertyDegrees refangle2mark) {
        return distancetomark * Math.cos(refangle2mark.getRadians());
    }

    private static PropertyDegrees refangletomark(PropertyDegrees tomarkangle, PropertyDegrees refangle) {
        return tomarkangle.absDegreesDiff(refangle);
    }

    public final Boat boat;
    public final CurrentLeg leg;
    public final Decision decision;

    private final double length;
    private final PropertyDegrees portoffsetangle;
    private final PropertyDegrees starboardoffsetangle;

    public Strategy(Boat boat, CurrentLeg leg,
            PropertyDegrees portroundingportoffsetangle, PropertyDegrees portroundingstarboardoffsetangle,
            PropertyDegrees starboardroundingportoffsetangle, PropertyDegrees starboardroundingstarboardoffsetangle) {
        this.boat = boat;
        this.leg = leg;
        this.decision = new Decision(boat);
        this.length = boat.metrics.getWidth() * 2;
        if (leg.isPortRounding()) {
            this.portoffsetangle = portroundingportoffsetangle;
            this.starboardoffsetangle = portroundingstarboardoffsetangle;
        } else {
            this.portoffsetangle = starboardroundingportoffsetangle;
            this.starboardoffsetangle = starboardroundingstarboardoffsetangle;
        }
    }

    public Strategy(Boat boat, CurrentLeg leg,
            PropertyDegrees portroundingoffsetangle, PropertyDegrees starboardroundingoffsetangle) {
        this.boat = boat;
        this.decision = new Decision(boat);
        this.leg = leg;
        this.length = boat.metrics.getWidth() * 2;
        if (leg.isPortRounding()) {
            this.portoffsetangle = portroundingoffsetangle;
            this.starboardoffsetangle = portroundingoffsetangle;
        } else {
            this.portoffsetangle = starboardroundingoffsetangle;
            this.starboardoffsetangle = starboardroundingoffsetangle;
        }
    }

    public Strategy(Boat boat, CurrentLeg previousleg) {
        this.boat = boat;
        this.decision = new Decision(boat);
        this.leg = previousleg;
        this.length = 0.0;
        this.portoffsetangle = null;
        this.starboardoffsetangle = null;
    }

    public Strategy(Strategy clonefrom, Boat newboat) {
        this.boat = newboat;
        this.leg = clonefrom.leg;
        this.decision = new Decision(newboat);
        this.length = clonefrom.length;
        this.portoffsetangle = clonefrom.portoffsetangle;
        this.starboardoffsetangle = clonefrom.starboardoffsetangle;
    }

    double getDistanceToMark(PropertyLocation here) {
        return leg.getDistanceToEnd(here);
    }

    PropertyLocation getMarkLocation() {
        return leg.getEndLocation();
    }

    PropertyLocation getSailToLocation(boolean onPort) {
        return new PropertyDistanceVector(length, onPort ? portoffsetangle : starboardoffsetangle)
                .toLocation(leg.getEndLocation());
    }

    PropertyDegrees getPropertyDegreestoSail(PropertyLocation here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    abstract String nextBoatStrategyTimeInterval(SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow);

    public Strategy nextTimeInterval(SketchModel sketchproperty, int simulationtime, DecisionController timerlog, WindFlow windflow, WaterFlow waterflow) {
        String boatname = boat.getName();
        if (decision.getAction() == SAILON) {
            String reason = nextBoatStrategyTimeInterval(sketchproperty, windflow, waterflow);
            timerlog.add(new BoatLogEntry(boat));
            timerlog.add(new DecisionLogEntry(boatname, decision));
            timerlog.add(new ReasonLogEntry(boatname, reason));
        }
        if (boat.moveUsingDecision(windflow, waterflow, decision)) {
            return leg.isFollowingLeg()
                    ? Strategy.get(boat, leg.toFollowingLeg(), windflow, waterflow)
                    : new AfterFinishStrategy(boat, leg);
        }
        return null;
    }
}
