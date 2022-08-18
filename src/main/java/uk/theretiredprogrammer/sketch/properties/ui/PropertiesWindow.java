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

import javafx.scene.control.Menu;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.properties.control.PropertiesController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.flows.FlowComponent;

public class PropertiesWindow extends AbstractWindow<PropertiesController> {

    public PropertiesWindow(String fn, PropertiesController controller, SketchModel sketchmodel) {
        super(PropertiesWindow.class, controller);
        setDefaultWindowLeftOffset(800);
        setTitle("SKETCH Properties Viewer - " + fn);
        setContent(new PropertiesPane(sketchmodel));

        Menu boatmenu = UI.menu("Add Boat");
        Boat.getClasses().forEach(classname -> {
            boatmenu.getItems().add(UI.menuitem(classname, ev -> controller.addNewBoat(classname)));
        });
        Menu windmenu = UI.menu("Add WindFlowComponent");
        FlowComponent.getTypenames().forEach(typename -> {
            windmenu.getItems().add(UI.menuitem(typename, ev -> controller.addNewWindFlowComponent(typename)));
        });
        Menu watermenu = UI.menu("Add WaterFlowComponent");
        FlowComponent.getTypenames().forEach(typename -> {
            watermenu.getItems().add(UI.menuitem(typename, ev -> controller.addNewWaterFlowComponent(typename)));
        });
        addtoMenubar(
                boatmenu,
                windmenu,
                watermenu,
                UI.menu("Add  Course Elements",
                        UI.menuitem("Add Mark", ev -> controller.addNewMark()),
                        UI.menuitem("Add Course Leg", ev -> controller.addNewLeg())
                )
        );
        addtoToolbar(
                UI.toolbarButton("shape_flip_horizontal.png", "Add Laser2", ev -> controller.addNewBoat("laser2")),
                UI.toolbarButton("pencil.png", "Add Mark", ev -> controller.addNewMark())
        );
        build();
    }
}
