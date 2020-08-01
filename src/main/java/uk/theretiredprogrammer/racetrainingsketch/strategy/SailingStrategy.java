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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class SailingStrategy {

    abstract String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection);

    boolean tackifonstarboardlayline(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        if (boat.isPortTackingQuadrant(leg.getSailToLocation(false, winddirection), winddirection)) {
            decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), ANTICLOCKWISE);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        if (boat.isStarboardGybingQuadrant(leg.getSailToLocation(false, winddirection), winddirection)) {
            decision.setTURN(boat.getStarboardReachingCourse(winddirection), CLOCKWISE);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        if (boat.isStarboardTackingQuadrant(leg.getSailToLocation(true, winddirection), winddirection)) {
            decision.setTURN(boat.getPortCloseHauledCourse(winddirection), CLOCKWISE);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        if (boat.isPortGybingQuadrant(leg.getSailToLocation(true, winddirection), winddirection)) {
            decision.setTURN(boat.getPortReachingCourse(winddirection), ANTICLOCKWISE);
            return true;
        }
        return false;
    }

    boolean adjustDirectCourseToWindwardMarkOffset(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {

        // check and adjust if boat can sail dirctly to next mark (offset)
        if (boat.isPort(winddirection)) {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), true, winddirection);
            Angle closehauled = boat.getPortCloseHauledCourse(winddirection);
            if (coursetomark.lt(closehauled)) {
                return false;
            }
            return adjustCourse(boat.getDirection(),
                    coursetomark.gteq(closehauled) ? coursetomark : closehauled, decision);
        } else {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), false, winddirection);
            Angle closehauled = boat.getStarboardCloseHauledCourse(winddirection);
            if (coursetomark.gt(closehauled)) {
                return false;
            }
            return adjustCourse(boat.getDirection(),
                    coursetomark.lteq(closehauled) ? coursetomark : closehauled, decision);
        }
    }

    boolean adjustDirectCourseToLeewardMarkOffset(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        if (boat.isPort(winddirection)) {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), true, winddirection);
            Angle reaching = boat.getPortReachingCourse(winddirection);
            if (coursetomark.gt(reaching)) {
                return false;
            }
            return adjustCourse(boat.getDirection(),
                    coursetomark.lteq(reaching) ? coursetomark : reaching, decision);
        } else {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), false, winddirection);
            Angle reaching = boat.getStarboardReachingCourse(winddirection);
            if (coursetomark.lt(reaching)) {
                return false;
            }
            return adjustCourse(boat.getDirection(),
                    coursetomark.gteq(reaching) ? coursetomark : reaching, decision);
        }
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Boat boat, CourseLegWithStrategy leg, Decision decision, Angle winddirection) {
        if (boat.isPort(winddirection)) {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), true, winddirection);
            return adjustCourse(boat.getDirection(), coursetomark, decision);
        } else {
            Angle coursetomark = leg.getAngletoSail(boat.getLocation(), false, winddirection);
            return adjustCourse(boat.getDirection(), coursetomark, decision);
        }
    }

    private boolean adjustCourse(Angle current, Angle target, Decision decision) {
        if (target.neq(current)) {
            decision.setTURN(target, target.gt(current) ? CLOCKWISE : ANTICLOCKWISE);
            return true;
        }
        return false;
    }

}
