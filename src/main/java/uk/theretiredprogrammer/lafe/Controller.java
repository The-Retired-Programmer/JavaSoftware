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
import uk.theretiredprogrammer.lafe.ProbeStateWatchDog.ProbeState;

public class Controller {

    private USBSerialDevice usbdevice;
    private ProbeStateWatchDog probestatewatchdog;
    private Window window;
    private final ProbeConfiguration config;

    public Controller() {
        config = new ProbeConfiguration();
    }

    public final void open(Window window) {
        this.window = window;
        usbdevice = USBSerialDevice.selectCommPort((s) -> window.displayStatus(s));
        probestatewatchdog = new ProbeStateWatchDog(this, usbdevice);
        probestatewatchdog.start();
        window.setConnected(isprobeconnected());
    }

    private boolean isprobeconnected() {
        try {
            return ping();
        } catch (IOException ex) {
            return false;
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public final void close() {
        probestatewatchdog.stop();
        usbdevice.close();
        window.close();
    }

    public final ProbeConfiguration getProbeConfiguration() {
        return config;
    }

    public void probeStateChanged(ProbeState newstate) {
        window.probeStateChanged(newstate);
    }

    // -------------------------------------------------------------------------
    //
    //  Probe commands
    //
    // -------------------------------------------------------------------------
    public boolean ping() throws IOException {
        return usbdevice.sendCommandAndHandleResponse("p", (s) -> probetypeExpected(s));
    }

    private boolean probetypeExpected(String response) {
        config.probetype = response;
        window.displayConnectedProbe(response);
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
        boolean res = usbdevice.sendCommandAndHandleResponse("d", (s) -> sampleExpected(s));
        window.refreshSampleDisplay(samples);
        return res;
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
    
    public void resetProbe() {
        System.out.println("RESETING");
        probestatewatchdog.stop();
        usbdevice.write('!');
        usbdevice.close();
        try {
            Thread.sleep(3000); // lets start with a 3 secs wait
        } catch (InterruptedException ex) {
            // ingnore 
        }
        System.out.println("RESET");
        usbdevice.open();
        probestatewatchdog.start();
    }
    
    public boolean squareWaveGenerator(boolean on) {
        return usbdevice.sendCommandAndHandleResponse(config.getSquareWaveCommand("w", on), (s) -> onlyYNExpected(s));
    }
}
