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
package uk.theretiredprogrammer.sketch.jfx;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author richard
 */
public abstract class AbstractWindow {

    private AbstractWindow parentwindow = null;
    private final List<AbstractWindow> childwindows = new ArrayList<>();

    private final Stage stage;

    AbstractWindow() {
        this(new Stage());
    }

    AbstractWindow(Stage stage) {
        this.stage = stage;
    }

    Stage getStage() {
        return stage;
    }

    void setParentWindow(AbstractWindow parent) {
        parentwindow = parent;
        parent.childwindows.add(this);
    }

    void closeIncludingChildren() {
        childwindows.forEach((window) -> window.getStage().close());
        if (parentwindow != null) {
            parentwindow.childwindows.remove(this);
        }
    }

    Button toolbarButton(String buttontext, EventHandler<ActionEvent> action) {
        Button button = new Button(buttontext);
        button.setDisable(false);
        button.setOnAction(action);
        return button;
    }
}
