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

import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElement;
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardtoGybingDownwindStarboardRoundingStrategy extends RoundingStrategy {

    private final double clearance;

    WindwardtoGybingDownwindStarboardRoundingStrategy(double clearance) {
        this.clearance = clearance;
    }

    @Override
    void nextTimeInterval(Decision decision, BoatElement boat, CourseLegWithStrategy leg, Angle winddirection) {
        boolean onPort = boat.getDirection().gteq(winddirection);
        CourseLeg followingleg = leg.getFollowingLeg();
        if (leg.getEndLocation().angleto(boat.getLocation())
                .gteq(getOffsetAngle(onPort, winddirection))) {
            decision.setMARKROUNDING(followingleg != null
                    ? winddirection.add(boat.getDownwind())
                    : boat.getDirection(),
                    CLOCKWISE);
        }
    }
    
    private Angle getOffsetAngle(boolean onPort, Angle winddirection) {
        return winddirection.add(new Angle(onPort ? -45 : -135));
    }

    @Override
    DistancePolar getOffset(boolean onPort, Angle winddirection, CourseLeg leg) {
        return new DistancePolar(clearance, getOffsetAngle(onPort, winddirection));
    }
}
