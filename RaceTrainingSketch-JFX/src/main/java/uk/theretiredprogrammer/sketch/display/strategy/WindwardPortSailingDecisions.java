/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.strategy;

import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardPortSailingDecisions extends SailingDecisions {

    @Override
    public boolean nextTimeInterval(Params params) {
        if (tackifonstarboardlayline(params, "tacking on starboard layline - port->starboard")) {
            return true;
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(params, "Beating on Port Layline to windward mark - course adjustment")) {
            return true;
        }
        // stay in channel
        if (params.boat.upwindchannel != null) {
            if (params.leg.getDistanceToMark(params.location) > params.boat.upwindchannel.getInneroffset(params.marklocation) * 1.5) {
                if (!params.boat.upwindchannel.isInchannel(params.location)) {
                    return params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tacking onto starboard to stay within channel");
                }
            }
        }
        // check if need to tack onto best tack
        if (params.boat.isUpwindsailonbesttack()) {
            if (params.winddirection.gt(params.meanwinddirection)) {
                return params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tack onto best tack - starboard");
            }
        }
        // check if pointing high
        if (params.boat.absAngleFrom(params.winddirection).lt(params.upwindrelative)) {
            if (params.boat.isUpwindtackifheaded()) {
                return params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tack onto starboard when headed");
            }
            if (params.boat.isUpwindbearawayifheaded()) {
                return params.setTURN(params.portCloseHauled, STARBOARD, MINOR, "Bearaway when headed");
            }
        }
        // check if pointing low
        if (params.boat.absAngleFrom(params.winddirection).gt(params.upwindrelative)) {
            if (params.boat.isUpwindluffupiflifted()) {
                return params.setTURN(params.portCloseHauled, PORT, MINOR, "Luff when lifted");
            }
        }
        return params.setSAILON();
    }
}
