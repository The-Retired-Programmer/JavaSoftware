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

import java.io.IOException;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;

/**
 * The FlowElement Factory Class.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class FlowElementFactory {

    public static FlowElement createflowelement(JsonObject paramobj, ScenarioElement scenario) throws IOException{
        if (paramobj == null) {
            return null;
        }
        switch (paramobj.getString("type", "MISSING")){
            case "testflow":
                return new TestFlowElement(paramobj);
            case "complexflow":
                return new ComplexFlowElement(paramobj, scenario);
            case "constantflow":
                return new ConstantFlowElement(paramobj, scenario);
            case "gradientflow":
                return new GradientFlowElement(paramobj, scenario);
            default:
                throw new IOException("Missing or Unknown type parameter in flow definition");
        }
    }
}
