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

import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class GybingDownwindStarboardSailingDecisions extends SailingDecisions {

    @Override
    public boolean nextTimeInterval(Params params) {
        if (gybeifonportlayline(params, "gybing on port layline - starboard->port")) {
            return true;
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(params, "Reaching on starboard Layline to leeward mark - course adjustment")) {
            return true;
        }
        if (params.boat.downwindchannel != null) {
            if (params.leg.getDistanceToMark(params.location) > params.boat.downwindchannel.getInneroffset(params.marklocation) * 1.5) {
                if (!params.boat.downwindchannel.isInchannel(params.location)) {
                    return params.setTURN(params.portReaching, PORT, MAJOR, "Gybing onto port to stay in channel");
                }
            }
        }
        // check if need to gybe onto best tack
        if (params.boat.isDownwindsailonbestgybe()) {
            if (params.winddirection.gt(params.meanwinddirection)) {
                return params.setTURN(params.portReaching, PORT, MAJOR, "Gybe onto best tack - port");
            }
        }
        // check if sailing too low
        if (params.angletowind.gt(params.downwindrelative)) {
            if (params.boat.isDownwindgybeiflifted()) {
                return params.setTURN(params.portReaching, PORT, MAJOR, "Reaching - gybe oto port if lifted");
            }
            if (params.boat.isDownwindluffupiflifted()) {
                return params.setTURN(params.starboardReaching, STARBOARD, MINOR, "Reaching - luff if lifted");
            }
        }
        // check if sailing too high
        if (params.angletowind.lt(params.downwindrelative)) {
            if (params.boat.isDownwindbearawayifheaded()) {
                return params.setTURN(params.starboardReaching, PORT, MINOR, "Reaching - bearaway if headed");
            }
        }
        return params.setSAILON();
    }
}
