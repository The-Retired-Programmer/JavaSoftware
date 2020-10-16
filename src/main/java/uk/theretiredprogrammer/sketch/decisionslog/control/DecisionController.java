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
package uk.theretiredprogrammer.sketch.decisionslog.control;

import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.decisionslog.entity.TimerLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.ui.DecisionDisplayWindow;

/**
 *
 * @author richard
 */
public class DecisionController {

    private String mmsstime;
    private final List<TimerLogEntry> log = new ArrayList<>();

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
    
    private DecisionDisplayWindow decisiondisplaywindow = null;

    public void showFullDecisionWindow(String fn, AbstractWindow parent) {
        showDecisionWindow(fn, parent);
        log.stream()
                .forEach(entry -> decisiondisplaywindow.writeline(entry.toString()));
    }

    public void showFilteredDecisionWindow(String fn, AbstractWindow parent) {
        showDecisionWindow(fn, parent);
        log.stream()
                .filter(entry -> entry.hasName("SELECTED"))
                .forEach(entry -> decisiondisplaywindow.writeline(entry.toString()));
    }

    private void showDecisionWindow(String fn, AbstractWindow parent) {
        if (decisiondisplaywindow == null) {
            decisiondisplaywindow = new DecisionDisplayWindow(fn, parent);
        }
        decisiondisplaywindow.clear();
    }
}
