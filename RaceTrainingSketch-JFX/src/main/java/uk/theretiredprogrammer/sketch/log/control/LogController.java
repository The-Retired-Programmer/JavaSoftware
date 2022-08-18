/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import java.util.LinkedList;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boats;
import uk.theretiredprogrammer.sketch.log.entity.TimerLogEntry;
import uk.theretiredprogrammer.sketch.log.ui.LogDisplayWindow;

public class LogController extends AbstractController<LogDisplayWindow> {

    private String mmsstime;
    private final LinkedList<TimerLogEntry> log = new LinkedList<>();
    private final SketchModel model;

    public LogController(String fn, SketchModel model) {
        this.model = model;
        setWindow(new LogDisplayWindow(fn, this), ExternalCloseAction.HIDE);
    }

    public Boats getBoats() {
        return model.getBoats();
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

    public LinkedList getLogs() {
        return log;
    }
}
