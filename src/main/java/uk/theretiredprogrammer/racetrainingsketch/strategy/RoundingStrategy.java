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

import java.util.function.BiFunction;
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class RoundingStrategy extends SailingStrategy {
    
    private final double clearance;
    final BiFunction<Boolean, Angle, Angle> getOffsetAngle;

    RoundingStrategy(double clearance, BiFunction<Boolean, Angle, Angle> getOffsetAngle) {
        this.clearance = clearance;
        this.getOffsetAngle = getOffsetAngle;
    }
    
    final DistancePolar getOffset(boolean onPort, Angle winddirection, CourseLeg leg) {
        return new DistancePolar(clearance, getOffsetAngle.apply(onPort,  winddirection));
    }
}
