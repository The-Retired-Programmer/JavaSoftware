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

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    GybingDownwindPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(PropertySketch sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getMeanFlowAngle(strategy.boat.getProperty().getLocation());
        if (strategy.boat.isPort(winddirection)) {
            if (atPortRoundingTurnPoint(strategy)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, strategy);
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            if (gybeifonstarboardlayline(strategy, winddirection)) {
                return "gybing on starboard layline - port->starboard";
            }
            strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
            return "course adjustment - luff up to hold port reaching - port tack - port rounding";
        }
        if (strategy.boat.isPortRear90Quadrant(strategy.getMarkLocation())) {
            strategy.decision.setTURN(strategy.boat.getPortReachingCourse(winddirection), PORT);
            return "pre markrounding action - gybe to port - starboard tack - port rounding";
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(strategy, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - port rounding";
        }
        strategy.decision.setTURN(strategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - port rounding";
    }
}
