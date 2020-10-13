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
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.PORT;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class LeewardReachingPortRoundingDecisions extends RoundingDecisions {

    private final Function<Angle, Angle> getDirectionAfterTurn;

    LeewardReachingPortRoundingDecisions(Function<Angle, Angle> getDirectionAfterTurn) {
        this.getDirectionAfterTurn = getDirectionAfterTurn;
    }

    @Override
    final String nextTimeInterval(PropertySketch sketchproperty, BoatStrategyForLeg legstrategy, WindFlow windflow, WaterFlow waterflow) {
        Angle winddirection = windflow.getMeanFlowAngle(legstrategy.boat.getProperty().getLocation());
        if (legstrategy.boat.isPort(winddirection)) {
            if (atPortRoundingTurnPoint(legstrategy)) {
                return executePortRounding(getDirectionAfterTurn, winddirection, legstrategy);
            }
            if (adjustPortDirectCourseToLeewardMarkOffset(legstrategy, winddirection)) {
                return "course adjustment - approaching mark - port tack - port rounding";
            }
            if (gybeifonstarboardlayline(legstrategy, winddirection)) {
                return "gybing on starboard layline - port->starboard";
            }
            legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), PORT);
            return "course adjustment - luff up to hold port reaching - port tack - port rounding";
        }
        if (legstrategy.boat.isPortRear90Quadrant(legstrategy.getMarkLocation())) {
            legstrategy.decision.setTURN(legstrategy.boat.getPortReachingCourse(winddirection), PORT);
            return "pre markrounding action - gybe to port - starboard tack - port rounding";
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(legstrategy, winddirection)) {
            return "course adjustment - approaching mark - starboard tack - port rounding";
        }
        legstrategy.decision.setTURN(legstrategy.boat.getStarboardReachingCourse(winddirection), STARBOARD);
        return "course adjustment - luff up to hold starboard reaching - starboard tack - port rounding";
    }
}
