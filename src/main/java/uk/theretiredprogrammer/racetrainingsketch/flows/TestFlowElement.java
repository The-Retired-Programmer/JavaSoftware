/*
 * Copyright 2014-2020 Richard Linsdale.
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

import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import java.io.IOException;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;

/**
 * The ConstantFlow Class - represents a flow which is steady in speed and
 * direction across the entire plane.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TestFlowElement extends FlowElement {
    
    private Angle from;
    private double speed;
    private Angle mean;

    public TestFlowElement(JsonObject paramsobj) throws IOException {
        from = Angle.parse(paramsobj, "from").orElse(ANGLE0);
        speed= DoubleParser.parse(paramsobj, "speed").orElse(0.0);
        mean = Angle.parse(paramsobj, "mean").orElse(ANGLE0);
    }
    
    @Override
    public void change(JsonObject paramsobj) throws IOException {
        from = Angle.parse(paramsobj, "from").orElse(from);
        speed= DoubleParser.parse(paramsobj, "speed").orElse(speed);
        mean = Angle.parse(paramsobj, "mean").orElse(mean);
    }

    @Override
    public Angle getMeanFlowAngle() {
        return mean;
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        return new SpeedPolar(speed , from);
    }
}
