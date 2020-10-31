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
package uk.theretiredprogrammer.sketch.display.entity.base;

import uk.theretiredprogrammer.sketch.display.entity.course.PropertyMarks;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyCourse;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyMark;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyFlowShifts;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyFlowComponents;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyMap;
import uk.theretiredprogrammer.sketch.display.entity.boats.PropertyBoats;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConfig;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyConfig.MANDATORY;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyConfig.OPTIONAL;

/**
 *
 * @author richard
 */
public class PropertySketch extends PropertyMap {

    private final PropertyConfig<PropertyString, String> type = new PropertyConfig<>("type", MANDATORY, (s) -> new PropertyString(s, null));
    private final PropertyConfig<PropertyMeta, PropertyMeta> meta = new PropertyConfig<>("meta", OPTIONAL, (s) -> new PropertyMeta(s));
    private final PropertyConfig<PropertyDisplay, PropertyDisplay> display = new PropertyConfig<>("display", OPTIONAL, (s) -> new PropertyDisplay(s));
    private final PropertyConfig<PropertyFlowShifts, PropertyFlowShifts> windshifts = new PropertyConfig<>("windshifts", OPTIONAL, (s) -> new PropertyFlowShifts(s));
    private final PropertyConfig<PropertyFlowComponents, PropertyFlowComponents> wind = new PropertyConfig<>("wind", OPTIONAL, (s) -> new PropertyFlowComponents(s, () -> getDisplayArea()));
    private final PropertyConfig<PropertyFlowShifts, PropertyFlowShifts> watershifts = new PropertyConfig<>("watershifts", OPTIONAL, (s) -> new PropertyFlowShifts(s));
    private final PropertyConfig<PropertyFlowComponents, PropertyFlowComponents> water = new PropertyConfig<>("water", OPTIONAL, (s) -> new PropertyFlowComponents(s, () -> getDisplayArea()));
    private final PropertyConfig<PropertyMarks, PropertyMarks> marks = new PropertyConfig<>("marks", MANDATORY, (s) -> new PropertyMarks(s));
    private final PropertyConfig<PropertyCourse, PropertyCourse> course = new PropertyConfig<>("course", MANDATORY, (s) -> new PropertyCourse(s, getMarkNames()));
    private final PropertyConfig<PropertyBoats, PropertyBoats> boats = new PropertyConfig<>("boats", MANDATORY, (s) -> new PropertyBoats(s));
    private final ObservableList<String> marknames = FXCollections.observableArrayList();

    public PropertySketch() {
        addConfig(type, meta, display, windshifts, wind, watershifts, water, marks, course, boats);
        getMarks().setOnChange((ListChangeListener<PropertyMark>) (c) -> marklistchanged((Change<PropertyMark>) c));
    }

    private void marklistchanged(Change<PropertyMark> c) {
        while (c.next()) {
            c.getRemoved().forEach(remitem -> {
                marknames.remove(remitem.getName());
            });
            c.getAddedSubList().forEach(additem -> {
                marknames.add(additem.getName());
            });
        }
        assert marknames.size() == getMarks().getList().size();
//        marknames.clear();
//        getMarks().getList().forEach(mark -> marknames.add(mark.getName()));
    }

    @Override
    public void setOnChange(Runnable onchange) {
        super.setOnChange(onchange);
        getMeta().setOnChange(onchange);
        getDisplay().setOnChange(onchange);
        getWindshifts().setOnChange(onchange);
        getWind().setOnChange(onchange);
        getWatershifts().setOnChange(onchange);
        getWater().setOnChange(onchange);
        getMarks().setOnChange(onchange);
        getCourse().setOnChange(onchange);
        getBoats().setOnChange(onchange);
    }

    public Area getDisplayArea() {
        return getDisplay().getDisplayarea();
    }

    @Override
    public PropertySketch get() {
        return this;
    }

    public String getType() {
        return type.get(this, "PropertySketch type");
    }

    public PropertyMeta getMeta() {
        return meta.get(this, "PropertySketch meta");
    }

    public PropertyDisplay getDisplay() {
        return display.get(this, "PropertySketch display");
    }

    public PropertyFlowShifts getWindshifts() {
        return windshifts.get(this, "PropertySketch windshifts");
    }

    public PropertyFlowComponents getWind() {
        return wind.get(this, "PropertySketch wind");
    }

    public PropertyFlowShifts getWatershifts() {
        return watershifts.get(this, "PropertySketch watershifts");
    }

    public PropertyFlowComponents getWater() {
        return water.get(this, "PropertySketch water");
    }

    public final PropertyMarks getMarks() {
        return marks.get(this, "PropertySketch marks");
    }

    public PropertyCourse getCourse() {
        return course.get(this, "PropertySketch course");
    }

    public PropertyBoats getBoats() {
        return boats.get(this, "PropertySketch boats");
    }

    public ObservableList<String> getMarkNames() {
        return marknames;
    }
}
