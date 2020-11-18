/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.entity.flows.TestFlowComponent;

public class TurnTest {

    private Boat boat;
    private DisplayController controller;
    private Decision decision;

    Boat setupForTurn(String filename, Runnable... updateproperties) throws IOException {
        controller = new DisplayController(filename);
        boat = controller.boats.get("Red");
        for (var updateaction : updateproperties) {
            updateaction.run();
        }
        decision = boat.getCurrentLeg().getDecision();
        return boat;
    }

    Boat getUptospeed(int seconds) throws IOException {
        while (seconds > 0) {
            decision.setSAILON(boat.getDirection());
            boat.moveUsingDecision(controller.windflow, controller.waterflow, decision);
            seconds--;
        }
        return boat;
    }

    Boat makeTurn(PropertyDegrees finalangle, boolean turndirection) throws IOException {
        decision.setTURN(finalangle, turndirection);
        while (decision.getAction() != SAILON) {
            boat.moveUsingDecision(controller.windflow, controller.waterflow, decision);
        }
        return boat;
    }

    void setboatdirection(double degrees) {
        boat.setDirection(new PropertyDegrees(degrees));
    }

    void setboatlocation(double x, double y) {
        boat.setLocation(new PropertyLocation(x, y));
    }

    void setwindflow(double speed, double degrees) {
        setwindflow(speed, degrees, 0);
    }

    void setwindflow(double speed, double degrees, int zlevel) {
        controller.getProperty().getWindFlow().getFlowcomponents().stream()
                .filter(pfc -> (pfc.getZlevel() == zlevel) && (pfc.getType().equals("testflow")))
                .forEach(tfc -> {
                    ((TestFlowComponent) tfc).setFlow(new PropertySpeedVector(speed, degrees));
                });
    }
}
