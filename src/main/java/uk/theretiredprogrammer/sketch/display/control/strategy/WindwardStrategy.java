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

import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import java.util.Optional;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public class WindwardStrategy extends Strategy {

    private final WindwardStarboardSailingDecisions starboarddecisions;
    private final WindwardPortSailingDecisions portdecisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;

    public WindwardStrategy(Boat boat, Leg leg, WindFlow windflow, WaterFlow waterflow) {
        super(boat, leg,
                leg.endLegMeanwinddirection(windflow).plus(new PropertyDegrees(135)), leg.endLegMeanwinddirection(windflow).plus(new PropertyDegrees(45)),
                leg.endLegMeanwinddirection(windflow).plus(new PropertyDegrees(-45)), leg.endLegMeanwinddirection(windflow).plus(new PropertyDegrees(-135)));
        starboarddecisions = new WindwardStarboardSailingDecisions();
        portdecisions = new WindwardPortSailingDecisions();
        LegType followinglegtype = getLegType(boat, leg.getFollowingLeg(), windflow);
        switch (followinglegtype) {
            case OFFWIND ->
                roundingdecisions = leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg())
                        : new WindwardStarboardRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg());
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
    }

    @Override
    String nextBoatStrategyTimeInterval(SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees markMeanwinddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(sketchproperty, this, windflow, waterflow);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(sketchproperty, this, windflow, waterflow);
        }
        return (boat.isPort(winddirection) ? portdecisions : starboarddecisions).nextTimeInterval(sketchproperty, this, windflow, waterflow);
    }

    boolean isNear2Mark(Boat boat, PropertyDegrees markMeanwinddirection) {
        Optional<Double> refdistance = getRefDistance(boat.getLocation(), leg.getEndLocation(), markMeanwinddirection);
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }
}
