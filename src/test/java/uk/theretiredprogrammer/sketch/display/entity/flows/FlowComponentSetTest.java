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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class FlowComponentSetTest extends FlowComponentTest {

    @Test
    public void testFlowElementSet() throws IOException {
        System.out.println("FlowElementSet");
        initialiseFlow("/windflowset.json");
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(50, 50), new SpeedVector(8, 10));
        assertFlowAt(new Location(90, 65), new SpeedVector(8, 10));
        assertFlowAt(new Location(50, 65), new SpeedVector(8, 10));
        assertFlowAt(new Location(90, 50), new SpeedVector(8, 10));
        assertFlowAt(new Location(60, 40), new SpeedVector(12, -15));
        assertFlowAt(new Location(80, 40), new SpeedVector(12, -15));
        assertFlowAt(new Location(60, 60), new SpeedVector(12, -15));
        assertFlowAt(new Location(80, 60), new SpeedVector(12, -15));
        assertMeanFlowAngle(-0.2270468);
    }
}
