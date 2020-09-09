/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.upgraders;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;

/**
 *
 * @author richard
 */
public class Upgrader0 extends Upgrader {

    @Override
    public JsonObject upgrade(JsonObject root) throws IOException {
        JsonObject titleobj = root.getJsonObject("TITLE");
        String title = "";
        if (titleobj != null) {
            title = titleobj.getString("title", "");
        }
        JsonObjectBuilder meta = Json.createObjectBuilder();
        if (!title.isEmpty()) {
            meta.add("title", title);
        }
        //
        JsonObject newroot = Json.createObjectBuilder()
                .add("type", "sketch-v1")
                .add("meta", meta)
                .build();
        //
        newroot = copyobj(root, "DISPLAY", newroot, Json.createPointer("/display"));
        newroot = movesailingareaintodisplay(root, newroot);
        newroot = copyobj(root, "WIND-SHIFTS", newroot, Json.createPointer("/windshifts"));
        newroot = addarray(updateSpeedDirectionPairs2PolarsAndLocationWidthHeight2Area(root, "WIND"), newroot, Json.createPointer("/wind"));
        newroot = copyobj(root, "WATER-SHIFTS", newroot, Json.createPointer("/watershifts"));
        newroot = addarray(updateSpeedDirectionPairs2PolarsAndLocationWidthHeight2Area(root, "WATER"), newroot, Json.createPointer("/water"));
        newroot = copyobj(root, "COURSE", newroot, Json.createPointer("/course"));
        newroot = copyarray(root, "MARKS", newroot, Json.createPointer("/marks"));
        newroot = copyarray(root, "BOATS", newroot, Json.createPointer("/boats"));
        //
        return newroot;
    }

    private JsonObject movesailingareaintodisplay(JsonObject root, JsonObject newroot) {
        JsonObject sailingareaobj = root.getJsonObject("SAILING AREA");
        int west = sailingareaobj.getInt("west", -500);
        int east = sailingareaobj.getInt("east", -500);
        int north = sailingareaobj.getInt("north", 500);
        int south = sailingareaobj.getInt("south", -500);
        Area displayarea = new Area(new Location(west, south), east - west, north - south);
        JsonArray displayareajson = displayarea.toJson();
        JsonPointer inserthere = Json.createPointer("/display/displayarea");
        newroot = inserthere.add(newroot, displayareajson);
        //
        int westlimit = sailingareaobj.getInt("westlimit", west);
        int eastlimit = sailingareaobj.getInt("eastlimit", east);
        int northlimit = sailingareaobj.getInt("northlimit", north);
        int southlimit = sailingareaobj.getInt("southlimit", south);
        Area sailingarea = new Area(new Location(westlimit, southlimit), eastlimit - westlimit, northlimit - southlimit);
        if (!sailingarea.equals(displayarea)) {
            JsonArray sailingareajson = sailingarea.toJson();
            JsonPointer insert2here = Json.createPointer("/display/sailingarea");
            newroot = insert2here.add(newroot, sailingareajson);
        }
        return newroot;
    }

    private JsonArray updateSpeedDirectionPairs2PolarsAndLocationWidthHeight2Area(JsonObject root, String oldname) throws IOException {
        JsonArray array = root.getJsonArray(oldname);
        if (array == null) {
            return null;
        }
        JsonArray newarray = Json.createArrayBuilder().build();
        JsonObject newobj;
        JsonPointer inserthere;
        JsonPointer removehere;
        for (JsonValue item : array) {
            if (item.getValueType() == ValueType.OBJECT) {
                JsonObject jobj = (JsonObject) item;
                String type = jobj.getString("type", "UNKNOWN");
                switch (type) {
                    case "constantflow", "testflow" -> {
                        double speed = jobj.getJsonNumber("speed").doubleValue();
                        double direction = jobj.getJsonNumber("from").doubleValue();
                        SpeedPolar flow = new SpeedPolar(speed, new Angle(direction));
                        inserthere = Json.createPointer("/flow");
                        newobj = inserthere.add(jobj, flow.toJson());
                        removehere = Json.createPointer("/speed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/from");
                        newobj = removehere.remove(newobj);
                    }
                    case "complexflow" -> {
                        double northwestspeed = jobj.getJsonNumber("northwestspeed").doubleValue();
                        double northwestdirection = jobj.getJsonNumber("northwestfrom").doubleValue();
                        SpeedPolar northwestflow = new SpeedPolar(northwestspeed, new Angle(northwestdirection));
                        inserthere = Json.createPointer("/northwestflow");
                        newobj = inserthere.add(jobj, northwestflow.toJson());
                        removehere = Json.createPointer("/northwestspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/northwestfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double northeastspeed = jobj.getJsonNumber("northeastspeed").doubleValue();
                        double northeastdirection = jobj.getJsonNumber("northeastfrom").doubleValue();
                        SpeedPolar northeastflow = new SpeedPolar(northeastspeed, new Angle(northeastdirection));
                        inserthere = Json.createPointer("/northeastflow");
                        newobj = inserthere.add(newobj, northeastflow.toJson());
                        removehere = Json.createPointer("/northeastspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/northeastfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double southwestspeed = jobj.getJsonNumber("southwestspeed").doubleValue();
                        double southwestdirection = jobj.getJsonNumber("southwestfrom").doubleValue();
                        SpeedPolar southwestflow = new SpeedPolar(southwestspeed, new Angle(southwestdirection));
                        inserthere = Json.createPointer("/southwestflow");
                        newobj = inserthere.add(newobj, southwestflow.toJson());
                        removehere = Json.createPointer("/southwestspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/southwestfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double southeastspeed = jobj.getJsonNumber("southeastspeed").doubleValue();
                        double southeastdirection = jobj.getJsonNumber("southeastfrom").doubleValue();
                        SpeedPolar southeastflow = new SpeedPolar(southeastspeed, new Angle(southeastdirection));
                        inserthere = Json.createPointer("/southeastflow");
                        newobj = inserthere.add(newobj, southeastflow.toJson());
                        removehere = Json.createPointer("/southeastspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/southeastfrom");
                        newobj = removehere.remove(newobj);
                    }
                    default ->
                        throw new IOException("Oh dear - miised that case for Flow (" + type + ")");
                }
                // change location / width / height to area
                JsonArray loc = jobj.getJsonArray("location");
                if (loc != null) {
                    double x = loc.getJsonNumber(0).doubleValue();
                    double y = loc.getJsonNumber(1).doubleValue();
                    double w = jobj.getJsonNumber("width").doubleValue();
                    double h = jobj.getJsonNumber("height").doubleValue();
                    Area area = new Area(x, y, w, h);
                    inserthere = Json.createPointer("/area");
                    newobj = inserthere.add(newobj, area.toJson());
                    removehere = Json.createPointer("/location");
                    newobj = removehere.remove(newobj);
                    removehere = Json.createPointer("/width");
                    newobj = removehere.remove(newobj);
                    removehere = Json.createPointer("/height");
                    newobj = removehere.remove(newobj);
                }
                //
                inserthere = Json.createPointer("/-");
                newarray = inserthere.add(newarray, newobj);
            } else {
                inserthere = Json.createPointer("/-");
                newarray = inserthere.add(newarray, item);
            }
        }
        return newarray;
    }
}
