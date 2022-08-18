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
import uk.theretiredprogrammer.sketch.log.control.LogController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display3D.ui.Display3DPane;
import uk.theretiredprogrammer.sketch.display3D.ui.Display3DWindow;
import uk.theretiredprogrammer.sketch.fileselector.control.FileSelectorController;
import uk.theretiredprogrammer.sketch.properties.control.PropertiesController;

public class Display3DController extends AbstractController<Display3DWindow> {

    private PropertiesController propertiescontroller;
    private LogController logcontroller;
    private FileSelectorController fileselectorcontroller;
    private Simulation3DController simulationcontroller;
    private Display3DPane displaygroup;

    public Display3DController(PathWithShortName pn, FileSelectorController fileselectorcontroller) {
        this.fileselectorcontroller = fileselectorcontroller;
        propertiescontroller = new PropertiesController(pn);
        logcontroller = new LogController(pn.toString(), propertiescontroller.getModel());
        simulationcontroller = new Simulation3DController(this);
        showDisplayWindow(pn.toString());
    }

    public Display3DController(String resourcename) {
        propertiescontroller = new PropertiesController(resourcename);
    }

    public Display3DController(String resourcename, String fn, FileSelectorController fileselectorcontroller) {
        this(resourcename);
        logcontroller = new LogController(fn, propertiescontroller.getModel());
        this.fileselectorcontroller = fileselectorcontroller;
        showDisplayWindow(fn);
    }

    private void showDisplayWindow(String fn) {
        displaygroup = new Display3DPane(this);
        setWindow(new Display3DWindow(fn, this, this.getModel(), displaygroup));
        //propertiescontroller.getModel().setOnChange(() -> refreshrepaint());
    }

    @Override
    protected void whenWindowIsClosing() {
        simulationcontroller.close();
        propertiescontroller.close();
        logcontroller.close();
    }

    @Override
    protected void whenWindowIsClosedExternally() {
        fileselectorcontroller.remove3Dparentchildrelationship(this);
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
        logcontroller.clear();
        resetObjectProperties();
    }

    public final SketchModel getModel() {
        return propertiescontroller.getModel();
    }

    public void save(Display3DController controller, String fn) {
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

    public void showLogWindow() {
        logcontroller.showWindow();
    }

    public Simulation3DController getSimulationController() {
        return simulationcontroller;
    }

    public void updateTimeField(int seconds) {
        getWindow().updateTimeField(seconds);
    }

    public LogController getLogController() {
        return logcontroller;
    }
}
