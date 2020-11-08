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
import static uk.theretiredprogrammer.sketch.core.entity.PropertyLocation.LOCATIONZERO;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;

public class ConstantFlowComponentTest extends FlowComponentTest {

    @Test
    public void testGetConstantFlow() throws IOException {
        System.out.println("getConstantFlow");
        initialiseFlow("/constantwindflow.json");
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, DEGREES0));
        assertMeanFlowAngle(DEGREES0);
    }
}
