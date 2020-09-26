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
package uk.theretiredprogrammer.sketch.jfx.propertiesdisplay;

import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.core.PropertyString;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertiesPane extends Accordion {

    public PropertiesPane(Controller controller) {
        refreshcontent(controller);
    }

    private void refreshcontent(Controller controller) {
        this.getPanes().clear();
        this.getPanes().add(new PropertiesSection(controller.displayparameters.properties(), "Display", controller));
        this.getPanes().add(new PropertiesSection(controller.windflow.properties(), "Wind Flow", controller));
        createAllWindComponentPropertiesSection(controller);
        createAllWaterComponentPropertiesSection(controller);
        createCoursePropertiesSection(controller);
        createAllMarksPropertiesSection(controller);
        createAllBoatsPropertiesSection(controller);
    }

    private void createCoursePropertiesSection(Controller controller) {
        controller.course.setOnLegValuesChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(controller);
            }
        });
        this.getPanes().add(new PropertiesSection(controller.course.properties(), "Course", controller));
    }

    private void createAllWindComponentPropertiesSection(Controller controller) {
        controller.windflow.getComponents().forEach(
                component -> this.getPanes().add(new PropertiesSection(component.properties(), "Wind Component - ", "name", controller))
        );
        controller.windflow.getFlowComponentSet().setOnComponentsChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(controller);
            }
        });
    }

    private void createAllWaterComponentPropertiesSection(Controller controller) {
        controller.waterflow.getComponents().forEach(
                component -> this.getPanes().add(new PropertiesSection(component.properties(), "Water Component - ", "name", controller))
        );
        controller.waterflow.getFlowComponentSet().setOnComponentsChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(controller);
            }
        });
    }

    private void createAllBoatsPropertiesSection(Controller controller) {
        controller.boats.getBoats().forEach(boat -> this.getPanes().add(new PropertiesSection(boat.properties(), "Boat - ", "name", controller)));
        controller.boats.setOnBoatsChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(controller);
            }
        });
    }

    private void createAllMarksPropertiesSection(Controller controller) {
        controller.course.getMarks().forEach(mark -> this.getPanes().add(new PropertiesSection(mark.properties(), "Mark - ", "name", controller)));
        controller.course.setOnMarksChange(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refreshcontent(controller);
            }
        });
    }

    private class PropertiesSection extends TitledPane {

        PropertiesSection(Map<String, PropertyItem> properties, String title, Controller controller) {
            this.setText(title);
            this.setContent(new ScrollPane(createpropertiescontent(properties, controller)));
        }

        PropertiesSection(Map<String, PropertyItem> properties, String titleroot, String propertyname, Controller controller) {
            this.textProperty().bind(
                    new SimpleStringProperty(titleroot)
                            .concat(((PropertyString) properties.get(propertyname)).PropertyString())
            );
            this.setContent(createpropertiescontent(properties, controller));
        }

        private ScrollPane createpropertiescontent(Map<String, PropertyItem> properties, Controller controller) {
            GridPane propertiestable = new GridPane();
            int row = 0;
            for (Map.Entry<String, PropertyItem> mapentry : properties.entrySet()) {
                propertiestable.add(new Label(mapentry.getKey()), 0, row, 1, 1);
                Object value = mapentry.getValue();
                if (value instanceof PropertyItem propertyItem) {
                    propertiestable.add(propertyItem.createPropertySheetItem(controller), 1, row++, 1, 1);
                } else {
                    propertiestable.add(new Label(value.toString()), 1, row++, 1, 1);
                }
            }
            return new ScrollPane(propertiestable);
        }
    }
}
