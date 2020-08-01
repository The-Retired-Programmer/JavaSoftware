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
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class RoundingStrategy extends SailingStrategy {

    final double closetomark;
    final BiFunction<Boolean, Angle, Angle> getOffsetAngle;
    private final TurnDirection turndirection;

    RoundingStrategy(double closetomark, BiFunction<Boolean, Angle, Angle> getOffsetAngle,
            TurnDirection turndirection) {
        this.closetomark = closetomark;
        this.getOffsetAngle = getOffsetAngle;
        this.turndirection = turndirection;
    }

    final DistancePolar getOffset(boolean onPort, Angle winddirection, CourseLeg leg) {
        return new DistancePolar(closetomark, getOffsetAngle.apply(onPort, winddirection));
    }

    final TurnDirection getTurndirection() {
        return turndirection;
    }

    final boolean atPortRoundingTurnPoint(CourseLegWithStrategy leg, Boat boat) {
        return boat.isPortRear90Quadrant(leg.getEndLocation());
    }

    final boolean atStarboardRoundingTurnPoint(CourseLegWithStrategy leg, Boat boat) {
        return boat.isStarboardRear90Quadrant(leg.getEndLocation());
    }

    final String executePortRounding(Function<Angle, Angle> getDirectionAfterTurn, Angle winddirection, Boat boat, Decision decision) {
        Angle finaldirection = getDirectionAfterTurn.apply(winddirection);
        Angle turnangle = finaldirection.absAngleDiff(boat.getDirection());
        if (turnangle.gt(ANGLE90)) {
            decision.setTURN(boat.getDirection().sub(ANGLE90), ANTICLOCKWISE);
            return "markrounding action - first phase - starboard tack - port rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        decision.setMARKROUNDING(finaldirection, ANTICLOCKWISE);
        return "markrounding action - starboard tack - port rounding";
    }

    final String executeStarboardRounding(Function<Angle, Angle> getDirectionAfterTurn, Angle winddirection, Boat boat, Decision decision) {
        Angle finaldirection = getDirectionAfterTurn.apply(winddirection);
        Angle turnangle = finaldirection.absAngleDiff(boat.getDirection());
        if (turnangle.gt(ANGLE90)) {
            decision.setTURN(boat.getDirection().add(ANGLE90), CLOCKWISE);
            return "markrounding action - first phase - port tack - starboard rounding";
        }
        //TODO - potential race condition here if wind shift between first pahse and completion
        decision.setMARKROUNDING(finaldirection, CLOCKWISE);
        return "markrounding action - port tack - starboard rounding";
    }
}
