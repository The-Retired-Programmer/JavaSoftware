/*
 * Copyright 2014-2020 Richard Linsdale.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.ref.WeakReference;
import uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.MARKROUNDING;
import uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.ANTICLOCKWISE;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.TurnDirection.CLOCKWISE;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.course.MarkElement;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class RacingStrategy extends SimpleStrategy {

    private final BoatMetrics metrics;
    private final WeakReference<BoatElement> parametersref;
    private final Angle meanwinddirection;

    /**
     * Constructor
     */
    RacingStrategy(BoatBasics boat, ScenarioElement scenario, BoatMetrics metrics, BoatElement parameters) throws IOException {
        super(boat, scenario, metrics.getUpwindrelative(), metrics.getDownwindrelative());
        this.metrics = metrics;
        this.parametersref = new WeakReference<>(parameters);
        meanwinddirection = scenario.getWindmeanflowangle();
        calculateCurrentLegVariables();
    }

    private BoatElement parameters() throws IOException {
        BoatElement p = parametersref.get();
        if (p == null) {
            throw new IOException("System Fault - lost reference to BoatElement for parameter access");
        }
        return p;
    }

    @Override
    PropertyChangeListener getDecisionchangelistener() {
        return new DecisionChangeListener();
    }

    private boolean newlegactions = false;

    private class DecisionChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            if (pce.getPropertyName().equals("ACTION")
                    && ((DecisionAction) pce.getOldValue()).equals(MARKROUNDING)) {
                newlegactions = true;
            }
        }
    }

    private MarkElement nextMark;
    private DistancePolar starboardspacer;
    private DistancePolar portspacer;
    private DistancePolar directspacer;

    private void calculateCurrentLegVariables() throws IOException {
//        BoatElement parameters = parameters();
//        nextMark = marksupport.getNextmark();
//        Location origin = marksupport.getPreviousmarklocation();
//        DistancePolar directmarkpolar = new DistancePolar(origin, nextMark.getLocation());
//        Angle directmarkangle = directmarkpolar.getAngle();
//        directspacer = new DistancePolar(boat.clearance(), directmarkangle.add(nextMark.isPort() ? ANGLE90 : ANGLE270));
//        boolean starboardrounding = !nextMark.isPort();
//        //
//        Angle boat2wind = boat.getDirection().absAngleDiff(meanwinddirection);
//        if (boat2wind.lteq(metrics.getUpwindrelative())) {
//            portspacer = new DistancePolar(boat.clearance(), meanwinddirection.add(new Angle(135).inverseif(starboardrounding)));
//            starboardspacer = new DistancePolar(boat.clearance(), meanwinddirection.add(new Angle(45).inverseif(starboardrounding)));
//        } else if (boat2wind.gteq(metrics.getDownwindrelative()) && parameters.isReachdownwind()) {
//            portspacer = new DistancePolar(boat.clearance(), meanwinddirection.add(new Angle(-135).inverseif(starboardrounding)));
//            starboardspacer = new DistancePolar(boat.clearance(), meanwinddirection.add(new Angle(-45).inverseif(starboardrounding)));
//        } else {
//            portspacer = directspacer;
//            starboardspacer = directspacer;
//        }
    }

    private Location pos;
    private Angle direction;
    private Angle winddirection;
    private TurnDirection tackingturn;
    private DistancePolar toNextMark;
    private Angle windAngleToMeanWind;
    private Angle boatAngleToWind;
    private boolean isSailingonPort;

    private void calculateAllEnvironmentVariables() throws IOException {
        pos = boat.getLocation();
        direction = boat.getDirection();
        winddirection = scenario.getWindflow(pos).getAngle();
        boatAngleToWind = direction.absAngleDiff(winddirection);
        windAngleToMeanWind = winddirection.angleDiff(meanwinddirection);
        tackingturn = direction.angleDiff(winddirection).gt(ANGLE0) ? CLOCKWISE : ANTICLOCKWISE;
        toNextMark = new DistancePolar(pos, nextMark.getLocation());
        isSailingonPort = direction.angleDiff(winddirection).lt(ANGLE0);
    }

    @Override
    void nextStepDecision(Decision decision) throws IOException {
        if (newlegactions) {
            newlegactions = false;
//            if (marksupport.isEndOfCourse()) {
//                decision.setSTOP();
//                return;
//            }
//            marksupport.onNextLeg();
            calculateCurrentLegVariables();
        }
        limitsDecision(decision);
        calculateAllEnvironmentVariables();
        BoatElement parameters = parameters();
        if (toNextMark.getDistance() <= metrics.getLength() * 3) {
            // need three markrounding decisions?
            markroundingDecision(decision, parameters);
            return;
        }
        if (toNextMark.getAngle().abs().lt(metrics.getUpwindrelative())) {
            upwindDecision(decision);
            return;
        }
        if (toNextMark.getAngle().abs().gt(metrics.getDownwindrelative()) && parameters.isReachdownwind()) {
            reachdownwindDecision(decision, parameters);
            return;
        }
        offwindDecision(decision);
    }

    private void markroundingDecision(Decision decision, BoatElement parameters) throws IOException {
        TurnDirection turn = nextMark.isPort() ? ANTICLOCKWISE : CLOCKWISE;
        Angle nextMarktoBoat = new DistancePolar(nextMark.getLocation(), pos).getAngle();
        DistancePolar usespacer = isSailingonPort ? portspacer : starboardspacer;
        MarkElement followingmark = nextMark.nextMark();
        Location followingmarklocation;
        if (followingmark != null) {
            followingmarklocation = followingmark.getLocation();
            DistancePolar tofollowingmark = new DistancePolar(nextMark.getLocation(), followingmarklocation);

            if (nextMark.isPort()) {
                if (nextMarktoBoat.angleDiff(usespacer.getAngle()).isPositive()) {
                    decision.setMARKROUNDING(tofollowingmark.getAngle(), turn);
                }
            } else {
                if (nextMarktoBoat.angleDiff(usespacer.getAngle()).isNegative()) {
                    decision.setMARKROUNDING(tofollowingmark.getAngle(), turn);
                }
            }
        } else {
            if (nextMark.isPort()) {
                if (nextMarktoBoat.angleDiff(usespacer.getAngle()).isPositive()) {
                    decision.setMARKROUNDING(direction, turn); // straight on
                }
            } else {
                if (nextMarktoBoat.angleDiff(usespacer.getAngle()).isNegative()) {
                    decision.setMARKROUNDING(direction, turn); // straight on
                }
            }
        }
    }

    private void upwindDecision(Decision decision) throws IOException {
//        BoatElement parameters = parameters();
//        Channel channel = parameters.getUpwindchannel();
//        if (toNextMark.getDistance() > channel.getInneroffset(nextMark.getLocation()) * 1.5) {
//            if (!channel.isInchannel(pos)) {
//                tack(decision, winddirection);
//                return;
//            }
//        }
//        // check if need to tack onto best tack
//        if (parameters.isUpwindsailonbesttack() && tackingturn.equals(CLOCKWISE)) {
//            // on starboard
//            if (windAngleToMeanWind.isNegative()) {
//                tack(decision, winddirection);
//                return;
//            }
//        } else {
//            // on port
//            if (windAngleToMeanWind.gt(ANGLE0)) {
//                tack(decision, winddirection);
//                return;
//            }
//        }
//        // check if need to tack
//        if (parameters.isUpwindtackifheaded() && boatAngleToWind.lt(metrics.getUpwindrelative())) {
//            tack(decision, winddirection);
//        }
//        // check if need to bear away 
//        if (parameters.isUpwindbearawayifheaded() && boatAngleToWind.lt(metrics.getUpwindrelative())) {
//            bearawaytoclosehauled(decision, winddirection);
//        }
//        // check if need to luff
//        if (parameters.isUpwindluffupiflifted() && boatAngleToWind.gt(metrics.getUpwindrelative())) {
//            luffuptoclosehauled(decision, winddirection);
//        }
    }

    private void reachdownwindDecision(Decision decision, BoatElement parameters) throws IOException {
//        Channel channel = parameters.getDownwindchannel();
//        if (toNextMark.getDistance() > channel.getInneroffset(nextMark.getLocation()) * 1.5) {
//            if (!parameters.getDownwindchannel().isInchannel(pos)) {
//                gybe(decision, winddirection);
//            }
//        }
//        // check if need to gybe onto best tack
//        if (parameters.isDownwindsailonbestgybe()) {
//            if (tackingturn.equals(CLOCKWISE)) {
//                // on starboard
//                if (windAngleToMeanWind.gt(ANGLE0)) {
//                    gybe(decision, winddirection);
//                    return;
//                }
//            } else {
//                // on port
//                if (windAngleToMeanWind.isNegative()) {
//                    gybe(decision, winddirection);
//                    return;
//                }
//            }
//        }
//        // check if need to gybe
//        if (parameters.isDownwindgybeiflifted() && boatAngleToWind.gt(metrics.getDownwindrelative())) {
//            gybe(decision, winddirection);
//            return;
//        }
//        // check if need to luff
//        if (parameters.isDownwindluffupiflifted() && boatAngleToWind.gt(metrics.getDownwindrelative())) {
//            luffuptodownwind(decision, winddirection);
//            return;
//        }
//        // check if need to bear away 
//        if (parameters.isDownwindbearawayifheaded() && boatAngleToWind.lt(metrics.getDownwindrelative())) {
//            bearawaytodownwind(decision, winddirection);
//        }
    }

    private void offwindDecision(Decision decision) {
//        Angle nextDirection = toNextMark.add(directspacer).getAngle();
//        decision.setTURN(nextDirection, direction.gt(nextDirection) ? ANTICLOCKWISE : CLOCKWISE);
    }
}
