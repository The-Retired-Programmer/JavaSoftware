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
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.core.PropertySpeedPolar;
import uk.theretiredprogrammer.sketch.core.PropertyString;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * The ConstantFlow Class - represents a flow which is steady in speed and
 * direction across the entire plane.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ConstantFlowComponent extends FlowComponent {

    private final static String CONSTANTFLOWTYPE = "constantflow";

    static {
        FlowComponentSet.registerFlowType(CONSTANTFLOWTYPE);
    }

    private final PropertyString flowtypeproperty = new PropertyString(CONSTANTFLOWTYPE);
    private final PropertySpeedPolar flowproperty = new PropertySpeedPolar();

    public ConstantFlowComponent(Supplier<Controller> controllersupplier, JsonObject paramsobj) throws IOException {
        super(controllersupplier, paramsobj);
        flowproperty.set(SpeedPolar.parse(paramsobj, "flow").orElse(ZEROFLOW));
    }

    @Override
    public void change(JsonObject params) throws IOException {
        super.change(params);
        flowproperty.set(SpeedPolar.parse(params, "flow").orElse(flowproperty.get()));
    }

    @Override
    public LinkedHashMap<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        super.properties(map);
        map.put("type", flowtypeproperty);
        map.put("flow", flowproperty);
        return map;
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        testLocationWithinArea(pos);
        return flowproperty.get();
    }

}
