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
package uk.theretiredprogrammer.racetrainingsketch.course;

import uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import static uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg.LegType.GYBINGDOWNWIND;
import static uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg.LegType.OFFWIND;
import static uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg.LegType.WINDWARD;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class CourseLeg {

    public enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    private final Location startfrom;
    private final Location marklocation;
    private final TurnDirection turn;
    private CourseLeg followingleg;

    public CourseLeg(CourseLeg from) {
        this(from.startfrom, from.marklocation, from.turn, from.followingleg);
    }

    public CourseLeg(Location startfrom, Location marklocation, TurnDirection turn, CourseLeg followingleg) {
        this.startfrom = startfrom;
        this.marklocation = marklocation;
        this.turn = turn;
        this.followingleg = followingleg;
    }

    public CourseLeg getFollowingLeg() {
        return followingleg;
    }
    
    public TurnDirection getTurn() {
        return turn;
    }

    public double getDistanceToEnd(Location here) {
        return here.to(marklocation);
    }
    
    public Location getStartLocation() {
        return startfrom;
    }

    public Location getEndLocation() {
        return marklocation;
    }

    public Angle getAngletoEnd(Location here) {
        return here.angleto(marklocation);
    }

    public Angle getAngleofLeg() {
        return startfrom.angleto(marklocation);
    }

    public LegType getLegType(Angle winddirection, Angle upwindangle, Angle downwindangle) {
        Angle legtowind = getAngleofLeg().absAngleDiff(winddirection);

        if (legtowind.lteq(upwindangle)) {
            return WINDWARD;
        }
        if (legtowind.gteq(downwindangle)) {
            return GYBINGDOWNWIND;
        }
        return OFFWIND;
    }

    public LegType getLegType(Angle winddirection, Angle upwindangle) {
        Angle legtowind = getAngleofLeg().absAngleDiff(winddirection);

        if (legtowind.lteq(upwindangle)) {
            return WINDWARD;
        }
        return OFFWIND;
    }
}
