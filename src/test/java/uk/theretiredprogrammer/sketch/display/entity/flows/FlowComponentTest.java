/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;

public abstract class FlowComponentTest {

    final static Location TOPLEFT = new Location();
    final static Location TOPRIGHT = new Location(100, 0);
    final static Location BOTTOMLEFT = new Location(0, 100);
    final static Location BOTTOMRIGHT = new Location(100, 100);
    final static double DELTA = 0.0000001;
    private DisplayController controller;

    public void initialiseFlow(String filename) throws IOException {
        controller = new DisplayController(filename);
    }

    void assertFlowAtOrigin(SpeedVector expected) throws IOException {
        assertFlowAt(new Location(), expected);
    }

    void assertFlowAt(Location at, SpeedVector expected) throws IOException {
        SpeedVector flow = controller.getModel().getWindFlow().getFlow(at);
        assertEquals(expected.getDegrees(), flow.getDegrees(), DELTA);
        assertEquals(expected.getSpeed(), flow.getSpeed(), DELTA);
    }

    void assertMeanFlowAngle(double expected) throws IOException {
        assertEquals(expected, controller.getModel().getWindFlow().getMeanFlowAngle().get(), DELTA);
    }
}
