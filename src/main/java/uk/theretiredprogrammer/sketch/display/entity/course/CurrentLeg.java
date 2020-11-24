/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.course;

import java.util.Optional;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.log.control.LogController;
import uk.theretiredprogrammer.sketch.log.entity.BoatLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.DecisionLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.ReasonLogEntry;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.GYBINGDOWNWIND;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.NONE;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.OFFWIND;
import static uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType.WINDWARD;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;

public class CurrentLeg {

    public static enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    public LegType getLegType(Params params) {
        return getLegTypeFromMarkAngle(params.angletomark, params);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public LegType getFollowingLegType(Params params) {
        Angle angletofollowingmark = params.leg.getAngleofFollowingLeg();
        return angletofollowingmark == null ? NONE : getLegTypeFromMarkAngle(angletofollowingmark, params);
    }

    private LegType getLegTypeFromMarkAngle(Angle angletomark, Params params) {
        Angle legtowind = angletomark.absDegreesDiff(params.meanwinddirection);
        if (legtowind.lteq(params.upwindrelative)) {
            return WINDWARD;
        }
        if (params.reachesdownwind && legtowind.gteq(params.downwindrelative)) {
            return GYBINGDOWNWIND;
        }
        return OFFWIND;
    }

    private int legno = 0;

    private Leg currentleg;
    private final Course course;
    private final Strategy strategy = new Strategy();
    public final Decision decision;

    public CurrentLeg(Course course) {
        this(course, 0);
    }

    public CurrentLeg(CurrentLeg clonefrom) {
        this(clonefrom.course, clonefrom.legno);
    }

    private CurrentLeg(Course course, int legno) {
        this.decision = new Decision();
        this.course = course;
        this.legno = legno;
        currentleg = course.getLeg(legno);
        course.setOnChange(() -> refresh());
    }

    private void refresh() {
        currentleg = course.getLeg(legno);
    }

    public Decision getDecision() {
        return decision;
    }

    public boolean isFollowingLeg() {
        return course.getLegsProperty().size() > legno + 1;
    }

    public CurrentLeg toFollowingLeg() {
        if (isFollowingLeg()) {
            currentleg = course.getLeg(++legno);
        }
        return this;
    }

    public Angle getAngleofFollowingLeg() {
        return isFollowingLeg()
                ? course.getLeg(legno + 1).getAngleofLeg()
                : null;
    }

    // proxies to current Leg
    public boolean isPortRounding() {
        return currentleg.isPortRounding();
    }

    public double getDistanceToMark(Location here) {
        return currentleg.getDistanceToEnd(here);
    }

    public Location getMarkLocation() {
        return currentleg.getEndLocation();
    }

    public Angle getAngleofLeg() {
        return currentleg.getAngleofLeg();
    }

    public Location getSailToLocation(boolean onPort) {
        return strategy.getOffsetVector(onPort)
                .toLocation(getMarkLocation());
    }

    public Angle getAngletoSail(Location here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    public void nextTimeInterval(Params params, int simulationtime, LogController timerlog) {
        if (!strategy.hasStrategy()) {
            strategy.setStrategy(params, this);
        }
        if (decision.getAction() == SAILON) {
            strategy.strategyTimeInterval(params);
            timerlog.add(new BoatLogEntry(params.boat));
            timerlog.add(new DecisionLogEntry(params.boat.getName(), decision));
            timerlog.add(new ReasonLogEntry(params.boat.getName(), decision.getReason()));
        }
        if (params.boat.moveUsingDecision(params)) {
            if (isFollowingLeg()) {
                toFollowingLeg();
                params.refresh();
                strategy.setStrategy(params, this);
            } else {
                params.refresh();
                strategy.setAfterFinishStrategy(params);
            }
        }
    }
}
