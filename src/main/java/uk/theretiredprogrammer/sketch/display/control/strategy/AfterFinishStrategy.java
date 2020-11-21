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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

public class AfterFinishStrategy extends Strategy {

    @Override
    public String strategyTimeInterval(Params params) {
        double fromfinishmark = params.boat.getLocation().to(params.leg.getMarkLocation());
        if (fromfinishmark > params.boat.metrics.getLength() * 5) {
            params.decision.setSTOP(params.boat.getDirection());
            return "Stopping at end of course";
        } else {
            params.decision.setSAILON(params.boat.getDirection());
            return "Sail ON";
        }
    }

    @Override
    public PropertyDistanceVector getOffsetVector(boolean onPort) {
        throw new IllegalStateFailure("attempting to getOffsetVector on the afterFinish leg");
    }

}
