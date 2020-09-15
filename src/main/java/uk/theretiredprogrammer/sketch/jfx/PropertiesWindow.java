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

import java.util.Map;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertiesWindow extends AbstractWindow{
    
    public static PropertiesWindow create(String title, Controller controller, AbstractWindow parent) {
        return new PropertiesWindow(title, controller, parent);
    }

    private PropertiesWindow(String title, Controller controller, AbstractWindow parent) {
        super(PropertiesWindow.class, parent);
        setTitle(title);
        setContent(new PropertiesPane(controller));
        setOnCloseAction((e) -> closeIncludingChildren());
        show();
    }

    private class PropertiesPane extends Accordion {

        public PropertiesPane(Controller controller) {
            this.getPanes().clear();
            this.getPanes().addAll(
                    new PropertiesSection("Display", controller.displayparameters.properties()),
                    new PropertiesSection("Wind Flow", controller.windflow.properties())
            );
            controller.windflow.getFlowComponentSet().getComponents().forEach(component -> {
                int level = component.getZlevel();
                this.getPanes().add(new PropertiesSection("Wind Component - " + level, component.properties()));
            });
            WaterFlow waterflow = controller.waterflow;
            if (waterflow != null) {
                this.getPanes().add(
                        new PropertiesSection("Water Flow", waterflow.properties())
                );
                controller.waterflow.getFlowComponentSet().getComponents().forEach(component
                        -> this.getPanes().add(new PropertiesSection("Water Component", component.properties()))
                );
            }
            this.getPanes().add(
                    new PropertiesSection("Course", controller.course.properties())
            );
            controller.course.getMarks().forEach(mark -> {
                this.getPanes().add(
                        new PropertiesSection("Mark - " + mark.name, mark.properties())
                );
            });
            controller.boats.getBoats().forEach(boat -> {
                this.getPanes().add(
                        new PropertiesSection("Boat - " + boat.name, boat.properties())
                );
            });
        }
    }

    private class PropertiesSection extends TitledPane {

        public PropertiesSection(String propertiessectionname, Map<String, Object> properties) {
            GridPane propertiestable = new GridPane();
            int row = 0;
            for (Map.Entry<String, Object> mapentry : properties.entrySet()) {
                propertiestable.add(new Label(mapentry.getKey()), 0, row, 1, 1);
                Object value = mapentry.getValue();
                if (value instanceof Color) {
                    propertiestable.add(new Label(ColorParser.color2String((Color) value)), 1, row++, 1, 1);
                } else if (value instanceof PropertyItem) {
                    propertiestable.add(((PropertyItem) value).createPropertySheetItem(), 1, row++, 1, 1);
                } else {
                    propertiestable.add(new Label(value.toString()), 1, row++, 1, 1);
                }
            }
            this.setText(propertiessectionname);
            this.setContent(new ScrollPane(propertiestable));
        }
    }
}
