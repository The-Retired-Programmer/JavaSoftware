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

import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.properties.PropertyTestFlowComponent;

/**
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class TestFlowComponent extends FlowComponent {

    private final PropertyTestFlowComponent property;

    public TestFlowComponent(PropertyTestFlowComponent testflowcomponentproperty) {
        super(testflowcomponentproperty);
        this.property = testflowcomponentproperty;
    }

    @Override
    public SpeedPolar getFlow(Location pos) {
        testLocationWithinArea(pos);
        return property.getFlow();
    }

    @Override
    public Angle meanWindAngle() {
        return property.getmean();
    }
}
