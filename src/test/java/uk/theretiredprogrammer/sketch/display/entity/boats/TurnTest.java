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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyTestFlowComponent;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TurnTest {

    private Boat boat;
    private DisplayController controller;
    private Decision decision;

    Boat setupForTurn(String filename, Runnable... updateproperties) throws IOException {
        controller = new DisplayController(filename);
        boat = controller.boats.get("Red");
        //
        for (var updateaction : updateproperties) {
            updateaction.run();
        }
        decision = boat.getStrategy().decision;
        return boat;
    }

    Boat getUptospeed(int seconds) throws IOException {
        while (seconds > 0) {
            decision.setSAILON();
            boat.moveUsingDecision(controller.windflow, controller.waterflow, decision);
            seconds--;
        }
        return boat;
    }

    Boat makeTurn(Angle finalangle, boolean turndirection) throws IOException {
        decision.setTURN(finalangle, turndirection);
        while (decision.getAction() != SAILON) {
            boat.moveUsingDecision(controller.windflow, controller.waterflow, decision);
        }
        return boat;
    }

    void setboatdirection(double degrees) {
        boat.setDirection(new Angle(degrees));
    }

    void setboatlocation(double x, double y) {
        boat.setLocation(new Location(x, y));
    }

    void setwindflow(double speed, double degrees) {
        setwindflow(speed, degrees, 0);
    }

    void setwindflow(double speed, double degrees, int zlevel) {
        controller.getProperty().getWind().stream()
                .filter(pfc -> (pfc.getZlevel() == zlevel) && (pfc.getType().equals("testflow")))
                .forEach(tfc -> {
                    ((PropertyTestFlowComponent) tfc).setFlow(new SpeedPolar(speed, degrees));
                    controller.windflow.setFlows();
                });
    }
}
