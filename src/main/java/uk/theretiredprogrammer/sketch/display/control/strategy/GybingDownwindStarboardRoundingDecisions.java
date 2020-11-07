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

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

class GybingDownwindStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn;

    GybingDownwindStarboardRoundingDecisions(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees winddirection = windflow.getMeanFlowAngle(strategy.boat.getLocation());
        if (strategy.boat.isPort(winddirection)) {
            if (strategy.boat.isStarboardRear90Quadrant(strategy.getMarkLocation())) {
                strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "pre markrounding action - gybe to starboard - port tack - starboard rounding";
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
                return "course adjustment - approaching mark - port tack - starboard rounding";
            }
            strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
            return "course adjustment - luff up to hold port reaching - port tack - starboard rounding";
        }
        if (atStarboardRoundingTurnPoint(strategy)) {
            return executeStarboardRounding(getDirectionAfterTurn, winddirection, strategy);
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - starboard rounding";
        }
        if (gybeifonportlayline(strategy, winddirection)) {
            return "gybing on port layline - starboard->port";
        }
        strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - starboard rounding";
    }
}
