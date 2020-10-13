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
package uk.theretiredprogrammer.sketch.properties.entity;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Area;

/**
 *
 * @author richard
 */
public class PropertySketch extends PropertyMap {

    private final Config<PropertyString, String> type = new Config<>("type", MANDATORY, (s) -> new PropertyString(s, null));
    private final Config<PropertyMeta, PropertyMeta> meta = new Config<>("meta", OPTIONAL, (s) -> new PropertyMeta(s));
    private final Config<PropertyDisplay, PropertyDisplay> display = new Config<>("display", OPTIONAL, (s) -> new PropertyDisplay(s));
    private final Config<PropertyFlowShifts, PropertyFlowShifts> windshifts = new Config<>("windshifts", OPTIONAL, (s) -> new PropertyFlowShifts(s));
    private final Config<PropertyFlowComponents, PropertyFlowComponents> wind = new Config<>("wind", OPTIONAL, (s) -> new PropertyFlowComponents(s, () -> getDisplayArea()));
    private final Config<PropertyFlowShifts, PropertyFlowShifts> watershifts = new Config<>("watershifts", OPTIONAL, (s) -> new PropertyFlowShifts(s));
    private final Config<PropertyFlowComponents, PropertyFlowComponents> water = new Config<>("water", OPTIONAL, (s) -> new PropertyFlowComponents(s, () -> getDisplayArea()));
    private final Config<PropertyMarks, PropertyMarks> marks = new Config<>("marks", MANDATORY, (s) -> new PropertyMarks(s));
    private final Config<PropertyCourse, PropertyCourse> course = new Config<>("course", MANDATORY, (s) -> new PropertyCourse(s, getMarkNames()));
    private final Config<PropertyBoats, PropertyBoats> boats = new Config<>("boats", MANDATORY, (s) -> new PropertyBoats(s));
    private final ObservableList<String> marknames = FXCollections.observableArrayList();

    public PropertySketch() {
        addConfig(type, meta, display, windshifts, wind, watershifts, water, marks, course, boats);
        getMarks().getList().addListener((ListChangeListener<PropertyMark>) (c) -> marklistchanged((Change<PropertyMark>) c));
    }

    private void marklistchanged(Change<PropertyMark> c) {
        marknames.clear();
        getMarks().getList().forEach(mark -> marknames.add(mark.getName()));
    }

    public Area getDisplayArea() {
        return getDisplay().getDisplayarea();
    }

    @Override
    public PropertySketch get() {
        return this;
    }

    public String getType() {
        return type.get("PropertySketch type");
    }

    public PropertyMeta getMeta() {
        return meta.get("PropertySketch meta");
    }

    public PropertyDisplay getDisplay() {
        return display.get("PropertySketch display");
    }

    public PropertyFlowShifts getWindshifts() {
        return windshifts.get("PropertySketch windshifts");
    }

    public PropertyFlowComponents getWind() {
        return wind.get("PropertySketch wind");
    }

    public PropertyFlowShifts getWatershifts() {
        return watershifts.get("PropertySketch watershifts");
    }

    public PropertyFlowComponents getWater() {
        return water.get("PropertySketch water");
    }

    public final PropertyMarks getMarks() {
        return marks.get("PropertySketch marks");
    }

    public PropertyCourse getCourse() {
        return course.get("PropertySketch course");
    }

    public PropertyBoats getBoats() {
        return boats.get("PropertySketch boats");
    }

    public ObservableList<String> getMarkNames() {
        return marknames;
    }
}
