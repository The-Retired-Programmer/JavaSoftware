/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public abstract class Flow {

    private final FlowShiftModel flowshiftsproperty;
    private final FlowComponentSet flowcomponents;

    public Flow(SketchModel sketchproperty, FlowShiftModel flowshiftsproperty, FlowComponentSet flowcomponents) {
        this.flowshiftsproperty = flowshiftsproperty;
        this.flowcomponents = flowcomponents;
    }
    
    public final FlowShiftModel getShiftsproperty() {
        return flowshiftsproperty;
    }

    public final FlowComponentSet getFlowcomponents() {
        return flowcomponents;
    }

    public void timerAdvance(int simulationtime, DecisionController timerlog) {
        flowshiftsproperty.timerAdvance(simulationtime, timerlog);
    }

    public SpeedVector getFlow(Location pos) {
        return flowshiftsproperty.addShiftandSwing(flowcomponents.getFlowwithoutswing(pos));
    }

    public Angle getMeanFlowAngle() {
        return flowcomponents.getMeanFlowAngle();
    }

    public Angle getMeanFlowAngle(Location pos) {
        return flowcomponents.getMeanFlowAngle(pos);
    }
}
