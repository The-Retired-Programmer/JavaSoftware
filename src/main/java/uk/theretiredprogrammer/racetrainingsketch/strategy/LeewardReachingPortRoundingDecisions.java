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
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class LeewardReachingPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    LeewardReachingPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getMeanFlowAngle(boat.location);
        if (boat.isPort(winddirection)) {
            if (atPortRoundingTurnPoint(legstrategy, boat)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, boat, decision);
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(boat, legstrategy, decision, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            if (gybeifonstarboardlayline(boat, legstrategy, decision, winddirection)) {
                return "gybing on starboard layline - port->starboard";
            }
            decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
            return "course adjustment - luff up to hold port reaching - port tack - port rounding";
        }
        if (boat.isPortRear90Quadrant(legstrategy.getMarkLocation())) {
            decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
            return "pre markrounding action - gybe to port - starboard tack - port rounding";
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(boat, legstrategy, decision, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - port rounding";
        }
        decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - port rounding";
    }
}
