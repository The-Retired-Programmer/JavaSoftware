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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import uk.theretiredprogrammer.racetrainingsketch.strategy.*;
import java.io.IOException;
import java.util.function.Supplier;
import javax.json.Json;
import static org.junit.jupiter.api.Assertions.fail;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TurnTest {

    private Boat boat;
    private Controller controller;
    private Decision decision;

    Boat setupForTurn(String filename, Supplier<String>... configs) throws IOException {
        controller = new Controller(filename, (s) -> requestpaint(s));
        boat = controller.boats.getBoat("Red");
        //
        for (var config : configs) {
            String error = config.get();
            if (error != null) {
                throw new IOException(error);
            }
        }
        decision = controller.boatstrategies.getStrategy(boat).decision;
        return boat;
    }

    Boat getUptospeed(int seconds) throws IOException {
        while (seconds > 0) {
            decision.setSAILON();
            boat.moveUsingDecision();
            seconds--;
        }
        return boat;
    }

    Boat makeTurn(Angle finalangle, TurnDirection turndirection) throws IOException {
        decision.setTURN(finalangle, turndirection);
        while (decision.getAction() != SAILON) {
            boat.moveUsingDecision();
        }
        return boat;
    }

    private void requestpaint(String s) {
        fail("BAD - request for paint made -should not occur");
    }

    public String setboatintvalue(String name, int value) {
        try {
            boat.change(Json.createObjectBuilder().add(name, value).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setboatlocationvalue(String name, double x, double y) {
        try {
            boat.change(Json.createObjectBuilder()
                    .add(name, Json.createArrayBuilder().add(x).add(y).build())
                    .build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    String setwindfrom(String name, int degrees) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("from", degrees).build(), name);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindfrom(int zlevel, int degrees) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("from", degrees).build(), zlevel);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindfrom(int degrees) {
        return setwindfrom(0, degrees);
    }
    
    String setwindspeed(String name, double speed) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("speed", speed).build(), name);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindspeed(int zlevel, double speed) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("speed", speed).build(), zlevel);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindspeed(double speed) {
        return setwindspeed(0, speed);
    }
}
