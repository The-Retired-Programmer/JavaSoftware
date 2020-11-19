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
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.GYBINGDOWNWIND;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.NONE;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.OFFWIND;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

public class WindwardStrategy extends Strategy {

    public WindwardStrategy(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        this.setIsWindwardLeg();
        PropertyDegrees winddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees relative = boat.metrics.upwindrelative;
        if (leg.isPortRounding()) {
            setMarkOffset(boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            setMarkOffset(boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        //
        RoundingDecisions roundingdecisions;
        LegType followinglegtype = CurrentLeg.getLegType(boat.metrics, leg.getAngleofFollowingLeg(), windflow, boat.isReachdownwind());
        switch (followinglegtype) {
            case OFFWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg())
                        : new WindwardStarboardRoundingDecisions((windangle) -> leg.getAngleofFollowingLeg());
            case GYBINGDOWNWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle))
                        : new WindwardStarboardRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle));
            case NONE ->
                roundingdecisions = leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> windangle.sub(90))
                        : new WindwardStarboardRoundingDecisions((windangle) -> windangle.plus(90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported WindwardRounding: " + followinglegtype.toString());
        }
        setDecisions(new WindwardStarboardSailingDecisions(), new WindwardPortSailingDecisions(), roundingdecisions);
    }

    public WindwardStrategy(WindwardStrategy clonefrom) {
        super(clonefrom);
    }
}
