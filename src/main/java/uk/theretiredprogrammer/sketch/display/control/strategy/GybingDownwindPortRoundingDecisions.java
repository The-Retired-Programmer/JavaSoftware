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

public class GybingDownwindPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    public GybingDownwindPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final boolean nextTimeInterval(Params params) {
        return params.isPort
                ? executeRoundingIfAtPortRoundingTurnPoint(params, getDirectionAfterTurn)
                || adjustPortDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - port tack - port rounding")
                || gybeifonstarboardlayline(params, "gybing on starboard layline - port->starboard")
                || params.setTURN(params.portReaching, PORT, MINOR, "course adjustment - luff up to hold port reaching - port tack - port rounding")
                : gybeIfAtPortRoundingTurnPoint(params)
                || adjustStarboardDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - starboard tack - port rounding")
                || params.setTURN(params.starboardReaching, STARBOARD, MINOR, "course adjustment - luff up to hold starboard reaching - starboard tack - port rounding");
    }

    private boolean gybeIfAtPortRoundingTurnPoint(Params params) {
        if (params.boat.isPortRear90Quadrant(params.marklocation)) {
            params.setTURN(params.portReaching, PORT, MAJOR, "pre markrounding action - gybe to port - starboard tack - port rounding");
            return true;
        }
        return false;
    }
}
