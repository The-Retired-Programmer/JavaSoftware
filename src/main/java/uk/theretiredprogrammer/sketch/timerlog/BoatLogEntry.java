/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.timerlog;

import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.properties.PropertyBoat;

/**
 *
 * @author richard
 */
public class BoatLogEntry extends TimerLogEntry {

    private final double boatangle;
    private final double boatx;
    private final double boaty;
    private final String boatname;

    public BoatLogEntry(PropertyBoat boatproperty) {
        Location loc = boatproperty.getLocation();
        this.boatx = loc.getX();
        this.boaty = loc.getY();
        this.boatangle = boatproperty.getDirection().getDegrees();
        this.boatname = boatproperty.getName();
    }

    @Override
    public boolean hasName(String name) {
        return boatname.equals(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("  BOAT (");
        sb.append(boatname);
        sb.append("): [");
        sb.append(format2dp(boatx));
        sb.append(",");
        sb.append(format2dp(boaty));
        sb.append("] ");
        sb.append(format1dp(boatangle));
        sb.append("°");
        return sb.toString();
    }
}
