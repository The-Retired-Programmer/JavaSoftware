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
package uk.theretiredprogrammer.lafe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.DARKGREY;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import uk.theretiredprogrammer.lafe.ProbeCommands.ProbeState;

public class FrontPanelControls extends VBox {

    private ProbeState state;

    private Lamp connectedlamp;

    public FrontPanelControls(FrontPanelController controller, ProbeCommands probecommands) {
        super(10);
        try {
            this.getChildren().addAll(
                    connectedlamp = new Lamp("Probe connected", RED)
            );
            if (probecommands.ping()) {
                connectedlamp.changeColour(GREEN);
            }
            if (probecommands.getState()) {
                state = probecommands.getLastStateResponse();
            }
            this.getChildren().addAll(
                    new Lamp("Probe Sampling", RED),
                    new StopGoButton(controller, probecommands)
            );
        } catch (IOException ex) {
            // error in sending command
        }
    }

    public class Lamp extends Group {

        private final Circle colourlens;

        public Lamp(String label, Color colour) {
            colourlens = new Circle(40, 20, 10, colour);
            this.getChildren().addAll(
                    new Circle(40, 20, 15, DARKGREY),
                    colourlens,
                    new Text(label)
            );
        }

        public void changeColour(Color newcolour) {
            colourlens.setFill(newcolour);
        }
    }

    public class StopGoButton extends Group {

        private final Button button;
        private final ProbeCommands probecommands;
        private final FrontPanelController controller;

        public StopGoButton(FrontPanelController controller, ProbeCommands probecommands) {
            this.controller = controller;
            this.probecommands = probecommands;
            button = new Button(getButtonText());
            button.relocate(0, 20);
            this.getChildren().add(button);
            button.setOnAction((ev) -> buttonpressed(ev));
        }

        private String getButtonText() {
            switch (state) {
                case STATE_IDLE -> {
                    return "Start Sampling";
                }
                case STATE_SAMPLING -> {
                    return "Stop Sampling";
                }
                case STATE_SAMPLING_DONE -> {
                    return "Get Sample";
                }
                default ->
                    throw new IllegalStateFailure("illegal state");
            }
        }

        public void buttonpressed(Event ev) {
            switch (state) {
                case STATE_IDLE:
                    try {
                    // start sampling
                    probecommands.start();
                } catch (IOException ex) {
                    // failure to start
                    return;
                }
                break;
                case STATE_SAMPLING:
                    try {
                    // stop sampling request
                    probecommands.stop();
                } catch (IOException ex) {
                    // failure to stop
                    return;
                }
                break;
                case STATE_SAMPLING_DONE:
                    Map<Integer, List<String>> samples = new HashMap<>();
                    try {
                        probecommands.data(samples);
                    } catch (IOException ex) {
                        throw new Failure(ex);
                    }
                    controller.setData(samples);
            }
            try {
                if (probecommands.getState()) {
                    state = probecommands.getLastStateResponse();
                }
            } catch (IOException ex) {
                throw new Failure(ex);
            }
            button.setText(getButtonText());
        }
    }
}
