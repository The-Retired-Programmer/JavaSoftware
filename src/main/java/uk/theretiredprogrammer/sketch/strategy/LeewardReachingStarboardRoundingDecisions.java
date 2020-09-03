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

import java.io.IOException;
import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class LeewardReachingStarboardRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    LeewardReachingStarboardRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Controller controller, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getMeanFlowAngle(legstrategy.boat.getLocation());
        if (legstrategy.boat.isPort(winddirection)) {
            if (legstrategy.boat.isStarboardRear90Quadrant(legstrategy.getMarkLocation())) {
                legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "pre markrounding action - gybe to starboard - port tack - starboard rounding";
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(legstrategy, winddirection)) {
                return "course adjustment - approaching mark - port tack - starboard rounding";
            }
            legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), PORT);
            return "course adjustment - luff up to hold port reaching - port tack - starboard rounding";
        }
        if (atStarboardRoundingTurnPoint(legstrategy)) {
            return executeStarboardRounding(getDirectionAfterTurn, winddirection, legstrategy);
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(legstrategy, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - starboard rounding";
        }
        if (gybeifonportlayline(legstrategy, winddirection)) {
            return "gybing on port layline - starboard->port";
        }
        legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - starboard rounding";
    }
}
