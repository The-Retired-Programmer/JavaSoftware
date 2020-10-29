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
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    WindwardStarboardRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(PropertySketch sketchproperty, Strategy legstrategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(legstrategy.boat.getProperty().getLocation()).getAngle();
        if (!legstrategy.boat.isPort(winddirection)) {
            if (legstrategy.boat.isStarboardRear90Quadrant(legstrategy.getMarkLocation())) {
                legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "pre markrounding action - tack to port - starboard tack - starboard rounding";
            }
            if (adjustStarboardDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
                return "course adjustment - approaching mark - starboard tack - starboard rounding";
            }
            legstrategy.decision.setTURN(legstrategy.boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        }
        if (atStarboardRoundingTurnPoint(legstrategy)) {
            return executeStarboardRounding(getDirectionAfterTurn, winddirection, legstrategy);
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(legstrategy, winddirection)) {
            return "course adjustment - approaching mark - port tack - starboard rounding";
        }
        if (tackifonstarboardlayline(legstrategy, winddirection)) {
            return "tacking on starboard layline - port->starboard";
        }
        legstrategy.decision.setTURN(legstrategy.boat.getPortCloseHauledCourse(winddirection), PORT);
        return "course adjustment - bearing away to hold port c/h - port tack - starboard rounding";
    }
}
