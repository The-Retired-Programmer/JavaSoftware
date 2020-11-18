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
package uk.theretiredprogrammer.sketch.display.entity.course;

import java.util.Optional;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.decisionslog.entity.BoatLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.DecisionLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.ReasonLogEntry;
import uk.theretiredprogrammer.sketch.display.control.strategy.AfterFinishStrategy;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class CurrentLeg {

    private int legno = 0;

    private PropertyLeg currentleg;
    private final Course course;
    private Strategy strategy;
    public final Decision decision;

    public CurrentLeg(Course course) {
        this(course, 0);
    }

    public CurrentLeg(CurrentLeg clonefrom) {
        this(clonefrom.course, clonefrom.legno);
    }

    private CurrentLeg(Course course, int legno) {
        this.decision = new Decision();
        this.course = course;
        this.legno = legno;
        currentleg = course.getLeg(legno);
        course.setOnChange(() -> refresh());
    }

    private void refresh() {
        currentleg = course.getLeg(legno);
    }

    public Decision getDecision() {
        return decision;
    }

    public Strategy getStrategy(Boat boat, WindFlow windflow, WaterFlow waterflow) {
        if (strategy == null) {
            strategy = PropertyLeg.get(boat, this, windflow, waterflow);
        }
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public boolean isFollowingLeg() {
        return course.getLegsProperty().size() > legno + 1;
    }

    public CurrentLeg toFollowingLeg() {
        if (isFollowingLeg()) {
            currentleg = course.getLeg(++legno);
        }
        return this;
    }

    public PropertyDegrees getAngleofFollowingLeg() {
        return isFollowingLeg()
                ? course.getLeg(legno + 1).getAngleofLeg()
                : null;
    }

    // proxies to current PropertyLeg
    public boolean isPortRounding() {
        return currentleg.isPortRounding();
    }

    public double getDistanceToMark(PropertyLocation here) {
        return currentleg.getDistanceToEnd(here);
    }

    public PropertyLocation getMarkLocation() {
        return currentleg.getEndLocation();
    }

    public PropertyDegrees endLegMeanwinddirection(WindFlow windflow) {
        return currentleg.endLegMeanwinddirection(windflow);
    }

    public PropertyDegrees getAngleofLeg() {
        return currentleg.getAngleofLeg();
    }

    public PropertyLocation getSailToLocation(boolean onPort) {
        return strategy.getOffsetVector(onPort)
                .toLocation(getMarkLocation());
    }

    public PropertyDegrees getAngletoSail(PropertyLocation here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    public Strategy nextTimeInterval(Boat boat, SketchModel sketchproperty, int simulationtime, DecisionController timerlog, WindFlow windflow, WaterFlow waterflow) {
        if (decision.getAction() == SAILON) {
            String reason = getStrategy(boat, windflow, waterflow).strategyTimeInterval(boat, decision, this, sketchproperty, windflow, waterflow);
            timerlog.add(new BoatLogEntry(boat));
            timerlog.add(new DecisionLogEntry(boat.getName(), decision));
            timerlog.add(new ReasonLogEntry(boat.getName(), reason));
        }
        if (boat.moveUsingDecision(windflow, waterflow, decision)) {
            return isFollowingLeg()
                    ? PropertyLeg.get(boat, toFollowingLeg(), windflow, waterflow)
                    : new AfterFinishStrategy();
        }
        return null;
    }

    public boolean isNear2WindwardMark(Boat boat, PropertyDegrees markMeanwinddirection) {
        Optional<Double> refdistance = PropertyLeg.getRefDistance(boat.getLocation(), getMarkLocation(), markMeanwinddirection.get());
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }
    
    
    public boolean isNear2LeewardMark(Boat boat, PropertyDegrees markMeanwinddirection) {
        Optional<Double> refdistance = PropertyLeg.getRefDistance(boat.getLocation(), getMarkLocation(), markMeanwinddirection.sub(180).get());
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }
    
}
