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

public class GybingDownwindPortSailingDecisions extends SailingDecisions {

    @Override
    public String nextTimeInterval(Params params) {
        PropertyDegrees boatangletowind = params.boat.getDirection().absDegreesDiff(params.winddirection);
        PropertyDegrees meanwinddirection = params.windflow.getMeanFlowAngle();
        if (gybeifonstarboardlayline(params)) {
            return "Gybing onto starboard layline";
        }
        if (adjustPortDirectCourseToLeewardMarkOffset(params)) {
            return "Reaching on port Layline to leeward mark - course adjustment";
        }
        if (params.boat.downwindchannel != null) {
            if (params.leg.getDistanceToMark(params.boat.getLocation()) > params.boat.downwindchannel.getInneroffset(params.leg.getMarkLocation()) * 1.5) {
                if (!params.boat.downwindchannel.isInchannel(params.boat.getLocation())) {
                    params.decision.setTURN(params.boat.getStarboardReachingCourse(params.winddirection), STARBOARD);
                    return "Gybing onto starboard to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (params.boat.isDownwindsailonbestgybe()) {
            if (params.winddirection.lt(meanwinddirection)) {
                params.decision.setTURN(params.boat.getStarboardReachingCourse(params.winddirection), STARBOARD);
                return "Gybe onto best tack - starboard";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(params.boat.metrics.downwindrelative)) {
            if (params.boat.isDownwindgybeiflifted()) {
                params.decision.setTURN(params.boat.getStarboardReachingCourse(params.winddirection), STARBOARD);
                return "Reaching - gybe onto starboard if lifted";
            }
            if (params.boat.isDownwindluffupiflifted()) {
                params.decision.setTURN(params.boat.getPortReachingCourse(params.winddirection), PORT);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(params.boat.metrics.downwindrelative)) {
            if (params.boat.isDownwindbearawayifheaded()) {
                params.decision.setTURN(params.boat.getPortReachingCourse(params.winddirection), STARBOARD);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
