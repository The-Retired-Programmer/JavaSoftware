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

import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.entity.Location;

/**
 * The EastWestGradientFlow Class - represents a flow with differing parameters
 * (direction and speed) in a east-west direction. Intermediate positions are
 * interpolated to provide the changing flow.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GradientFlowComponent extends FlowComponent {

    private final PropertyGradientFlowComponent property;

    public GradientFlowComponent(PropertyGradientFlowComponent gradientflowcomponentproperty) {
        super(gradientflowcomponentproperty);
        this.property = gradientflowcomponentproperty;
    }

    @Override
    public SpeedPolar getFlow(Location pos) {
        testLocationWithinArea(pos);
        return property.getGradient().getFlow(pos);
    }
}
