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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.Optional;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE180;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatStrategyForOffwindLeg extends BoatStrategyForLeg {

    private final OffwindSailingDecisions decisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;

    public BoatStrategyForOffwindLeg(Controller controller, Boat boat, Leg leg) throws IOException {
        super(boat, leg, leg.getAngleofLeg().add(ANGLE90), leg.getAngleofLeg().sub(ANGLE90));
        decisions = new OffwindSailingDecisions();
        LegType followinglegtype = getLegType(controller, boat, leg.getFollowingLeg());
        switch (followinglegtype) {
            case WINDWARD:
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortCloseHauledCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardCloseHauledCourse(windangle));
                break;
            case OFFWIND:
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg());
                break;
            case GYBINGDOWNWIND:
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle));
                break;
            case NONE:
                roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> windangle.add(ANGLE90))
                        : new OffwindStarboardRoundingDecisions((windangle) -> windangle.sub(ANGLE90));
                break;
            default:
                throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: Offwind to "
                        + followinglegtype.toString());
        }
    }

    @Override
    String nextBoatStrategyTimeInterval(Controller controller) throws IOException {
        Angle markMeanwinddirection = leg.getMarkMeanwinddirection();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(controller, this);
        }
        if (isNear2Mark(boat, markMeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(controller, this);
        }
        return decisions.nextTimeInterval(controller, this);
    }

    boolean isNear2Mark(Boat boat, Angle markMeanwinddirection) {
        Optional<Double> refdistance = getRefDistance(boat.location, leg.getEndLocation(), markMeanwinddirection.sub(ANGLE180));
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getWidth() * 20 : true;
    }

}
