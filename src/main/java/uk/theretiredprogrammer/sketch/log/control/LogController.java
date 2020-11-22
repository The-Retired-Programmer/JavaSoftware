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
package uk.theretiredprogrammer.sketch.log.control;

import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.log.entity.TimerLogEntry;
import uk.theretiredprogrammer.sketch.log.ui.LogDisplayWindow;

/**
 *
 * @author richard
 */
public class LogController extends AbstractController<LogDisplayWindow> {

    private String mmsstime;
    private final List<TimerLogEntry> log = new ArrayList<>();

    public LogController(String fn, Boolean requiresfilter) {
        setWindow(new LogDisplayWindow(fn, this), ExternalCloseAction.HIDE);
        getWindow().clear();
        if (requiresfilter) {
            log.stream()
                    .filter(entry -> entry.hasName("SELECTED"))
                    .forEach(entry -> getWindow().writeline(entry.toString()));
        } else {
            log.stream()
                    .forEach(entry -> getWindow().writeline(entry.toString()));
        }
    }

    public void setTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        this.mmsstime = Integer.toString(mins) + ":" + ss;
    }

    public void clear() {
        log.clear();
    }

    public void add(TimerLogEntry entry) {
        entry.setTime(mmsstime);
        log.add(entry);
    }

}
