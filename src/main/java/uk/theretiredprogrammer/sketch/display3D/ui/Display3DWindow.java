/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display3D.ui;

import javafx.scene.text.Text;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.Display3DController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public class Display3DWindow extends AbstractWindow<Display3DController> {

    private final Text timetext;

    public Display3DWindow(String fn, Display3DController controller, SketchModel sketchproperty, Display3DPane pane) {
        super(Display3DWindow.class, controller);
        setTitle("SKETCH Scenario 3DViewer - " + fn);
        setDefaultWindow();
        addtoMenubar(
                UI.menu("Control",
                        UI.menuitem("Start", ev -> controller.getSimulationController().start()),
                        UI.menuitem("Pause", ev -> controller.getSimulationController().stop()),
                        UI.menuitem("Reset", ev -> controller.resetSimulation())
                ),
                UI.menu("Window Management",
                        UI.menuitem("Show Properties", ev -> controller.showPropertiesWindow()),
                        UI.menuitem("Show Decision Log", ev -> controller.showLogWindow())
                )
        );
        addtoToolbar(
                UI.toolbarButton("control_play_blue.png", "Start", ev -> controller.getSimulationController().start()),
                UI.toolbarButton("control_pause_blue.png", "Pause", ev -> controller.getSimulationController().stop()),
                UI.toolbarButton("control_rewind_blue.png", "Reset", ev -> controller.resetSimulation()),
                UI.toolbarButton("table_save.png", "Save Properties", ev -> controller.save(controller,
                "/Users/richard/SKETCHSAVE/save.json")),
                UI.toolbarButton("table.png", "Show Properties", ev -> controller.showPropertiesWindow()),
                UI.toolbarButton("script.png", "Show Decision Log", ev -> controller.showLogWindow()),
                timetext = new Text("      ")
        );
        setContent(pane, SCROLLABLE);
        build();
        show();
    }

    public void updateTimeField(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        timetext.setText(Integer.toString(mins) + ":" + ss);
    }
}
