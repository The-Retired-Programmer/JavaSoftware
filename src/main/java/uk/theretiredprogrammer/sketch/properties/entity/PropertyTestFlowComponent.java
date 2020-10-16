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
package uk.theretiredprogrammer.sketch.properties.entity;

import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import static uk.theretiredprogrammer.sketch.core.entity.SpeedPolar.FLOWZERO;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyTestFlowComponent extends PropertyFlowComponent {

    private final Config<PropertySpeedPolar, SpeedPolar> flow = new Config<>("flow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));
    private final Config<PropertyAngle, Angle> mean = new Config<>("mean", OPTIONAL, (s) -> new PropertyAngle(s, ANGLE0));

    public PropertyTestFlowComponent(Supplier<Area> getdisplayarea, String type) {
        super(getdisplayarea, type);
        this.addConfig(flow, mean);
    }

    public SpeedPolar getFlow() {
        return flow.get("PropertyTestFlowComponent flow");
    }

    public Angle getmean() {
        return mean.get("PropertyTestFlowComponent mean");
    }

    public void setFlow(SpeedPolar newvalue) {
        flow.getProperty().set(newvalue);
    }

}
