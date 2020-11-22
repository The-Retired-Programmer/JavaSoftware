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

public class GybingDownwindStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    public GybingDownwindStarboardRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final boolean nextTimeInterval(Params params) {
        return params.isPort
                ? gybeIfAtStarboardRoundingTurnPoint(params)
                || adjustPortDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - port tack - starboard rounding")
                || params.setTURN(params.portReaching, PORT, MINOR, "course adjustment - luff up to hold port reaching - port tack - starboard rounding")
                : ExecuteRoundingIfAtStarboardRoundingTurnPoint(params, getDirectionAfterTurn)
                || adjustStarboardDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - starboard tack - starboard rounding")
                || gybeifonportlayline(params, "gybing on port layline - starboard->port")
                || params.setTURN(params.starboardReaching, STARBOARD, MINOR, "course adjustment - luff up to hold starboard reaching - starboard tack - starboard rounding");
    }

    private boolean gybeIfAtStarboardRoundingTurnPoint(Params params) {
        if (params.boat.isStarboardRear90Quadrant(params.marklocation)) {
            params.setTURN(params.starboardReaching, STARBOARD, MAJOR, "pre markrounding action - gybe to starboard - port tack - starboard rounding");
            return true;
        }
        return false;
    }
}
