/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class BoatFactory {

    public static Boat createBoat(String type, CurrentLeg firstleg, WindFlow windflow, WaterFlow waterflow) {
        switch (type) {
            case "laser2" -> {
                return new Laser2(firstleg, windflow, waterflow);
            }
            default ->
                throw new IllegalStateFailure("Missing or Unknown class parameter in boat definition");
        }
    }
    
    public static Boat createBoat(String type, PropertyLocation location, CurrentLeg firstleg, WindFlow windflow, WaterFlow waterflow) {
        switch (type) {
            case "laser2" -> {
                return new Laser2(location, firstleg, windflow, waterflow);
            }
            default ->
                throw new IllegalStateFailure("Missing or Unknown class parameter in boat definition");
        }
    }
    
    public static Boat cloneBoat(String newname, Boat clonefrom) {
        switch (clonefrom.getType()) {
            case "laser2" -> {
                return new Laser2(newname, (Laser2) clonefrom);
            }
            default ->
                throw new IllegalStateFailure("Missing or Unknown class parameter in clonefrom boat definition");
        }
    }
}
