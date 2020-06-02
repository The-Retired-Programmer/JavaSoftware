/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90MINUS;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class SimpleStrategy {
    
    final BoatBasics boat;
    final ScenarioElement scenario;
    private final Angle upwindrelative;
    private final Angle downwindrelative;

    /**
     * Constructor
     */
    SimpleStrategy(BoatBasics boat, ScenarioElement scenario, Angle upwindrelative, Angle downwindrelative) {
        this.boat = boat;
        this.scenario = scenario;
        this.upwindrelative = upwindrelative;
        this.downwindrelative = downwindrelative;
    }
    
    PropertyChangeListener getDecisionchangelistener() {
        return null;
    }
    
    void nextStepDecision(Decision decision) throws IOException {
        limitsDecision(decision);
        nogoZoneDecision(decision);
    }
    
    private void nogoZoneDecision(Decision decision) throws IOException {
        Angle winddirection = scenario.getWindflow(boat.getLocation()).getAngle();
        Angle boatAngleToWind = winddirection.angleDiff(decision.getAngle());
        if (boatAngleToWind.abs().lt(upwindrelative)) {
            boolean onPort = decision.getAngle().gteq(winddirection);
            Angle nextDirection = winddirection.add(upwindrelative.negateif(!onPort));
            decision.setTURN(nextDirection, onPort ? CLOCKWISE : ANTICLOCKWISE);
        }
    }
    
    void limitsDecision(Decision decision) {
        Angle direction = boat.getDirection();
        Location location = boat.getLocation();
        double turndistance = boat.getTurndistance();
        
        if (location.getX() <= (scenario.getWestlimit() + turndistance) && direction.isNegative()) {
            decision.setTURN(direction.reflectV(), direction.gt(ANGLE90MINUS)? CLOCKWISE: ANTICLOCKWISE);
            return;
        }
        if (location.getX() >= (scenario.getEastlimit() - turndistance) && direction.isPositive()) {
            decision.setTURN(direction.reflectV(), direction.gt(ANGLE90)? CLOCKWISE: ANTICLOCKWISE);
            return;
        }
        if (location.getY() >= (scenario.getNorthlimit() - turndistance) && direction.abs().lt(ANGLE90)) {
            decision.setTURN(direction.reflectH(), direction.isPositive()? CLOCKWISE: ANTICLOCKWISE);
            return;
        }
        if (location.getY() <= (scenario.getSouthlimit() + turndistance) && direction.abs().gt(ANGLE90)) {
            decision.setTURN(direction.reflectH(), direction.isNegative()? CLOCKWISE: ANTICLOCKWISE);
            return;
        }
        decision.setSAILON();
    }
}
