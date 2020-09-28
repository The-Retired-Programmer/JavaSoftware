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
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.properties.PropertyAngle;
import uk.theretiredprogrammer.sketch.properties.PropertyItem;
import uk.theretiredprogrammer.sketch.properties.PropertySpeedPolar;
import uk.theretiredprogrammer.sketch.properties.PropertyString;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TestFlowComponent extends FlowComponent {

    private final static String TESTFLOWTYPE = "testflow";

    static {
        FlowComponentSet.registerFlowType(TESTFLOWTYPE);
    }

    private final PropertyString flowtypeproperty = new PropertyString(TESTFLOWTYPE);
    private final PropertySpeedPolar flowproperty = new PropertySpeedPolar();
    private final PropertyAngle meanproperty = new PropertyAngle();

    public TestFlowComponent(Supplier<Controller> controllersupplier, JsonObject paramsobj) throws IOException {
        super(controllersupplier, paramsobj);
        flowproperty.set(SpeedPolar.parse(paramsobj, "flow").orElse(ZEROFLOW));
        meanproperty.setValue(Angle.parse(paramsobj, "mean").orElse(ANGLE0));
    }

    @Override
    public void change(JsonObject paramsobj) throws IOException {
        super.change(paramsobj);
        flowproperty.set(SpeedPolar.parse(paramsobj, "flow").orElse(flowproperty.get()));
        meanproperty.setValue(Angle.parse(paramsobj, "mean").orElse(meanproperty.getValue()));
    }

    @Override
    public LinkedHashMap<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        super.properties(map);
        map.put("type", flowtypeproperty);
        map.put("flow", flowproperty);
        map.put("mean", meanproperty);
        return map;
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        testLocationWithinArea(pos);
        return flowproperty.get();
    }

    @Override
    public Angle meanWindAngle() {
        return meanproperty.getValue();
    }
}
