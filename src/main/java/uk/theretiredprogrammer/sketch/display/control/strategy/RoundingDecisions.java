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

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES90;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class RoundingDecisions extends SailingDecisions {

    final boolean atPortRoundingTurnPoint(Strategy strategy) {
        return strategy.boat.isPortRear90Quadrant(strategy.getMarkLocation());
    }

    final boolean atStarboardRoundingTurnPoint(Strategy strategy) {
        return strategy.boat.isStarboardRear90Quadrant(strategy.getMarkLocation());
    }

    final String executePortRounding(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn, PropertyDegrees winddirection, Strategy strategy) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(strategy.boat.getDirection());
        if (turnangle.gt(DEGREES90)) {
            strategy.decision.setTURN(strategy.boat.getDirection().sub(DEGREES90), PORT);
            return "markrounding action - first phase - starboard tack - port rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        strategy.decision.setMARKROUNDING(finaldirection, PORT);
        return "markrounding action - starboard tack - port rounding";
    }

    final String executeStarboardRounding(Function<PropertyDegrees, PropertyDegrees> getDirectionAfterTurn, PropertyDegrees winddirection, Strategy strategy) {
        PropertyDegrees finaldirection = getDirectionAfterTurn.apply(winddirection);
        PropertyDegrees turnangle = finaldirection.absDegreesDiff(strategy.boat.getDirection());
        if (turnangle.gt(DEGREES90)) {
            strategy.decision.setTURN(strategy.boat.getDirection().plus(DEGREES90), STARBOARD);
            return "markrounding action - first phase - port tack - starboard rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        strategy.decision.setMARKROUNDING(finaldirection, STARBOARD);
        return "markrounding action - port tack - starboard rounding";
    }
}
