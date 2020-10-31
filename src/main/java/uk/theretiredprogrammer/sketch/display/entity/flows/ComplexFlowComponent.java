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

import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;

/**
 * The ComplexFlow Class - represents a flow which is described by flows (speed
 * and direction) at the four corners points. Flows within the described area
 * are interpolated.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ComplexFlowComponent extends FlowComponent {

    private final PropertyComplexFlowComponent property;

    public ComplexFlowComponent(PropertyComplexFlowComponent complexflowcomponentproperty) {
        super(complexflowcomponentproperty);
        this.property = complexflowcomponentproperty;
    }

    @Override
    public SpeedPolar getFlow(Location pos) {
        testLocationWithinArea(pos);
        Location bottomleft = property.getArea().getBottomleft();
        double xfraction = (pos.getX() - bottomleft.getX()) / property.getArea().getWidth();
        double yfraction = (pos.getY() - bottomleft.getY()) / property.getArea().getHeight();
        Location fractions = new Location(xfraction, yfraction);
        return property.getSouthwestflow().extrapolate(
                property.getNorthwestflow(),
                property.getNortheastflow(),
                property.getSoutheastflow(),
                fractions
        );
    }
}
