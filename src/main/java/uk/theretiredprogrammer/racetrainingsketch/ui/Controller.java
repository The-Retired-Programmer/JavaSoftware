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
package uk.theretiredprogrammer.racetrainingsketch.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.openide.awt.StatusDisplayer;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boats;
import uk.theretiredprogrammer.racetrainingsketch.course.Course;
import uk.theretiredprogrammer.racetrainingsketch.flows.WaterFlow;
import uk.theretiredprogrammer.racetrainingsketch.flows.WindFlow;
import uk.theretiredprogrammer.racetrainingsketch.strategy.BoatStrategies;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;

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
    private final Consumer<String> displayupdaterequest;

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

    public Controller(DefFileDataObject dataobj, Consumer<String> displayupdaterequest) {
        this.displayupdaterequest = displayupdaterequest;
        try {
            createController(dataobj.getPrimaryFile().getInputStream());
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }

    public Controller(String resourcename, Consumer<String> displayupdaterequest) {
        this.displayupdaterequest = displayupdaterequest;
        try {
            createController(this.getClass().getResourceAsStream(resourcename));
        } catch (JsonException | IOException ex) {
            reportfailure(ex);
        }
    }

    Dimension getGraphicDimension() {
        return sailingarea.getGraphicDimension(displayparameters.zoom);
    }

    void paint(Graphics2D g2D) {
        try {
            double scale = displayparameters.zoom;
            sailingarea.draw(g2D, scale);
            windflow.draw(g2D, scale);
            if (waterflow != null) {
                waterflow.draw(g2D, scale);
            }
            course.draw(g2D, scale);
            boats.draw(g2D, scale);
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
        StatusDisplayer.getDefault().setStatusText(ex.getLocalizedMessage());
    }

    public void displaylog() {
        timerlog.write2output("Timer Log");
    }

    public void displayfilteredlog(String boatname) {
        timerlog.writefiltered2output("Timer Log", boatname);
    }

    /**
     * Act on a function keystroke.
     *
     * @param key the keystroke
     */
    // TODO - keyaction body disabled - needs to be reworked at a later date
    public void keyAction(String key) {
//        try {
//            sailingarea.actionKey(key);
//            for (BoatElement boat : boats.values()) {
//                boat.actionKey(key);
//            }
//        } catch (IOException ex) {
//            displayablefailuremessage = ex.getLocalizedMessage();
//        }

    }

    private TimerLog timerlog = new TimerLog();

    private class TimeStepRunner extends TimerTask {

        @Override
        public void run() {
            try {
                int secondsperdisplay = displayparameters.secondsperdisplay;
                while (secondsperdisplay > 0) {
//TODO timer call to actionFutureParameters disabled - will needto be enabled in the future                   
//                    sailingarea.actionFutureParameters(simulationtime);
                    timerlog.setTime(mmssformat(simulationtime));
                    windflow.timerAdvance(simulationtime, timerlog);
                    if (waterflow != null) {
                        waterflow.timerAdvance(simulationtime, timerlog);
                    }
                    boatstrategies.timerAdvance(Controller.this, simulationtime, timerlog);
                    secondsperdisplay--;
                    simulationtime++;
                }
                displayupdaterequest.accept("Time: " + mmssformat(simulationtime));
            } catch (IOException ex) {
                reportfailure(ex);
            }
        }
    }

    private String mmssformat(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        return Integer.toString(mins) + ":" + ss;
    }
}
