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

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;

class GybingDownwindPortSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(Boat boat, Decision decision, SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        PropertyDegrees boatangletowind = boat.getDirection().absDegreesDiff(winddirection);
        PropertyDegrees meanwinddirection = windflow.getMeanFlowAngle();
        if (gybeifonstarboardlayline(boat, decision, strategy, winddirection)) {
            return "Gybing onto starboard layline";
        }
        if (adjustPortDirectCourseToLeewardMarkOffset(boat, decision, strategy, winddirection)) {
            return "Reaching on port Layline to leeward mark - course adjustment";
        }
        if (boat.downwindchannel != null) {
            if (strategy.getDistanceToMark(boat.getLocation()) > boat.downwindchannel.getInneroffset(strategy.getMarkLocation()) * 1.5) {
                if (!boat.downwindchannel.isInchannel(boat.getLocation())) {
                    decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
                    return "Gybing onto starboard to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (boat.isDownwindsailonbestgybe()) {
            if (winddirection.lt(meanwinddirection)) {
                decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Gybe onto best tack - starboard";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(boat.metrics.downwindrelative)) {
            if (boat.isDownwindgybeiflifted()) {
                decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Reaching - gybe onto starboard if lifted";
            }
            if (boat.isDownwindluffupiflifted()) {
                decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(boat.metrics.downwindrelative)) {
            if (boat.isDownwindbearawayifheaded()) {
                decision.setTURN(boat.getPortReachingCourse(winddirection), STARBOARD);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
