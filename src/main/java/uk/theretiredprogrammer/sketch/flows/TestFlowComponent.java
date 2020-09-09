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
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyAngle;
import uk.theretiredprogrammer.sketch.core.PropertySpeedPolar;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TestFlowComponent extends FlowComponent {
    
    @Override
    public final String getFlowType() {
        return "Test Flow";
    }

    private final PropertySpeedPolar flowproperty = new PropertySpeedPolar();
    private final PropertyAngle meanproperty = new PropertyAngle();

    public TestFlowComponent(Supplier<Controller>controllersupplier, JsonObject paramsobj) throws IOException {
        super(controllersupplier, paramsobj);
        flowproperty.set(new SpeedPolar(
                DoubleParser.parse(paramsobj, "speed").orElse(0.0),
                Angle.parse(paramsobj, "from").orElse(ANGLE0)));
        meanproperty.setValue(Angle.parse(paramsobj, "mean").orElse(ANGLE0));
    }

    @Override
    public void change(JsonObject paramsobj) throws IOException {
       super.change(paramsobj);
        flowproperty.set(new SpeedPolar(
                DoubleParser.parse(paramsobj, "speed").orElse(flowproperty.getSpeed()),
                Angle.parse(paramsobj, "from").orElse(flowproperty.getAngle())));
        meanproperty.setValue(Angle.parse(paramsobj, "mean").orElse(meanproperty.getValue()));
    }
    
    @Override
    public LinkedHashMap<String,Object> properties() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        super.properties(map);
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
