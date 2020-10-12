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
package uk.theretiredprogrammer.sketch.strategy;

import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.properties.PropertyMark;
import uk.theretiredprogrammer.sketch.flows.WindFlow;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Leg {

    private final Location startfrom;
    private final Location marklocation;
    private final boolean portrounding;
    private Leg followingleg;

    public Leg(Location startfrom, PropertyMark markproperties, boolean portrounding, Leg followingleg) {
        this.startfrom = startfrom;
        this.marklocation = markproperties.getLocation();
        this.portrounding = portrounding;
        this.followingleg = followingleg;
    }

    public Leg getFollowingLeg() {
        return followingleg;
    }

    public void setFollowingLeg(Leg leg) {
        followingleg = leg;
    }

    boolean isPortRounding() {
        return portrounding;
    }

    double getDistanceToEnd(Location here) {
        return here.to(marklocation);
    }

    public Location getEndLocation() {
        return marklocation;
    }

    Angle getMarkMeanwinddirection(WindFlow windflow) {
        return windflow.getMeanFlowAngle(marklocation);
    }

    Angle getAngleofLeg() {
        return startfrom.angleto(marklocation);
    }
}
