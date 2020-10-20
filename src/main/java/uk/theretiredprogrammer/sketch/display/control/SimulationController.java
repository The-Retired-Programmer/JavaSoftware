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

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import uk.theretiredprogrammer.sketch.core.control.Execute;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SimulationController extends TimerTask {

    private int simulationtime = 0;
    private boolean isRunning;
    private Timer timer;
    private final PropertySketch sketchproperty;
    private final DisplayController controller;

    public SimulationController(DisplayController controller, PropertySketch sketchproperty) {
        this.sketchproperty = sketchproperty;
        this.controller = controller;
    }

    public void start() {
            if (isRunning) {
                return;
            }
            int rate = (int) (sketchproperty.getDisplay().getSecondsperdisplay() * 1000 / sketchproperty.getDisplay().getSpeedup());
            timer = new Timer();
            timer.scheduleAtFixedRate(this, 0, rate);
            isRunning = true;
    }

    public void stop() {
            if (!isRunning) {
                return;
            }
            isRunning = false;
            timer.cancel();
    }

    @Override
    public void run() {
        new Execute().setExceptionHandler(() -> stop())
                .run(() -> {
                    int secondsperdisplay = sketchproperty.getDisplay().getSecondsperdisplay();
                    while (secondsperdisplay > 0) {
                        DecisionController decisioncontroller = controller.getDecisionController();
                        decisioncontroller.setTime(simulationtime);
                        controller.windflow.timerAdvance(simulationtime, decisioncontroller);
                        controller.waterflow.timerAdvance(simulationtime, decisioncontroller);
                        controller.boatstrategies.timerAdvance(sketchproperty, simulationtime, decisioncontroller,
                                controller.windflow, controller.waterflow);
                        secondsperdisplay--;
                        simulationtime++;
                    }
                    controller.updateTimeField(simulationtime);
                    Execute.runLater(() -> controller.repaint());
                });
    }
}
