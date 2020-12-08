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
package uk.theretiredprogrammer.sketch.log.entity;

import uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance;

public class WindShiftLogEntry extends TimerLogEntry {

    private final double windshift;
    private final Importance importance;

    public WindShiftLogEntry(double shift, Importance importance) {
        this.windshift = shift;
        this.importance = importance;
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
    
    @Override
    public boolean hasMajorImportance() {
        return importance == Importance.MAJOR;
    }

    @Override
    public boolean hasMajorMinorImportance() {
        return importance == Importance.MAJOR || importance == Importance.MINOR;
    }
}
