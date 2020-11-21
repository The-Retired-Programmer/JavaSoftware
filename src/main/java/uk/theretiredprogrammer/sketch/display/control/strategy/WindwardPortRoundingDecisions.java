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
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardPortRoundingDecisions extends RoundingDecisions {

    private final Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn;

    public WindwardPortRoundingDecisions(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final String nextTimeInterval(Params params) {
        if (params.isPort) {
            if (params.boat.isPortRear90Quadrant(params.marklocation)) {
                params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "pre markrounding action - tack to starboard - port tack - port rounding");
                return "pre markrounding action - tack to starboard - port tack - port rounding";
            }
            if (adjustPortDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - port tack - port rounding")) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            params.setTURN(params.portCloseHauled, STARBOARD, MINOR, "course adjustment - bearing away to hold port c/h - port tack - port rounding");
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        } else {
            if (atPortRoundingTurnPoint(params.boat, params.leg)) {
                return executePortRounding(params, getDirectionAfterTurn);
            }
            if (adjustStarboardDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - starboard tack - port rounding")) {
                return "course adjustment - approaching mark - starboard tack - port rounding";
            }
            if (tackifonportlayline(params, "tacking on port layline - starboard->port")) {
                return "tacking on port layline - starboard->port"; //DONE
            }
            params.setTURN(params.starboardCloseHauled, PORT, MINOR, "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding");
            return "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding";
        }
    }
}
