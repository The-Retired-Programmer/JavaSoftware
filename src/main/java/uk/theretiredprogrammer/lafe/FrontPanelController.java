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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.event.Event;
import static javafx.scene.paint.Color.GREEN;
import static uk.theretiredprogrammer.lafe.FrontPanelController.ProbeState.STATE_IDLE;

public class FrontPanelController {

    private final USBSerialDevice usbdevice;
    private FrontPanelWindow window;
    private final ProbeConfiguration config;
    private Consumer<String> messagewriter;

    public FrontPanelController() {
        config = new ProbeConfiguration();
        usbdevice = new USBSerialDevice();
    }

    public final void open(FrontPanelWindow window) {
        this.window = window;
        setstatusmessagewriter((message) -> window.writestatusmessage(message));
        window.writestatusmessage(isprobeconnected() ? "Probe Connected" : "Probe connection failed");
    }
    
    private void setstatusmessagewriter(Consumer<String> messagewriter) {
        this.messagewriter = messagewriter;
    }
    
    
    private void displaymessage(String message) {
        if (!message.isBlank()) {
            messagewriter.accept(message);
        }
    }

    private void displaymessage(String message, int startindex) {
        if (message.length() > startindex) {
            message = message.substring(startindex);
            if (!message.isBlank()) {
                messagewriter.accept(message);
            }
        }
    }

    private boolean isprobeconnected() {
//        try {
//            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> doconnectedcheck());
//            while (!completableFuture.isDone()) {
//                Platform.runLater( () -> probecommands.displaymessage("Waiting to connect to probe"));
//            }
//            Platform.runLater( () -> probecommands.displaymessage(""));
//            return completableFuture.get();
//        } catch (InterruptedException | ExecutionException ex) {
//            return false;
//        }
        return doconnectedcheck();
    }

    private boolean doconnectedcheck() {
        try {
            if (ping()) {
                window.setConnectedLampColour(GREEN);
            }
            getState();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public final void close() throws IOException {
        usbdevice.close();
        window.close();
    }

    public final ProbeConfiguration getProbeConfiguration() {
        return config;
    }

    // -------------------------------------------------------------------------
    //
    //  Probe commands
    //
    // -------------------------------------------------------------------------
    
    public enum ProbeState {
        STATE_IDLE(0), STATE_SAMPLING(1), STATE_STOPPING_SAMPLING(2), STATE_SAMPLING_DONE(3);

        private final int numericvalue;

        ProbeState(int numericvalue) {
            this.numericvalue = numericvalue;
        }

        public int numericvalue() {
            return this.numericvalue;
        }
    }

    private ProbeState state = STATE_IDLE;

    static ProbeState getProbeState(int numericvalue) {
        for (ProbeState state : ProbeState.values()) {
            if (state.numericvalue() == numericvalue) {
                return state;
            }
        }
        throw new IllegalProgramStateFailure("Unknow probestate numeric value during lookup: " + numericvalue);
    }

    public String getButtonText() {
        switch (state) {
            case STATE_IDLE -> {
                return "Start Sampling";
            }
            case STATE_SAMPLING -> {
                return "Stop Sampling";
            }
            case STATE_STOPPING_SAMPLING -> {
                return "Stopping  in Progress";
            }
            case STATE_SAMPLING_DONE -> {
                return "Get Sample";
            }
            default ->
                throw new IllegalProgramStateFailure("illegal state");
        }
    }

    public void buttonpressedaction(Event ev) {
        switch (state) {
            case STATE_IDLE -> {
                try {
                    // start sampling
                    start();
                } catch (IOException ex) {
                    // failure to start
                    return;
                }
            }
            case STATE_SAMPLING -> {
                try {
                    // stop sampling request
                    stop();
                } catch (IOException ex) {
                    // failure to stop
                    return;
                }
            }
            case STATE_STOPPING_SAMPLING -> {
            }
            case STATE_SAMPLING_DONE -> {
                try {
                    data();
                } catch (IOException ex) {
                    throw new Failure(ex);
                }
                window.refreshSampleDisplay(samples);
            }
        }
        try {
            getState();
        } catch (IOException ex) {
            throw new Failure(ex);
        }
        window.setStopGoButtonText(getButtonText());
    }

    public boolean ping() throws IOException {
        return sendCommandAndHandleResponse("p", (s) -> probetypeExpected(s));
    }

    private boolean probetypeExpected(String response) {
        config.probetype = response;
        displaymessage(response);
        return true;
    }

    public boolean getState() throws IOException {
        return sendCommandAndHandleResponse("?", (s) -> statusExpected(s));
    }

    private boolean statusExpected(String responseline) {
        int response = Integer.parseInt(responseline);
        state = getProbeState(response);
        return true;
    }

    public boolean start() throws IOException {
        return sendCommandAndHandleResponse(config.getprobecommand("g"), (s) -> onlyYNExpected(s));
    }

    private boolean onlyYNExpected(String response) {
        return false;
    }

    public boolean stop() throws IOException {
        return sendCommandAndHandleResponse("s", (s) -> onlyYNExpected(s));
    }

    public boolean data() throws IOException {
        samples.clear();
        return sendCommandAndHandleResponse("d", (s) -> sampleExpected(s));
    }

    private int currentpinsample = 0;
    private final Map<Integer, List<String>> samples = new LinkedHashMap<>();

    private boolean sampleExpected(String responseline) {
        if (responseline.startsWith("#")) {
            currentpinsample = Integer.parseUnsignedInt(responseline.substring(2));
            samples.put(currentpinsample, new ArrayList());
        } else {
            samples.get(currentpinsample).add(responseline);
        }
        return true;
    }

    private synchronized boolean sendCommandAndHandleResponse(String s, Function<String, Boolean> responselinehandler) throws IOException {
        sendcommand(s);
        return handleResponse(responselinehandler);
    }

    private void sendcommand(String s) throws IOException {
        IOException ioex;
        System.out.println("W: " + s);
        try {
            usbdevice.writeln(s);
            return;
        } catch (IOException ex) {
            ioex = ex;
        }
        try {
            System.out.println("W: !");
            usbdevice.writeln("!"); // attempt to send an abandon command
        } catch (IOException ex) {
        }
        throw ioex;
    }

    private boolean handleResponse(Function<String, Boolean> responselinehandler) {
        try {
            while (true) {
                String response = usbdevice.readln();
                if (response.startsWith("**DEBUG:")) {
                    displaymessage(response);
                } else {
                    System.out.println("R: " + response);
                    if (response.startsWith("Y")) {
                        displaymessage(response, 2);
                        return true;
                    }
                    if (response.startsWith("N")) {
                        displaymessage(response, 2);
                        return false;
                    }
                    if (!responselinehandler.apply(response)) {
                        return false;
                    }
                }
            }
        } catch (IOException ex) { // treat IOException as a N response
            return false;
        }
    }
}
