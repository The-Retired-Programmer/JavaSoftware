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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import uk.theretiredprogrammer.sketch.boats.Boats;
import uk.theretiredprogrammer.sketch.course.Course;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.strategy.BoatStrategies;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Controller {

    public SailingArea sailingarea;
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
    private final Consumer<Integer> displayupdaterequest;
    private Consumer<String> outwriter;

    private void createController(InputStream is) throws JsonException, IOException {
        simulationtime = 0;
        JsonObject parsedjson;
        try ( JsonReader rdr = Json.createReader(is)) {
            parsedjson = rdr.readObject();
        }
        sailingarea = new SailingArea(parsedjson);
        displayparameters = new DisplayParameters(parsedjson);
        waterflow = WaterFlow.create(() -> this, parsedjson);
        windflow = WindFlow.create(() -> this, parsedjson);
        course = new Course(() -> this, parsedjson);
        boats = new Boats(() -> this, parsedjson);
        boatstrategies = new BoatStrategies(this);
    }

    public Controller(Path path, Consumer<Integer> displayupdaterequest, Consumer<String> outwriter) {
        this.displayupdaterequest = displayupdaterequest;
        this.outwriter = outwriter;
        try {
            createController(Files.newInputStream(path));
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }

    public Controller(String resourcename, Consumer<Integer> displayupdaterequest, Consumer<String> outwriter) {
        this.displayupdaterequest = displayupdaterequest;
        this.outwriter = outwriter;
        try {
            createController(this.getClass().getResourceAsStream(resourcename));
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }

    public Dimension getGraphicDimension() {
        return sailingarea.getGraphicDimension(displayparameters.zoom);
    }

    public void paint(Graphics2D g2D) {
        try {
            sailingarea.draw(g2D, displayparameters.zoom);
            windflow.draw(g2D, displayparameters.zoom);
            if (waterflow != null) {
                waterflow.draw(g2D, displayparameters.zoom);
            }
            course.draw(g2D, displayparameters.zoom);
            boats.draw(g2D, displayparameters.zoom);
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
        int rate = (int) (displayparameters.secondsperdisplay * 1000 / displayparameters.speedup);
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
        timerlog.write2output(outwriter);
    }

    public void displayfilteredlog(String boatname) {
        timerlog.writefiltered2output(outwriter, boatname);
    }

    private TimerLog timerlog = new TimerLog();

    private class TimeStepRunner extends TimerTask {

        @Override
        public void run() {
            try {
                int secondsperdisplay = displayparameters.secondsperdisplay;
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
                displayupdaterequest.accept(simulationtime);
            } catch (IOException ex) {
                reportfailure(ex);
            }
        }
    }
}
