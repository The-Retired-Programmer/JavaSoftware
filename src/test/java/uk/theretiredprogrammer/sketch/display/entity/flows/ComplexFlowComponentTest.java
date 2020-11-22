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

public class ComplexFlowComponentTest extends FlowComponentTest {

    @Test
    public void testGetComplexFlow() throws IOException {
        System.out.println("getComplexFlow");
        initialiseFlow("/complexwindflow.json");
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 0), new SpeedVector(4, 0));
        assertFlowAt(new Location(0, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 0));
        assertFlowAt(new Location(10, 80), new SpeedVector(4, 0));
        assertMeanFlowAngle(0);
    }

    @Test
    public void testGetComplexFlow2() throws IOException {
        System.out.println("getComplexFlow2");
        initialiseFlow("/complexwindflow2.json");
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 0), new SpeedVector(2, 0));
        assertFlowAt(new Location(0, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 100), new SpeedVector(2, 0));
        assertFlowAt(new Location(50, 50), new SpeedVector(3, 0));
        assertFlowAt(new Location(10, 80), new SpeedVector(3.8, 0));
        assertMeanFlowAngle(0);
    }

    @Test
    public void testGetComplexFlow3() throws IOException {
        System.out.println("getComplexFlow3");
        initialiseFlow("/complexwindflow3.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 0), new SpeedVector(4, 45));
        assertFlowAt(new Location(0, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 100), new SpeedVector(4, 45));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 22.5));
        assertFlowAt(new Location(10, 80), new SpeedVector(4, 4.1663085));
        assertMeanFlowAngle(22.5);
    }

    @Test
    public void testGetComplexFlow4() throws IOException {
        System.out.println("getComplexFlow4");
        initialiseFlow("/complexwindflow4.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 0), new SpeedVector(2, 45));
        assertFlowAt(new Location(0, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 100), new SpeedVector(2, 45));
        assertFlowAt(new Location(50, 50), new SpeedVector(3, 22.5));
        assertFlowAt(new Location(10, 80), new SpeedVector(3.8, 4.1663085));
        assertMeanFlowAngle(22.5);
    }

    @Test
    public void testGetComplexFlow5() throws IOException {
        System.out.println("getComplexFlow5");
        initialiseFlow("/complexwindflow5.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 0), new SpeedVector(4, 0));
        assertFlowAt(new Location(0, 100), new SpeedVector(4, 0));
        assertFlowAt(new Location(100, 100), new SpeedVector(4, 45));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 11.25));
        assertFlowAt(new Location(10, 80), new SpeedVector(4, 3.4683275));
        assertMeanFlowAngle(11.1957483);
    }
}
