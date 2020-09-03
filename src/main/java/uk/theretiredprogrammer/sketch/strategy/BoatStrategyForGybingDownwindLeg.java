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

import java.io.IOException;
import java.util.Optional;
import uk.theretiredprogrammer.sketch.boats.Boat;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE180;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatStrategyForGybingDownwindLeg extends BoatStrategyForLeg {

    private final GybingDownwindStarboardSailingDecisions starboarddecisions;
    private final GybingDownwindPortSailingDecisions portdecisions;
    private final RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;

    public BoatStrategyForGybingDownwindLeg(Controller controller, Boat boat, Leg leg) throws IOException {
        super(boat, leg,
                leg.getMarkMeanwinddirection().add(new Angle(-135)), leg.getMarkMeanwinddirection().add(new Angle(-45)),
                leg.getMarkMeanwinddirection().add(new Angle(45)), leg.getMarkMeanwinddirection().add(new Angle(135)));
        portdecisions = new GybingDownwindPortSailingDecisions();
        starboarddecisions = new GybingDownwindStarboardSailingDecisions();
        LegType followinglegtype = getLegType(controller, boat, leg.getFollowingLeg());
        switch (followinglegtype) {
            case WINDWARD -> roundingdecisions = leg.isPortRounding()
                        ? new LeewardReachingPortRoundingDecisions((windangle) -> boat.getPortCloseHauledCourse(windangle))
                        : new LeewardReachingStarboardRoundingDecisions((windangle) -> boat.getStarboardCloseHauledCourse(windangle));
            case OFFWIND -> roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> leg.getFollowingLeg().getAngleofLeg());
            case NONE -> roundingdecisions = leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> boat.getPortReachingCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> boat.getStarboardReachingCourse(windangle));
            default -> throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: Gybing downwind to "
                        + followinglegtype.toString());
        }
    }

    @Override
    String nextBoatStrategyTimeInterval(Controller controller) throws IOException {
        Angle markMeanwinddirection = leg.getMarkMeanwinddirection();
        Angle winddirection = controller.windflow.getFlow(boat.getLocation()).getAngle();
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
        Optional<Double> refdistance = getRefDistance(boat.getLocation(), leg.getEndLocation(), markMeanwinddirection.sub(ANGLE180));
        return refdistance.isPresent() ? refdistance.get() <= boat.metrics.getWidth() * 20 : true;
    }
}
