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

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class RoundingDecisions extends SailingDecisions {

    final boolean atPortRoundingTurnPoint(Boat boat, CurrentLeg leg) {
        return boat.isPortRear90Quadrant(leg.getMarkLocation());
    }

    final boolean atStarboardRoundingTurnPoint(Boat boat, CurrentLeg leg) {
        return boat.isStarboardRear90Quadrant(leg.getMarkLocation());
    }

    final String executePortRounding(Boat boat, Decision decision, Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn, PropertyDegrees winddirection, Strategy strategy) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(boat.getDirection());
        if (turnangle.gt(90)) {
            decision.setTURN(boat.getDirection().sub(90), PORT);
            return "markrounding action - first phase - starboard tack - port rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        decision.setMARKROUNDING(finaldirection, PORT);
        return "markrounding action - starboard tack - port rounding";
    }

    final String executeStarboardRounding(Boat boat, Decision decision, Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn, PropertyDegrees winddirection, Strategy strategy) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(boat.getDirection());
        if (turnangle.gt(90)) {
            decision.setTURN(boat.getDirection().plus(90), STARBOARD);
            return "markrounding action - first phase - port tack - starboard rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        decision.setMARKROUNDING(finaldirection, STARBOARD);
        return "markrounding action - port tack - starboard rounding";
    }
}
