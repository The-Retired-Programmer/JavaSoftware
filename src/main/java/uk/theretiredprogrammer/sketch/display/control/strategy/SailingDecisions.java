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
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class SailingDecisions {

    abstract String nextTimeInterval(SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow);

    boolean tackifonstarboardlayline(Strategy strategy, Angle winddirection) {
        if (strategy.boat.isPortTackingQuadrant(strategy.getSailToLocation(false), winddirection)) {
            strategy.decision.setTURN(strategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Strategy strategy, Angle winddirection) {
        if (strategy.boat.isStarboardGybingQuadrant(strategy.getSailToLocation(false), winddirection)) {
            strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Strategy strategy, Angle winddirection) {
        if (strategy.boat.isStarboardTackingQuadrant(strategy.getSailToLocation(true), winddirection)) {
            strategy.decision.setTURN(strategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Strategy strategy, Angle winddirection) {
        if (strategy.boat.isPortGybingQuadrant(strategy.getSailToLocation(true), winddirection)) {
            strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Strategy strategy, Angle winddirection) {
        Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), true);
        Angle closehauled = strategy.boat.getPortCloseHauledCourse(winddirection);
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(strategy.boat.getProperty().getDirection(),
                coursetomark.gteq(closehauled) ? coursetomark : closehauled, strategy.decision);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Strategy strategy, Angle winddirection) {
        Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), false);
        Angle closehauled = strategy.boat.getStarboardCloseHauledCourse(winddirection);
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(strategy.boat.getProperty().getDirection(),
                coursetomark.lteq(closehauled) ? coursetomark : closehauled, strategy.decision);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Strategy strategy, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), true);
        Angle reaching = strategy.boat.getPortReachingCourse(winddirection);
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(strategy.boat.getProperty().getDirection(),
                coursetomark.lteq(reaching) ? coursetomark : reaching, strategy.decision);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Strategy strategy, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), false);
        Angle reaching = strategy.boat.getStarboardReachingCourse(winddirection);
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(strategy.boat.getProperty().getDirection(),
                coursetomark.gteq(reaching) ? coursetomark : reaching, strategy.decision);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Strategy strategy, Angle winddirection) {
        if (strategy.boat.isPort(winddirection)) {
            Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), true);
            return adjustCourse(strategy.boat.getProperty().getDirection(), coursetomark, strategy.decision);
        } else {
            Angle coursetomark = strategy.getAngletoSail(strategy.boat.getProperty().getLocation(), false);
            return adjustCourse(strategy.boat.getProperty().getDirection(), coursetomark, strategy.decision);
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
