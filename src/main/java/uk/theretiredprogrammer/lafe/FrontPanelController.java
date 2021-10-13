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
import java.util.List;
import java.util.Map;

public class FrontPanelController {

    private USBSerialDevice usbdevice;
    private ProbeCommands probecommands;
    private Map<Integer, List<String>> samples;
    private FrontPanelWindow window;
    private ProbeConfiguration config;

    public FrontPanelController() {
        config = new ProbeConfiguration();
        usbdevice = new USBSerialDevice();
        probecommands = new ProbeCommands(config, usbdevice);
    }
    
    public final void open(FrontPanelWindow window) {
        this.window = window;
        // in wrong place
        probecommands.setstatusmessagewriter((message) -> window.writestatusmessage(message));
        window.checkifprobeconnected();
    }
    
    public final void close() throws IOException {
        usbdevice.close();
        window.close();
    }
    
    public final ProbeConfiguration getProbeConfiguration() {
        return config;
    }

    public final ProbeCommands getProbeCommands() {
        return probecommands;
    }

    public void setData(Map<Integer, List<String>> samples) {
        // received samples to display - handoff to display window needed
        this.samples = samples;
        window.refreshSampleDislay();
    }

    public Map<Integer, List<String>> getSamples() {
        return samples;
    }
}
