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

import java.io.IOException;
import uk.theretiredprogrammer.sketch.jfx.AbstractWindow;
import uk.theretiredprogrammer.sketch.jfx.UI;
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
        setDefaultWindowLeftOffset(800);
        setTitle("SKETCH Properties Viewer - " + fn);
        setContent(new PropertiesPane(controller));
        addtoMenubar(
                UI.menu("Add Element",
                        UI.menuitem("Add Boat", actionEvent -> addnewboat(controller)),
                        UI.menuitem("Add Mark", actionEvent -> addnewmark(controller)),
                        UI.menuitem("Add WindFlowComponent", actionEvent -> addnewwindflow(controller)),
                        UI.menuitem("Add WaterFlowComponent", actionEvent -> addnewwaterflow(controller)),
                        UI.menuitem("Add Course Leg", actionEvent -> addNewLeg(controller))
                )
        );
        addtoToolbar(
                UI.toolbarButton("shape_flip_horizontal.png", "Add Boat", actionEvent -> addnewboat(controller)),
                UI.toolbarButton("pencil.png", "Add Mark", actionEvent -> addnewmark(controller))
        );
        show();
    }

    private void addnewboat(Controller controller) {
        try {
            controller.addNewBoat();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Boat: " + ex.getLocalizedMessage());
        }
    }

    private void addnewmark(Controller controller) {
        try {
            controller.addNewMark();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Mark: " + ex.getLocalizedMessage());
        }
    }

    private void addnewwindflow(Controller controller) {
        try {
            controller.addNewWindFlowComponent();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Wind Flow: " + ex.getLocalizedMessage());
        }
    }

    private void addnewwaterflow(Controller controller) {
        try {
            controller.addNewWaterFlowComponent();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Water Flow: " + ex.getLocalizedMessage());
        }
    }

    private void addNewLeg(Controller controller) {
        try {
            controller.addNewLeg();
        } catch (IOException ex) {
            statusbarDisplay("failed to create new Leg: " + ex.getLocalizedMessage());
        }
    }
}
