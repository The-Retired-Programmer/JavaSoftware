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
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class ComplexFlowComponentTest extends FlowComponentTest {

    @Test
    public void testGetComplexFlow() throws IOException {
        System.out.println("getComplexFlow");
        initialiseFlow("/complexwindflow.json");
        assertFlowAtOrigin(new SpeedVector(4, 270));
        assertFlowAt(TOPRIGHT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMLEFT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMRIGHT, new SpeedVector(4, 270));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 270));
        assertFlowAt(new Location(10, 80), new SpeedVector(4, 270));
        assertMeanFlowAngle(270);
    }

    @Test
    public void testGetComplexFlow2() throws IOException {
        System.out.println("getComplexFlow2");
        initialiseFlow("/complexwindflow2.json");
        assertFlowAtOrigin(new SpeedVector(4, 270));
        assertFlowAt(TOPRIGHT, new SpeedVector(2, 270));
        assertFlowAt(BOTTOMLEFT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMRIGHT, new SpeedVector(2, 270));
        assertFlowAt(new Location(50, 50), new SpeedVector(3, 270));
        assertFlowAt(new Location(10, 80), new SpeedVector(3.8, 270));
        assertMeanFlowAngle(270);
    }

    @Test
    public void testGetComplexFlow3() throws IOException {
        System.out.println("getComplexFlow3");
        initialiseFlow("/complexwindflow3.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 270));
        assertFlowAt(TOPRIGHT, new SpeedVector(4, 315));
        assertFlowAt(BOTTOMLEFT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMRIGHT, new SpeedVector(4, 315));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 292.5));
        assertFlowAt(new Location(10, 80), new SpeedVector(4, 274.5));
        assertMeanFlowAngle(292.5);
    }

    @Test
    public void testGetComplexFlow4() throws IOException {
        System.out.println("getComplexFlow4");
        initialiseFlow("/complexwindflow4.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 270));
        assertFlowAt(TOPRIGHT, new SpeedVector(2, 315));
        assertFlowAt(BOTTOMLEFT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMRIGHT, new SpeedVector(2, 315));
        assertFlowAt(new Location(50, 50), new SpeedVector(3, 292.5));
        assertFlowAt(new Location(10, 80), new SpeedVector(3.8, 274.5));
        assertMeanFlowAngle(292.5);
    }

    @Test
    public void testGetComplexFlow5() throws IOException {
        System.out.println("getComplexFlow5");
        initialiseFlow("/complexwindflow5.json");
        //
        assertFlowAtOrigin(new SpeedVector(4, 270));
        assertFlowAt(TOPRIGHT, new SpeedVector(4, 315));
        assertFlowAt(BOTTOMLEFT, new SpeedVector(4, 270));
        assertFlowAt(BOTTOMRIGHT, new SpeedVector(4, 270));
        assertFlowAt(new Location(50, 0), new SpeedVector(4, 292.5));
        assertFlowAt(new Location(50, 100), new SpeedVector(4, 270));
        assertFlowAt(new Location(50, 50), new SpeedVector(4, 281.25));
        assertFlowAt(new Location(10, 20), new SpeedVector(4, 273.6));
        assertMeanFlowAngle(281.25);
    }
}
