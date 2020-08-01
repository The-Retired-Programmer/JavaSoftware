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
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Controller {

    private int simulationtime;
    private Scenario scenario;
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
        scenario = new Scenario(parsedjson);
        scenario.setWaterFlow(WaterFlow.create(parsedjson, scenario));
        scenario.setWindFlow(WindFlow.create(parsedjson, scenario));
        scenario.setCourse(new Course(parsedjson, scenario));
        scenario.setBoats(new Boats(parsedjson, scenario, scenario.getCourse().getFirstCourseLeg()));
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
    
    public Scenario getScenario() {
        return scenario;
    }

    Dimension getGraphicDimension() {
        return scenario.getGraphicDimension();
    }

    void paint(Graphics2D g2D) {
        try {
            scenario.draw(g2D);
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
        int rate = (int) (scenario.getSecondsperdisplay() * 1000 / scenario.getSpeedup());
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
//            scenario.actionKey(key);
//            for (BoatElement boat : boats.values()) {
//                boat.actionKey(key);
//            }
//        } catch (IOException ex) {
//            displayablefailuremessage = ex.getLocalizedMessage();
//        }

    }
    
    private TimerLog timerlog  = new TimerLog();

    private class TimeStepRunner extends TimerTask {
        
        @Override
        public void run() {
            try {
                int secondsperdisplay = scenario.getSecondsperdisplay();
                while (secondsperdisplay > 0) {
//TODO timer call to actionFutureParameters disabled - will needto be enabled in the future                   
//                    scenario.actionFutureParameters(simulationtime);
                    timerlog.setTime(mmssformat(simulationtime));
                    scenario.timerAdvance(simulationtime, timerlog);
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
