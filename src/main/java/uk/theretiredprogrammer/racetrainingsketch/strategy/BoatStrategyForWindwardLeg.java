/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.Optional;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.SailingDecisions.getRefDistance;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author richard
 */
public class BoatStrategyForWindwardLeg extends BoatStrategyForLeg {

    private final WindwardStarboardSailingDecisions starboarddecisions;
    private final WindwardPortSailingDecisions portdecisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;

    public BoatStrategyForWindwardLeg(Controller controller, Boat boat, Leg leg) throws IOException {
        super(boat, leg,
                leg.getMarkMeanwinddirection().add(new Angle(135)), leg.getMarkMeanwinddirection().add(new Angle(45)),
                leg.getMarkMeanwinddirection().add(new Angle(-45)), leg.getMarkMeanwinddirection().add(new Angle(-135)));
        starboarddecisions = new WindwardStarboardSailingDecisions();
        portdecisions = new WindwardPortSailingDecisions();
        roundingdecisions = getRoundingStrategy(controller, boat, leg);
    }

    private RoundingDecisions getRoundingStrategy(Controller controller, Boat boat, Leg leg) throws IOException {
        LegType followinglegtype = getLegType(controller, boat, leg.getFollowingLeg());
        switch (followinglegtype) {
            case OFFWIND:
                return leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg())
                        : new WindwardStarboardRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg());

            case GYBINGDOWNWIND:
                return leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions(
                                (windangle) -> boat.getStarboardReachingCourse(windangle)
                        )
                        : new WindwardStarboardRoundingDecisions(
                                (windangle) -> boat.getPortReachingCourse(windangle)
                        );
            case NONE:
                return leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions(
                                (windangle) -> windangle.sub(ANGLE90)
                        )
                        : new WindwardStarboardRoundingDecisions(
                                (windangle) -> windangle.add(ANGLE90)
                        );
            default:
                throw new IOException("Illegal/unknown/Unsupported WindwardRounding: " + followinglegtype.toString());
        }
    }

    @Override
    String nextBoatStrategyTimeInterval(Controller controller) throws IOException {
        Angle markMeanwinddirection = leg.getMarkMeanwinddirection();
        Angle winddirection = controller.windflow.getFlow(boat.location).getAngle();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(controller, this);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(controller, this);
        }
        return (boat.isPort(winddirection) ? portdecisions : starboarddecisions).nextTimeInterval(controller, this);
    }

    boolean isNear2Mark(Boat boat, Angle markMeanwinddirection) {
        Optional<Double> refdistance = getRefDistance(boat.location, leg.getEndLocation(), markMeanwinddirection);
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getLength() * 5 : true;
    }
}
