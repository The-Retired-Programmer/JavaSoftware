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
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE180;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.SailingDecisions.getRefDistance;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author richard
 */
public class BoatStrategyForOffwindLeg extends BoatStrategyForLeg {

    private final OffwindSailingDecisions strategy;
    private final RoundingDecisions roundingstrategy;
    private boolean useroundingstrategy = false;

    public BoatStrategyForOffwindLeg(Controller controller, Boat boat, Leg leg) throws IOException {
        super(boat, leg, 
                leg.getAngleofLeg().add(ANGLE90), leg.getAngleofLeg().sub(ANGLE90));
        strategy = new OffwindSailingDecisions();
        roundingstrategy = getRoundingStrategy(controller, boat, leg);
    }

    private RoundingDecisions getRoundingStrategy(Controller controller, Boat boat, Leg leg) throws IOException {
        LegType followinglegtype = getLegType(controller, boat, leg.getFollowingLeg());
        switch (followinglegtype) {
            case WINDWARD:
                return !leg.isPortRounding()
                        ? new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardCloseHauledCourse(windangle))
                        : new OffwindPortRoundingDecisions((windangle) -> boat.getPortCloseHauledCourse(windangle));
            case OFFWIND:
                return !leg.isPortRounding()
                        ? new OffwindStarboardRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg())
                        : new OffwindPortRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg());
            case GYBINGDOWNWIND:
                return !leg.isPortRounding()
                        ? new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle))
                        : new OffwindPortRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle));
            case NONE:
                return !leg.isPortRounding()
                        ? new OffwindStarboardRoundingDecisions((windangle) -> windangle.sub(ANGLE90))
                        : new OffwindPortRoundingDecisions((windangle) -> windangle.add(ANGLE90));
            default:
                throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: Offwind to "
                        + followinglegtype.toString());
        }
    }

    @Override
    String nextTimeInterval(Controller controller) throws IOException {
        Angle markMeanwinddirection = leg.getMarkMeanwinddirection();
        if (useroundingstrategy) {
            return roundingstrategy.nextTimeInterval(controller, decision, boat, this);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingstrategy = true;
            return roundingstrategy.nextTimeInterval(controller, decision, boat, this);
        }
        return strategy.nextTimeInterval(controller, decision, boat, this);
    }

    boolean isNear2Mark(Boat boat, Angle markMeanwinddirection) {
        Optional<Double> refdistance = getRefDistance(boat.location, leg.getEndLocation(), markMeanwinddirection.sub(ANGLE180));
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getWidth() * 20 : true;
    }

}
