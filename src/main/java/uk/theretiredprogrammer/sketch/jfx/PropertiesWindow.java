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
package uk.theretiredprogrammer.sketch.jfx;

import java.io.IOException;
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
import uk.theretiredprogrammer.sketch.course.Mark;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertiesWindow extends AbstractWindow {

    public static PropertiesWindow create(String fn, Controller controller, AbstractWindow parent) {
        return new PropertiesWindow(fn, controller, parent);
    }

    private PropertiesWindow(String fn, Controller controller, AbstractWindow parent) {
        super(PropertiesWindow.class, parent);
        setTitle("SKETCH Properties Viewer - " + fn);
        setContent(new PropertiesPane(controller));
        this.addtoToolbar(
                toolbarButton("Add Boat", actionEvent -> {
                }),
                toolbarButton("Add WindFlowComponent", actionEvent -> {
                }),
                toolbarButton("Add Mark", actionEvent -> addnewmark(controller))
        );
        show();
    }

    private void addnewmark(Controller controller) {
        try {
            controller.addNewMark();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Mark: " + ex.getLocalizedMessage());
        }
    }

    private class PropertiesPane extends Accordion {

        public PropertiesPane(Controller controller) {
            refreshcontent(controller);
        }

        private void refreshcontent(Controller controller) {
            this.getPanes().clear();
            this.getPanes().addAll(
                    new PropertiesSection(controller.displayparameters.properties(), "Display"),
                    new PropertiesSection(controller.windflow.properties(), "Wind Flow")
            );
            controller.windflow.getFlowComponentSet().getComponents().forEach(component -> {
                this.getPanes().add(new PropertiesSection(component.properties(), "Wind Component - ", "name"));
            });
            WaterFlow waterflow = controller.waterflow;
            if (waterflow != null) {
                this.getPanes().add(
                        new PropertiesSection(waterflow.properties(), "Water Flow")
                );
                controller.waterflow.getFlowComponentSet().getComponents().forEach(component
                        -> this.getPanes().add(new PropertiesSection(component.properties(), "Water Component - ", "name"))
                );
            }
            this.getPanes().add(
                    new PropertiesSection(controller.course.properties(), "Course")
            );
            createAllMarkPropertiesSection(controller);
            controller.boats.getBoats().forEach(boat -> {
                this.getPanes().add(
                        new PropertiesSection(boat.properties(), "Boat - ", "name")
                );
            });
        }

        private void createAllMarkPropertiesSection(Controller controller) {
            controller.course.getMarks().forEach(mark -> this.getPanes().add(new MarkPropertiesSection(mark)));
            controller.course.setOnMarksChange(new ListChangeListener() {
                @Override
                public void onChanged(ListChangeListener.Change change) {
                    refreshcontent(controller);
                }
            });
        }
    }

    private class MarkPropertiesSection extends PropertiesSection {

        public MarkPropertiesSection(Mark mark) {
            super(mark.properties(), "Mark - ", "name");
        }
    }

    private class PropertiesSection extends TitledPane {

        public PropertiesSection(Map<String, PropertyItem> properties, String title) {
            this.setText(title);
            this.setContent(new ScrollPane(createpropertiescontent(properties)));
        }

        public PropertiesSection(Map<String, PropertyItem> properties, String titleroot, String propertyname) {
            this.textProperty().bind(
                    new SimpleStringProperty(titleroot)
                            .concat(((PropertyString) properties.get(propertyname)).PropertyString())
            );
            this.setContent(new ScrollPane(createpropertiescontent(properties)));
        }

        private GridPane createpropertiescontent(Map<String, PropertyItem> properties) {
            GridPane propertiestable = new GridPane();
            int row = 0;
            for (Map.Entry<String, PropertyItem> mapentry : properties.entrySet()) {
                propertiestable.add(new Label(mapentry.getKey()), 0, row, 1, 1);
                Object value = mapentry.getValue();
                if (value instanceof PropertyItem propertyItem) {
                    propertiestable.add(propertyItem.createPropertySheetItem(), 1, row++, 1, 1);
                } else {
                    propertiestable.add(new Label(value.toString()), 1, row++, 1, 1);
                }
            }
            return propertiestable;
        }
    }
}
