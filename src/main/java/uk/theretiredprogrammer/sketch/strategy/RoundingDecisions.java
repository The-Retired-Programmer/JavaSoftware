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
package uk.theretiredprogrammer.sketch.strategy;

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.strategy.Decision.STARBOARD;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class RoundingDecisions extends SailingDecisions {

    final boolean atPortRoundingTurnPoint(BoatStrategyForLeg legstrategy) {
        return legstrategy.boat.isPortRear90Quadrant(legstrategy.getMarkLocation());
    }

    final boolean atStarboardRoundingTurnPoint(BoatStrategyForLeg legstrategy) {
        return legstrategy.boat.isStarboardRear90Quadrant(legstrategy.getMarkLocation());
    }

    final String executePortRounding(Function<Angle, Angle> getDirectionAfterTurn, Angle winddirection, BoatStrategyForLeg legstrategy) {
        Angle finaldirection = getDirectionAfterTurn.apply(winddirection);
        Angle turnangle = finaldirection.absAngleDiff(legstrategy.boat.direction);
        if (turnangle.gt(ANGLE90)) {
            legstrategy.decision.setTURN(legstrategy.boat.direction.sub(ANGLE90), PORT);
            return "markrounding action - first phase - starboard tack - port rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        legstrategy.decision.setMARKROUNDING(finaldirection, PORT);
        return "markrounding action - starboard tack - port rounding";
    }

    final String executeStarboardRounding(Function<Angle, Angle> getDirectionAfterTurn, Angle winddirection, BoatStrategyForLeg legstrategy) {
        Angle finaldirection = getDirectionAfterTurn.apply(winddirection);
        Angle turnangle = finaldirection.absAngleDiff(legstrategy.boat.direction);
        if (turnangle.gt(ANGLE90)) {
            legstrategy.decision.setTURN(legstrategy.boat.direction.add(ANGLE90), STARBOARD);
            return "markrounding action - first phase - port tack - starboard rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        legstrategy.decision.setMARKROUNDING(finaldirection, STARBOARD);
        return "markrounding action - port tack - starboard rounding";
    }
}
