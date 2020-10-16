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
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

/**
 *
 * @author richard
 */
public class Upgrader0 extends Upgrader {

    @Override
    public JsonObject upgrade(JsonObject root) {
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
        JsonArray displayareajson = area2Json(west, south, east - west, north - south);
        JsonPointer inserthere = Json.createPointer("/display/displayarea");
        newroot = inserthere.add(newroot, displayareajson);
        //
        int westlimit = sailingareaobj.getInt("westlimit", west);
        int eastlimit = sailingareaobj.getInt("eastlimit", east);
        int northlimit = sailingareaobj.getInt("northlimit", north);
        int southlimit = sailingareaobj.getInt("southlimit", south);
        if (!(west == westlimit && east == eastlimit && north == northlimit && south == southlimit)) {
            JsonArray sailingareajson = area2Json(westlimit, southlimit, eastlimit - westlimit, northlimit - southlimit);
            JsonPointer insert2here = Json.createPointer("/display/sailingarea");
            newroot = insert2here.add(newroot, sailingareajson);
        }
        return newroot;
    }

    private JsonArray updateSpeedDirectionPairs2PolarsAndLocationWidthHeight2Area(JsonObject root, String oldname) {
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
                        inserthere = Json.createPointer("/flow");
                        newobj = inserthere.add(jobj, flow2Json(speed, direction));
                        removehere = Json.createPointer("/speed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/from");
                        newobj = removehere.remove(newobj);
                    }
                    case "complexflow" -> {
                        double northwestspeed = jobj.getJsonNumber("northwestspeed").doubleValue();
                        double northwestdirection = jobj.getJsonNumber("northwestfrom").doubleValue();
                        inserthere = Json.createPointer("/northwestflow");
                        newobj = inserthere.add(jobj, flow2Json(northwestspeed, northwestdirection));
                        removehere = Json.createPointer("/northwestspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/northwestfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double northeastspeed = jobj.getJsonNumber("northeastspeed").doubleValue();
                        double northeastdirection = jobj.getJsonNumber("northeastfrom").doubleValue();
                        inserthere = Json.createPointer("/northeastflow");
                        newobj = inserthere.add(newobj, flow2Json(northeastspeed, northeastdirection));
                        removehere = Json.createPointer("/northeastspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/northeastfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double southwestspeed = jobj.getJsonNumber("southwestspeed").doubleValue();
                        double southwestdirection = jobj.getJsonNumber("southwestfrom").doubleValue();
                        inserthere = Json.createPointer("/southwestflow");
                        newobj = inserthere.add(newobj, flow2Json(southwestspeed, southwestdirection));
                        removehere = Json.createPointer("/southwestspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/southwestfrom");
                        newobj = removehere.remove(newobj);
                        //
                        double southeastspeed = jobj.getJsonNumber("southeastspeed").doubleValue();
                        double southeastdirection = jobj.getJsonNumber("southeastfrom").doubleValue();
                        inserthere = Json.createPointer("/southeastflow");
                        newobj = inserthere.add(newobj, flow2Json(southeastspeed, southeastdirection));
                        removehere = Json.createPointer("/southeastspeed");
                        newobj = removehere.remove(newobj);
                        removehere = Json.createPointer("/southeastfrom");
                        newobj = removehere.remove(newobj);
                    }
                    default ->
                        throw new ParseFailure("Upgrader0 - illegal type for a flow object (" + type + ")");
                }
                // change location / width / height to area
                JsonArray loc = jobj.getJsonArray("location");
                if (loc != null) {
                    double x = loc.getJsonNumber(0).doubleValue();
                    double y = loc.getJsonNumber(1).doubleValue();
                    double w = jobj.getJsonNumber("width").doubleValue();
                    double h = jobj.getJsonNumber("height").doubleValue();
                    inserthere = Json.createPointer("/area");
                    newobj = inserthere.add(newobj, area2Json(x, y, w, h));
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

    private JsonArray flow2Json(double speed, double direction) {
        return Json.createArrayBuilder().add(speed).add(direction).build();
    }

    private JsonArray area2Json(double x, double y, double w, double h) {
        return Json.createArrayBuilder()
                .add(x)
                .add(y)
                .add(w)
                .add(h)
                .build();
    }
}
