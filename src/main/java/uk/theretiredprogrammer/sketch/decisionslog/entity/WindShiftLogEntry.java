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
package uk.theretiredprogrammer.sketch.decisionslog.entity;

import uk.theretiredprogrammer.sketch.core.entity.Angle;

/**
 *
 * @author richard
 */
public class WindShiftLogEntry extends TimerLogEntry {

    private final double windshift;

    public WindShiftLogEntry(Angle shift) {
        this.windshift = shift.getDegrees();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("  WINDSHIFT: ");
        sb.append(format1dp(windshift));
        sb.append("°");
        return sb.toString();
    }
}
