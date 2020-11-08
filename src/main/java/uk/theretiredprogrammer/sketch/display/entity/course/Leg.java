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

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class Leg {

    private final PropertyLocation startfrom;
    private final PropertyLocation endat;
    private final boolean portrounding;
    private Leg followingleg;

    public Leg(PropertyLocation startfrom, PropertyLocation endat, boolean portrounding, Leg followingleg) {
        this.startfrom = startfrom;
        this.endat = endat;
        this.portrounding = portrounding;
        this.followingleg = followingleg;
    }

    public Leg getFollowingLeg() {
        return followingleg;
    }

    public void setFollowingLeg(Leg leg) {
        followingleg = leg;
    }

    public boolean isPortRounding() {
        return portrounding;
    }

    public double getDistanceToEnd(PropertyLocation here) {
        return here.to(endat);
    }

    public PropertyLocation getEndLocation() {
        return endat;
    }

    public PropertyDegrees endLegMeanwinddirection(WindFlow windflow) {
        return windflow.getMeanFlowAngle(endat);
    }

    public PropertyDegrees getAngleofLeg() {
        return startfrom.angleto(endat);
    }
}
