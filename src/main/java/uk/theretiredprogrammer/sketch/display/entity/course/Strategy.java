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

import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.display.control.strategy.GybingDownwindPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.GybingDownwindPortSailingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.GybingDownwindStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.GybingDownwindStarboardSailingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.OffwindPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.OffwindSailingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.OffwindStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.RoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.SailingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.WindwardPortRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.WindwardPortSailingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.WindwardStarboardRoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.WindwardStarboardSailingDecisions;

public class Strategy {

    private SailingDecisions starboarddecisions;
    private SailingDecisions portdecisions;
    private RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;
    private PropertyDegrees portoffsetangle;
    private PropertyDegrees starboardoffsetangle;
    private double offset;
    private Function<Params, String> timeintervalhandler;

    public Strategy() {
    }

    public Strategy(Strategy clonefrom) {
        this.setDecisions(clonefrom.starboarddecisions, clonefrom.portdecisions, clonefrom.roundingdecisions);
        this.useroundingdecisions = clonefrom.useroundingdecisions;
        this.setMarkOffset(clonefrom.offset, clonefrom.starboardoffsetangle, clonefrom.portoffsetangle);
    }

    public void setWindwardStrategy(Params params) {
        this.setTimeIntervalHandler(p -> windwardTimeInterval(p));
        PropertyDegrees winddirection = params.markmeanwinddirection;
        PropertyDegrees relative = params.upwindrelative;
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
        PropertyDegrees winddirection = params.markmeanwinddirection;
        PropertyDegrees relative = params.boat.metrics.downwindrelative;
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

    public String afterFinishingTimeInterval(Params params) {
        double fromfinishmark = params.location.to(params.marklocation);
        if (fromfinishmark > params.boat.metrics.getLength() * 5) {
            params.setSTOP();
            return "Stopping at end of course";
        } else {
            params.setSAILON();
            return "Sail ON";
        }
    }

    public final void setTimeIntervalHandler(Function<Params, String> timeintervalhandler) {
        this.timeintervalhandler = timeintervalhandler;
    }

    public final void setDecisions(SailingDecisions starboarddecisions, SailingDecisions portdecisions, RoundingDecisions roundingdecisions) {
        this.starboarddecisions = starboarddecisions;
        this.portdecisions = portdecisions;
        this.roundingdecisions = roundingdecisions;
    }

    public final void setMarkOffset(double offset, PropertyDegrees starboardoffsetangle, PropertyDegrees portoffsetangle) {
        this.offset = offset;
        this.starboardoffsetangle = starboardoffsetangle;
        this.portoffsetangle = portoffsetangle;
    }

    public PropertyDistanceVector getOffsetVector(boolean onPort) {
        return new PropertyDistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }

    public String strategyTimeInterval(Params params) {
        return timeintervalhandler.apply(params);
    }

    public String windwardTimeInterval(Params params) {
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(params);
        }
        if (params.leg.isNear2WindwardMark(params.boat, params.markmeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(params);
        }
        return (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }

    public String leewardTimeInterval(Params params) {
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(params);
        }
        if (params.leg.isNear2LeewardMark(params.boat, params.markmeanwinddirection)) {
            useroundingdecisions = true;
            return roundingdecisions.nextTimeInterval(params);

        }
        return (params.isPort ? portdecisions : starboarddecisions).nextTimeInterval(params);
    }
}
