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

public class WindwardPortSailingDecisions extends SailingDecisions {

    @Override
    public void nextTimeInterval(Params params) {
        if (tackifonstarboardlayline(params, "tacking on starboard layline - port->starboard")) {
            return;
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(params, "Beating on Port Layline to windward mark - course adjustment")) {
            return;
        }
        // stay in channel
        if (params.boat.upwindchannel != null) {
            if (params.leg.getDistanceToMark(params.location) > params.boat.upwindchannel.getInneroffset(params.marklocation) * 1.5) {
                if (!params.boat.upwindchannel.isInchannel(params.location)) {
                    params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tacking onto starboard to stay within channel");
                    return;
                }
            }
        }
        // check if need to tack onto best tack
        if (params.boat.isUpwindsailonbesttack()) {
            if (params.winddirection.gt(params.meanwinddirection)) {
                params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tack onto best tack - starboard");
                return;
            }
        }
        // check if pointing high
        if (params.angletowind.lt(params.upwindrelative)) {
            if (params.boat.isUpwindtackifheaded()) {
                params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "Tack onto starboard when headed");
                return;
            }
            if (params.boat.isUpwindbearawayifheaded()) {
                params.setTURN(params.portCloseHauled, STARBOARD, MINOR, "Bearaway when headed");
                return;
            }
        }
        // check if pointing low
        if (params.angletowind.gt(params.upwindrelative)) {
            if (params.boat.isUpwindluffupiflifted()) {
                params.setTURN(params.portCloseHauled, PORT, MINOR, "Luff when lifted");
                return;
            }
        }
        params.setSAILON();
    }
}
