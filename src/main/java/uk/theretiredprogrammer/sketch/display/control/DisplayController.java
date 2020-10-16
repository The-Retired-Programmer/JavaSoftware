/*
 * Copyright 2020 richard linsdale.
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
import uk.theretiredprogrammer.sketch.display.ui.DisplayPainter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.display.entity.course.Course;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;
import uk.theretiredprogrammer.sketch.display.control.strategy.BoatStrategies;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.display.ui.DisplayPane;
import uk.theretiredprogrammer.sketch.display.ui.DisplayWindow;
import uk.theretiredprogrammer.sketch.properties.control.PropertiesController;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DisplayController extends AbstractController {

    private DisplayWindow sketchwindow;
    private PropertiesController propertiescontroller;
    private final DecisionController decisioncontroller = new DecisionController();
    public BoatStrategies boatstrategies;
    public WindFlow windflow;
    public WaterFlow waterflow;
    public Course course;
    public Boats boats;
    private DisplayPainter painter;
    private String fn;

    public void createFromFile(Path path) {
        propertiescontroller = new PropertiesController();
        propertiescontroller.fileReadUpgradeParse(path);
        createDisplayEntities();
    }

    public void createFromResource(String resourcename) {
        propertiescontroller = new PropertiesController();
        propertiescontroller.resourceReadUpgradeParse(resourcename);
        createDisplayEntities();
    }

    private void createDisplayEntities() {
        PropertySketch sketchproperty = propertiescontroller.getProperty();
        simulationtime = 0;
        windflow = new WindFlow(sketchproperty);
        waterflow = new WaterFlow(sketchproperty);
        course = new Course(sketchproperty);
        boats = new Boats(sketchproperty);
        boatstrategies = new BoatStrategies(sketchproperty, course, boats, windflow, waterflow);
        painter = new DisplayPainter(sketchproperty, windflow, waterflow, boats);
    }

    public void showDisplayWindow(String fn, AbstractWindow parent) {
        this.fn = fn;
        sketchwindow = new DisplayWindow(fn, this, this.getProperty(), parent);
    }

    public DisplayPane getDisplayPanePainter() {
        return painter;
    }

    private void resetObjectProperties() {
//        try {
//            createObjectProperties(configfilecontroller.getParsedConfigFile());
//        } catch (IOException ex) {
//            writetostatusline.accept(ex.getLocalizedMessage());
//        }
//        sketchchangeaction.run();
    }

    public PropertySketch getProperty() {
        return propertiescontroller.getProperty();
    }

    public boolean save(DisplayController controller, String fn) {
        Path path = Path.of(fn);
        JsonObject jobj = controller.getProperty().toJson();
        if (path != null) {
            //Files.move(path, path.resolveSibling(path.getFileName() + ".v" + fromversion));
            try ( JsonWriter jsonWriter = Json.createWriter(Files.newOutputStream(path))) {
                jsonWriter.write(jobj);
            } catch (IOException ex) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void showPropertiesWindow() {
        propertiescontroller.showPropertiesWindow(fn, sketchwindow);
    }

    public void showFullDecisionWindow() {
        decisioncontroller.showFullDecisionWindow(fn, sketchwindow);
    }

    public void showFilteredDecisionWindow() {
        decisioncontroller.showFilteredDecisionWindow(fn, sketchwindow);
    }

    //  the TIMER CONTROLLER STUFF //
    private int simulationtime;
    private boolean isRunning;
    private Timer timer;
    private TimeStepRunner runner;

    public void start() {
        if (isRunning) {
            return;
        }
        int rate = (int) (getProperty().getDisplay().getSecondsperdisplay() * 1000 / getProperty().getDisplay().getSpeedup());
        timer = new Timer();
        runner = new TimeStepRunner();
        timer.scheduleAtFixedRate(runner, 0, rate);
        isRunning = true;
    }

    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        timer.cancel();
    }

    public void reset() {
        stop();
        decisioncontroller.clear();
        resetObjectProperties();
    }

    private class TimeStepRunner extends TimerTask {

        @Override
        public void run() {
            new WorkRunner(() -> doWork())
                    .setExceptionHandler(() -> stop())
                    .run();
        }

        private void doWork() {
            int secondsperdisplay = getProperty().getDisplay().getSecondsperdisplay();
            while (secondsperdisplay > 0) {
                decisioncontroller.setTime(simulationtime);
                windflow.timerAdvance(simulationtime, decisioncontroller);
                waterflow.timerAdvance(simulationtime, decisioncontroller);
                boatstrategies.timerAdvance(getProperty(), simulationtime, decisioncontroller, windflow, waterflow);
                secondsperdisplay--;
                simulationtime++;
            }
            sketchwindow.updateTimeField(simulationtime);
            Platform.runLater(() -> painter.repaint());
        }
    }
}
