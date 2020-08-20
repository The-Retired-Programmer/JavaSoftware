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
package uk.theretiredprogrammer.sketch.jfx;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author richard
 */
public class SketchPane {
    
    public static VBox create() {
        return (new SketchPane()).get();
    }
    
    private Label timeDisplay = new Label("");
    
    public VBox get() {
    
    // control buttons
        var controlButton = new Button("Start");
        controlButton.setOnAction((e)-> SketchPane.control(controlButton, e));
        //
        var resetButton = new Button("Reset to Start");
        resetButton.setOnAction((e)-> SketchPane.reset(controlButton, e));
        //
        var displayLogButton = new Button("Display Decision Log");
        displayLogButton.setOnAction((e)-> SketchPane.displaylog(e));
        //
        var displayFilteredlogButton = new Button("Display Selected Decision Log");
        displayFilteredlogButton.setOnAction((e)-> SketchPane.displayfilteredlog(e));
        //
        updateTimedisplay(0);
        var controls = new HBox();
        controls.getChildren().add(resetButton);
        controls.getChildren().add(controlButton);
        controls.getChildren().add(displayLogButton);
        controls.getChildren().add(displayFilteredlogButton);
        controls.getChildren().add(timeDisplay);
        
        VBox display = new VBox(new Label("Display"));
        display.getChildren().add(controls);
        return display;
    }
    
    public void updateTimedisplay(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        timeDisplay.setText("Time: " + Integer.toString(mins) + ":" + ss);
    }

    public static boolean running = false;

    public static final void control(Button button, ActionEvent e) {
        if (running) {
            // stop running
            running = false;
            // stop the display
            button.setText("Continue");
        } else {
            // start the display
            // start running
            running = true;
            button.setText("Pause");
        }
    }

    public static final void reset(Button controlbutton, ActionEvent e) {
        if (running) {
            // stop running
            running = false;
        }
        // reset the display
        controlbutton.setText("Start");
    }
    
    public static final void displaylog(ActionEvent e) {
        //controller.displaylog();
    }
    public static final void displayfilteredlog(ActionEvent e) {
        //controller.displayfilteredlog("SELECTED");
    }
}
