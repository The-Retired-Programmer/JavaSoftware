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
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import static uk.theretiredprogrammer.sketch.core.entity.SpeedPolar.FLOWZERO;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyComplexFlowComponent extends PropertyFlowComponent {

    private final Config<PropertySpeedPolar, SpeedPolar> northwestflow = new Config<>("northwestflow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));
    private final Config<PropertySpeedPolar, SpeedPolar> northeastflow = new Config<>("northeastflow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));
    private final Config<PropertySpeedPolar, SpeedPolar> southeastflow = new Config<>("southeastflow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));
    private final Config<PropertySpeedPolar, SpeedPolar> southwestflow = new Config<>("southwestflow", OPTIONAL, (s) -> new PropertySpeedPolar(s, FLOWZERO));

    public PropertyComplexFlowComponent(Supplier<Area> getdisplayarea, String type) {
        super(getdisplayarea, type);
        this.addConfig(northwestflow, northeastflow, southeastflow, southwestflow);
    }

    public SpeedPolar getNorthwestflow() {
        return northwestflow.get("PropertyComplexFlowcomponent northwestflow");
    }

    public SpeedPolar getNortheastflow() {
        return northeastflow.get("PropertyComplexFlowcomponent northeastflow");
    }

    public SpeedPolar getSoutheastflow() {
        return southeastflow.get("PropertyComplexFlowcomponent southeastflow");
    }

    public SpeedPolar getSouthwestflow() {
        return southwestflow.get("PropertyComplexFlowcomponent southwestflow");
    }

}
