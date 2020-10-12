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
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardStarboardSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(PropertySketch sketchproperty, BoatStrategyForLeg legstrategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(legstrategy.boat.getProperty().getLocation()).getAngle();
        Angle meanwinddirection = windflow.getMeanFlowAngle();
        Angle boatangletowind = legstrategy.boat.getProperty().getDirection().absAngleDiff(winddirection);
        if (tackifonportlayline(legstrategy, winddirection)) {
            return "tacking on port layline - starboard->port";
        }
        if (adjustStarboardDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
            return "Beating on Starboard Layline to windward mark - course adjustment";
        }
        // stay in channel
        if (legstrategy.boat.getProperty().upwindchannel != null) {
            if (legstrategy.getDistanceToMark(legstrategy.boat.getProperty().getLocation()) > legstrategy.boat.getProperty().upwindchannel.getInneroffset(legstrategy.getMarkLocation()) * 1.5) {
                if (!legstrategy.boat.getProperty().upwindchannel.isInchannel(legstrategy.boat.getProperty().getLocation())) {
                    legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                    return "Tacking onto port to stay within channel";
                }
            }
        }
        // check if need to tack onto best tack
        if (legstrategy.boat.getProperty().isUpwindsailonbesttack()) {
            if (winddirection.lt(meanwinddirection)) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto best tack - port";
            }
        }
        // check if pointing high
        if (boatangletowind.lt(legstrategy.boat.metrics.upwindrelative)) {
            if (legstrategy.boat.getProperty().isUpwindtackifheaded()) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto port when headed";
            }
            if (legstrategy.boat.getProperty().isUpwindbearawayifheaded()) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Bearaway when headed";
            }
        }
        // check if pointing low
        if (boatangletowind.gt(legstrategy.boat.metrics.upwindrelative)) {
            if (legstrategy.boat.getProperty().isUpwindluffupiflifted()) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), STARBOARD);
                return "Luff when lifted";
            }
        }
        return "Sail ON";
    }
}
