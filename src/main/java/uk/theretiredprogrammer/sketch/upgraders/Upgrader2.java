/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public class Upgrader2 extends Upgrader {

    // x, y -> correctionx + x , correctiony - y;
    private double correctionx = 0;
    private double correctiony = 0;
    // angle -> correctionangle + angle;
    private double correctionangle = -90;

    @Override
    public JsonObject upgrade(JsonObject root) {
        findcorrectionfactors(Json.createPointer("/display/displayarea"), root);
        JsonObject newroot = Json.createObjectBuilder()
                .add("type", "sketch-v3")
                .build();
        newroot = copyobj(root, "meta", newroot, Json.createPointer("/meta"));
        newroot = changeDisplay(root, "display", newroot, Json.createPointer("/display"));
        newroot = copyobj(root, "windshifts", newroot, Json.createPointer("/windshifts"));
        newroot = changeFlow(root, "wind", newroot, Json.createPointer("/wind"));
        newroot = copyobj(root, "watershifts", newroot, Json.createPointer("/watershifts"));
        if (root.containsKey("water")) {
            newroot = changeFlow(root, "water", newroot, Json.createPointer("/water"));
        }
        newroot = changeCourse(root, "course", newroot, Json.createPointer("/course"));
        newroot = changeMarks(root, "marks", newroot, Json.createPointer("/marks"));
        newroot = changeBoats(root, "boats", newroot, Json.createPointer("/boats"));
        //
        return newroot;
    }

    private void findcorrectionfactors(JsonPointer displayareaPointer, JsonObject root) {
        JsonArray displayarea = (JsonArray) displayareaPointer.getValue(root);
        correctionx = -getValue(displayarea.get(0));
        correctiony = getValue(displayarea.get(1)) + getValue(displayarea.get(3));
    }

    private JsonObject changeDisplay(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonObject displayobject = root.getJsonObject(oldsetname);
        if (displayobject == null) {
            throw new ParseFailure("Upgrader2 - display object missing");
        }
        return addobj(updateArea(updateArea(displayobject, "displayarea"), "sailingarea"), newroot, insertpoint);
    }

    private JsonObject changeFlow(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonArray array = root.getJsonArray(oldsetname);
        if (array == null) {
            throw new ParseFailure("Upgrader2 - flow array missing");
        }
        JsonArray newarray = Json.createArrayBuilder().build();
        for (JsonValue item : array) {
            if (item.getValueType() == ValueType.OBJECT) {
                JsonObject flowcomponent = (JsonObject) item;
                switch (flowcomponent.getString("type")) {
                    case "manualflow" -> {
                        flowcomponent = updateFlow(flowcomponent, "flow");
                        flowcomponent = updateAngle(flowcomponent, "mean");
                    }
                    case "complexflow" -> {
                        flowcomponent = updateFlow(flowcomponent, "northwestflow", "topleftflow");
                        flowcomponent = updateFlow(flowcomponent, "northeastflow", "toprightflow");
                        flowcomponent = updateFlow(flowcomponent, "southwestflow", "bottomleftflow");
                        flowcomponent = updateFlow(flowcomponent, "southeastflow", "bottomrightflow");
                    }
                    case "constantflow" ->
                        flowcomponent = updateFlow(flowcomponent, "flow");
                    case "gradientflow" ->
                        throw new ParseFailure("Upgrader2 - gradient upgrades not supported");
                    default ->
                        throw new ParseFailure("Upgrader2 - illegal flow type defined");
                }
                flowcomponent = updateArea(flowcomponent, "area");
                newarray = Json.createPointer("/-").add(newarray, flowcomponent);
            } else {
                throw new ParseFailure("Upgrader2 - mark object type incorrect");
            }
        }
        return addarray(newarray, newroot, insertpoint);
    }

    private JsonObject changeCourse(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonObject courseobject = root.getJsonObject(oldsetname);
        if (courseobject == null) {
            throw new ParseFailure("Upgrader2 - course object missing");
        }
        return addobj(updateLocation(courseobject, "start"), newroot, insertpoint);
    }

    private JsonObject changeMarks(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonArray array = root.getJsonArray(oldsetname);
        if (array == null) {
            throw new ParseFailure("Upgrader2 - marks array missing");
        }
        JsonArray newarray = Json.createArrayBuilder().build();
        for (JsonValue item : array) {
            if (item.getValueType() == ValueType.OBJECT) {
                JsonObject mark = (JsonObject) item;
                mark = updateLocation(mark, "location");
                newarray = Json.createPointer("/-").add(newarray, mark);
            } else {
                throw new ParseFailure("Upgrader2 - mark object type incorrect");
            }
        }
        return addarray(newarray, newroot, insertpoint);
    }

    private JsonObject changeBoats(JsonObject root, String oldsetname, JsonObject newroot, JsonPointer insertpoint) {
        JsonArray array = root.getJsonArray(oldsetname);
        if (array == null) {
            throw new ParseFailure("Upgrader2 - boats array missing");
        }
        JsonArray newarray = Json.createArrayBuilder().build();
        for (JsonValue item : array) {
            if (item.getValueType() == ValueType.OBJECT) {
                JsonObject boat = (JsonObject) item;
                boat = updateLocation(boat, "location");
                boat = updateAngle(boat, "heading");
                newarray = Json.createPointer("/-").add(newarray, boat);
            } else {
                throw new ParseFailure("Upgrader2 - boat object type incorrect");
            }
        }
        return addarray(newarray, newroot, insertpoint);
    }

    private JsonObject updateArea(JsonObject parentobject, String key) {
        JsonArray area = parentobject.getJsonArray(key);
        return area == null ? parentobject : Json.createPointer("/" + key).replace(parentobject, correctedArea(area));
    }

    private JsonArray correctedArea(JsonArray olddimensions) {
        return Json.createArrayBuilder()
                .add(correctedx(olddimensions))
                .add(correctedy(olddimensions) - getValue(olddimensions.get(3)))
                .add(getValue(olddimensions.get(2)))
                .add(getValue(olddimensions.get(3)))
                .build();
    }

    private JsonObject updateLocation(JsonObject parentobject, String key) {
        JsonArray location = parentobject.getJsonArray(key);
        return location == null ? parentobject : Json.createPointer("/" + key).replace(parentobject, correctedLocation(location));
    }

    private JsonArray correctedLocation(JsonArray olddimensions) {
        return Json.createArrayBuilder()
                .add(correctedx(olddimensions))
                .add(correctedy(olddimensions))
                .build();
    }

    private JsonObject updateFlow(JsonObject parentobject, String key) {
        JsonArray flow = parentobject.getJsonArray(key);
        return flow == null ? parentobject : Json.createPointer("/" + key).replace(parentobject, correctedFlow(flow));
    }

    private JsonObject updateFlow(JsonObject parentobject, String key, String insertkey) {
        JsonArray flow = parentobject.getJsonArray(key);
        if (flow == null) {
            return parentobject;
        }
        parentobject = Json.createPointer("/" + key).remove(parentobject);
        return Json.createPointer("/" + insertkey).add(parentobject, correctedFlow(flow));
    }

    private JsonArray correctedFlow(JsonArray olddimensions) {
        return Json.createArrayBuilder()
                .add(olddimensions.get(0))
                .add(correctedanglevalue(olddimensions.get(1)))
                .build();
    }

    private JsonObject updateAngle(JsonObject parentobject, String key) {
        JsonValue angle = parentobject.getJsonNumber(key);
        return angle == null ? parentobject : Json.createPointer("/" + key).replace(parentobject, correctedAngle(angle));
    }

    private JsonValue correctedAngle(JsonValue oldangle) {
        return Json.createValue(correctedanglevalue(oldangle));
    }

    private double correctedx(JsonArray array) {
        return correctionx + getValue(array.get(0));
    }

    private double correctedy(JsonArray array) {
        return correctiony - getValue(array.get(1));
    }

    private double correctedanglevalue(JsonValue oldangle) {
        return normalise(getValue(oldangle) + correctionangle);
    }

    private double normalise(double angle) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        return angle;
    }

    private double getValue(JsonValue jvalue) {
        if (jvalue.getValueType() != ValueType.NUMBER) {
            throw new ParseFailure("Upgrader2 - illegal type - expecting a number");
        }
        return ((JsonNumber) jvalue).doubleValue();
    }
}
