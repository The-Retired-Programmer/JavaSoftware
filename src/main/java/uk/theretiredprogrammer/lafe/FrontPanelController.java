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
import javafx.stage.Stage;

public class FrontPanelController {

    private final FrontPanelWindow window;
    private final USBSerialDevice usbdevice;
    private Map<Integer, List<String>> samples;

    public FrontPanelController(Stage stage) {
        ProbeConfiguration config = new ProbeConfiguration();
        usbdevice = new USBSerialDevice();
        ProbeCommands probecommands = new ProbeCommands(config, usbdevice);
        window = new FrontPanelWindow(stage, this, config, probecommands);
    }

    public final void close() throws IOException {
        usbdevice.close();
        window.close();
    }

    public void setData(Map<Integer, List<String>> samples) {
        // received samples to display - handoff to display window needed
        this.samples = samples;
        window.refreshDisplay();
    }

    public Map<Integer, List<String>> getSamples() {
        return samples;
    }
}
