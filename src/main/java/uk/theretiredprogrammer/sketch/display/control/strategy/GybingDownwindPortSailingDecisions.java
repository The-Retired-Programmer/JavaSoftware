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
import uk.theretiredprogrammer.sketch.display.entity.base.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindPortSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(PropertySketch sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(strategy.boat.getProperty().getLocation()).getAngle();
        Angle boatangletowind = strategy.boat.getProperty().getDirection().absAngleDiff(winddirection);
        Angle meanwinddirection = windflow.getMeanFlowAngle();
        if (gybeifonstarboardlayline(strategy, winddirection)) {
            return "Gybing onto starboard layline";
        }
        if (adjustPortDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
            return "Reaching on port Layline to leeward mark - course adjustment";
        }
        if (strategy.boat.getProperty().downwindchannel != null) {
            if (strategy.getDistanceToMark(strategy.boat.getProperty().getLocation()) > strategy.boat.getProperty().downwindchannel.getInneroffset(strategy.getMarkLocation()) * 1.5) {
                if (!strategy.boat.getProperty().downwindchannel.isInchannel(strategy.boat.getProperty().getLocation())) {
                    strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                    return "Gybing onto starboard to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (strategy.boat.getProperty().isDownwindsailonbestgybe()) {
            if (winddirection.lt(meanwinddirection)) {
                strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Gybe onto best tack - starboard";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(strategy.boat.metrics.downwindrelative)) {
            if (strategy.boat.getProperty().isDownwindgybeiflifted()) {
                strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Reaching - gybe onto starboard if lifted";
            }
            if (strategy.boat.getProperty().isDownwindluffupiflifted()) {
                strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(strategy.boat.metrics.downwindrelative)) {
            if (strategy.boat.getProperty().isDownwindbearawayifheaded()) {
                strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), STARBOARD);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
