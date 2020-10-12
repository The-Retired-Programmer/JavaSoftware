/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.sketch.boats;

import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.properties.PropertyBoat;
import uk.theretiredprogrammer.sketch.controller.Controller;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatFactory {

    public static Boat createBoat(PropertyBoat boatproperty, PropertySketch sketchproperty) throws IOException {
        switch (boatproperty.getType()) {
            case "laser2" -> {
                return new Laser2(boatproperty, sketchproperty);
            }
            default ->
                throw new IOException("Missing or Unknown class parameter in boat definition");
        }
    }
}
