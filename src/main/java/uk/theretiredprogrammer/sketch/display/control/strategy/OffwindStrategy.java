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

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.GYBINGDOWNWIND;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.OFFWIND;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.WINDWARD;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

public class OffwindStrategy extends Strategy {

    public OffwindStrategy(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        if (leg.isPortRounding()) {
            this.setMarkOffset(boat.metrics.getWidth() * 2, leg.getAngleofLeg().plus(90), leg.getAngleofLeg().plus(90));
        } else {
            this.setMarkOffset(boat.metrics.getWidth() * 2, leg.getAngleofLeg().sub(90), leg.getAngleofLeg().sub(90));
        }
        RoundingDecisions roundingdecisions;
        LegType followinglegtype = CurrentLeg.getLegType(boat.metrics, leg.getAngleofFollowingLeg(), windflow, boat.isReachdownwind());
        switch (followinglegtype) {
            case WINDWARD ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortCloseHauledCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardCloseHauledCourse(windangle));
            case OFFWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg());
            case GYBINGDOWNWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle));
            case NONE ->
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> windangle.plus(90))
                        : new OffwindStarboardRoundingDecisions((windangle) -> windangle.sub(90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported LEGTYPE combination: Offwind to "
                        + followinglegtype.toString());
        }
        this.setDecisions(new OffwindSailingDecisions(), new OffwindSailingDecisions(), roundingdecisions);
    }

    public OffwindStrategy(OffwindStrategy clonefrom) {
        super(clonefrom);
    }
}
