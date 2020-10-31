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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedPolar;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import static uk.theretiredprogrammer.sketch.core.entity.SpeedPolar.FLOWZERO;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConfig;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyConfig.OPTIONAL;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyConstantFlowComponent extends PropertyFlowComponent {

    private final PropertyConfig<PropertySpeedPolar, SpeedPolar> flow = new PropertyConfig<>("flow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));

    public PropertyConstantFlowComponent(Supplier<Area> getdisplayarea, String type) {
        super(getdisplayarea, type);
        this.addConfig(flow);
    }

    public SpeedPolar getFlow() {
        return flow.get(this, "PropertyConstantFlowComponent flow");
    }
}
