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
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn;

    public WindwardStarboardRoundingDecisions(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final String nextTimeInterval(Params params) {

        if (!params.boat.isPort(params.winddirection)) {
            if (params.boat.isStarboardRear90Quadrant(params.leg.getMarkLocation())) {
                params.decision.setTURN(params.boat.getPortCloseHauledCourse(params.winddirection), STARBOARD);
                return "pre markrounding action - tack to port - starboard tack - starboard rounding";
            }
            if (adjustStarboardDirectCourseToWindwardMarkOffset(params)) {
                return "course adjustment - approaching mark - starboard tack - starboard rounding";
            }
            params.decision.setTURN(params.boat.getStarboardCloseHauledCourse(params.winddirection), PORT);
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        }
        if (atStarboardRoundingTurnPoint(params.boat, params.leg)) {
            return executeStarboardRounding(params, getDirectionAfterTurn);
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(params)) {
            return "course adjustment - approaching mark - port tack - starboard rounding";
        }
        if (tackifonstarboardlayline(params)) {
            return "tacking on starboard layline - port->starboard";
        }
        params.decision.setTURN(params.boat.getPortCloseHauledCourse(params.winddirection), PORT);
        return "course adjustment - bearing away to hold port c/h - port tack - starboard rounding";
    }
}
