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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class SailingDecisions {

    abstract String nextTimeInterval(Boat boat, Decision decision, SketchModel sketchproperty, CurrentLeg leg, Strategy strategy, WindFlow windflow, WaterFlow waterflow);

    boolean tackifonstarboardlayline(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        if (boat.isPortTackingQuadrant(strategy.getSailToLocation(false), winddirection)) {
            decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        if (boat.isStarboardGybingQuadrant(strategy.getSailToLocation(false), winddirection)) {
            decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        if (boat.isStarboardTackingQuadrant(strategy.getSailToLocation(true), winddirection)) {
            decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        if (boat.isPortGybingQuadrant(strategy.getSailToLocation(true), winddirection)) {
            decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), true);
        PropertyDegrees closehauled = boat.getPortCloseHauledCourse(winddirection);
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(boat.getDirection(),
                coursetomark.gteq(closehauled) ? coursetomark : closehauled, decision);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), false);
        PropertyDegrees closehauled = boat.getStarboardCloseHauledCourse(winddirection);
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(boat.getDirection(),
                coursetomark.lteq(closehauled) ? coursetomark : closehauled, decision);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), true);
        PropertyDegrees reaching = boat.getPortReachingCourse(winddirection);
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(boat.getDirection(),
                coursetomark.lteq(reaching) ? coursetomark : reaching, decision);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), false);
        PropertyDegrees reaching = boat.getStarboardReachingCourse(winddirection);
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(boat.getDirection(),
                coursetomark.gteq(reaching) ? coursetomark : reaching, decision);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Boat boat, Decision decision, Strategy strategy, PropertyDegrees winddirection) {
        if (boat.isPort(winddirection)) {
            PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), true);
            return adjustCourse(boat.getDirection(), coursetomark, decision);
        } else {
            PropertyDegrees coursetomark = strategy.getPropertyDegreestoSail(boat.getLocation(), false);
            return adjustCourse(boat.getDirection(), coursetomark, decision);
        }
    }

    private boolean adjustCourse(PropertyDegrees current, PropertyDegrees target, Decision decision) {
        if (target.neq(current)) {
            decision.setTURN(target, target.lteq(current));
        }
        return true;
    }
}
