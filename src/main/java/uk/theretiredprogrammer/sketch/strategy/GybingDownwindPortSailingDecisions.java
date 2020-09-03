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

import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindPortSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(Controller controller, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getFlow(legstrategy.boat.getLocation()).getAngle();
        Angle boatangletowind = legstrategy.boat.getDirection().absAngleDiff(winddirection);
        Angle meanwinddirection = controller.windflow.getMeanFlowAngle();
        if (gybeifonstarboardlayline(legstrategy, winddirection)) {
            return "Gybing onto starboard layline";
        }
        if (adjustPortDirectCourseToLeewardMarkOffset(legstrategy, winddirection)) {
            return "Reaching on port Layline to leeward mark - course adjustment";
        }
        if (legstrategy.boat.downwindchannel != null) {
            if (legstrategy.getDistanceToMark(legstrategy.boat.getLocation()) > legstrategy.boat.downwindchannel.getInneroffset(legstrategy.getMarkLocation()) * 1.5) {
                if (!legstrategy.boat.downwindchannel.isInchannel(legstrategy.boat.getLocation())) {
                    legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                    return "Gybing onto starboard to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (legstrategy.boat.getDownwindsailonbestgybe()) {
            if (winddirection.lt(meanwinddirection)) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Gybe onto best tack - starboard";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(legstrategy.boat.metrics.downwindrelative)) {
            if (legstrategy.boat.getDownwindgybeiflifted()) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Reaching - gybe onto starboard if lifted";
            }
            if (legstrategy.boat.getDownwindluffupiflifted()) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), PORT);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(legstrategy.boat.metrics.downwindrelative)) {
            if (legstrategy.boat.getDownwindbearawayifheaded()) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), STARBOARD);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
