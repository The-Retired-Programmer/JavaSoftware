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
package uk.theretiredprogrammer.sketch.ui;

import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.scene.canvas.Canvas;
import uk.theretiredprogrammer.sketch.boats.Boats;
import uk.theretiredprogrammer.sketch.course.Course;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.strategy.BoatStrategies;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;
import uk.theretiredprogrammer.sketch.upgraders.ConfigFileController;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Controller {

    public DisplayParameters displayparameters;
    public WindFlow windflow;
    public WaterFlow waterflow;
    public Course course;
    public Boats boats;
    public BoatStrategies boatstrategies;
    //
    private int simulationtime;
    private boolean isRunning;
    private Timer timer;
    private TimeStepRunner runner;
    private Runnable updatedisplay = () ->{};
    private Consumer<Integer> updatetime = (t) -> {};
    private Consumer<String> showdecisionLine = (l)-> {};

    public Controller(Path path) {
        try {
            ConfigFileController configfilecontroller = new ConfigFileController(path);
            if (configfilecontroller.needsUpgrade()) {
                configfilecontroller.upgrade();
            }
            createObjectProperties(configfilecontroller.getParsedConfigFile());
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }

    public Controller(String resourcename) {
        try {
            ConfigFileController configfilecontroller = new ConfigFileController(this.getClass().getResourceAsStream(resourcename));
            if (configfilecontroller.needsUpgrade()) {
                configfilecontroller.upgrade();
            }
            createObjectProperties(configfilecontroller.getParsedConfigFile());
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }
    
    public Controller setOnSketchChange(Runnable updatesketch){
        this.updatedisplay = updatesketch;
        return this;
    }
    
    public Controller setOnTimeChange(Consumer<Integer> updatetime){
        this.updatetime = updatetime;
        return this;
    }
    
    public Controller setShowDecisionLine(Consumer<String> showdecisionLine){
        this.showdecisionLine = showdecisionLine;
        return this;
    }

    private void createObjectProperties(JsonObject parsedjson) throws IOException {
        simulationtime = 0;
        displayparameters = new DisplayParameters(parsedjson);
        waterflow = WaterFlow.create(() -> this, parsedjson);
        windflow = WindFlow.create(() -> this, parsedjson);
        course = new Course(() -> this, parsedjson);
        boats = new Boats(() -> this, parsedjson);
        boatstrategies = new BoatStrategies(this);
    }

    public double getWidth() {
        return displayparameters.getWidth(displayparameters.getZoom());
    }

    public double getHeight() {
        return displayparameters.getHeight(displayparameters.getZoom());
    }

    public void paint(SketchPane canvas) {
        canvas.clear();
        try {
            displayparameters.draw(canvas, displayparameters.getZoom());
            windflow.draw(canvas, displayparameters.getZoom());
            if (waterflow != null) {
                waterflow.draw(canvas, displayparameters.getZoom());
            }
            course.draw(canvas, displayparameters.getZoom());
            boats.draw(canvas, displayparameters.getZoom());
        } catch (IOException ex) {
            reportfailure(ex);
        }
    }

    /**
     * Start running the simulation.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        int rate = (int) (displayparameters.getSecondsperdisplay() * 1000 / displayparameters.getSpeedup());
        timer = new Timer();
        runner = new TimeStepRunner();
        timer.scheduleAtFixedRate(runner, 0, rate);
        isRunning = true;
    }

    /**
     * Terminate the simulation.
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        timer.cancel();
    }

    public void reset() {
        stop();
        timerlog.clear();
    }

    private void reportfailure(Exception ex) {
        System.out.println(ex.getLocalizedMessage());
    }

    public void displaylog() {
        timerlog.write2output(showdecisionLine);
    }

    public void displayfilteredlog(String boatname) {
        timerlog.writefiltered2output(showdecisionLine, boatname);
    }

    private TimerLog timerlog = new TimerLog();

    private class TimeStepRunner extends TimerTask {

        @Override
        public void run() {
            try {
                int secondsperdisplay = displayparameters.getSecondsperdisplay();
                while (secondsperdisplay > 0) {
                    timerlog.setTime(simulationtime);
                    windflow.timerAdvance(simulationtime, timerlog);
                    if (waterflow != null) {
                        waterflow.timerAdvance(simulationtime, timerlog);
                    }
                    boatstrategies.timerAdvance(Controller.this, simulationtime, timerlog);
                    secondsperdisplay--;
                    simulationtime++;
                }
                updatetime.accept(simulationtime);
                updatedisplay.run();

            } catch (IOException ex) {
                reportfailure(ex);
            }
        }
    }
}
