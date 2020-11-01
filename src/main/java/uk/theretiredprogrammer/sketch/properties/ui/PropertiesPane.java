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
package uk.theretiredprogrammer.sketch.properties.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Accordion;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

/**
 *
 * @author richard
 */
public class PropertiesPane extends Accordion {

    public PropertiesPane(SketchModel sketchmodel) {
        refreshcontent(sketchmodel);
    }

    private void refreshcontent(SketchModel sketchmodel) {
        this.getPanes().clear();
        this.getPanes().add(new PropertyMapPane(sketchmodel.getDisplay(), "Display"));
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWindshifts(), "Wind Shifts"));
        createAllWindComponentPropertiesSection(sketchmodel);
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWatershifts(), "Water Shifts"));
        createAllWaterComponentPropertiesSection(sketchmodel);
        createCoursePropertiesSection(sketchmodel);
        createAllMarksPropertiesSection(sketchmodel);
        createAllBoatsPropertiesSection(sketchmodel);
    }

    private void createCoursePropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getCourse().getPropertyLegValues().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
        this.getPanes().add(new PropertyMapPane(sketchmodel.getCourse(), "Course"));
    }

    private void createAllWindComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWind().stream().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component.get(), "Wind Component - ", component.getNameProperty()))
        );
        sketchmodel.getWind().get().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllWaterComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWater().stream().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component.get(), "Water Component - ", component.getNameProperty()))
        );
        sketchmodel.getWater().get().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllBoatsPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getBoats().stream().forEach(boatproperty -> this.getPanes().add(new PropertyMapPane(boatproperty, "Boat - ", boatproperty.getNameProperty())));
        sketchmodel.getBoats().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllMarksPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getMarks().getList().forEach(markproperty -> this.getPanes().add(new PropertyMapPane(markproperty, "Mark - ", markproperty.getNameProperty())));
        sketchmodel.getMarks().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }
}
