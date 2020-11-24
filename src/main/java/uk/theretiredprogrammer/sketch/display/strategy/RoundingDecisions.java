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
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public abstract class RoundingDecisions extends SailingDecisions {

    final boolean executeRoundingIfAtPortRoundingTurnPoint(Params params, Function<Angle, Angle> getDirectionAfterTurn) {
        if (params.boat.isPortRear90Quadrant(params.leg.getMarkLocation())) {
            Angle finaldirection = getDirectionAfterTurn.apply(params.winddirection);
            if (finaldirection.absDegreesDiff(params.heading).gt(90)) {
                params.setTURN(params.heading.sub(90), PORT, MAJOR, "markrounding action - first phase - starboard tack - port rounding");
            } else {
                params.setMARKROUNDING(finaldirection, PORT, MAJOR, "markrounding action - starboard tack - port rounding");
            }
            return true;
        }
        return false;
    }
    
    final boolean ExecuteRoundingIfAtStarboardRoundingTurnPoint(Params params, Function<Angle, Angle> getDirectionAfterTurn) {
        if (params.boat.isStarboardRear90Quadrant(params.leg.getMarkLocation())) {
            Angle finaldirection = getDirectionAfterTurn.apply(params.winddirection);
            if (finaldirection.absDegreesDiff(params.heading).gt(90)) {
                params.setTURN(params.heading.plus(90), STARBOARD, MAJOR, "markrounding action - first phase - port tack - starboard rounding");
            } else {
                params.setMARKROUNDING(finaldirection, STARBOARD, MAJOR, "markrounding action - port tack - starboard rounding");
            }
            return true;
        }
        return false;
    }
}
