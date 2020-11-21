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

public class GybingDownwindPortSailingDecisions extends SailingDecisions {

    @Override
    public void nextTimeInterval(Params params) {
        if (gybeifonstarboardlayline(params, "gybing on starboard layline - port->starboard")) {
            return;
        }
        if (adjustPortDirectCourseToLeewardMarkOffset(params, "Reaching on port Layline to leeward mark - course adjustment")) {
            return;
        }
        if (params.boat.downwindchannel != null) {
            if (params.leg.getDistanceToMark(params.location) > params.boat.downwindchannel.getInneroffset(params.marklocation) * 1.5) {
                if (!params.boat.downwindchannel.isInchannel(params.location)) {
                    params.setTURN(params.starboardReaching, STARBOARD, MAJOR, "Gybing onto starboard to stay in channel");
                    return;
                }
            }
        }
        // check if need to gybe onto best tack
        if (params.boat.isDownwindsailonbestgybe()) {
            if (params.winddirection.lt(params.meanwinddirection)) {
                params.setTURN(params.starboardReaching, STARBOARD, MAJOR, "Gybe onto best tack - starboard");
                return;
            }
        }
        // check if sailing too low
        if (params.angletowind.gt(params.boat.metrics.downwindrelative)) {
            if (params.boat.isDownwindgybeiflifted()) {
                params.setTURN(params.starboardReaching, STARBOARD, MAJOR, "Reaching - gybe onto starboard if lifted");
                return;
            }
            if (params.boat.isDownwindluffupiflifted()) {
                params.setTURN(params.portReaching, PORT, MINOR, "Reaching - luff if lifted");
                return;
            }
        }
        // check if sailing too high
        if (params.angletowind.lt(params.boat.metrics.downwindrelative)) {
            if (params.boat.isDownwindbearawayifheaded()) {
                params.setTURN(params.portReaching, STARBOARD, MINOR, "Reaching - bearaway if headed");
                return;
            }
        }
        params.setSAILON();
        return;
    }
}
