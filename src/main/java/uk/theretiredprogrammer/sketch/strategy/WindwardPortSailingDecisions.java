/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.strategy;

import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardPortSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(PropertySketch sketchproperty, BoatStrategyForLeg legstrategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(legstrategy.boat.getProperty().getLocation()).getAngle();
        Angle meanwinddirection = windflow.getMeanFlowAngle();
        Angle boatangletowind = legstrategy.boat.getProperty().getDirection().absAngleDiff(winddirection);
        if (tackifonstarboardlayline(legstrategy, winddirection)) {
            return "tacking on starboard layline - port->starboard";
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
            return "Beating on Port Layline to windward mark - course adjustment";
        }
        // stay in channel
        if (legstrategy.boat.getProperty().upwindchannel != null) {
            if (legstrategy.getDistanceToMark(legstrategy.boat.getProperty().getLocation()) > legstrategy.boat.getProperty().upwindchannel.getInneroffset(legstrategy.getMarkLocation()) * 1.5) {
                if (!legstrategy.boat.getProperty().upwindchannel.isInchannel(legstrategy.boat.getProperty().getLocation())) {
                    legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                    return "Tacking onto starboard to stay within channel";
                }
            }
        }
        // check if need to tack onto best tack
        if (legstrategy.boat.getProperty().isUpwindsailonbesttack()) {
            if (winddirection.gt(meanwinddirection)) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Tack onto best tack - starboard";
            }
        }
        // check if pointing high
        if (boatangletowind.lt(legstrategy.boat.metrics.upwindrelative)) {
            if (legstrategy.boat.getProperty().isUpwindtackifheaded()) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Tack onto starboard when headed";
            }
            if (legstrategy.boat.getProperty().isUpwindbearawayifheaded()) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Bearaway when headed";
            }
        }
        // check if pointing low
        if (boatangletowind.gt(legstrategy.boat.metrics.upwindrelative)) {
            if (legstrategy.boat.getProperty().isUpwindluffupiflifted()) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), PORT);
                return "Luff when lifted";
            }
        }
        return "Sail ON";
    }
}
