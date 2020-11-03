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
package uk.theretiredprogrammer.sketch.properties.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Accordion;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public class PropertiesPane extends Accordion {

    public PropertiesPane(SketchModel sketchmodel) {
        refreshcontent(sketchmodel);
    }

    private void refreshcontent(SketchModel sketchmodel) {
        this.getPanes().clear();
        this.getPanes().add(new PropertyMapPane(sketchmodel.getDisplay().getProperties(), "Display"));
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWindshifts().getProperties(), "Wind Shifts"));
        createAllWindComponentPropertiesSection(sketchmodel);
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWatershifts().getProperties(), "Water Shifts"));
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
        this.getPanes().add(new PropertyMapPane(sketchmodel.getCourse().getProperties(), "Course"));
    }

    private void createAllWindComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWind().getProperties().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component.getProperties(), "Wind Component - ", component.getNameProperty()))
        );
        sketchmodel.getWind().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllWaterComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWater().getProperties().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component.getProperties(), "Water Component - ", component.getNameProperty()))
        );
        sketchmodel.getWater().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllBoatsPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getBoats().getProperties().forEach(boat -> this.getPanes().add(new PropertyMapPane(boat.getProperties(), "Boat - ", boat.getNameProperty())));
        sketchmodel.getBoats().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllMarksPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getMarks().getProperties().forEach(mark -> this.getPanes().add(new PropertyMapPane(mark.getProperties(), "Mark - ", mark.getNameProperty())));
        sketchmodel.getMarks().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }
}
