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

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class OffwindPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    OffwindPortRoundingDecisions(
            Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(SketchModel sketchproperty, Strategy strategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getFlow(strategy.boat.getLocation()).getAngle();
        if (atPortRoundingTurnPoint(strategy)) {
            return executePortRounding(getDirectionAfterTurn, winddirection, strategy);
        }
        adjustDirectCourseToDownwindMarkOffset(strategy, winddirection);
        return "course adjustment - approaching mark - port rounding";
    }
}
