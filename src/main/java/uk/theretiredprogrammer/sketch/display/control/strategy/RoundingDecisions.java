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
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public abstract class RoundingDecisions extends SailingDecisions {

    final boolean atPortRoundingTurnPoint(Boat boat, CurrentLeg leg) {
        return boat.isPortRear90Quadrant(leg.getMarkLocation());
    }

    final boolean atStarboardRoundingTurnPoint(Boat boat, CurrentLeg leg) {
        return boat.isStarboardRear90Quadrant(leg.getMarkLocation());
    }

    final String executePortRounding(Params params, Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(params.winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(params.heading);
        if (turnangle.gt(90)) {
            params.setTURN(params.heading.sub(90), PORT, MAJOR, "markrounding action - first phase - starboard tack - port rounding");
            return "markrounding action - first phase - starboard tack - port rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        params.setMARKROUNDING(finaldirection, PORT, MAJOR, "markrounding action - starboard tack - port rounding");
        return "markrounding action - starboard tack - port rounding";
    }

    final String executeStarboardRounding(Params params, Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(params.winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(params.heading);
        if (turnangle.gt(90)) {
            params.setTURN(params.heading.plus(90), STARBOARD, MAJOR, "markrounding action - first phase - port tack - starboard rounding");
            return "markrounding action - first phase - port tack - starboard rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        params.setMARKROUNDING(finaldirection, STARBOARD, MAJOR, "markrounding action - port tack - starboard rounding");
        return "markrounding action - port tack - starboard rounding";
    }
}
