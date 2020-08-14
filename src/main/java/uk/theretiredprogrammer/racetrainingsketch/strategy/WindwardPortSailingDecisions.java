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
class WindwardPortSailingDecisions extends SailingDecisions {

    private final boolean sailonbesttack;
    private final boolean tackifheaded;
    private final boolean bearawayifheaded;
    private final boolean luffupiflifted;
    private final Channel channel;

    WindwardPortSailingDecisions(boolean sailonbesttack, boolean tackifheaded, boolean bearawayifheaded,
            boolean luffupiflifted, Channel channel) {
        this.sailonbesttack = sailonbesttack;
        this.tackifheaded = tackifheaded;
        this.bearawayifheaded = bearawayifheaded;
        this.luffupiflifted = luffupiflifted;
        this.channel = channel;
    }

    @Override
    String nextTimeInterval(Controller controller, Decision decision, Boat boat, BoatStrategyForLeg legstrategy) throws IOException {
        Angle winddirection = controller.windflow.getFlow(boat.location).getAngle();
        Angle meanwinddirection = controller.windflow.getMeanFlowAngle();
        Angle boatangletowind = boat.direction.absAngleDiff(winddirection);
        if (tackifonstarboardlayline(boat, legstrategy, decision, winddirection)) {
            return "tacking on starboard layline - port->starboard";
        }
        if (adjustPortDirectCourseToWindwardMarkOffset(boat, legstrategy, decision, winddirection)) {
            return "Beating on Port Layline to windward mark - course adjustment";
        }
        // stay in channel
        if (channel != null) {
            if (legstrategy.getDistanceToMark(boat.location) > channel.getInneroffset(legstrategy.getMarkLocation()) * 1.5) {
                if (!channel.isInchannel(boat.location)) {
                    decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
                    return "Tacking onto starboard to stay within channel";
                }
            }
        }
        // check if need to tack onto best tack
        if (sailonbesttack) {
            if (winddirection.gt(meanwinddirection)) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Tack onto best tack - starboard";
            }
        }
        // check if pointing high
        if (boatangletowind.lt(boat.metrics.upwindrelative)) {
            if (tackifheaded) {
                decision.setTURN(boat.getStarboardCloseHauledCourse(winddirection), PORT);
                return "Tack onto starboard when headed";
            }
            if (bearawayifheaded) {
                decision.setTURN(boat.getPortCloseHauledCourse(winddirection), STARBOARD);
                return "Bearaway when headed";
            }
        }
        // check if pointing low
        if (boatangletowind.gt(boat.metrics.upwindrelative)) {
            if (luffupiflifted) {
                decision.setTURN(boat.getPortCloseHauledCourse(winddirection), PORT);
                return "Luff when lifted";
            }
        }
        return "Sail ON";
    }
}
