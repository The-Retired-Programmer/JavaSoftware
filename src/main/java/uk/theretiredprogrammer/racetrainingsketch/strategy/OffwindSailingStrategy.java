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

import java.util.Optional;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE180;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class OffwindSailingStrategy extends SailingLegStrategy {

    @Override
    String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        boolean onPort = boat.getDirection().gteq(winddirection);
        Angle nextDirection = leg.getAngletoSail(boat.getLocation(), onPort, winddirection);
        if (nextDirection.neq(boat.getDirection())){
            decision.setTURN(nextDirection, boat.getDirection().gt(nextDirection) ? ANTICLOCKWISE : CLOCKWISE);
            return "Adjust direction to sailin directly to mark (offwind sailing)";
        }
        return "Sail ON";
    }
    // TODO - could add a channel to this as well

    @Override
    boolean applyRoundingStrategy(CourseLegWithStrategy leg, Boat boat, Angle winddirection) {
        Optional<Double> refdistance = getRefDistance(boat.getLocation(), leg.getEndLocation(), winddirection.sub(ANGLE180));
        return refdistance.isPresent() ? refdistance.get() <= boat.getMetrics().getWidth() * 20 : true;
    }
}
