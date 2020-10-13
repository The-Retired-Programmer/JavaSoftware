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
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    WindwardPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(PropertySketch sketchproperty, BoatStrategyForLeg legstrategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(legstrategy.boat.getProperty().getLocation()).getAngle();
        if (legstrategy.boat.isPort(winddirection)) {
            if (legstrategy.boat.isPortRear90Quadrant(legstrategy.getMarkLocation())) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "pre markrounding action - tack to starboard - port tack - port rounding";
            }
            if (adjustPortDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        } else {
            if (atPortRoundingTurnPoint(legstrategy)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, legstrategy);
            }
            if (adjustStarboardDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
                return "course adjustment - approaching mark - starboard tack - port rounding";
            }
            if (tackifonportlayline(legstrategy, winddirection)) {
                return "tacking on port layline - starboard->port";
            }
            legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding";
        }
    }
}
