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

public class GybingDownwindStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn;

    public GybingDownwindStarboardRoundingDecisions(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    public final void nextTimeInterval(Params params) {
        if (params.isPort) {
            if (params.boat.isStarboardRear90Quadrant(params.marklocation)) {
                params.setTURN(params.starboardReaching, STARBOARD, MAJOR, "pre markrounding action - gybe to starboard - port tack - starboard rounding");
                return;
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - port tack - starboard rounding")) {
                return;
            }
            params.setTURN(params.portReaching, PORT, MINOR, "course adjustment - luff up to hold port reaching - port tack - starboard rounding");
            return;
        }
        if (atStarboardRoundingTurnPoint(params.boat, params.leg)) {
            executeStarboardRounding(params, getDirectionAfterTurn);
            return;
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(params, "course adjustment - approaching mark - starboard tack - starboard rounding")) {
            return;
        }
        if (gybeifonportlayline(params, "gybing on port layline - starboard->port")) {
            return;
        }
        params.setTURN(params.starboardReaching, STARBOARD, MINOR, "course adjustment - luff up to hold starboard reaching - starboard tack - starboard rounding");
    }
}
