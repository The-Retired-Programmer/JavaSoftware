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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.function.Function;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.PORT;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.STARBOARD;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    WindwardPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getFlow(boat.location).getAngle();
        if (boat.isPort(winddirection)) {
            if (boat.isPortRear90Quadrant(legstrategy.getMarkLocation())) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "pre markrounding action - tack to starboard - port tack - port rounding";
            }
            if (adjustPortDirectCourseToWindwardMarkOffset(boat, legstrategy, decision, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        } else {
            if (atPortRoundingTurnPoint(legstrategy, boat)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, boat, decision);
            }
            if (adjustStarboardDirectCourseToWindwardMarkOffset(boat, legstrategy, decision, winddirection)) {
                return "course adjustment - approaching mark - starboard tack - port rounding";
            }
            if (tackifonportlayline(boat, legstrategy, decision, winddirection)) {
                return "tacking on port layline - starboard->port";
            }
            decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding";
        }
    }
}
