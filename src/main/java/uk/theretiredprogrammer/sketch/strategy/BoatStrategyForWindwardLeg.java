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
package uk.theretiredprogrammer.sketch.strategy;

import java.util.Optional;
import uk.theretiredprogrammer.sketch.boats.Boat;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.core.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatStrategyForWindwardLeg extends BoatStrategyForLeg {

    private final WindwardStarboardSailingDecisions starboarddecisions;
    private final WindwardPortSailingDecisions portdecisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;

    public BoatStrategyForWindwardLeg(Boat boat, Leg leg, WindFlow windflow, WaterFlow waterflow) {
        super(boat, leg,
                leg.getMarkMeanwinddirection(windflow).add(new Angle(135)), leg.getMarkMeanwinddirection(windflow).add(new Angle(45)),
                leg.getMarkMeanwinddirection(windflow).add(new Angle(-45)), leg.getMarkMeanwinddirection(windflow).add(new Angle(-135)));
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
                        ? new WindwardPortRoundingDecisions((windangle) -> windangle.sub(ANGLE90))
                        : new WindwardStarboardRoundingDecisions((windangle) -> windangle.add(ANGLE90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported WindwardRounding: " + followinglegtype.toString());
        }
    }

    @Override
    String nextBoatStrategyTimeInterval(PropertySketch sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        Angle markMeanwinddirection = leg.getMarkMeanwinddirection(windflow);
        Angle winddirection = windflow.getFlow(boat.getProperty().getLocation()).getAngle();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(sketchproperty, this, windflow, waterflow);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(sketchproperty, this, windflow, waterflow);
        }
        return (boat.isPort(winddirection) ? portdecisions : starboarddecisions).nextTimeInterval(sketchproperty, this, windflow, waterflow);
    }

    boolean isNear2Mark(Boat boat, Angle markMeanwinddirection) {
        Optional<Double> refdistance = getRefDistance(boat.getProperty().getLocation(), leg.getEndLocation(), markMeanwinddirection);
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }
}
