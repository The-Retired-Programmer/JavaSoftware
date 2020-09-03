/*
 * Copyright 2020 richard Linsdale.
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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import uk.theretiredprogrammer.sketch.jfx.DisplaySurface;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.Displayable;
import uk.theretiredprogrammer.sketch.ui.Timerable;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Boats implements Displayable, Timerable {

    private final Map<String, Boat> boats = new HashMap<>();

    public Boats(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray boatarray = parsedjson.getJsonArray("BOATS");
        if (boatarray == null) {
            throw new IOException("Malformed Definition File - missing BOATS array");
        }
        for (JsonValue boatv : boatarray) {
            if (boatv.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject boatparams = (JsonObject) boatv;
                Boat boat = BoatFactory.createboatelement(controllersupplier, boatparams);
                boats.put(boat.name, boat);
            } else {
                throw new IOException("Malformed Definition File - BOATS array contains items other that boat objects");
            }
        }
        
    }
    
    public Boat getBoat(String name){
        return boats.get(name);
    }
    
    public Collection<Boat> getBoats() {
        return boats.values();
    }

    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog ) throws IOException {
    }

    @Override
    public void draw(DisplaySurface canvas, double zoom) throws IOException {
        for (var boat : boats.values()) {
            boat.draw(canvas, zoom);
        }
    }
}
