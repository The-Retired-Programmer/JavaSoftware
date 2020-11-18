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
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg.LegType;
import uk.theretiredprogrammer.sketch.display.entity.course.Strategy;

public class GybingDownwindStrategy extends Strategy {

    private final GybingDownwindStarboardSailingDecisions starboarddecisions;
    private final GybingDownwindPortSailingDecisions portdecisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;
    private PropertyDegrees portoffsetangle;
    private PropertyDegrees starboardoffsetangle;
    private double offset;

    public GybingDownwindStrategy(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        offset = boat.metrics.getWidth() * 2;
        PropertyDegrees winddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees relative = boat.metrics.downwindrelative;
        if (leg.isPortRounding()) {
            portoffsetangle = winddirection.plus(90).plus(relative);
            starboardoffsetangle = winddirection.plus(90).sub(relative);
        } else {
            portoffsetangle = winddirection.sub(90).plus(relative);
            starboardoffsetangle = winddirection.sub(90).sub(relative);
        }
        portdecisions = new GybingDownwindPortSailingDecisions();
        starboarddecisions = new GybingDownwindStarboardSailingDecisions();
        LegType followinglegtype = PropertyLeg.getLegType(boat.metrics, leg.getAngleofFollowingLeg(), windflow, boat.isReachdownwind());
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
    }

    public GybingDownwindStrategy(GybingDownwindStrategy clonefrom, Boat newboat) {
        this.portdecisions = clonefrom.portdecisions;
        this.starboarddecisions = clonefrom.starboarddecisions;
        this.roundingdecisions = clonefrom.roundingdecisions;
        this.useroundingdecisions = clonefrom.useroundingdecisions;
        this.offset = clonefrom.offset;
        this.portoffsetangle = clonefrom.portoffsetangle;
        this.starboardoffsetangle = clonefrom.starboardoffsetangle;
    }

    @Override
    public String strategyTimeInterval(Boat boat, Decision decision, CurrentLeg leg, SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees markMeanwinddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
        }
        if (leg.isNear2LeewardMark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
        }
        return (boat.isPort(winddirection) ? portdecisions : starboarddecisions).nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
    }

    @Override
    public PropertyDistanceVector getOffsetVector(boolean onPort) {
        return new PropertyDistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }
}
