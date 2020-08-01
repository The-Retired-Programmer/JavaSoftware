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

import java.util.function.BiFunction;
import java.util.function.Function;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardPortRoundingStrategy extends RoundingStrategy {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    WindwardPortRoundingStrategy(Boat boat,
            BiFunction<Boolean, Angle, Angle> getOffsetAngle,
            Function<Angle, Angle> getDirectionAfterTurn) {
        super(boat.getMetrics().getWidth() * 2, getOffsetAngle, ANTICLOCKWISE);
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        if (boat.isPort(winddirection)) {
            if (boat.isPortRear90Quadrant(leg.getEndLocation())) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), ANTICLOCKWISE);
                return "pre markrounding action - tack to starboard - port tack - port rounding";
            }
            if (adjustDirectCourseToWindwardMarkOffset(boat, leg, decision, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            decision.setTURN(boat.getPortCloseHauledCourse(winddirection), CLOCKWISE);
            return "course adjustment - bearing away to hold port c/h - port tack - port rounding";
        } else {
            if (atPortRoundingTurnPoint(leg, boat)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, boat, decision);
            }
            if (adjustDirectCourseToWindwardMarkOffset(boat, leg, decision, winddirection)) {
                return "course adjustment - approaching mark - starboard tack - port rounding";
            }
            if (tackifonportlayline(boat, leg, decision, winddirection)) {
                return "tacking on port layline - starboard->port";
            }
            decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), ANTICLOCKWISE);
            return "course adjustment - bearing away to hold starboard c/h - starboard tack - port rounding";
        }
    }
}
