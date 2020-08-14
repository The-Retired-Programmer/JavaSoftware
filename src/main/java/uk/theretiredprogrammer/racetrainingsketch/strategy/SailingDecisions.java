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
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.PORT;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.STARBOARD;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class SailingDecisions {

    abstract String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException;

    boolean tackifonstarboardlayline(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        if (boat.isPortTackingQuadrant(legstrategy.getSailToLocation(false), winddirection)) {
            decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean gybeifonstarboardlayline(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        if (boat.isStarboardGybingQuadrant(legstrategy.getSailToLocation(false), winddirection)) {
            decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean tackifonportlayline(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        if (boat.isStarboardTackingQuadrant(legstrategy.getSailToLocation(true), winddirection)) {
            decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
            return true;
        }
        return false;
    }

    boolean gybeifonportlayline(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        if (boat.isPortGybingQuadrant(legstrategy.getSailToLocation(true), winddirection)) {
            decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
            return true;
        }
        return false;
    }

    boolean adjustPortDirectCourseToWindwardMarkOffset(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        Angle coursetomark = legstrategy.getAngletoSail(boat.location, true);
        Angle closehauled = boat.getPortCloseHauledCourse(winddirection);
        if (coursetomark.lt(closehauled)) {
            return false;
        }
        return adjustCourse(boat.direction,
                coursetomark.gteq(closehauled) ? coursetomark : closehauled, decision);
    }

    boolean adjustStarboardDirectCourseToWindwardMarkOffset(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        Angle coursetomark = legstrategy.getAngletoSail(boat.location, false);
        Angle closehauled = boat.getStarboardCloseHauledCourse(winddirection);
        if (coursetomark.gt(closehauled)) {
            return false;
        }
        return adjustCourse(boat.direction,
                coursetomark.lteq(closehauled) ? coursetomark : closehauled, decision);
    }

    boolean adjustPortDirectCourseToLeewardMarkOffset(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = legstrategy.getAngletoSail(boat.location, true);
        Angle reaching = boat.getPortReachingCourse(winddirection);
        if (coursetomark.gt(reaching)) {
            return false;
        }
        return adjustCourse(boat.direction,
                coursetomark.lteq(reaching) ? coursetomark : reaching, decision);
    }

    boolean adjustStarboardDirectCourseToLeewardMarkOffset(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        // check and adjust if boat can sail dirctly to next mark (offset)
        Angle coursetomark = legstrategy.getAngletoSail(boat.location, false);
        Angle reaching = boat.getStarboardReachingCourse(winddirection);
        if (coursetomark.lt(reaching)) {
            return false;
        }
        return adjustCourse(boat.direction,
                coursetomark.gteq(reaching) ? coursetomark : reaching, decision);
    }

    boolean adjustDirectCourseToDownwindMarkOffset(Boat boat, BoatStrategyForLeg legstrategy, Decision decision, Angle winddirection) {
        if (boat.isPort(winddirection)) {
            Angle coursetomark = legstrategy.getAngletoSail(boat.location, true);
            return adjustCourse(boat.direction, coursetomark, decision);
        } else {
            Angle coursetomark = legstrategy.getAngletoSail(boat.location, false);
            return adjustCourse(boat.direction, coursetomark, decision);
        }
    }

    private boolean adjustCourse(Angle current, Angle target, Decision decision) {
        if (target.neq(current)) {
            decision.setTURN(target, target.gt(current) ? STARBOARD : PORT);
            return true;
        }
        return true;
    }

    static Optional<Double> getRefDistance(Location location, Location marklocation, Angle refangle) {
        DistancePolar tomark = new DistancePolar(location, marklocation);
        Angle refangle2mark = refangletomark(tomark.getAngle(), refangle);
        if (refangle2mark.gt(ANGLE90)) {
            return Optional.empty();
        }
        return Optional.of(refdistancetomark(tomark.getDistance(), refangle2mark));
    }

    private static double refdistancetomark(double distancetomark, Angle refangle2mark) {
        return distancetomark * Math.cos(refangle2mark.getRadians());
    }

    private static Angle refangletomark(Angle tomarkangle, Angle refangle) {
        return tomarkangle.absAngleDiff(refangle);
    }
}
