/*
 * Copyright 2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.flows;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;
import uk.theretiredprogrammer.racetrainingsketch.ui.Scenario;

/**
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class FlowComponentTest {

    final static double DELTA = 0.0000001;
    private Scenario scenario;

    public void initialiseFlow(String filename) throws IOException {
        Controller controller = new Controller(filename, (s) -> requestpaint(s));
        scenario = controller.getScenario();
    }

    private void requestpaint(String s) {
        fail("BAD - should not be calling controller repaint");
    }

    void assertFlowAt(Location at, SpeedPolar expected) throws IOException {
        SpeedPolar flow = scenario.getWindflow(at);
        assertEquals(expected.getAngle().getDegrees(), flow.getAngle().getDegrees(),DELTA);
        assertEquals(expected.getSpeed(), flow.getSpeed(), DELTA);
    }

    void assertMeanFlowAngle(Angle expected) throws IOException {
        assertEquals(expected.getDegrees(), scenario.getWindmeanflowangle().getDegrees(), DELTA);
    }
}
