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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindStarboardSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(strategy.boat.getLocation()).getAngle();
        Angle boatangletowind = strategy.boat.getDirection().absAngleDiff(winddirection);
        Angle meanwinddirection = windflow.getMeanFlowAngle();
        // check if need to gybe for mark
        if (gybeifonportlayline(strategy, winddirection)) {
            return "Gybing onto port layline";
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
            return "Reaching on starboard Layline to leeward mark - course adjustment";
        }
        if (strategy.boat.downwindchannel != null) {
            if (strategy.getDistanceToMark(strategy.boat.getLocation()) > strategy.boat.downwindchannel.getInneroffset(strategy.getMarkLocation()) * 1.5) {
                if (!strategy.boat.downwindchannel.isInchannel(strategy.boat.getLocation())) {
                    strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
                    return "Gybing onto port to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (strategy.boat.isDownwindsailonbestgybe()) {
            if (winddirection.gt(meanwinddirection)) {
                strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
                return "Gybe onto best tack - port";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(strategy.boat.metrics.downwindrelative)) {
            if (strategy.boat.isDownwindgybeiflifted()) {
                strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
                return "Reaching - gybe oto port if lifted";
            }
            if (strategy.boat.isDownwindluffupiflifted()) {
                strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(strategy.boat.metrics.downwindrelative)) {
            if (strategy.boat.isDownwindbearawayifheaded()) {
                strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), PORT);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
