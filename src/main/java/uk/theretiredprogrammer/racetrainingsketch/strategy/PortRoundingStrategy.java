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
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class PortRoundingStrategy extends RoundingStrategy {

    private final Function<Angle, Angle> getDirectionAfterTurn;
    
    PortRoundingStrategy(BoatElement boat,
            BiFunction<Boolean,Angle,Angle> getOffsetAngle,
            Function<Angle, Angle> getDirectionAfterTurn ) {
        super(boat.getMetrics().getWidth()*2, getOffsetAngle);
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final void nextTimeInterval(Decision decision, BoatElement boat, CourseLegWithStrategy leg, Angle winddirection) {
        boolean onPort = boat.getDirection().gteq(winddirection);
        if (leg.getEndLocation().angleto(boat.getLocation())
                .lteq(getOffsetAngle.apply(onPort, winddirection))) {
            decision.setMARKROUNDING(getDirectionAfterTurn.apply(winddirection), ANTICLOCKWISE);
        }
    }

}
