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
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.PORT;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.STARBOARD;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class OffwindSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getFlow(boat.location).getAngle();
        boolean onPort = boat.isPort(winddirection);
        Angle nextDirection = legstrategy.getAngletoSail(boat.location, onPort);
        if (nextDirection.neq(boat.direction)) {
            decision.setTURN(nextDirection, boat.direction.gt(nextDirection) ? PORT : STARBOARD);
            return "Adjust direction to sailin directly to mark (offwind sailing)";
        }
        return "Sail ON";
    }
    // TODO - could add a channel to this as well
}
