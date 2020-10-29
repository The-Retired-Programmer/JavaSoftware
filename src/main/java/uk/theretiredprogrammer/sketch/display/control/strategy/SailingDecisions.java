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

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class SailingDecisions {

    abstract String nextTimeInterval(PropertySketch sketchproperty, Strategy legstrategy, WindFlow windflow, WaterFlow waterflow);

    boolean tackifonstarboardlayline(Strategy legstrategy, Angle winddirection) {
        if (legstrategy.boat.isPortTackingQuadrant(legstrategy.getSailToLocation(false), winddirection)) {
            legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Strategy legstrategy, Angle winddirection) {
        if (legstrategy.boat.isStarboardGybingQuadrant(legstrategy.getSailToLocation(false), winddirection)) {
            legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Strategy legstrategy, Angle winddirection) {
        if (legstrategy.boat.isStarboardTackingQuadrant(legstrategy.getSailToLocation(true), winddirection)) {
            legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Strategy legstrategy, Angle winddirection) {
        if (legstrategy.boat.isPortGybingQuadrant(legstrategy.getSailToLocation(true), winddirection)) {
            legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Strategy legstrategy, Angle winddirection) {
        Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), true);
        Angle closehauled = legstrategy.boat.getPortCloseHauledCourse(winddirection);
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(legstrategy.boat.getProperty().getDirection(),
                coursetomark.gteq(closehauled) ? coursetomark : closehauled, legstrategy.decision);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Strategy legstrategy, Angle winddirection) {
        Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), false);
        Angle closehauled = legstrategy.boat.getStarboardCloseHauledCourse(winddirection);
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(legstrategy.boat.getProperty().getDirection(),
                coursetomark.lteq(closehauled) ? coursetomark : closehauled, legstrategy.decision);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Strategy legstrategy, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), true);
        Angle reaching = legstrategy.boat.getPortReachingCourse(winddirection);
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(legstrategy.boat.getProperty().getDirection(),
                coursetomark.lteq(reaching) ? coursetomark : reaching, legstrategy.decision);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Strategy legstrategy, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), false);
        Angle reaching = legstrategy.boat.getStarboardReachingCourse(winddirection);
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(legstrategy.boat.getProperty().getDirection(),
                coursetomark.gteq(reaching) ? coursetomark : reaching, legstrategy.decision);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Strategy legstrategy, Angle winddirection) {
        if (legstrategy.boat.isPort(winddirection)) {
            Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), true);
            return adjustCourse(legstrategy.boat.getProperty().getDirection(), coursetomark, legstrategy.decision);
        } else {
            Angle coursetomark = legstrategy.getAngletoSail(legstrategy.boat.getProperty().getLocation(), false);
            return adjustCourse(legstrategy.boat.getProperty().getDirection(), coursetomark, legstrategy.decision);
        }
    }

    private boolean adjustCourse(Angle current, Angle target, Decision decision) {
        if (target.neq(current)) {
            decision.setTURN(target, target.lteq(current));
            return true;
        }
        return true;
    }
}
