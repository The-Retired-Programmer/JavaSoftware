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
package uk.theretiredprogrammer.sketch.display.control;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.core.control.Failure;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PathWithShortName;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.ui.DisplayPane;
import uk.theretiredprogrammer.sketch.display.ui.DisplayWindow;
import uk.theretiredprogrammer.sketch.fileselector.control.FileSelectorController;
import uk.theretiredprogrammer.sketch.properties.control.PropertiesController;

public class DisplayController extends AbstractController<DisplayWindow> {

    private PropertiesController propertiescontroller;
    private DecisionController decisioncontroller;
    private FileSelectorController fileselectorcontroller;
    private SimulationController simulationcontroller;
    private DisplayPane displaygroup;

    public DisplayController(PathWithShortName pn, FileSelectorController fileselectorcontroller) {
        this.fileselectorcontroller = fileselectorcontroller;
        propertiescontroller = new PropertiesController(pn);
        decisioncontroller = new DecisionController(pn.toString(), false);
        simulationcontroller = new SimulationController(this);
        showDisplayWindow(pn.toString());
    }

    public DisplayController(String resourcename) {
        propertiescontroller = new PropertiesController(resourcename);
    }

    public DisplayController(String resourcename, String fn, FileSelectorController fileselectorcontroller) {
        this(resourcename);
        decisioncontroller = new DecisionController(fn, false);
        this.fileselectorcontroller = fileselectorcontroller;
        showDisplayWindow(fn);
    }

    private void showDisplayWindow(String fn) {
        displaygroup = new DisplayPane(this);
        setWindow(new DisplayWindow(fn, this, this.getModel(), displaygroup));
        propertiescontroller.getProperty().setOnChange(() -> refreshrepaint());
    }

    @Override
    protected void whenWindowIsClosing() {
        simulationcontroller.close();
        propertiescontroller.close();
        decisioncontroller.close();
    }

    @Override
    protected void whenWindowIsClosedExternally() {
        fileselectorcontroller.removeparentchildrelationship(this);
    }

    private void resetObjectProperties() {
//        try {
//            createObjectProperties(configfilecontroller.getParsedConfigFile());
//        } catch (IOException ex) {
//            writetostatusline.accept(ex.getLocalizedMessage());
//        }
//        sketchchangeaction.run();
    }

    public void resetSimulation() {
        simulationcontroller.stop();
        decisioncontroller.clear();
        resetObjectProperties();
    }

    public final SketchModel getModel() {
        return propertiescontroller.getProperty();
    }

    public void save(DisplayController controller, String fn) {
        Path path = Path.of(fn);
        JsonObject jobj = controller.getModel().toJson();
        if (path != null) {
            try ( JsonWriter jsonWriter = Json.createWriter(Files.newOutputStream(path))) {
                jsonWriter.write(jobj);
            } catch (IOException ex) {
                throw new Failure("Problem writting config file", ex);
            }
        } else {
            throw new IllegalStateFailure("Problem writting config file - file name illegal " + fn);
        }
    }

    public void showPropertiesWindow() {
        propertiescontroller.showWindow();
    }

    public void showFullDecisionWindow() {
        decisioncontroller.showWindow();
    }

    public void showFilteredDecisionWindow() {
        decisioncontroller.showWindow();
    }

    public SimulationController getSimulationController() {
        return simulationcontroller;
    }

    public void updateTimeField(int seconds) {
        getWindow().updateTimeField(seconds);
    }

    public void repaint() {
        displaygroup.repaint();
    }

    public void refreshrepaint() {
        displaygroup.refreshParameters();
    }

    public DecisionController getDecisionController() {
        return decisioncontroller;
    }
}
