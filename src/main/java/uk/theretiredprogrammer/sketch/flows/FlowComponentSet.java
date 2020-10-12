/*
 * Copyright 2020 richard.
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.properties.PropertyFlowComponent;

/**
 *
 * @author richard
 */
public class FlowComponentSet {

    private final ObservableList<PropertyFlowComponent> flows;
    
    public FlowComponentSet(ObservableList<PropertyFlowComponent> flows) {
        this.flows = flows;
    }

    public SpeedPolar getFlow(Location pos) {
        int zlevel = -1;
        PropertyFlowComponent flowtouse = null;
        for (PropertyFlowComponent flow : flows) {
            if (flow.getArea().isWithinArea(pos) && flow.getZlevel() > zlevel) {
                zlevel = flow.getZlevel();
                flowtouse = flow;
            }
        }
        return flowtouse != null ? FlowComponent.factory(flowtouse).getFlow(pos) : SpeedPolar.FLOWZERO;
    }

    public Angle meanWindAngle() {
        for (PropertyFlowComponent flowproperty : flows) {
            FlowComponent flow = FlowComponent.factory(flowproperty);
            Angle res = flow.meanWindAngle();
            if (res != null) {
                return res;
            }
        }
        return null;
    }
}
