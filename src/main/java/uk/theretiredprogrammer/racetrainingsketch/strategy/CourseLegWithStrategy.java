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
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElement;
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatMetrics;
import uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;
import static uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg.LegType.NONE;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class CourseLegWithStrategy extends CourseLeg  {
    
    private final SailingStrategy legstrategy;
    private final RoundingStrategy roundingstrategy;
    private final double closetomark;
    
    public CourseLegWithStrategy(CourseLeg courseleg) {
        super(courseleg);
        legstrategy = new AfterFinishSailingStrategy();
        roundingstrategy = null;
        closetomark = 0;
}
     
    public CourseLegWithStrategy(CourseLeg courseleg, Angle meanwinddirection,
            BoatElement boat) throws IOException {
        super(courseleg);
        BoatMetrics metrics = boat.getMetrics();
        this.closetomark = metrics.getLength() * 3;
        LegType thislegtype = boat.isReachdownwind()
                ? courseleg.getLegType(meanwinddirection, metrics.getUpwindrelative(), metrics.getDownwindrelative())
                : courseleg.getLegType(meanwinddirection, metrics.getUpwindrelative());
        legstrategy = selectlegstrategy(thislegtype, meanwinddirection, boat);
        CourseLeg followingleg = courseleg.getFollowingLeg();
        LegType followinglegtype = followingleg != null
                ? (boat.isReachdownwind()
                ? followingleg.getLegType(meanwinddirection, metrics.getUpwindrelative(), metrics.getDownwindrelative())
                : followingleg.getLegType(meanwinddirection, metrics.getUpwindrelative()))
                : NONE;
        roundingstrategy = selectroundingstrategy(thislegtype, followinglegtype, courseleg.getTurn(), boat);
    }
    
    private SailingStrategy selectlegstrategy(LegType thislegtype, Angle meanwinddirection,
            BoatElement boatstrategy) throws IOException {
        switch (thislegtype){
            case WINDWARD:
                return new WindwardSailingStrategy(
                        boatstrategy.isUpwindsailonbesttack(),
                        boatstrategy.isUpwindtackifheaded(),
                        boatstrategy.isUpwindbearawayifheaded(),
                        boatstrategy.isUpwindluffupiflifted(),
                        null,
                        meanwinddirection
                );
            case OFFWIND:
                return new OffwindSailingStrategy();
            case GYBINGDOWNWIND:
                return new GybingDownwindSailingStrategy(
                        boatstrategy.isDownwindsailonbestgybe(),
                        boatstrategy.isDownwindgybeiflifted(),
                        boatstrategy.isDownwindbearawayifheaded(),
                        boatstrategy.isDownwindluffupiflifted(),
                        null,
                        meanwinddirection
                );
            default:
                throw new IOException("Illegal/unknown LEGTYPE: " + thislegtype.toString());
        }
    }
    
    private RoundingStrategy selectroundingstrategy(LegType thislegtype, LegType followinglegtype,
            TurnDirection turn, BoatElement boat) throws IOException {
    switch (thislegtype){
            case WINDWARD:
                switch (followinglegtype){
                    case OFFWIND:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? -45 : -135)),
                                        (winddirection) -> this.getEndLocation().angleto(this.getFollowingLeg().getEndLocation())
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 135 : 45)),
                                        (winddirection) -> this.getEndLocation().angleto(this.getFollowingLeg().getEndLocation())
                                );
                    case GYBINGDOWNWIND:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? -45 : -135)),
                                        (winddirection) -> winddirection.add(boat.getDownwind())
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 135 : 45)),
                                        (winddirection) -> winddirection.sub(boat.getDownwind())
                                        );
                    case NONE:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? -45 : -135)),
                                        (winddirection) -> winddirection.add(ANGLE90)
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 135 : 45)),
                                        (winddirection) -> winddirection.sub(ANGLE90)
                                        );
                    default:
                       throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: "
                               + thislegtype.toString()+"->"+followinglegtype.toString()); 
                }
            case OFFWIND:
                switch (followinglegtype){
                    case WINDWARD:
                         return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().sub(ANGLE90),
                                        (winddirection) -> winddirection.sub(boat.getClosehauled())
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().add(ANGLE90),
                                        (winddirection) -> winddirection.add(boat.getClosehauled())
                                );
                    case OFFWIND:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().sub(ANGLE90),
                                        (winddirection) -> this.getFollowingLeg().getAngleofLeg()
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().add(ANGLE90),
                                        (winddirection) -> this.getFollowingLeg().getAngleofLeg()
                                );
                    case NONE:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().sub(ANGLE90),
                                        (winddirection) -> winddirection.sub(ANGLE90)
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> this.getAngleofLeg().add(ANGLE90),
                                        (winddirection) -> winddirection.add(ANGLE90)
                                );
                    default:
                       throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: "
                               + thislegtype.toString()+"->"+followinglegtype.toString()); 
                }
            case GYBINGDOWNWIND:
                switch (followinglegtype){
                    case WINDWARD:
                       return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 45 : 135)),
                                        (winddirection) -> winddirection.sub(boat.getClosehauled())
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) ->  winddirection.add(new Angle(onport ? -135 : -45)),
                                        (winddirection) -> winddirection.add(boat.getClosehauled())
                                );
                    case OFFWIND:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 45 : 135)),
                                        (winddirection) -> this.getFollowingLeg().getAngleofLeg()
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) ->  winddirection.add(new Angle(onport ? -135 : -45)),
                                        (winddirection) -> this.getFollowingLeg().getAngleofLeg()
                                );
                    case NONE:
                        return turn.equals(CLOCKWISE)
                                ? new StarboardRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? 45 : 135)),
                                        (winddirection) -> winddirection.sub(ANGLE90)
                                )
                                : new PortRoundingStrategy(boat,
                                        (onport, winddirection) -> winddirection.add(new Angle(onport ? -135 : -45)),
                                        (winddirection) -> winddirection.add(ANGLE90)
                                );
                    default:
                       throw new IOException("Illegal/unknown/Unsupported LEGTYPE combination: "
                               + thislegtype.toString()+"->"+followinglegtype.toString()); 
                }
            default:
                throw new IOException("Illegal/unknown LEGTYPE: " + thislegtype.toString());
        }
    }
    
    Location getSailToLocation(boolean onPort, Angle winddirection){
        return getOffset(onPort, winddirection).polar2Location(getEndLocation());
    }

    Angle getAngletoSail(Location here,boolean onPort, Angle winddirection) {
        return here.angleto(getSailToLocation(onPort, winddirection));
    }
        
    public void nextTimeInterval(Decision decision, BoatElement boat, Angle winddirection) {
        getSailingStrategy(boat.getLocation()).nextTimeInterval(decision, boat, this, winddirection);
    }
    
    private SailingStrategy getSailingStrategy(Location here) {
        return getDistanceToEnd(here) <= closetomark ? roundingstrategy : legstrategy;
    }

    DistancePolar getOffset(boolean onPort, Angle winddirection) {
        return roundingstrategy.getOffset(onPort, winddirection, this);
    }
}
