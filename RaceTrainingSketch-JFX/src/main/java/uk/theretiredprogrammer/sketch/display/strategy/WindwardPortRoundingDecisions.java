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
package uk.theretiredprogrammer.sketch.display.strategy;

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class WindwardPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    public WindwardPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final boolean nextTimeInterval(Params params) {
        return params.isPort
                ? tackIfAtPortRoundingTurnPoint(params)
                || adjustPortDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - port tack - port rounding")
                || params.setTURN(params.portCloseHauled, STARBOARD, MINOR, "course adjustment - bearing away to hold port c/h - port tack - port rounding")
                : executeRoundingIfAtPortRoundingTurnPoint(params, getDirectionAfterTurn)
                || adjustStarboardDirectCourseToWindwardMarkOffset(params, "course adjustment - approaching mark - starboard tack - port rounding")
                || tackifonportlayline(params, "tacking on port layline - starboard->port")
                || params.setTURN(params.starboardCloseHauled, PORT, MINOR, "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding");
    }

    private boolean tackIfAtPortRoundingTurnPoint(Params params) {
        if (params.boat.isPortRear90Quadrant(params.marklocation)) {
            params.setTURN(params.starboardCloseHauled, PORT, MAJOR, "pre markrounding action - tack to starboard - port tack - port rounding");
            return true;
        }
        return false;
    }
}
