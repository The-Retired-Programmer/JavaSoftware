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

import java.util.Optional;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE180;
import uk.theretiredprogrammer.racetrainingsketch.core.Channel;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class GybingDownwindSailingStrategy extends SailingLegStrategy {

    private final boolean sailonbestgybe;
    private final boolean gybeiflifted;
    private final boolean bearawayifheaded;
    private final boolean luffupiflifted;
    private final Channel channel;
    private final Angle meanwinddirection;

    GybingDownwindSailingStrategy(boolean sailonbestgybe, boolean gybeiflifted, boolean bearawayifheaded,
            boolean luffupiflifted, Channel channel, Angle meanwinddirection) {
        this.sailonbestgybe = sailonbestgybe;
        this.gybeiflifted = gybeiflifted;
        this.bearawayifheaded = bearawayifheaded;
        this.luffupiflifted = luffupiflifted;
        this.channel = channel;
        this.meanwinddirection = meanwinddirection;
    }

    @Override
    String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        Angle boatangletowind = boat.getDirection().absAngleDiff(winddirection);
        boolean onPort = boat.isPort(winddirection);
        // check if need to gybe for mark
        if (onPort) {
            if (leg.getAngletoSail(boat.getLocation(), !onPort, winddirection).gteq(boat.getDownwind().negate())) {
                Angle nextDirection = winddirection.sub(boat.getDownwind());
                decision.setTURN(nextDirection, CLOCKWISE);
                return "Gybing onto starboard layline";
            }
        } else {
            if (leg.getAngletoSail(boat.getLocation(), !onPort, winddirection).lteq(boat.getDownwind())) {
                Angle nextDirection = winddirection.add(boat.getDownwind());
                decision.setTURN(nextDirection, ANTICLOCKWISE);
                return "Gybing onto port layline";
            }
        }
        if (adjustDirectCourseToLeewardMarkOffset(boat, leg, decision, winddirection)) {
            return "Reaching on Layline to leeward mark - course adjustment";
        }
        if (channel != null) {
            if (leg.getDistanceToEnd(boat.getLocation()) > channel.getInneroffset(leg.getEndLocation()) * 1.5) {
                if (!channel.isInchannel(boat.getLocation())) {
                    Angle nextDirection = winddirection.add(boat.getDownwind().negateif(onPort));
                    decision.setTURN(nextDirection, onPort ? CLOCKWISE : ANTICLOCKWISE);
                    return "Gybing to stay in channel";
                }
            }
        }
        // check if need to gybe onto best tack
        if (sailonbestgybe) {
            if (onPort) {// on port
                if (winddirection.lt(meanwinddirection)) {
                    Angle nextDirection = winddirection.sub(boat.getDownwind());
                    decision.setTURN(nextDirection, CLOCKWISE);
                    return "Gybe onto best tack - starboard";
                }
            } else {
                if (winddirection.gt(meanwinddirection)) {
                    Angle nextDirection = winddirection.add(boat.getDownwind());
                    decision.setTURN(nextDirection, ANTICLOCKWISE);
                    return "Gybe onto best tack - port";
                }
            }
        }
        // check if sailing too low
        if (boatangletowind.gt(boat.getDownwind())) {
            if (gybeiflifted) {
                Angle nextDirection = winddirection.add(boat.getDownwind().negateif(onPort));
                decision.setTURN(nextDirection, onPort ? CLOCKWISE : ANTICLOCKWISE);
                return "Reaching - gybe if lifted";
            }
            if (luffupiflifted) {
                Angle nextDirection = winddirection.add(boat.getDownwind().negateif(!onPort));
                decision.setTURN(nextDirection, onPort ? ANTICLOCKWISE : CLOCKWISE);
                return "Reaching - luff if lifted";
            }
        }
        // check if sailing too high
        if (boatangletowind.lt(boat.getDownwind())) {
            if (bearawayifheaded) {
                Angle nextDirection = winddirection.add(boat.getDownwind().negateif(!onPort));
                decision.setTURN(nextDirection, onPort ? CLOCKWISE : ANTICLOCKWISE);
                return "Reaching - bearaway if headed";
            }
        }
        return "Sail ON";
    }
    
    @Override
    boolean applyRoundingStrategy(CourseLegWithStrategy leg, Boat boat, Angle winddirection) {
       Optional<Double> refdistance = getRefDistance(boat.getLocation(), leg.getEndLocation(), winddirection.sub(ANGLE180)); 
       return refdistance.isPresent()? refdistance.get() <= boat.getMetrics().getWidth() * 20 : true;
    }
}
