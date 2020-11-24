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
import java.util.function.Consumer;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg.LegType;
import uk.theretiredprogrammer.sketch.display.strategy.GybingDownwindPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.GybingDownwindPortSailingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.GybingDownwindStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.GybingDownwindStarboardSailingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.OffwindPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.OffwindSailingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.OffwindStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.RoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.SailingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.WindwardPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.WindwardPortSailingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.WindwardStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.strategy.WindwardStarboardSailingDecisions;

public class Strategy {

    private SailingDecisions starboarddecisions;
    private SailingDecisions portdecisions;
    private RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;
    private Angle portoffsetangle;
    private Angle starboardoffsetangle;
    private double offset;
    private Consumer<Params> timeintervalhandler;

    public Strategy() {
    }

    public Strategy(Strategy clonefrom) {
        this.setDecisions(clonefrom.starboarddecisions, clonefrom.portdecisions, clonefrom.roundingdecisions);
        this.useroundingdecisions = clonefrom.useroundingdecisions;
        this.setMarkOffset(clonefrom.offset, clonefrom.starboardoffsetangle, clonefrom.portoffsetangle);
    }

    public boolean hasStrategy() {
        return roundingdecisions != null;
    }

    public void setStrategy(Params params, CurrentLeg leg) {
        LegType legtype = leg.getLegType(params);
        switch (legtype) {
            case WINDWARD ->
                setWindwardStrategy(params);
            case OFFWIND ->
                setOffwindStrategy(params);
            case GYBINGDOWNWIND ->
                setGybingDownwindStrategy(params);
            default ->
                throw new IllegalStateFailure("Illegal/unknown LEGTYPE: " + legtype.toString());
        }
        useroundingdecisions = false;
    }

    private void setWindwardStrategy(Params params) {
        this.setTimeIntervalHandler(p -> windwardTimeInterval(p));
        Angle winddirection = params.markmeanwinddirection;
        Angle relative = params.upwindrelative;
        if (params.leg.isPortRounding()) {
            setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        LegType followinglegtype = params.leg.getFollowingLegType(params);
        switch (followinglegtype) {
            case OFFWIND ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg())
                        : new WindwardStarboardRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg());
            case GYBINGDOWNWIND ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> params.boat.getStarboardReachingCourse(windangle))
                        : new WindwardStarboardRoundingDecisions((windangle) -> params.boat.getPortReachingCourse(windangle));
            case NONE ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new WindwardPortRoundingDecisions((windangle) -> windangle.sub(90))
                        : new WindwardStarboardRoundingDecisions((windangle) -> windangle.plus(90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported WindwardRounding: " + followinglegtype.toString());
        }
        this.starboarddecisions = new WindwardStarboardSailingDecisions();
        this.portdecisions = new WindwardPortSailingDecisions();
    }

    private void setGybingDownwindStrategy(Params params) {
        this.setTimeIntervalHandler(p -> leewardTimeInterval(p));
        Angle winddirection = params.markmeanwinddirection;
        Angle relative = params.boat.metrics.downwindrelative;
        if (params.leg.isPortRounding()) {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        LegType followinglegtype = params.leg.getFollowingLegType(params);
        switch (followinglegtype) {
            case WINDWARD ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new GybingDownwindPortRoundingDecisions((windangle) -> params.boat.getPortCloseHauledCourse(windangle))
                        : new GybingDownwindStarboardRoundingDecisions((windangle) -> params.boat.getStarboardCloseHauledCourse(windangle));
            case OFFWIND ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg());
            case NONE ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> windangle.plus(90))
                        : new OffwindStarboardRoundingDecisions((windangle) -> windangle.sub(90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported LEGTYPE combination: Gybing downwind to "
                        + followinglegtype.toString());
        }
        this.starboarddecisions = new GybingDownwindStarboardSailingDecisions();
        this.portdecisions = new GybingDownwindPortSailingDecisions();
    }

    private void setOffwindStrategy(Params params) {
        this.setTimeIntervalHandler(p -> leewardTimeInterval(p));
        if (params.leg.isPortRounding()) {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, params.leg.getAngleofLeg().plus(90), params.leg.getAngleofLeg().plus(90));
        } else {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, params.leg.getAngleofLeg().sub(90), params.leg.getAngleofLeg().sub(90));
        }
        LegType followinglegtype = params.leg.getFollowingLegType(params);
        switch (followinglegtype) {
            case WINDWARD ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> params.boat.getPortCloseHauledCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> params.boat.getStarboardCloseHauledCourse(windangle));
            case OFFWIND ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg())
                        : new OffwindStarboardRoundingDecisions((windangle) -> params.leg.getAngleofFollowingLeg());
            case GYBINGDOWNWIND ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> params.boat.getPortReachingCourse(windangle))
                        : new OffwindStarboardRoundingDecisions((windangle) -> params.boat.getStarboardReachingCourse(windangle));
            case NONE ->
                roundingdecisions = params.leg.isPortRounding()
                        ? new OffwindPortRoundingDecisions((windangle) -> windangle.plus(90))
                        : new OffwindStarboardRoundingDecisions((windangle) -> windangle.sub(90));
            default ->
                throw new IllegalStateFailure("Illegal/unknown/Unsupported LEGTYPE combination: Offwind to "
                        + followinglegtype.toString());
        }
        this.starboarddecisions = new OffwindSailingDecisions();
        this.portdecisions = new OffwindSailingDecisions();
    }

    void setAfterFinishStrategy(Params params) {
        this.setMarkOffset(0, params.markmeanwinddirection, params.markmeanwinddirection);
        this.setTimeIntervalHandler(p -> afterFinishingTimeInterval(p));
        useroundingdecisions = false;
    }

    private void afterFinishingTimeInterval(Params params) {
        double fromfinishmark = params.location.to(params.marklocation);
        if (fromfinishmark > params.boat.metrics.getLength() * 5) {
            params.setSTOP();
        } else {
            params.setSAILON();
        }
    }

    private void setTimeIntervalHandler(Consumer<Params> timeintervalhandler) {
        this.timeintervalhandler = timeintervalhandler;
    }

    private void setDecisions(SailingDecisions starboarddecisions, SailingDecisions portdecisions, RoundingDecisions roundingdecisions) {
        this.starboarddecisions = starboarddecisions;
        this.portdecisions = portdecisions;
        this.roundingdecisions = roundingdecisions;
    }

    private void setMarkOffset(double offset, Angle starboardoffsetangle, Angle portoffsetangle) {
        this.offset = offset;
        this.starboardoffsetangle = starboardoffsetangle;
        this.portoffsetangle = portoffsetangle;
    }

    DistanceVector getOffsetVector(boolean onPort) {
        return new DistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }

    public void strategyTimeInterval(Params params) {
        timeintervalhandler.accept(params);
    }

    private void windwardTimeInterval(Params params) {
        if (useroundingdecisions) {
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        if (isNear2WindwardMark(params)) {
            useroundingdecisions = true;
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }

    private void leewardTimeInterval(Params params) {
        if (useroundingdecisions) {
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        if (isNear2LeewardMark(params)) {
            useroundingdecisions = true;
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }

    public boolean isNear2WindwardMark(Params params) {
        Optional<Double> refdistance = getRefDistance(params.boat.getLocation(), params.marklocation, params.markmeanwinddirection.get());
        return refdistance.isPresent() ? refdistance.get() <= params.boat.metrics.getLength() * 5 : true;
    }

    public boolean isNear2LeewardMark(Params params) {
        Optional<Double> refdistance = getRefDistance(params.boat.getLocation(), params.marklocation, params.markmeanwinddirection.sub(180).get());
        return refdistance.isPresent() ? refdistance.get() <= params.boat.metrics.getLength() * 5 : true;
    }

    private Optional<Double> getRefDistance(Location location, Location marklocation, Angle refangle) {
        return getRefDistance(location, marklocation, refangle.get());
    }

    public static  Optional<Double> getRefDistance(Location location, Location marklocation, double refangle) {
        DistanceVector tomark = new DistanceVector(location, marklocation);
        Angle refangle2mark = tomark.getDegreesProperty().absDegreesDiff(refangle);
        if (refangle2mark.gt(90)) {
            return Optional.empty();
        }
        return Optional.of(tomark.getDistance() * Math.cos(refangle2mark.getRadians()));
    }
}
