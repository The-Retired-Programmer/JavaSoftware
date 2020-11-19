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

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

class WindwardStarboardSailingDecisions extends SailingDecisions {

    @Override
    public String nextTimeInterval(Boat boat, Decision decision, SketchModel sketchproperty, CurrentLeg leg, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        PropertyDegrees meanwinddirection = windflow.getMeanFlowAngle();
        PropertyDegrees boatangletowind = boat.getDirection().absDegreesDiff(winddirection);
        if (tackifonportlayline(boat, decision, leg, strategy, winddirection)) {
            return "tacking on port layline - starboard->port";
        }
        if (adjustStarboardDirectCourseToWindwardMarkOffset(boat, decision, leg, strategy, winddirection)) {
            return "Beating on Starboard Layline to windward mark - course adjustment";
        }
        // stay in channel
        if (boat.upwindchannel != null) {
            if (leg.getDistanceToMark(boat.getLocation()) > boat.upwindchannel.getInneroffset(leg.getMarkLocation()) * 1.5) {
                if (!boat.upwindchannel.isInchannel(boat.getLocation())) {
                    decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                    return "Tacking onto port to stay within channel";
                }
            }
        }
        // check if need to tack onto best tack
        if (boat.isUpwindsailonbesttack()) {
            if (winddirection.lt(meanwinddirection)) {
                decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto best tack - port";
            }
        }
        // check if pointing high
        if (boatangletowind.lt(boat.metrics.upwindrelative)) {
            if (boat.isUpwindtackifheaded()) {
                decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto port when headed";
            }
            if (boat.isUpwindbearawayifheaded()) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Bearaway when headed";
            }
        }
        // check if pointing low
        if (boatangletowind.gt(boat.metrics.upwindrelative)) {
            if (boat.isUpwindluffupiflifted()) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), STARBOARD);
                return "Luff when lifted";
            }
        }
        return "Sail ON";
    }
}
