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
import java.util.Optional;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg.LegType;

public class OffwindStrategy extends Strategy {

    private final OffwindSailingDecisions decisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;
    private PropertyDegrees portoffsetangle;
    private PropertyDegrees starboardoffsetangle;
    private double offset;

    public OffwindStrategy(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        super(leg);
        offset = boat.metrics.getWidth() * 2;
        if (leg.isPortRounding()) {
            portoffsetangle = leg.getAngleofLeg().plus(90);
            starboardoffsetangle = leg.getAngleofLeg().plus(90);
        } else {
            portoffsetangle = leg.getAngleofLeg().sub(90);
            starboardoffsetangle = leg.getAngleofLeg().sub(90);
        }

        decisions = new OffwindSailingDecisions();
        LegType followinglegtype = PropertyLeg.getLegType(boat.metrics, leg.getAngleofFollowingLeg(), windflow, boat.isReachdownwind());
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
    }

    public OffwindStrategy(OffwindStrategy clonefrom, Boat newboat) {
        super(clonefrom);
        this.decisions = clonefrom.decisions;
        this.roundingdecisions = clonefrom.roundingdecisions;
        this.useroundingdecisions = clonefrom.useroundingdecisions;
        this.offset = clonefrom.offset;
        this.portoffsetangle = clonefrom.portoffsetangle;
        this.starboardoffsetangle = clonefrom.starboardoffsetangle;
    }

    @Override
    String strategyTimeInterval(Boat boat, Decision decision, CurrentLeg leg, SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees markMeanwinddirection = leg.endLegMeanwinddirection(windflow);
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
        }
        return decisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
    }

    private boolean isNear2Mark(Boat boat, PropertyDegrees markMeanwinddirection) {
        Optional<Double> refdistance = PropertyLeg.getRefDistance(boat.getLocation(), leg.getEndLocation(), markMeanwinddirection.sub(180).get());
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }

    @Override
    PropertyDistanceVector getOffsetVector(boolean onPort) {
        return new PropertyDistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }
}
