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
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    public WindwardStarboardRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final boolean nextTimeInterval(Params params) {
        return params.isPort
                ? ExecuteRoundingIfAtStarboardRoundingTurnPoint(params, getDirectionAfterTurn)
                || adjustPortDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - port tack - starboard rounding")
                || tackifonstarboardlayline(params, "tacking on starboard layline - port->starboard")
                || params.setTURN(params.portCloseHauled, PORT, MINOR, "course adjustment - bearing away to hold port c/h - port tack - starboard rounding")
                : tackIfAtStarboardRoundingTurnPoint(params)
                || adjustStarboardDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - starboard tack - starboard rounding")
                || params.setTURN(params.starboardCloseHauled, PORT, MINOR, "course adjustment - bearing away to hold port c/h - port tack - port rounding");
    }

    private boolean tackIfAtStarboardRoundingTurnPoint(Params params) {
        if (params.boat.isStarboardRear90Quadrant(params.marklocation)) {
            params.setTURN(params.portCloseHauled, STARBOARD, MAJOR, "pre markrounding action - tack to port - starboard tack - starboard rounding");
            return true;
        }
        return false;
    }
}
