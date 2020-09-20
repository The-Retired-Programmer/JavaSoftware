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
package uk.theretiredprogrammer.sketch.flows;

import jakarta.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.Gradient;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyGradient;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.core.PropertyString;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * The EastWestGradientFlow Class - represents a flow with differing parameters
 * (direction and speed) in a east-west direction. Intermediate positions are
 * interpolated to provide the changing flow.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GradientFlowComponent extends FlowComponent {

    private final static String GRADIENTFLOWTYPE = "gradientflow";

    static {
        FlowComponentSet.registerFlowType(GRADIENTFLOWTYPE);
    }

    private final PropertyString flowtypeproperty = new PropertyString(GRADIENTFLOWTYPE);
    private final PropertyGradient gradientproperty = new PropertyGradient();

    public GradientFlowComponent(Supplier<Controller> controllersupplier, JsonObject paramsobj) throws IOException {
        super(controllersupplier, paramsobj);
        gradientproperty.set(Gradient.parse(paramsobj, "gradient").orElse(new Gradient()));
    }

    @Override
    public void change(JsonObject params) throws IOException {
        super.change(params);
        gradientproperty.set(Gradient.parse(params, "gradient").orElse(gradientproperty.get()));
    }

    @Override
    public LinkedHashMap<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        super.properties(map);
        map.put("flowtype", flowtypeproperty);
        map.put("gradient", gradientproperty);
        return map;
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        testLocationWithinArea(pos);
        return gradientproperty.get().getFlow(pos);
    }
}
