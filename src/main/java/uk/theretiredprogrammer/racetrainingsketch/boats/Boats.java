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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import uk.theretiredprogrammer.racetrainingsketch.course.CourseLeg;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;
import uk.theretiredprogrammer.racetrainingsketch.ui.Scenario;
import uk.theretiredprogrammer.racetrainingsketch.ui.Displayable;
import uk.theretiredprogrammer.racetrainingsketch.ui.Timerable;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Boats implements Displayable, Timerable {

    private final Map<String, Boat> boats = new HashMap<>();

    public Boats(JsonObject parsedjson, Scenario scenario, CourseLeg firstcourseleg) throws IOException {
        JsonArray boatarray = parsedjson.getJsonArray("BOATS");
        if (boatarray == null) {
            throw new IOException("Malformed Definition File - missing BOATS array");
        }
        for (JsonValue boatv : boatarray) {
            if (boatv.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject boatparams = (JsonObject) boatv;
                Boat boat = BoatFactory.createboatelement(boatparams, scenario, firstcourseleg);
                boats.put(boat.getName(), boat);
            } else {
                throw new IOException("Malformed Definition File - BOATS array contains items other that boat objects");
            }
        }
    }
    
    public Boat getBoat(String name){
        return boats.get(name);
    }

    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog ) throws IOException {
        for (var boatentry : boats.entrySet()) {
            boatentry.getValue().timerAdvance(simulationtime, timerlog);
        }
    }

    @Override
    public void draw(Graphics2D g2D, double zoom) throws IOException {
        for (var boatentry : boats.entrySet()) {
            boatentry.getValue().draw(g2D, zoom);
        }
    }
}
