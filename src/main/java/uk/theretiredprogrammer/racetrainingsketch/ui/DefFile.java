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
package uk.theretiredprogrammer.racetrainingsketch.ui;

import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.openide.filesystems.FileObject;

/**
 * The Definition file parser.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DefFile {

    private final FileObject fo;
    private final String name;

    public DefFile(FileObject fo) {
        this.fo = fo;
        this.name = null;
    }

    public DefFile(String name) {
        this.name = name;
        this.fo = null;
    }

    public ScenarioElement parse() throws IOException {
        JsonObject parsedjson;
        try ( JsonReader rdr = Json.createReader(name == null ? fo.getInputStream() : this.getClass().getResourceAsStream(name))) {
            parsedjson = rdr.readObject();
        }
        ScenarioElement scenario;
        JsonObject extraction;
        JsonObject scenarioobj = parsedjson.getJsonObject("DISPLAY");
        if (scenarioobj == null) {
            throw new IOException("Malformed Definition File - missing DISPLAY object");
        }
        scenario = new ScenarioElement(scenarioobj);
        extraction = parsedjson.getJsonObject("WATER");
        if (extraction != null) {
            scenario.setWater(extraction);
        }
        extraction = parsedjson.getJsonObject("WIND");
        if (extraction == null) {
            throw new IOException("Malformed Definition File - missing WIND object");
        }
        scenario.setWind(extraction);
        JsonArray markarray = parsedjson.getJsonArray("MARKS");
        if (markarray != null) {
            for (JsonValue markv : markarray) {
                if (markv.getValueType() == ValueType.OBJECT) {
                    JsonObject mark = (JsonObject) markv;
                    scenario.addMark(mark);
                } else {
                    throw new IOException("Malformed Definition File - MARKS array contains items other that mark objects");
                }
            }
        }
        JsonObject courseobj = parsedjson.getJsonObject("COURSE");
        if (courseobj == null) {
            throw new IOException("Malformed Definition File - missing COURSE object");
        }
        scenario.setCourse(courseobj);
        JsonArray boatarray = parsedjson.getJsonArray("BOATS");
        if (boatarray == null) {
            throw new IOException("Malformed Definition File - missing BOATS array");
        }
        for (JsonValue boatv : boatarray) {
            if (boatv.getValueType() == ValueType.OBJECT) {
                JsonObject boat = (JsonObject) boatv;
                scenario.addBoat(boat);
            } else {
                throw new IOException("Malformed Definition File - BOATS array contains items other that boat objects");
            }
        }
        return scenario;
    }
}
