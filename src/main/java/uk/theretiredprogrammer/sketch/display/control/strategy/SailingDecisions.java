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

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public abstract class SailingDecisions {

    public abstract String nextTimeInterval(Params params);

    boolean tackifonstarboardlayline(Params params) {
        if (params.boat.isPortTackingQuadrant(params.leg.getSailToLocation(false), params.winddirection)) {
            params.decision.setTURN(params.boat.getStarboardCloseHauledCourse(params.winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Params params) {
        if (params.boat.isStarboardGybingQuadrant(params.leg.getSailToLocation(false), params.winddirection)) {
            params.decision.setTURN(params.boat.getStarboardReachingCourse(params.winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Params params) {
        if (params.boat.isStarboardTackingQuadrant(params.leg.getSailToLocation(true), params.winddirection)) {
            params.decision.setTURN(params.boat.getPortCloseHauledCourse(params.winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Params params) {
        if (params.boat.isPortGybingQuadrant(params.leg.getSailToLocation(true), params.winddirection)) {
            params.decision.setTURN(params.boat.getPortReachingCourse(params.winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Params params) {
        PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), true);
        PropertyDegrees closehauled = params.boat.getPortCloseHauledCourse(params.winddirection);
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(params.boat.getDirection(),
                coursetomark.gteq(closehauled) ? coursetomark : closehauled, params.decision);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Params params) {
        PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), false);
        PropertyDegrees closehauled = params.boat.getStarboardCloseHauledCourse(params.winddirection);
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(params.boat.getDirection(),
                coursetomark.lteq(closehauled) ? coursetomark : closehauled, params.decision);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Params params) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), true);
        PropertyDegrees reaching = params.boat.getPortReachingCourse(params.winddirection);
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(params.boat.getDirection(),
                coursetomark.lteq(reaching) ? coursetomark : reaching, params.decision);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Params params) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), false);
        PropertyDegrees reaching = params.boat.getStarboardReachingCourse(params.winddirection);
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(params.boat.getDirection(),
                coursetomark.gteq(reaching) ? coursetomark : reaching, params.decision);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Params params) {
        if (params.boat.isPort(params.winddirection)) {
            PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), true);
            return adjustCourse(params.boat.getDirection(), coursetomark, params.decision);
        } else {
            PropertyDegrees coursetomark = params.leg.getAngletoSail(params.boat.getLocation(), false);
            return adjustCourse(params.boat.getDirection(), coursetomark, params.decision);
        }
    }

    private boolean adjustCourse(PropertyDegrees current, PropertyDegrees target, Decision decision) {
        if (target.neq(current)) {
            decision.setTURN(target, target.lteq(current));
        }
        return true;
    }
}
