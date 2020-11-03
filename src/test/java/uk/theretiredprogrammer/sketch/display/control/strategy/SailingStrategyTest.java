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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import java.io.IOException;
import java.util.Arrays;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.STOP;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.entity.flows.TestFlowComponentModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SailingStrategyTest {

    private Boat boat;
    private DisplayController controller;
    private Angle winddirection;

    public Decision makeDecision(String filename, Runnable... updateproperties) throws IOException {
        controller = new DisplayController(filename);
        boat = controller.boats.get("Red");
        for (var updateaction : updateproperties) {
            updateaction.run();
        }
        Leg leg = controller.course.getFirstLeg();
        winddirection = controller.windflow.getFlow(boat.getLocation()).getAngle();
        Strategy strategy = Strategy.get(boat, leg, controller.windflow, controller.waterflow);
        strategy.nextBoatStrategyTimeInterval(controller.getProperty(), controller.windflow, controller.waterflow);
        return strategy.decision;
    }

    void setboatdirection(double degrees) {
        boat.setDirection(new Angle(degrees));
    }

    void setboatlocation(double x, double y) {
        boat.setLocation(new Location(x, y));
    }

    void setboattrue(String... propertynames) {
        boat.getProperties().entrySet().stream().filter(p
                -> Arrays.asList(propertynames).stream()
                        .anyMatch(pn -> pn.equals(p.getKey()) && (p.getValue() instanceof PropertyBoolean)))
                .forEach(p -> ((PropertyBoolean) p.getValue()).set(true));
    }

    void setwindflow(double speed, double degrees) {
        setwindflow(speed, degrees, 0);
    }

    void setwindflow(double speed, double degrees, int zlevel) {
        controller.getProperty().getWind().getProperties().stream()
                .filter(pfc -> (pfc.getZlevel() == zlevel) && (pfc.getType().equals("testflow")))
                .forEach(tfc -> {
                    ((TestFlowComponentModel) tfc).setFlow(new SpeedPolar(speed, degrees));
                    controller.windflow.setFlows();
                });
    }

    Angle getStarboardCloseHauled() {
        return boat.getStarboardCloseHauledCourse(winddirection);
    }

    Angle getPortCloseHauled() {
        return boat.getPortCloseHauledCourse(winddirection);
    }

    Angle getStarboardReaching() {
        return boat.getStarboardReachingCourse(winddirection);
    }

    Angle getPortReaching() {
        return boat.getPortReachingCourse(winddirection);
    }

    void assertTURN(Decision decision, int angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(new Angle(angle), decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertTURN(Decision decision, Angle angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(angle, decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSailing(Decision decision, Angle minangle, Angle maxangle) {
        switch (decision.getAction()) {
            case TURN -> {
                assertAll("Check angle Range of a Turn",
                        () -> assertTrue(minangle.lteq(decision.getAngle()), "angle " + decision.getAngle().toString() + " is less than minimum " + minangle.toString()),
                        () -> assertTrue(maxangle.gteq(decision.getAngle()), "angle " + decision.getAngle().toString() + " is greater than maximum " + maxangle.toString())
                );
                return;
            }
            case SAILON -> {
                return;
            }
            default ->
                fail("action is not TURN or SAILON");
        }

    }

    void assertSAILON(Decision decision) {
        assertAll("Decision is SAILON?",
                () -> assertEquals(SAILON, decision.getAction())
        );
    }

    void assertMARKROUNDING(Decision decision, int angle, boolean isSTARBOARD) {
        assertAll("Decision is MARKROUNDING?",
                () -> assertEquals(MARKROUNDING, decision.getAction()),
                () -> assertEquals(new Angle(angle), decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSTOP(Decision decision) {
        assertAll("Decision is STOP?",
                () -> assertEquals(STOP, decision.getAction())
        );
    }

}
