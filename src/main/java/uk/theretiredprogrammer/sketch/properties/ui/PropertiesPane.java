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
        this.getPanes().add(new PropertyMapPane(sketchmodel.getDisplay(), "Display"));
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWindFlow().getShiftsproperty(), "Wind Shifts"));
        createAllWindComponentPropertiesSection(sketchmodel);
        this.getPanes().add(new PropertyMapPane(sketchmodel.getWaterFlow().getShiftsproperty(), "Water Shifts"));
        createAllWaterComponentPropertiesSection(sketchmodel);
        createCoursePropertiesSection(sketchmodel);
        createAllMarksPropertiesSection(sketchmodel);
        createAllBoatsPropertiesSection(sketchmodel);
    }
    
    private void createCoursePropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getCourse().getLegs().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
        sketchmodel.getCourse().setOnChange(() -> refreshcontent(sketchmodel));
        this.getPanes().add(new PropertyMapPane(sketchmodel.getCourse(), "Course"));
    }
    
    private void createAllWindComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWindFlow().getFlowcomponents().stream().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component, "Wind Component - ", component.getNameProperty()))
        );
        sketchmodel.getWindFlow().getFlowcomponents().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }
    
    private void createAllWaterComponentPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getWaterFlow().getFlowcomponents().stream().forEach(
                component -> this.getPanes().add(new PropertyMapPane(component, "Water Component - ", component.getNameProperty()))
        );
        sketchmodel.getWaterFlow().getFlowcomponents().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }
    
    private void createAllBoatsPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getBoats().stream().forEach(boat -> this.getPanes().add(new PropertyMapPane(boat, "Boat - ", boat.getNameProperty())));
        sketchmodel.getBoats().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }
    
    private void createAllMarksPropertiesSection(SketchModel sketchmodel) {
        sketchmodel.getMarks().stream().forEach(mark -> this.getPanes().add(new PropertyMapPane(mark, "Mark - ", mark.getNameProperty())));
        sketchmodel.getMarks().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
        sketchmodel.getMarks().setOnChange(() -> refreshcontent(sketchmodel));
    }
}
