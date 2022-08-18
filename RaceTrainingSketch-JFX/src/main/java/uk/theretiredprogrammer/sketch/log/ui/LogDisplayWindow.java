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
package uk.theretiredprogrammer.sketch.log.ui;

import java.util.Iterator;
import java.util.LinkedList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Booln;
import uk.theretiredprogrammer.sketch.core.entity.Intgr;
import uk.theretiredprogrammer.sketch.core.entity.Obj;
import uk.theretiredprogrammer.sketch.core.entity.Strg;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.log.control.LogController;
import uk.theretiredprogrammer.sketch.log.entity.TimerLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.WindShiftLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.WindSwingLogEntry;

public class LogDisplayWindow extends AbstractWindow<LogController> {

    private final Booln filterbyboat = new Booln(false);
    private final Obj<Boat> boatselection = new Obj<Boat>();
    private final Strg importanceselection = new Strg("Major only");
    private final Booln includewindlogs = new Booln(false);
    private final Intgr displaylogsize = new Intgr(50);

    private final VBox logdisplay = new VBox();

    public LogDisplayWindow(String fn, LogController controller) {
        super(LogDisplayWindow.class, controller);
        setTitle("SKETCH Decisions Viewer - " + fn);
        setDefaultWindowWidth(400);
        addtoToolbar(
                UI.toolbarInteger("Display most recent logs", displaylogsize),
                UI.toolbarCheckBox("Include Wind Logs", includewindlogs),
                UI.toolbarComboBox("Filter by importance", importanceselection, "Major only", "Major and Minor", "All"),
                UI.toolbarCheckBox("Filter by Boat", filterbyboat),
                UI.toolbarBoatComboBox("Filter by Boat", boatselection, controller.getBoats()),
                UI.toolbarButton("eye.png", "View", ev -> view())
        );
        setContent(logdisplay, SCROLLABLE);
        build();
    }

    public void view() {
        logdisplay.getChildren().clear();
        LinkedList<TimerLogEntry> list = getController().getLogs();
        Iterator<TimerLogEntry> it = list.descendingIterator();
        int maxlogstodisplay = displaylogsize.get();
        while (it.hasNext()) {
            var item = it.next();
            if (filter(item)) {
                logdisplay.getChildren().add(new Label(item.toString()));
                if (--maxlogstodisplay < 1) {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("null")
    private boolean filter(TimerLogEntry item) {
        boolean selected = false;
        if (!includewindlogs.get() && (item instanceof WindShiftLogEntry || item instanceof WindSwingLogEntry)) {
            return false;
        }
        if (filterbyboat.get() && !item.hasName(boatselection.get().getNamed())) {
            return false;
        }

        switch (importanceselection.get()) {
            case "Major only":
                selected = item.hasMajorImportance();
                break;
            case "Major and Minor":
                selected = item.hasMajorMinorImportance();
                break;
            case "All":
                selected = true;
                break;
            default:
                throw new IllegalStateFailure("view log filter - illegal importance filter value");
        }
        return selected;
    }
}
