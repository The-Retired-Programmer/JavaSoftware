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

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class OffwindSailingDecisions extends SailingDecisions {

    @Override
    String nextTimeInterval(SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(strategy.boat.getLocation()).getAngle();
        boolean onPort = strategy.boat.isPort(winddirection);
        Angle nextDirection = strategy.getAngletoSail(strategy.boat.getLocation(), onPort);
        if (nextDirection.neq(strategy.boat.getDirection())) {
            strategy.decision.setTURN(nextDirection, strategy.boat.getDirection().gt(nextDirection));
            return "Adjust direction to sailin directly to mark (offwind sailing)";
        }
        return "Sail ON";
    }
}
