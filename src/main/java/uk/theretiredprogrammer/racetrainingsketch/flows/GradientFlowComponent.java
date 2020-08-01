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
import uk.theretiredprogrammer.racetrainingsketch.core.Gradient;
import uk.theretiredprogrammer.racetrainingsketch.ui.Scenario;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;

/**
 * The EastWestGradientFlow Class - represents a flow with differing parameters
 * (direction and speed) in a east-west direction. Intermediate positions are
 * interpolated to provide the changing flow.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GradientFlowComponent extends FlowComponent{
    
    private Gradient gradient;

    /**
     * Constructor
     *
     * @param name the name
     * @param scenario the field of play
     */
    public GradientFlowComponent(JsonObject paramsobj, Scenario scenario) throws IOException {
        super(paramsobj, scenario);
        gradient = Gradient.parse(paramsobj, "gradient").orElse(new Gradient());
    }
    
    @Override
    public void change(JsonObject params) throws IOException {
        super.change(params);
        gradient = Gradient.parse(params, "gradient").orElse(gradient);
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        testLocationWithinArea(pos);
        return gradient.getFlow(pos);
    }
}
