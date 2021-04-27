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

import static javafx.geometry.Orientation.VERTICAL;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.Display3DController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public class Display3DWindow extends AbstractWindow<Display3DController> {

    private final Text timetext;
    private Slider angleslider;
//    private Slider xpointtoslider;
//    private Slider ypointtoslider;
//    private Slider zpointtoslider;
    private Slider elevationslider;
    private Slider distanceslider;
    private Slider fieldofviewslider;

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
        createUIcontrols(sketchproperty);
        addToHControlArea(
                //                new VBox(5, new Label("Point to X"), xpointtoslider),
                //                new VBox(5, new Label("Point to Y"), ypointtoslider),
                //                new VBox(5, new Label("Point to Z"), zpointtoslider),
                new VBox(5, new Label("Angle"), angleslider),
                new VBox(5, new Label("Elevation"), elevationslider),
                new VBox(5, new Label("Distance"), distanceslider),
                new VBox(5, new Label("Field of View"), fieldofviewslider)
        );
        pane.setCameraAngle(angleslider.valueProperty());
//        pane.setCameraPointTo(xpointtoslider.valueProperty(), ypointtoslider.valueProperty(), zpointtoslider.valueProperty());
        pane.setCameraElevation(elevationslider.valueProperty());
        pane.setCameraDistance(distanceslider.valueProperty());
        pane.setCameraFieldOfView(fieldofviewslider.valueProperty());
        double zoom = sketchproperty.getDisplay().getZoom();
        pane.getTransforms().add(new Scale(zoom, zoom, zoom));
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

    private void createUIcontrols(SketchModel sketchproperty) {
        Area displayarea = sketchproperty.getDisplay().getDisplayarea();
        double arearadius = Math.max(displayarea.getWidth(), displayarea.getHeight()) / 2;
        angleslider = createslider(-180, 180, 10, 30, 0);
//        xpointtoslider = createslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
//        ypointtoslider = createslider(0, 1000, 100, 250, DISPLAYSIZE / 2);
//        zpointtoslider = createslider(-1000, 0, 100, 250, 0);
        elevationslider = createslider(0, 90, 10, 30, 45);
        distanceslider = createslider(0, 1000, 100, 250, arearadius);
        fieldofviewslider = createslider(0, 180, 10, 30, 120);
    }

    private Slider createslider(double min, double max, double tickunit, double majortickunit, double initialvalue) {
        Slider slider = new Slider(min, max, initialvalue);
        slider.setOrientation(VERTICAL);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(majortickunit);
        slider.setBlockIncrement(tickunit);
        slider.setSnapToTicks(true);
        return slider;
    }
}
