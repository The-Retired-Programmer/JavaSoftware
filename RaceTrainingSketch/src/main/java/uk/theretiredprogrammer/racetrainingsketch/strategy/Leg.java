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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.function.Supplier;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

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

    public Leg(Supplier<Controller> controllersupplier, Location startfrom, Location marklocation, boolean portrounding, Leg followingleg) {
        this.controllersupplier = controllersupplier;
        this.startfrom = startfrom;
        this.marklocation = marklocation;
        this.portrounding = portrounding;
        this.followingleg = followingleg;
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
