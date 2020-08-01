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
class LeewardReachingStarboardRoundingStrategy extends RoundingStrategy {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    LeewardReachingStarboardRoundingStrategy(Boat boat,
            BiFunction<Boolean, Angle, Angle> getOffsetAngle,
            Function<Angle, Angle> getDirectionAfterTurn) {
        super(boat.getMetrics().getWidth() * 2, getOffsetAngle, CLOCKWISE);
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        if (boat.isPort(winddirection)) {
            if (boat.isStarboardRear90Quadrant(leg.getEndLocation())) {
                decision.setTURN(boat.getStarboardReachingCourse(winddirection), CLOCKWISE);
                return "pre markrounding action - gybe to starboard - port tack - starboard rounding";
            }
            if (adjustDirectCourseToLeewardMarkOffset(boat, leg, decision, winddirection)) {
                return "course adjustment - approaching mark - port tack - starboard rounding";
            }
            decision.setTURN(boat.getPortReachingCourse(winddirection), ANTICLOCKWISE);
            return "course adjustment - luff up to hold port reaching - port tack - starboard rounding";
        }
        if (atStarboardRoundingTurnPoint(leg, boat)) {
            return executeStarboardRounding(getDirectionAfterTurn, winddirection, boat, decision);
        }
        if (adjustDirectCourseToLeewardMarkOffset(boat, leg, decision, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - starboard rounding";
        }
        if (gybeifonportlayline(boat, leg, decision, winddirection)) {
            return "gybing on port layline - starboard->port";
        }
        decision.setTURN(boat.getStarboardReachingCourse(winddirection), CLOCKWISE);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - starboard rounding";
    }
}
