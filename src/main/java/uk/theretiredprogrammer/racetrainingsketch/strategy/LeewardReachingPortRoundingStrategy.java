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
class LeewardReachingPortRoundingStrategy extends RoundingStrategy {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    LeewardReachingPortRoundingStrategy(Boat boat,
            BiFunction<Boolean, Angle, Angle> getOffsetAngle,
            Function<Angle, Angle> getDirectionAfterTurn) {
        super(boat.getMetrics().getWidth() * 2, getOffsetAngle, ANTICLOCKWISE);
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        if (boat.isPort(winddirection)) {
            if (atPortRoundingTurnPoint(leg, boat)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, boat, decision);
            }
            if (adjustDirectCourseToLeewardMarkOffset(boat, leg, decision, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            if (gybeifonstarboardlayline(boat, leg, decision, winddirection)) {
                return "gybing on starboard layline - port->starboard";
            }
            decision.setTURN(boat.getPortReachingCourse(winddirection), ANTICLOCKWISE);
            return "course adjustment - luff up to hold port reaching - port tack - port rounding";
        }
        if (boat.isPortRear90Quadrant(leg.getEndLocation())) {
            decision.setTURN(boat.getPortReachingCourse(winddirection), ANTICLOCKWISE);
            return "pre markrounding action - gybe to port - starboard tack - port rounding";
        }
        if (adjustDirectCourseToLeewardMarkOffset(boat, leg, decision, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - port rounding";
        }
        decision.setTURN(boat.getStarboardReachingCourse(winddirection), CLOCKWISE);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - port rounding";
    }
}
