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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import static uk.theretiredprogrammer.lafe.ProbeStateWatchDog.ProbeState.STATE_SAMPLING_DONE;

//
//  background function which polls the probe for its state
//

public class ProbeStateWatchDog implements Runnable {

    private final USBSerialDevice usbdevice;
    private ScheduledExecutorService service;
    private ScheduledFuture<?>  watchdogHandle;
    private final Controller controller;
    
    public ProbeStateWatchDog(Controller controller, USBSerialDevice usbdevice) {
        this.controller = controller;
        this.usbdevice = usbdevice;
    }
    
    public void start() {
        service = Executors.newSingleThreadScheduledExecutor();
        watchdogHandle = service.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }
    
    public void stop() {
        Runnable canceller = () -> watchdogHandle.cancel(false);
        service.schedule(canceller, 0, TimeUnit.SECONDS);
        service.shutdown();
    }
   
    @Override
    public void run() {
        ExecuteAndCatch.run(()-> pollForProbeState(), () -> stop());
    }
    
    public ProbeState getProbeState() {
        return state;
    }

    private ProbeState state = ProbeState.STATE_IDLE;
    
    public enum ProbeState {
        STATE_IDLE(0, "Waiting to Sample"), STATE_SAMPLING(1, "Sampling"),
        STATE_STOPPING_SAMPLING(2, "Stopping - completing Sampling"), STATE_SAMPLING_DONE(3, "Sampling Completed");

        private final int numericvalue;
        private final String stringvalue;

        ProbeState(int numericvalue, String stringvalue) {
            this.numericvalue = numericvalue;
            this.stringvalue = stringvalue;
        }

        public int numericvalue() {
            return this.numericvalue;
        }
        
        @Override
        public String toString() {
            return stringvalue;
        }
    }

    private static ProbeState probeStateFromValue(int numericvalue) {
        for (ProbeState state : ProbeState.values()) {
            if (state.numericvalue() == numericvalue) {
                return state;
            }
        }
        throw new IllegalProgramStateFailure("Unknow probestate numeric value during lookup: " + numericvalue);
    }

    public boolean pollForProbeState() {
        return usbdevice.sendCommandAndHandleResponse("?", (s) -> statusExpected(s));
    }

    private boolean statusExpected(String responseline) {
        int response = Integer.parseInt(responseline);
        ProbeState newstate = probeStateFromValue(response);
        if (newstate != state){
            changedProbeState(state, newstate);
        }
        state = newstate;
        return true;
    }
    
    private void changedProbeState(ProbeState oldstate, ProbeState newstate) {
        Platform.runLater(() -> controller.probeStateChanged(newstate));
        if (newstate == STATE_SAMPLING_DONE) {
            Platform.runLater(() -> getAndDisplaySampleData());
        }
    }
    
    private void getAndDisplaySampleData() {
        try {
            controller.data();
        } catch (IOException ex) {
            throw new Failure(ex);
        }
    }
}
