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
package uk.theretiredprogrammer.sketch.display.ui;

import javafx.scene.Group;
import javafx.scene.text.Text;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author richard
 */
public class DisplayWindow extends AbstractWindow<DisplayController> {

    private DisplayPane pane;
    private final Text timetext;
    private Group group;

    public DisplayWindow(String fn, DisplayController controller, PropertySketch sketchproperty) {
        super(DisplayWindow.class, controller);
        setTitle("SKETCH Scenario Viewer - " + fn);
        setDefaultWindow();
        addtoMenubar(
                UI.menu("Control",
                        UI.menuitem("Start", ev -> controller.getSimulationController().start()),
                        UI.menuitem("Pause", ev -> controller.getSimulationController().stop()),
                        UI.menuitem("Reset", ev -> controller.resetSimulation())
                ),
                UI.menu("Window Management",
                        UI.menuitem("Show Properties", ev -> controller.showPropertiesWindow()),
                        UI.menuitem("Show Decision Log", ev -> controller.showFullDecisionWindow()),
                        UI.menuitem("Show Filtered Decision Log", ev -> controller.showFilteredDecisionWindow())
                )
        );
        addtoToolbar(
                UI.toolbarButton("control_play_blue.png", "Start", ev -> controller.getSimulationController().start()),
                UI.toolbarButton("control_pause_blue.png", "Pause", ev -> controller.getSimulationController().stop()),
                UI.toolbarButton("control_rewind_blue.png", "Reset", ev -> controller.resetSimulation()),
                UI.toolbarButton("table_save.png", "Save Properties", ev -> controller.save(controller,
                "/Users/richard/SKETCHSAVE/save.json")),
                UI.toolbarButton("table.png", "Show Properties", ev -> controller.showPropertiesWindow()),
                UI.toolbarButton("script.png", "Show Decision Log", ev -> controller.showFullDecisionWindow()),
                UI.toolbarButton("script_code.png", "Show Filtered Decision Log", ev -> controller.showFilteredDecisionWindow()),
                UI.toolbarButton("arrow_refresh.png", "Refresh", ev -> controller.refreshrepaint()),
                timetext = new Text("      ")
        );
        group = new Group();
        pane = controller.getDisplayPanePainter();
        group.getChildren().add(pane);
        setContent(group, SCROLLABLE);
        build();
        show();
    }
    
    public void setDisplayPane(DisplayPane pane) {
        group.getChildren().clear();
        group.getChildren().add(pane);
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
