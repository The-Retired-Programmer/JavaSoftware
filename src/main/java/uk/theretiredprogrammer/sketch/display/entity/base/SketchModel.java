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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.display.entity.course.Course;
import uk.theretiredprogrammer.sketch.display.entity.course.Mark;
import uk.theretiredprogrammer.sketch.display.entity.course.Marks;
import uk.theretiredprogrammer.sketch.display.entity.flows.FlowComponentSet;
import uk.theretiredprogrammer.sketch.display.entity.flows.FlowShiftModel;

public class SketchModel extends ModelMap {

    private final ObservableList<String> marknames = FXCollections.observableArrayList();
    private final PropertyString type = new PropertyString(null);
    private final MetaModel meta = new MetaModel();
    private final DisplayModel display = new DisplayModel();
    private final FlowShiftModel windshifts = new FlowShiftModel();
    private final FlowComponentSet wind = new FlowComponentSet(() -> getDisplayArea());
    private final FlowShiftModel watershifts = new FlowShiftModel();
    private final FlowComponentSet water = new FlowComponentSet(() -> getDisplayArea());
    private final Marks marks = new Marks();
    private final Course course = new Course(marks, getMarkNames());
    private final Boats boats = new Boats(this);

    public SketchModel() {
        marks.addListener((ListChangeListener<Mark>) (c) -> marklistchanged((ListChangeListener.Change<Mark>) c));
    }

    private void marklistchanged(ListChangeListener.Change<Mark> c) {
        while (c.next()) {
            c.getRemoved().forEach(remitem -> {
                marknames.remove(remitem.getName());
            });
            c.getAddedSubList().forEach(additem -> {
                marknames.add(additem.getName());
            });
        }
        if (marknames.size() != marks.stream().count()) {
            // trap - maybe removed later
            throw new IllegalStateFailure("Number of Marks does not equal number of Marknames");
        }
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseMandatoryProperty("type", type, jobj);
        parseOptionalProperty("meta", meta, jobj);
        parseOptionalProperty("display", display, jobj);
        parseOptionalProperty("windshifts", windshifts, jobj);
        parseOptionalProperty("wind", wind, jobj);
        parseOptionalProperty("watershifts", watershifts, jobj);
        parseOptionalProperty("water", water, jobj);
        parseMandatoryProperty("marks", marks, jobj);
        parseMandatoryProperty("course", course, jobj);
        parseMandatoryProperty("boats", boats, jobj);
    }

    @Override
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

    @Override
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

    public PropertyArea getDisplayArea() {
        return getDisplay().getDisplayarea();
    }

    public String getType() {
        return type.get();
    }

    public MetaModel getMeta() {
        return meta;
    }

    public final DisplayModel getDisplay() {
        return display;
    }

    public FlowShiftModel getWindshifts() {
        return windshifts;
    }

    public FlowComponentSet getWind() {
        return wind;
    }

    public FlowShiftModel getWatershifts() {
        return watershifts;
    }

    public FlowComponentSet getWater() {
        return water;
    }

    public final Marks getMarks() {
        return marks;
    }

    public Course getCourse() {
        return course;
    }

    public Boats getBoats() {
        return boats;
    }

    public ObservableList<String> getMarkNames() {
        return marknames;
    }
}
