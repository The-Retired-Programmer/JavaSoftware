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
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.log.control.LogController;
import uk.theretiredprogrammer.sketch.log.entity.BoatLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.DecisionLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.ReasonLogEntry;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;

public class CurrentLeg {

    public static enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    private int legno = 0;
    private Optional<Leg> currentleg;
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

    public Strategy getStrategy() {
        return strategy;
    }

    public Angle getAngleofFollowingLeg() {
        Optional<Leg> followingleg = course.getLeg(legno + 1);
        return followingleg.map(fleg -> fleg.getAngleofLeg()).orElse(null);
    }

    // proxies to current Leg
    public boolean isPortRounding() {
        return currentleg.map(cleg -> cleg.isPortRounding())
                .orElseThrow(() -> new IllegalStateFailure("No CurrentLeg proxy"));
    }

    public double getDistanceToMark(Location here) {
        return currentleg.map(cleg -> cleg.getDistanceToEnd(here))
                .orElseThrow(() -> new IllegalStateFailure("No CurrentLeg proxy"));
    }

    public Location getMarkLocation() {
        return currentleg.map(cleg -> cleg.getEndLocation())
                .orElse(null);
    }

    public Angle getAngleofLeg() {
        return currentleg.map(cleg -> cleg.getAngleofLeg())
                .orElse(null);
    }

    public Location getSailToLocation(boolean onPort) {
        return strategy.getOffsetVector(onPort)
                .toLocation(getMarkLocation());
    }

    public Angle getAngletoSail(Location here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    public void tick(Params params, int simulationtime, LogController timerlog) {
        if (decision.getAction() == SAILON) {
            strategy.tick(params);
            timerlog.add(new DecisionLogEntry(params.boat, params.windflow, decision));
        }
        if (params.boat.moveUsingDecision(params)) {
            currentleg = course.getLeg(++legno);
            params.refresh();
            currentleg.ifPresentOrElse(
                    (cleg) -> strategy.setStrategy(params),
                    () -> strategy.setAfterFinishStrategy(params)
            );
        }
    }
}
