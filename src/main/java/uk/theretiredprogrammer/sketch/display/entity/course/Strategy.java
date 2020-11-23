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

import java.util.function.Consumer;
import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
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

    public void setWindwardStrategy(Params params) {
        this.setTimeIntervalHandler(p -> windwardTimeInterval(p));
        Angle winddirection = params.markmeanwinddirection;
        Angle relative = params.upwindrelative;
        if (params.leg.isPortRounding()) {
            setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        CurrentLeg.LegType followinglegtype = CurrentLeg.getLegType(params.boat.metrics, params.leg.getAngleofFollowingLeg(), params.windflow, params.boat.isReachdownwind());
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

    public void setGybingDownwindStrategy(Params params) {
        this.setTimeIntervalHandler(p -> leewardTimeInterval(p));
        Angle winddirection = params.markmeanwinddirection;
        Angle relative = params.boat.metrics.downwindrelative;
        if (params.leg.isPortRounding()) {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.plus(90).sub(relative), winddirection.plus(90).plus(relative));
        } else {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, winddirection.sub(90).sub(relative), winddirection.sub(90).plus(relative));
        }
        CurrentLeg.LegType followinglegtype = CurrentLeg.getLegType(params.boat.metrics, params.leg.getAngleofFollowingLeg(), params.windflow, params.boat.isReachdownwind());
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

    public void setOffwindStrategy(Params params) {
        this.setTimeIntervalHandler(p -> leewardTimeInterval(p));
        if (params.leg.isPortRounding()) {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, params.leg.getAngleofLeg().plus(90), params.leg.getAngleofLeg().plus(90));
        } else {
            this.setMarkOffset(params.boat.metrics.getWidth() * 2, params.leg.getAngleofLeg().sub(90), params.leg.getAngleofLeg().sub(90));
        }
        CurrentLeg.LegType followinglegtype = CurrentLeg.getLegType(params.boat.metrics, params.leg.getAngleofFollowingLeg(), params.windflow, params.boat.isReachdownwind());
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

    public void setAfterFinishStrategy(Params params) {
        this.setMarkOffset(0, params.markmeanwinddirection, params.markmeanwinddirection);
        this.setTimeIntervalHandler(p -> afterFinishingTimeInterval(p));
    }

    public void afterFinishingTimeInterval(Params params) {
        double fromfinishmark = params.location.to(params.marklocation);
        if (fromfinishmark > params.boat.metrics.getLength() * 5) {
            params.setSTOP();
        } else {
            params.setSAILON();
        }
    }

    public final void setTimeIntervalHandler(Consumer<Params> timeintervalhandler) {
        this.timeintervalhandler = timeintervalhandler;
    }

    public final void setDecisions(SailingDecisions starboarddecisions, SailingDecisions portdecisions, RoundingDecisions roundingdecisions) {
        this.starboarddecisions = starboarddecisions;
        this.portdecisions = portdecisions;
        this.roundingdecisions = roundingdecisions;
    }

    public final void setMarkOffset(double offset, Angle starboardoffsetangle, Angle portoffsetangle) {
        this.offset = offset;
        this.starboardoffsetangle = starboardoffsetangle;
        this.portoffsetangle = portoffsetangle;
    }

    public DistanceVector getOffsetVector(boolean onPort) {
        return new DistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }

    public void strategyTimeInterval(Params params) {
        timeintervalhandler.accept(params);
    }

    public void windwardTimeInterval(Params params) {
        if (useroundingdecisions) {
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        if (params.leg.isNear2WindwardMark(params.boat, params.markmeanwinddirection)) {
            useroundingdecisions = true;
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }

    public void leewardTimeInterval(Params params) {
        if (useroundingdecisions) {
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        if (params.leg.isNear2LeewardMark(params.boat, params.markmeanwinddirection)) {
            useroundingdecisions = true;
            roundingdecisions.nextTimeInterval(params);
            return;
        }
        (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }
}
