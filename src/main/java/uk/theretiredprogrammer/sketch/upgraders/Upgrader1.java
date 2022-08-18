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
package uk.theretiredprogrammer.sketch.upgraders;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public class Upgrader1 extends Upgrader {

    @Override
    public JsonObject upgrade(JsonObject root) {
        JsonObject newroot = Json.createObjectBuilder()
                .add("type", "sketch-v2")
                .build();
        //
        newroot = copyobj(root, "meta", newroot, Json.createPointer("/meta"));
        newroot = copyobj(root, "display", newroot, Json.createPointer("/display"));
        newroot = copyobj(root, "windshifts", newroot, Json.createPointer("/windshifts"));
        newroot = changeTestFlow2ManualFlow(root, "wind", newroot, Json.createPointer("/wind"));
        newroot = copyobj(root, "watershifts", newroot, Json.createPointer("/watershifts"));
        newroot = changeTestFlow2ManualFlow(root, "water", newroot, Json.createPointer("/water"));
        newroot = copyobj(root, "course", newroot, Json.createPointer("/course"));
        newroot = copyarray(root, "marks", newroot, Json.createPointer("/marks"));
        newroot = copyarray(root, "boats", newroot, Json.createPointer("/boats"));
        //
        return newroot;
    }

    private JsonObject changeTestFlow2ManualFlow(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonArray array = root.getJsonArray(oldsetname);
        if (array == null) {
            return newroot;
        }
        JsonArray newarray = Json.createArrayBuilder().build();
        JsonObject newobj;
        for (JsonValue item : array) {
            if (item.getValueType() == ValueType.OBJECT) {
                JsonObject jobj = (JsonObject) item;
                String type = jobj.getString("type", "UNKNOWN");
                switch (type) {
                    case "testflow" -> {
                        newobj = Json.createPointer("/type").replace(jobj, Json.createValue("manualflow"));
                        newarray = Json.createPointer("/-").add(newarray, newobj);
                    }
                    case "UNKNOWN" ->
                        throw new ParseFailure("Upgrader1 - undefined type for a flow object");
                    default ->
                        newarray = Json.createPointer("/-").add(newarray, jobj);
                }
            } else {
                newarray = Json.createPointer("/-").add(newarray, item);
            }
        }
        return addarray(newarray, newroot, insertpoint);
    }
}
