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
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatFactory;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.display.entity.course.Mark;
import uk.theretiredprogrammer.sketch.display.entity.course.Marks;
import uk.theretiredprogrammer.sketch.display.entity.flows.FlowComponent;
import uk.theretiredprogrammer.sketch.display.entity.flows.FlowComponentSet;

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
        FlowComponentSet components = sketchmodel.getWindFlow().getFlowcomponents();

        components.stream().forEach(
                component -> {
                    var pane = new PropertyMapPane(component, "Wind Flow Component - ", component.getNameProperty());
                    pane.addTitleButton("add.png", "Duplicate", (ev) -> duplicate(components, component));
                    pane.addTitleButton("delete.png", "Delete", (ev) -> delete(components, component));
                    this.getPanes().add(pane);
                });
        sketchmodel.getWindFlow().getFlowcomponents().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void duplicate(FlowComponentSet components, FlowComponent component) {
        components.add(FlowComponent.factory(component));
    }

    private void delete(FlowComponentSet components, FlowComponent component) {
        components.remove(component);
    }

    private void createAllWaterComponentPropertiesSection(SketchModel sketchmodel) {
        FlowComponentSet components = sketchmodel.getWaterFlow().getFlowcomponents();
        components.stream().forEach(
                component -> {
                    var pane = new PropertyMapPane(component, "Water Flow Component - ", component.getNameProperty());
                    pane.addTitleButton("add.png", "Duplicate", (ev) -> duplicate(components, component));
                    pane.addTitleButton("delete.png", "Delete", (ev) -> delete(components, component));
                    this.getPanes().add(pane);
                });
        sketchmodel.getWaterFlow().getFlowcomponents().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void createAllBoatsPropertiesSection(SketchModel sketchmodel) {
        Boats boats = sketchmodel.getBoats();
        boats.stream().forEach(boat -> {
            var pane = new PropertyMapPane(boat, "Boat - ", boat.getNameProperty());
            pane.addTitleButton("add.png", "Duplicate", (ev) -> duplicate(boats, boat));
            pane.addTitleButton("delete.png", "Delete", (ev) -> delete(boats, boat));
            this.getPanes().add(pane);
        });
        sketchmodel.getBoats().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
    }

    private void duplicate(Boats boats, Boat boat) {
        boats.add(BoatFactory.cloneBoat(boat.getNamed() + "-1", boat));
    }

    private void delete(Boats boats, Boat boat) {
        boats.remove(boat);
    }

    private void createAllMarksPropertiesSection(SketchModel sketchmodel) {
        Marks marks = sketchmodel.getMarks();
        marks.stream().forEach(mark -> {
            var pane = new PropertyMapPane(mark, "Mark - ", mark.getNameProperty());
            pane.addTitleButton("add.png", "Duplicate", (ev) -> duplicate(marks, mark));
            pane.addTitleButton("delete.png", "Delete", (ev) -> delete(marks, mark));
            this.getPanes().add(pane);
        });
        sketchmodel.getMarks().addListChangeListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchmodel);
            }
        });
        sketchmodel.getMarks().setOnChange(() -> refreshcontent(sketchmodel));
    }

    private void duplicate(Marks marks, Mark mark) {
        marks.add(new Mark(mark));
    }

    private void delete(Marks marks, Mark mark) {
        marks.remove(mark);
    }
}
