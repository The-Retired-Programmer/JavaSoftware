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

import java.text.DecimalFormat;

public abstract class TimerLogEntry {

    String mmsstime;
    static final DecimalFormat dp1 = new DecimalFormat("#.#");
    static final DecimalFormat dp2 = new DecimalFormat("#.##");
    static final DecimalFormat dp3 = new DecimalFormat("#.###");

    String format1dp(double value) {
        return dp1.format(value);
    }

    String format2dp(double value) {
        return dp2.format(value);
    }

    String format3dp(double value) {
        return dp3.format(value);
    }

    public void setTime(String mmsstime) {
        this.mmsstime = mmsstime;
    }

    @Override
    public String toString() {
        return mmsstime;
    }

    public boolean hasName(String name) {
        return true;
    }
    
    public boolean hasMajorImportance() {
        return true;
    }
    public boolean hasMajorMinorImportance() {
        return true;
    }
}
