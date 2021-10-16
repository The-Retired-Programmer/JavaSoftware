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
import javafx.application.Platform;
import javafx.event.Event;
import uk.theretiredprogrammer.lafe.ProbeStateWatchDog.ProbeState;

public class FrontPanelController {

    private final USBSerialDevice usbdevice;
    private final ProbeStateWatchDog probestatewatchdog;
    private FrontPanelWindow window;
    private final ProbeConfiguration config;

    public FrontPanelController() {
        config = new ProbeConfiguration();
        usbdevice = new USBSerialDevice();
        probestatewatchdog = new ProbeStateWatchDog(this, usbdevice);
    }

    public final void open(FrontPanelWindow window) {
        this.window = window;
        usbdevice.open(window);
        window.setConnected(isprobeconnected());
        probestatewatchdog.start();
    }

    private boolean isprobeconnected() {
        try {
            return ping();
        } catch (IOException ex) {
            return false;
        }
    }

    public final void close() throws IOException {
        probestatewatchdog.stop();
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
    
    public void changedProbeState(ProbeState oldstate, ProbeState newstate) {
        Platform.runLater(() -> changedstate(newstate));
    }
    
    private void changedstate(ProbeState newstate){
        window.setStopGoButtonText(getButtonText(newstate));
    } 
    
    public String getButtonText(ProbeState state) {
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
        switch (probestatewatchdog.getProbeState()) {
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
    }

    public boolean ping() throws IOException {
        return usbdevice.sendCommandAndHandleResponse("p", (s) -> probetypeExpected(s));
    }

    private boolean probetypeExpected(String response) {
        config.probetype = response;
        window.displayStatus(response);
        return true;
    }

    public boolean start() throws IOException {
        return usbdevice.sendCommandAndHandleResponse(config.getprobecommand("g"), (s) -> onlyYNExpected(s));
    }

    private boolean onlyYNExpected(String response) {
        return false;
    }

    public boolean stop() throws IOException {
        return usbdevice.sendCommandAndHandleResponse("s", (s) -> onlyYNExpected(s));
    }

    public boolean data() throws IOException {
        samples.clear();
        return usbdevice.sendCommandAndHandleResponse("d", (s) -> sampleExpected(s));
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
}
