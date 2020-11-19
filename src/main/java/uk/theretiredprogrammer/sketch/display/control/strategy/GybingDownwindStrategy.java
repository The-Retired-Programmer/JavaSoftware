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

import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

public class GybingDownwindStrategy extends Strategy {

    public GybingDownwindStrategy(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees winddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees relative = boat.metrics.downwindrelative;
        if (leg.isPortRounding()) {
            this.setMarkOffset(boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            this.setMarkOffset(boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        RoundingDecisions roundingdecisions;
        LegType followinglegtype = CurrentLeg.getLegType(boat.metrics, leg.getAngleofFollowingLeg(), windflow, boat.isReachdownwind());
        switch (followinglegtype) {
            case WINDWARD ->
                roundingdecisions = leg.isPortRounding()
                        ? new GybingDownwindPortRoundingDecisions((windangle) -> boat.getPortCloseHauledCourse(windangle))
                        : new GybingDownwindStarboardRoundingDecisions((windangle) -> boat.getStarboardCloseHauledCourse(windangle));
            case OFFWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg());
            case NONE ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported LEGTYPE combination: Gybing downwind to "
                        + followinglegtype.toString());
        }
        this.setDecisions(new GybingDownwindStarboardSailingDecisions(), new GybingDownwindPortSailingDecisions(), roundingdecisions);
    }

    public GybingDownwindStrategy(GybingDownwindStrategy clonefrom) {
        super(clonefrom);
    }
}
