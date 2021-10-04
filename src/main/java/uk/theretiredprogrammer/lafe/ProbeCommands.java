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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProbeCommands {

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

    private ProbeState laststateresponse;

    static ProbeState getProbeState(int numericvalue) {
        for (ProbeState state : ProbeState.values()) {
            if (state.numericvalue() == numericvalue) {
                return state;
            }
        }
        throw new IllegalStateFailure("Unknow probestate numeric value during lookup: " + numericvalue);
    }

    private final USBSerialDevice device;
    private final ProbeConfiguration config;
    private Consumer<String> messagewriter;

    public ProbeCommands(ProbeConfiguration config, USBSerialDevice device) {
        this.device = device;
        this.config = config;
    }

    public void setstatusmessagewriter(Consumer<String> messagewriter) {
        this.messagewriter = messagewriter;
    }

    public boolean ping() throws IOException {
        sendcommand("p");
        return handleResponse((s) -> probetypeExpected(s));
    }

    private boolean probetypeExpected(String response) {
        config.probetype = response;
        displaymessage(response);
        return true;
    }

    public boolean getState() throws IOException {
        sendcommand("?");
        return handleResponse((s) -> statusExpected(s));
    }

    public ProbeState getLastStateResponse() {
        return laststateresponse;
    }

    private boolean statusExpected(String responseline) {
        int response = Integer.parseInt(responseline);
        laststateresponse = getProbeState(response);
        return true;
    }

    public boolean start() throws IOException {
        sendcommand(config.getprobecommand("g"));
        return handleResponse((s) -> onlyYNExpected(s));
    }

    private boolean onlyYNExpected(String response) {
        return false;
    }

    public boolean stop() throws IOException {
        sendcommand("s");
        return handleResponse((s) -> onlyYNExpected(s));
    }

    public boolean data(Map<Integer, List<String>> samples) throws IOException {
        sendcommand("d");
        return handleResponse((s) -> sampleExpected(s, samples));
    }
    
    private int currentpinsample = 0;

    private boolean sampleExpected(String responseline, Map<Integer, List<String>> samples) {
        if (responseline.startsWith("#")) {
            currentpinsample = Integer.parseUnsignedInt(responseline.substring(2));
            samples.put(currentpinsample, new ArrayList());
        } else {
            samples.get(currentpinsample).add(responseline);
        }
        return true;
    }

    private void sendcommand(String s) throws IOException {
        IOException ioex;
        System.out.println("W: " + s);
        try {
            device.writeln(s);
            return;
        } catch (IOException ex) {
            ioex = ex;
        }
        try {
            System.out.println("W: !");
            device.writeln("!"); // attempt to send an abandon command
        } catch (IOException ex) {
        }
        throw ioex;
    }

    private boolean handleResponse(Function<String, Boolean> responselinehandler) {
        try {
            while (true) {
                String response = device.readln();
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
}
