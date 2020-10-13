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

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyElement;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyString;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyAny;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMap;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author richard
 */
public class PropertiesPane extends Accordion {

    public PropertiesPane(PropertySketch sketchproperty) {
        refreshcontent(sketchproperty);
    }

    private void refreshcontent(PropertySketch sketchproperty) {
        this.getPanes().clear();
        this.getPanes().add(new PropertiesSection(sketchproperty.getDisplay(), "Display"));
        this.getPanes().add(new PropertiesSection(sketchproperty.getWindshifts(), "Wind Shifts"));
        createAllWindComponentPropertiesSection(sketchproperty);
        this.getPanes().add(new PropertiesSection(sketchproperty.getWatershifts(), "Water Shifts"));
        createAllWaterComponentPropertiesSection(sketchproperty);
        createCoursePropertiesSection(sketchproperty);
        createAllMarksPropertiesSection(sketchproperty);
        createAllBoatsPropertiesSection(sketchproperty);
    }

    private void createCoursePropertiesSection(PropertySketch sketchproperty) {
        sketchproperty.getCourse().getPropertyLegValues().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchproperty);
            }
        });
        this.getPanes().add(new PropertiesSection(sketchproperty.getCourse(), "Course"));
    }

    private void createAllWindComponentPropertiesSection(PropertySketch sketchproperty) {
        sketchproperty.getWind().stream().forEach(
                component -> this.getPanes().add(new PropertiesSection(component.get(), "Wind Component - ", component.getNameProperty()))
        );
        sketchproperty.getWind().get().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchproperty);
            }
        });
    }

    private void createAllWaterComponentPropertiesSection(PropertySketch sketchproperty) {
        sketchproperty.getWater().stream().forEach(
                component -> this.getPanes().add(new PropertiesSection(component.get(), "Water Component - ", component.getNameProperty()))
        );
        sketchproperty.getWater().get().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchproperty);
            }
        });
    }

    private void createAllBoatsPropertiesSection(PropertySketch sketchproperty) {
        sketchproperty.getBoats().stream().forEach(boatproperty -> this.getPanes().add(new PropertiesSection(boatproperty, "Boat - ", boatproperty.getNameProperty())));
        sketchproperty.getBoats().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchproperty);
            }
        });
    }

    private void createAllMarksPropertiesSection(PropertySketch sketchproperty) {
        sketchproperty.getMarks().getList().forEach(markproperty -> this.getPanes().add(new PropertiesSection(markproperty, "Mark - ", markproperty.getNameProperty())));
        sketchproperty.getMarks().setOnChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(sketchproperty);
            }
        });
    }

    private class PropertiesSection extends TitledPane {

        PropertiesSection(PropertyMap properties, String title) {
            this.setText(title);
            this.setContent(new ScrollPane(createpropertiescontent(properties)));
        }

        PropertiesSection(PropertyMap properties, String titleroot, PropertyString propertyname) {
            this.textProperty().bind(new SimpleStringProperty(titleroot)
                    .concat(propertyname.propertyString())
            );
            this.setContent(createpropertiescontent(properties));
        }

        @SuppressWarnings("null")
        private ScrollPane createpropertiescontent(PropertyMap properties) {
            GridPane propertiestable = new GridPane();
            int row = 0;
            for (var pe : properties.getMap().entrySet()) {
                propertiestable.add(new Label(pe.getKey()), 0, row, 1, 1);
                PropertyAny value = pe.getValue();
                if (value instanceof PropertyElement propertyElement) {
                    propertiestable.add(propertyElement.getField(null), 1, row++, 1, 1);
                } else {
                    propertiestable.add(new Label(value.toString()), 1, row++, 1, 1);
                }
            }
            return new ScrollPane(propertiestable);
        }
    }
}
