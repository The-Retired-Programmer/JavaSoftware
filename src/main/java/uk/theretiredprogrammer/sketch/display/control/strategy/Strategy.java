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
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.decisionslog.entity.BoatLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.DecisionLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.ReasonLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg;

public abstract class Strategy {
    public final CurrentLeg leg;

    public Strategy(CurrentLeg leg) {
        this.leg = leg;
    }

    public Strategy(Strategy clonefrom) {
        this.leg = new CurrentLeg(clonefrom.leg);
    }

    double getDistanceToMark(PropertyLocation here) {
        return leg.getDistanceToEnd(here);
    }

    PropertyLocation getMarkLocation() {
        return leg.getEndLocation();
    }
    
    abstract PropertyDistanceVector getOffsetVector(boolean onPort);

    PropertyLocation getSailToLocation(boolean onPort) {
        return getOffsetVector(onPort)
                .toLocation(leg.getEndLocation());
    }

    PropertyDegrees getPropertyDegreestoSail(PropertyLocation here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    abstract String strategyTimeInterval(Boat boat, Decision decision, SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow);

    public Strategy nextTimeInterval(Boat boat, SketchModel sketchproperty, int simulationtime, DecisionController timerlog, WindFlow windflow, WaterFlow waterflow) {
        Decision decision = boat.getDecision();
        if (decision.getAction() == SAILON) {
            String reason = strategyTimeInterval(boat, decision, sketchproperty, windflow, waterflow);
            timerlog.add(new BoatLogEntry(boat));
            timerlog.add(new DecisionLogEntry(boat.getName(), decision));
            timerlog.add(new ReasonLogEntry(boat.getName(), reason));
        }
        if (boat.moveUsingDecision(windflow, waterflow, decision)) {
            return leg.isFollowingLeg()
                    ? PropertyLeg.get(boat,leg.toFollowingLeg(), windflow, waterflow)
                    : new AfterFinishStrategy(boat, leg);
        }
        return null;
    }
}
