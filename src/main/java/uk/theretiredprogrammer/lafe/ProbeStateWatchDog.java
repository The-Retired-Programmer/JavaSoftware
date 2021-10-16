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

//
//  background function which polls the probe for its state
//

public class ProbeStateWatchDog implements Runnable {

    private final USBSerialDevice usbdevice;
    private ScheduledExecutorService service;
    private ScheduledFuture<?>  watchdogHandle;
    private final FrontPanelController controller;
    
    public ProbeStateWatchDog(FrontPanelController controller, USBSerialDevice usbdevice) {
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
        STATE_IDLE(0), STATE_SAMPLING(1), STATE_STOPPING_SAMPLING(2), STATE_SAMPLING_DONE(3);

        private final int numericvalue;

        ProbeState(int numericvalue) {
            this.numericvalue = numericvalue;
        }

        public int numericvalue() {
            return this.numericvalue;
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
            controller.changedProbeState(state, newstate);
        }
        state = newstate;
        return true;
    }
}
