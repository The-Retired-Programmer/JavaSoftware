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

import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class AfterFinishSailingStrategy extends SailingLegStrategy {

    @Override
    String nextTimeInterval(Decision decision, Boat boat, CourseLegWithStrategy leg, Angle winddirection) {
        double fromfinishmark = boat.getLocation().to(leg.getEndLocation());
        if (fromfinishmark > boat.getMetrics().getLength()*5) {
            decision.setSTOP();
            return "stoping at end of course";
        } else {
            decision.setSAILON();
            return "Sail ON";
        }
    }
    @Override
    boolean applyRoundingStrategy(CourseLegWithStrategy leg, Boat boat, Angle winddirection) {
        return false; // no rounding stategy after finsihing
    }
}
