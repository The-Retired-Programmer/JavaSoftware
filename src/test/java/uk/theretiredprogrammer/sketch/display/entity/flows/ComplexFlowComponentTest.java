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
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyLocation.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;

public class ComplexFlowComponentTest extends FlowComponentTest {

    @Test
    public void testGetComplexFlow() throws IOException {
        System.out.println("getComplexFlow");
        initialiseFlow("/complexwindflow.json");
        assertFlowAt(LOCATIONZERO, new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 0), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(0, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(50, 50), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(10, 80), new PropertySpeedVector(4, DEGREES0));
        assertMeanFlowAngle(DEGREES0);
    }

    @Test
    public void testGetComplexFlow2() throws IOException {
        System.out.println("getComplexFlow2");
        initialiseFlow("/complexwindflow2.json");
        assertFlowAt(LOCATIONZERO, new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 0), new PropertySpeedVector(2, DEGREES0));
        assertFlowAt(new PropertyLocation(0, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 100), new PropertySpeedVector(2, DEGREES0));
        assertFlowAt(new PropertyLocation(50, 50), new PropertySpeedVector(3, DEGREES0));
        assertFlowAt(new PropertyLocation(10, 80), new PropertySpeedVector(3.8, DEGREES0));
        assertMeanFlowAngle(DEGREES0);
    }

    @Test
    public void testGetComplexFlow3() throws IOException {
        System.out.println("getComplexFlow3");
        initialiseFlow("/complexwindflow3.json");
        //
        assertFlowAt(LOCATIONZERO, new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 0), new PropertySpeedVector(4, new PropertyDegrees(45)));
        assertFlowAt(new PropertyLocation(0, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 100), new PropertySpeedVector(4, new PropertyDegrees(45)));
        assertFlowAt(new PropertyLocation(50, 50), new PropertySpeedVector(4, new PropertyDegrees(22.5)));
        assertFlowAt(new PropertyLocation(10, 80), new PropertySpeedVector(4, new PropertyDegrees(4.1663085)));
        assertMeanFlowAngle(new PropertyDegrees(22.5));
    }

    @Test
    public void testGetComplexFlow4() throws IOException {
        System.out.println("getComplexFlow4");
        initialiseFlow("/complexwindflow4.json");
        //
        assertFlowAt(LOCATIONZERO, new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 0), new PropertySpeedVector(2, new PropertyDegrees(45)));
        assertFlowAt(new PropertyLocation(0, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 100), new PropertySpeedVector(2, new PropertyDegrees(45)));
        assertFlowAt(new PropertyLocation(50, 50), new PropertySpeedVector(3, new PropertyDegrees(22.5)));
        assertFlowAt(new PropertyLocation(10, 80), new PropertySpeedVector(3.8, new PropertyDegrees(4.1663085)));
        assertMeanFlowAngle(new PropertyDegrees(22.5));
    }

    @Test
    public void testGetComplexFlow5() throws IOException {
        System.out.println("getComplexFlow5");
        initialiseFlow("/complexwindflow5.json");
        //
        assertFlowAt(LOCATIONZERO, new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 0), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(0, 100), new PropertySpeedVector(4, DEGREES0));
        assertFlowAt(new PropertyLocation(100, 100), new PropertySpeedVector(4, new PropertyDegrees(45)));
        assertFlowAt(new PropertyLocation(50, 50), new PropertySpeedVector(4, new PropertyDegrees(11.25)));
        assertFlowAt(new PropertyLocation(10, 80), new PropertySpeedVector(4, new PropertyDegrees(3.4683275)));
        assertMeanFlowAngle(new PropertyDegrees(11.1957483));
    }
}
