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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MINOR;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;

public class OffwindSailingDecisions extends SailingDecisions {

    @Override
    public boolean nextTimeInterval(Params params) {
        PropertyDegrees nextDirection = params.angleToSailToMark();
        if (nextDirection.neq(params.heading)) {
            return params.setTURN(nextDirection, params.heading.gt(nextDirection), MINOR, "Adjust direction to sailin directly to mark (offwind sailing)");
        }
        return params.setSAILON();
    }
}
