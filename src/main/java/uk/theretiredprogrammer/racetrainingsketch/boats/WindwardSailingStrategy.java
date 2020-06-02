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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Channel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class WindwardSailingStrategy extends SailingStrategy {

    private final boolean sailonbesttack;
    private final boolean tackifheaded;
    private final boolean bearawayifheaded;
    private final boolean luffupiflifted;
    private final Channel channel;
    private final Angle meanwinddirection;

    WindwardSailingStrategy(boolean sailonbesttack, boolean tackifheaded, boolean bearawayifheaded,
            boolean luffupiflifted, Channel channel,
            Angle meanwinddirection) {
        this.sailonbesttack = sailonbesttack;
        this.tackifheaded = tackifheaded;
        this.bearawayifheaded = bearawayifheaded;
        this.luffupiflifted = luffupiflifted;
        this.channel = channel;
        this.meanwinddirection = meanwinddirection;
    }

    @Override
    void nextTimeInterval(Decision decision, BoatElement boat, CourseLegWithStrategy leg, Angle winddirection) {
        Angle boatangletowind = boat.getDirection().absAngleDiff(winddirection);
        boolean onPort = boat.getDirection().gteq(winddirection);
        Angle closehauledangle = boat.getClosehauled();
        // check if need to tack for mark
        if (onPort) {
            if (leg.getAngletoSail(boat.getLocation(), !onPort, winddirection).lteq(closehauledangle.negate())) {
                Angle nextDirection = winddirection.sub(closehauledangle);
                decision.setTURN(nextDirection, ANTICLOCKWISE);
                return;
            }
        } else {
            if (leg.getAngletoSail(boat.getLocation(), !onPort, winddirection).gteq(closehauledangle)) {
                Angle nextDirection = winddirection.add(closehauledangle);
                decision.setTURN(nextDirection, CLOCKWISE);
                return;
            }
        }
        // stay in channel
        if (channel != null) {
            if (leg.getDistanceToEnd(boat.getLocation()) > channel.getInneroffset(leg.getEndLocation()) * 1.5) {
                if (!channel.isInchannel(boat.getLocation())) {
                    Angle nextDirection = winddirection.add(closehauledangle.negateif(onPort));
                    decision.setTURN(nextDirection, onPort ? ANTICLOCKWISE : CLOCKWISE);
                    return;
                }
            }
        }
        // check if need to tack onto best tack
        if (sailonbesttack) {
            if (onPort) {
                if (winddirection.gt(meanwinddirection)) {
                    Angle nextDirection = winddirection.sub(closehauledangle);
                    decision.setTURN(nextDirection, ANTICLOCKWISE);
                    return;
                }
            } else {
                if (winddirection.lt(meanwinddirection)) {
                    Angle nextDirection = winddirection.add(closehauledangle);
                    decision.setTURN(nextDirection, CLOCKWISE);
                    return;
                }
            }
        }
        // check if pointing high
        if (boatangletowind.lt(closehauledangle)) {
            if (tackifheaded) {
                Angle nextDirection = winddirection.add(closehauledangle.negateif(onPort));
                decision.setTURN(nextDirection, onPort ? ANTICLOCKWISE : CLOCKWISE);
                return;
            }
            if (bearawayifheaded) {
                Angle nextDirection = winddirection.add(closehauledangle.negateif(!onPort));
                decision.setTURN(nextDirection, onPort ? CLOCKWISE : ANTICLOCKWISE);
                return;
            }
        }
        // check if pointing low
        if (boatangletowind.gt(closehauledangle)) {
            if (luffupiflifted) {
                Angle nextDirection = winddirection.add(closehauledangle.negateif(!onPort));
                decision.setTURN(nextDirection, onPort ? ANTICLOCKWISE : CLOCKWISE);
            }
        }
    }
}
