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
package uk.theretiredprogrammer.sketch.display.entity.base;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.PropertyAny;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.display.entity.boats.PropertyBoats;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyCourse;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyMark;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyMarks;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyFlowComponents;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyFlowShifts;

public class SketchModel {

    private final ObservableList<String> marknames = FXCollections.observableArrayList();
    private final PropertyString type = new PropertyString("type", null);
    private final PropertyMeta meta = new PropertyMeta("meta");
    private final PropertyDisplay display = new PropertyDisplay("display");
    private final PropertyFlowShifts windshifts = new PropertyFlowShifts("windshifts");
    private final PropertyFlowComponents wind = new PropertyFlowComponents("wind", () -> getDisplayArea());
    private final PropertyFlowShifts watershifts = new PropertyFlowShifts("watershifts");
    private final PropertyFlowComponents water = new PropertyFlowComponents("water", () -> getDisplayArea());
    private final PropertyMarks marks = new PropertyMarks("marks");
    private final PropertyCourse course = new PropertyCourse("course", getMarkNames());
    private final PropertyBoats boats = new PropertyBoats("boats");
    
    public SketchModel() {
        marks.setOnChange((ListChangeListener<PropertyMark>) (c) -> marklistchanged((ListChangeListener.Change<PropertyMark>) c));
    }

    private void marklistchanged(ListChangeListener.Change<PropertyMark> c) {
        while (c.next()) {
            c.getRemoved().forEach(remitem -> {
                marknames.remove(remitem.getName());
            });
            c.getAddedSubList().forEach(additem -> {
                marknames.add(additem.getName());
            });
        }
        assert marknames.size() == marks.getList().size();
    }

    public void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.OBJECT) {
            parseMandatoryProperty("type", type, (JsonObject) jvalue);
            parseOptionalProperty("meta", meta, (JsonObject) jvalue);
            parseOptionalProperty("display", display, (JsonObject) jvalue);
            parseOptionalProperty("windshifts", windshifts, (JsonObject) jvalue);
            parseOptionalProperty("wind", wind, (JsonObject) jvalue);
            parseOptionalProperty("watershifts", watershifts, (JsonObject) jvalue);
            parseOptionalProperty("water", water, (JsonObject) jvalue);
            parseMandatoryProperty("marks", marks, (JsonObject) jvalue);
            parseMandatoryProperty("course", course, (JsonObject) jvalue);
            parseMandatoryProperty("boats", boats, (JsonObject) jvalue);
        }
    }

    private void parseMandatoryProperty(String key, PropertyAny property, JsonObject parentobj) {
        if (!parseOptionalProperty(key, property, parentobj)) {
            throw new ParseFailure("Missing a Mandatory parameter: " + key);
        }
    }

    private boolean parseOptionalProperty(String key, PropertyAny property, JsonObject parentobj) {
        JsonValue jvalue = parentobj.get(key);
        if (jvalue == null) {
            return false;
        }
        property.parse(jvalue);
        return true;
    }

    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("type", type.toJson());
        job.add("meta", meta.toJson());
        job.add("display", display.toJson());
        job.add("windshifts", windshifts.toJson());
        job.add("wind", wind.toJson());
        job.add("watershifts", watershifts.toJson());
        job.add("water", water.toJson());
        job.add("marks", marks.toJson());
        job.add("course", course.toJson());
        job.add("boats", boats.toJson());
        return job.build();
    }

    public void setOnChange(Runnable onchange) {
        type.setOnChange(onchange);
        meta.setOnChange(onchange);
        display.setOnChange(onchange);
        windshifts.setOnChange(onchange);
        wind.setOnChange(onchange);
        watershifts.setOnChange(onchange);
        water.setOnChange(onchange);
        marks.setOnChange(onchange);
        course.setOnChange(onchange);
        boats.setOnChange(onchange);
    }

    public Area getDisplayArea() {
        return getDisplay().getDisplayarea();
    }

    public String getType() {
        return type.get();
    }

    public PropertyMeta getMeta() {
        return meta.get();
    }

    public PropertyDisplay getDisplay() {
        return display.get();
    }

    public PropertyFlowShifts getWindshifts() {
        return windshifts.get();
    }

    public PropertyFlowComponents getWind() {
        return wind.get();
    }

    public PropertyFlowShifts getWatershifts() {
        return watershifts.get();
    }

    public PropertyFlowComponents getWater() {
        return water.get();
    }

    public final PropertyMarks getMarks() {
        return marks.get();
    }

    public PropertyCourse getCourse() {
        return course.get();
    }

    public PropertyBoats getBoats() {
        return boats.get();
    }

    public ObservableList<String> getMarkNames() {
        return marknames;
    }
}
