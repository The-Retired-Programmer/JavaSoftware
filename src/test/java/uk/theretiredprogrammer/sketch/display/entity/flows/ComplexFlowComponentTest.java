/*
 * Copyright 2020 richard.
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
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;

/**
 *
 * @author richard
 */
public class ComplexFlowComponentTest extends FlowComponentTest {

    @Test
    public void testGetComplexFlow() throws IOException {
        System.out.println("getComplexFlow");
        initialiseFlow("/complexwindflow.json");
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 0), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(0, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(50, 50), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(10, 80), new SpeedPolar(4, ANGLE0));
        assertMeanFlowAngle(ANGLE0);
    }

    @Test
    public void testGetComplexFlow2() throws IOException {
        System.out.println("getComplexFlow2");
        initialiseFlow("/complexwindflow2.json");
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 0), new SpeedPolar(2, ANGLE0));
        assertFlowAt(new Location(0, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 100), new SpeedPolar(2, ANGLE0));
        assertFlowAt(new Location(50, 50), new SpeedPolar(3, ANGLE0));
        assertFlowAt(new Location(10, 80), new SpeedPolar(3.8, ANGLE0));
        assertMeanFlowAngle(ANGLE0);
    }

    @Test
    public void testGetComplexFlow3() throws IOException {
        System.out.println("getComplexFlow3");
        initialiseFlow("/complexwindflow3.json");
        //
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 0), new SpeedPolar(4, new Angle(45)));
        assertFlowAt(new Location(0, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 100), new SpeedPolar(4, new Angle(45)));
        assertFlowAt(new Location(50, 50), new SpeedPolar(4, new Angle(22.5)));
        assertFlowAt(new Location(10, 80), new SpeedPolar(4, new Angle(4.1663085)));
        assertMeanFlowAngle(new Angle(22.5));
    }

    @Test
    public void testGetComplexFlow4() throws IOException {
        System.out.println("getComplexFlow4");
        initialiseFlow("/complexwindflow4.json");
        //
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 0), new SpeedPolar(2, new Angle(45)));
        assertFlowAt(new Location(0, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 100), new SpeedPolar(2, new Angle(45)));
        assertFlowAt(new Location(50, 50), new SpeedPolar(3, new Angle(22.5)));
        assertFlowAt(new Location(10, 80), new SpeedPolar(3.8, new Angle(4.1663085)));
        assertMeanFlowAngle(new Angle(22.5));
    }

    @Test
    public void testGetComplexFlow5() throws IOException {
        System.out.println("getComplexFlow5");
        initialiseFlow("/complexwindflow5.json");
        //
        assertFlowAt(LOCATIONZERO, new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 0), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(0, 100), new SpeedPolar(4, ANGLE0));
        assertFlowAt(new Location(100, 100), new SpeedPolar(4, new Angle(45)));
        assertFlowAt(new Location(50, 50), new SpeedPolar(4, new Angle(11.25)));
        assertFlowAt(new Location(10, 80), new SpeedPolar(4, new Angle(3.4683275)));
        assertMeanFlowAngle(new Angle(11.1957483));
    }
}
