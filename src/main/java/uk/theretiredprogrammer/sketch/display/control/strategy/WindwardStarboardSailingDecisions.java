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
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardStarboardSailingDecisions extends SailingDecisions {

    @Override
    public String nextTimeInterval(Params params) {
        PropertyDegrees winddirection = params.windflow.getFlow(params.boat.getLocation()).getDegreesProperty();
        PropertyDegrees meanwinddirection = params.windflow.getMeanFlowAngle();
        PropertyDegrees boatangletowind = params.boat.getDirection().absDegreesDiff(winddirection);
        if (tackifonportlayline(params)) {
            return "tacking on port layline - starboard->port";
        }
        if (adjustStarboardDirectCourseToWindwardMarkOffset(params)) {
            return "Beating on Starboard Layline to windward mark - course adjustment";
        }
        // stay in channel
        if (params.boat.upwindchannel != null) {
            if (params.leg.getDistanceToMark(params.boat.getLocation()) > params.boat.upwindchannel.getInneroffset(params.leg.getMarkLocation()) * 1.5) {
                if (!params.boat.upwindchannel.isInchannel(params.boat.getLocation())) {
                    params.decision.setTURN(params.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                    return "Tacking onto port to stay within channel";
                }
            }
        }
        // check if need to tack onto best tack
        if (params.boat.isUpwindsailonbesttack()) {
            if (winddirection.lt(meanwinddirection)) {
                params.decision.setTURN(params.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto best tack - port";
            }
        }
        // check if pointing high
        if (boatangletowind.lt(params.boat.metrics.upwindrelative)) {
            if (params.boat.isUpwindtackifheaded()) {
                params.decision.setTURN(params.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Tack onto port when headed";
            }
            if (params.boat.isUpwindbearawayifheaded()) {
                params.decision.setTURN(params.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Bearaway when headed";
            }
        }
        // check if pointing low
        if (boatangletowind.gt(params.boat.metrics.upwindrelative)) {
            if (params.boat.isUpwindluffupiflifted()) {
                params.decision.setTURN(params.boat.getStarboardCloseHauledCourse(winddirection), STARBOARD);
                return "Luff when lifted";
            }
        }
        return "Sail ON";
    }
}
