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

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public abstract class SailingDecisions {

    public abstract boolean nextTimeInterval(Params params);

    boolean tackifonstarboardlayline(Params params, String reason) {
        if (params.boat.isPortTackingQuadrant(params.leg.getSailToLocation(false), params.winddirection)) {
            params.setTURN(params.starboardCloseHauled, PORT, MAJOR, reason);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Params params, String reason) {
        if (params.boat.isStarboardGybingQuadrant(params.leg.getSailToLocation(false), params.winddirection)) {
            params.setTURN(params.starboardReaching, STARBOARD, MAJOR, reason);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Params params, String reason) {
        if (params.boat.isStarboardTackingQuadrant(params.leg.getSailToLocation(true), params.winddirection)) {
            params.setTURN(params.portCloseHauled, STARBOARD, MAJOR, reason);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Params params, String reason) {
        if (params.boat.isPortGybingQuadrant(params.leg.getSailToLocation(true), params.winddirection)) {
            params.setTURN(params.portReaching, PORT, MAJOR, reason);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Params params, String reason) {
        Angle coursetomark = params.angleToSailToMark(true);
        Angle closehauled = params.portCloseHauled;
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(params, coursetomark.gteq(closehauled) ? coursetomark : closehauled, MINOR, reason);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Params params, String reason) {
        Angle coursetomark = params.angleToSailToMark(false);
        Angle closehauled = params.starboardCloseHauled;
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(params, coursetomark.lteq(closehauled) ? coursetomark : closehauled, MINOR, reason);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Params params, String reason) {
        Angle coursetomark = params.angleToSailToMark(true);
        Angle reaching = params.portReaching;
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(params, coursetomark.lteq(reaching) ? coursetomark : reaching, MINOR, reason);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Params params, String reason) {
        Angle coursetomark = params.angleToSailToMark(false);
        Angle reaching = params.starboardReaching;
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(params, coursetomark.gteq(reaching) ? coursetomark : reaching, MINOR, reason);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Params params, String reason) {
        Angle coursetomark = params.angleToSailToMark();
        return adjustCourse(params, coursetomark, MINOR, reason);
    }

    private boolean adjustCourse(Params params, Angle target, Importance importance, String reason) {
        if (target.neq(params.heading)) {
            params.setTURN(target, target.lteq(params.heading), importance, reason);
        }
        return true;
    }
}
