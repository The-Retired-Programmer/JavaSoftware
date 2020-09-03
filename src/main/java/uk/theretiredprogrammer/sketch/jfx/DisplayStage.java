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

import java.nio.file.Path;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class DisplayStage {

    private final Controller controller;
    private final DisplaySurface canvas;
    private final Text timetext;

    public DisplayStage(Path path) {
        this.controller = new Controller(path, (i) -> updatetime(i), (s) -> writedecisionlog(s), () -> updatedisplay());;
        Group group = new Group();
        //group.minHeight(controller.getHeight());
        //group.minWidth(controller.getWidth());
        canvas = new DisplaySurface(controller.sailingarea.getArea(), controller.displayparameters.getZoom());
        updatedisplay(canvas);
        group.getChildren().add(canvas);
        ScrollPane scrollpane = new ScrollPane(group);
        PropertiesPane propertiespane = new PropertiesPane();
        propertiespane.updateAllproperties(controller);
        SplitPane splitpane = new SplitPane();
        splitpane.getItems().addAll(
                scrollpane,
                propertiespane
        );
        //
        Button startbutton = new Button("Start");
        startbutton.setDisable(false);
        startbutton.setOnAction(actionEvent -> {
            controller.start();
        });
        Button pausebutton = new Button("Pause");
        pausebutton.setDisable(false);
        pausebutton.setOnAction(actionEvent -> {
            controller.stop();
        });
        Button resetbutton = new Button("Reset");
        resetbutton.setDisable(false);
        resetbutton.setOnAction(actionEvent -> {
            controller.reset();
        });
        timetext = new Text("      ");
        //
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().addAll(startbutton, pausebutton, resetbutton, timetext);
        //
        VBox vbox = new VBox();
        vbox.getChildren().addAll(toolbar, splitpane);
        Scene scene = new Scene(vbox, 300, 300);
        //
        Stage displaystage = new Stage();
        displaystage.setScene(scene);
        displaystage.initStyle(StageStyle.DECORATED);
        displaystage.setTitle("");
        displaystage.show();
        displaystage.setTitle(path.getFileName().toString());
        displaystage.show();
    }

    public void updatedisplay() {
        updatedisplay(canvas);
    }

    private void updatedisplay(DisplaySurface canvas) {
        Platform.runLater(() -> controller.paint(canvas));
    }

    private void updatetime(int seconds) {
        Platform.runLater(() -> updteTime(seconds));
    }

    private void updteTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        timetext.setText(Integer.toString(mins) + ":" + ss);
    }

    private void writedecisionlog(String s) {
        // platform.runLater( ... );
    }
}
