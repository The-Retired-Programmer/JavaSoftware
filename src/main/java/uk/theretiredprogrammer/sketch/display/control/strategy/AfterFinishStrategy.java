/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class AfterFinishStrategy extends Strategy {

    AfterFinishStrategy(Boat boat, Leg previousleg) {
        super(boat, previousleg);
    }

    @Override
    String nextBoatStrategyTimeInterval(SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        double fromfinishmark = boat.getLocation().to(leg.getEndLocation());
        if (fromfinishmark > boat.metrics.getLength() * 5) {
            decision.setSTOP();
            return "Stopping at end of course";
        } else {
            decision.setSAILON();
            return "Sail ON";
        }
    }

}
