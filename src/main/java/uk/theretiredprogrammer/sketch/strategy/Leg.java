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

import java.io.IOException;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.LegValue;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyLegValue;
import uk.theretiredprogrammer.sketch.course.Mark;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Leg {

    private final Location startfrom;
    private final Location marklocation;
    private final boolean portrounding;
    private final Leg followingleg;
    private final Supplier<Controller> controllersupplier;
    private final PropertyLegValue legproperty = new PropertyLegValue();

    public Leg(Supplier<Controller> controllersupplier, Location startfrom, Mark mark, boolean portrounding, Leg followingleg) throws IOException {
        this.controllersupplier = controllersupplier;
        this.startfrom = startfrom;
        this.marklocation = mark.getLocation();
        this.portrounding = portrounding;
        this.followingleg = followingleg;
        this.legproperty.set(new LegValue(mark.getName(), portrounding ? "port" : "starboard"));
    }

    @Override
    public final String toString() {
        return startfrom + "->" + marklocation + (portrounding ? " to Port" : " to Starboard");
    }

    public PropertyLegValue getProperty() {
        return legproperty;
    }

    public Leg getFollowingLeg() {
        return followingleg;
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

    Angle getMarkMeanwinddirection() throws IOException {
        return controllersupplier.get().windflow.getMeanFlowAngle(marklocation);
    }

    Angle getAngleofLeg() {
        return startfrom.angleto(marklocation);
    }
}
