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
package uk.theretiredprogrammer.sketch.log.ui;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.log.control.LogController;

/**
 *
 * @author richard
 */
public class LogDisplayWindow extends AbstractWindow<LogController> {

    private final TextFlow textarea;

    public LogDisplayWindow(String fn, LogController controller) {
        super(LogDisplayWindow.class, controller);
        setTitle("SKETCH Decisions Viewer - " + fn);
        setDefaultWindowWidth(400);
        setContent(textarea = new TextFlow(), SCROLLABLE);
        build();
    }

    public void clear() {
        textarea.getChildren().clear();
    }

    public void writeline(String line) {
        textarea.getChildren().add(new Text(line + "\n"));
    }
}
