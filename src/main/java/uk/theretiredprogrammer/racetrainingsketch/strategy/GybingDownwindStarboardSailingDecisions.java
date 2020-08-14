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
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Channel;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.PORT;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.STARBOARD;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindStarboardSailingDecisions extends SailingDecisions {

    private final boolean sailonbestgybe;
    private final boolean gybeiflifted;
    private final boolean bearawayifheaded;
    private final boolean luffupiflifted;
    private final Channel channel;

    GybingDownwindStarboardSailingDecisions(boolean sailonbestgybe, boolean gybeiflifted, boolean bearawayifheaded,
            boolean luffupiflifted, Channel channel) {
        this.sailonbestgybe = sailonbestgybe;
        this.gybeiflifted = gybeiflifted;
        this.bearawayifheaded = bearawayifheaded;
        this.luffupiflifted = luffupiflifted;
        this.channel = channel;
    }

    @Override
    String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getFlow(boat.location).getAngle();
        Angle boatangletowind = boat.direction.absAngleDiff(winddirection);
        Angle meanwinddirection = controller.windflow.getMeanFlowAngle();
        // check if need to gybe for mark
        if (gybeifonportlayline(boat, legstrategy, decision, winddirection)) {
            return "Gybing onto port layline";
        }
        if (adjustStarboardDirectCourseToLeewardMarkOffset(boat, legstrategy, decision, winddirection)) {
            return "Reaching on starboard Layline to leeward mark - course adjustment";
        }
        if (channel != null) {
            if (legstrategy.getDistanceToMark(boat.location) > channel.getInneroffset(legstrategy.getMarkLocation()) * 1.5) {
                if (!channel.isInchannel(boat.location)) {
                    decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
                    return "Gybing onto port to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (sailonbestgybe) {
            if (winddirection.gt(meanwinddirection)) {
                decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
                return "Gybe onto best tack - port";
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(boat.metrics.downwindrelative)) {
            if (gybeiflifted) {
                decision.setTURN(boat.getPortReachingCourse(winddirection), PORT);
                return "Reaching - gybe oto port if lifted";
            }
            if (luffupiflifted) {
                decision.setTURN(boat.getStarboardReachingCourse(winddirection), STARBOARD);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(boat.metrics.downwindrelative)) {
            if (bearawayifheaded) {
                decision.setTURN(boat.getStarboardReachingCourse(winddirection), PORT);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
}
