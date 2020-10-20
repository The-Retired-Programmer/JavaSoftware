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

    private final DisplayPane pane;
    private final Text timetext;

    public DisplayWindow(String fn, DisplayController controller, PropertySketch sketchproperty) {
        super(DisplayWindow.class, controller);
        setTitle("SKETCH Scenario Viewer - " + fn);
        setDefaultWindow();
        addtoMenubar(
                UI.menu("Control",
                        UI.menuitem("Start", actionEvent -> controller.start()),
                        UI.menuitem("Pause", actionEvent -> controller.stop()),
                        UI.menuitem("Reset", actionEvent -> controller.reset())
                ),
                UI.menu("Window Management",
                        UI.menuitem("Show Properties", actionEvent -> controller.showPropertiesWindow()),
                        UI.menuitem("Show Decision Log", actionEvent -> controller.showFullDecisionWindow()),
                        UI.menuitem("Show Filtered Decision Log", actionEvent -> controller.showFilteredDecisionWindow())
                )
        );
        addtoToolbar(
                UI.toolbarButton("control_play_blue.png", "Start", actionEvent -> controller.start()),
                UI.toolbarButton("control_pause_blue.png", "Pause", actionEvent -> controller.stop()),
                UI.toolbarButton("control_rewind_blue.png", "Reset", actionEvent -> controller.reset()),
                UI.toolbarButton("table_save.png", "Save Properties", actionEvent -> controller.save(controller,
                "/Users/richard/SKETCHSAVE/save.json")),
                UI.toolbarButton("table.png", "Show Properties", actionEvent -> controller.showPropertiesWindow()),
                UI.toolbarButton("script.png", "Show Decision Log", actionEvent -> controller.showFullDecisionWindow()),
                UI.toolbarButton("script_code.png", "Show Filtered Decision Log", actionEvent -> controller.showFilteredDecisionWindow()),
                timetext = new Text("      ")
        );
        Group group = new Group();
        pane = controller.getDisplayPanePainter();
        group.getChildren().add(pane);
        setContent(group, SCROLLABLE);
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
