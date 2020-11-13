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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import java.io.IOException;
import java.util.Arrays;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.STOP;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.display.entity.flows.TestFlowComponent;

public class SailingStrategyTest {

    private Boat boat;
    private DisplayController controller;
    private PropertyDegrees winddirection;

    public Decision makeDecision(String filename, Runnable... updateproperties) throws IOException {
        controller = new DisplayController(filename);
        boat = controller.boats.get("Red");
        for (var updateaction : updateproperties) {
            updateaction.run();
        }
        Leg leg = controller.course.getFirstLeg();
        winddirection = controller.windflow.getFlow(boat.getLocation()).getDegreesProperty();
        Strategy strategy = Strategy.get(boat, leg, controller.windflow, controller.waterflow);
        strategy.nextBoatStrategyTimeInterval(controller.getProperty(), controller.windflow, controller.waterflow);
        return strategy.decision;
    }

    void setboatdirection(double degrees) {
        boat.setDirection(new PropertyDegrees(degrees));
    }

    void setboatlocation(double x, double y) {
        boat.setLocation(new PropertyLocation(x, y));
    }

    void setboattrue(String... propertynames) {
        boat.stream().filter(p
                -> Arrays.asList(propertynames).stream()
                        .anyMatch(pn -> pn.equals(p.getKey()) && (p.getValue() instanceof PropertyBoolean)))
                .forEach(p -> ((PropertyBoolean) p.getValue()).set(true));
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

    PropertyDegrees getStarboardCloseHauled() {
        return boat.getStarboardCloseHauledCourse(winddirection);
    }

    PropertyDegrees getPortCloseHauled() {
        return boat.getPortCloseHauledCourse(winddirection);
    }

    PropertyDegrees getStarboardReaching() {
        return boat.getStarboardReachingCourse(winddirection);
    }

    PropertyDegrees getPortReaching() {
        return boat.getPortReachingCourse(winddirection);
    }

    void assertTURN(Decision decision, PropertyDegrees angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(angle, decision.getDegreesProperty()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertTURN(Decision decision, double angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(angle, decision.getDegrees()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSailing(Decision decision, PropertyDegrees minangle, PropertyDegrees maxangle) {
        assertSailing(decision, minangle.get(), maxangle.get());
    }

    void assertSailing(Decision decision, double minangle, double maxangle) {
        switch (decision.getAction()) {
            case TURN -> {
                assertAll("Check angle Range of a Turn",
                        () -> assertTrue(decision.getDegrees() >= minangle, "angle " + Double.toString(decision.getDegrees()) + " is less than minimum " + Double.toString(minangle)),
                        () -> assertTrue(decision.getDegrees() <= maxangle, "angle " + Double.toString(decision.getDegrees()) + " is greater than maximum " + Double.toString(maxangle))
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

    void assertMARKROUNDING(Decision decision, double angle, boolean isSTARBOARD) {
        assertAll("Decision is MARKROUNDING?",
                () -> assertEquals(MARKROUNDING, decision.getAction()),
                () -> assertEquals(angle, decision.getDegrees()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertMARKROUNDING(Decision decision, PropertyDegrees angle, boolean isSTARBOARD) {
        assertAll("Decision is MARKROUNDING?",
                () -> assertEquals(MARKROUNDING, decision.getAction()),
                () -> assertEquals(angle, decision.getDegreesProperty()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSTOP(Decision decision) {
        assertAll("Decision is STOP?",
                () -> assertEquals(STOP, decision.getAction())
        );
    }

}
