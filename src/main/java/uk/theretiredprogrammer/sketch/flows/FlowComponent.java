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

import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.properties.PropertyComplexFlowComponent;
import uk.theretiredprogrammer.sketch.properties.PropertyConstantFlowComponent;
import uk.theretiredprogrammer.sketch.properties.PropertyFlowComponent;
import uk.theretiredprogrammer.sketch.properties.PropertyGradientFlowComponent;
import uk.theretiredprogrammer.sketch.properties.PropertyTestFlowComponent;

/**
 * Abstract Class describing a Flow Model. A Flow Model represents variable
 * flows over a area.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class FlowComponent {
    
    public static FlowComponent factory(PropertyFlowComponent componentproperty) {
        switch (componentproperty.getType()) {
            case "testflow" -> {
                return new TestFlowComponent((PropertyTestFlowComponent)componentproperty);
            }
            case "complexflow" -> {
                return new ComplexFlowComponent((PropertyComplexFlowComponent)componentproperty);
            }
            case "constantflow" -> {
                return new ConstantFlowComponent((PropertyConstantFlowComponent)componentproperty);
            }
            case "gradientflow" -> {
                return new GradientFlowComponent((PropertyGradientFlowComponent)componentproperty);
            }
            default ->
                throw new IllegalStateFailure("Missing or Unknown type parameter in a flow definition");
        }
    }

    private final PropertyFlowComponent componentproperty;
    
    public FlowComponent(PropertyFlowComponent componentproperty) {
        this.componentproperty = componentproperty;
    }
    
    public abstract SpeedPolar getFlow(Location pos) ;
    
    void testLocationWithinArea(Location pos)  {
        if (!componentproperty.getArea().isWithinArea(pos)) {
            throw new IllegalStateFailure("Location is not with the Area " + pos);
        }
    }
    
    public Angle meanWindAngle() {
        return null; // should override if manual control  of mean wind angle required
    }
}
