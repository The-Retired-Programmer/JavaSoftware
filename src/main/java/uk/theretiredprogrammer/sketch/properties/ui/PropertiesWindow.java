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

import javafx.scene.control.Menu;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.properties.control.PropertiesController;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyBoat;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyFlowComponent;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author richard
 */
public class PropertiesWindow extends AbstractWindow {

    public PropertiesWindow(String fn, PropertiesController controller, PropertySketch sketchproperty, AbstractWindow parent) {
        super(PropertiesWindow.class, parent);
        setDefaultWindowLeftOffset(800);
        setTitle("SKETCH Properties Viewer - " + fn);
        setContent(new PropertiesPane(sketchproperty));
        
        Menu boatmenu = UI.menu("Add Boat");
        for (var classname : PropertyBoat.getClasses()) {
            boatmenu.getItems().add(UI.menuitem(classname, actionEvent -> controller.addNewBoat(classname)));
        }
        Menu windmenu = UI.menu("Add WindFlowComponent");
        for(var typename : PropertyFlowComponent.getTypenames()){
            windmenu.getItems().add(UI.menuitem(typename, actionEvent -> controller.addNewWindFlowComponent(typename)));
        }
        Menu watermenu = UI.menu("Add WaterFlowComponent");
        for(var typename : PropertyFlowComponent.getTypenames()){
            watermenu.getItems().add(UI.menuitem(typename, actionEvent -> controller.addNewWaterFlowComponent(typename)));
        }
        addtoMenubar(
                boatmenu,
                windmenu,
                watermenu,
                UI.menu("Add  Course Elements",
                    UI.menuitem("Add Mark", actionEvent -> controller.addNewMark()),
                    UI.menuitem("Add Course Leg", actionEvent -> controller.addNewLeg())
                )
        );
        addtoToolbar(
                UI.toolbarButton("shape_flip_horizontal.png", "Add Laser2", actionEvent -> controller.addNewBoat("laser2")),
                UI.toolbarButton("pencil.png", "Add Mark", actionEvent -> controller.addNewMark())
        );
        show();
    }
}
