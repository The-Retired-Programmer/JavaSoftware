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
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;
import uk.theretiredprogrammer.sketch.display.entity.flows.TestFlowComponent;

public class TurnTest {

    private DisplayController controller;
    private Params params;

    Boat setupForTurn(String filename, Runnable... updateproperties) throws IOException {
        controller = new DisplayController(filename);
        SketchModel model = controller.getModel();
        Boat boat = model.getBoats().get("Red");
        params = new Params(model, boat);
        for (var updateaction : updateproperties) {
            updateaction.run();
        }
        return boat;
    }

    Boat getUptospeed(int seconds) throws IOException {
        while (seconds > 0) {
            params.setSAILON();
            params.boat.moveUsingDecision(params);
            seconds--;
        }
        return params.boat;
    }

    Boat makeTurn(Angle finalangle, boolean turndirection) throws IOException {
        params.setTURN(finalangle, turndirection, MAJOR, "Test: makeTurn setup");
        while (params.decision.getAction() != SAILON) {
            params.boat.moveUsingDecision(params);
        }
        return params.boat;
    }

    void setboatdirection(double degrees) {
        params.boat.setDirection(new Angle(degrees));
    }

    void setboatlocation(double x, double y) {
        params.boat.setLocation(new Location(x, y));
    }

    void setwindflow(double speed, double degrees) {
        setwindflow(speed, degrees, 0);
    }

    void setwindflow(double speed, double degrees, int zlevel) {
        params.windflow.getFlowcomponents().stream()
                .filter(pfc -> (pfc.getZlevel() == zlevel) && (pfc.getType().equals("testflow")))
                .forEach(tfc -> {
                    ((TestFlowComponent) tfc).setFlow(new SpeedVector(speed, degrees));
                });
    }
}
