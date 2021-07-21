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

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FrontPanelWindow {

    private final Class clazz;
    private final Stage stage;
    private Rectangle2D windowsize;
    private final FrontPanelDisplay displaypane;
    private final Text messagenode;
    private final FrontPanelControls frontpanelcontrols;

    public FrontPanelWindow(Stage stage, FrontPanelController controller, ProbeConfiguration config, ProbeCommands probecommands) {
        this.clazz = FrontPanelWindow.class;
        this.stage = stage;
        //
        displaypane = new FrontPanelDisplay(controller,config);
        setDefaultWindowWidth(400);
        BorderPane borderpane = new BorderPane();
        ScrollPane pane = new ScrollPane(displaypane);
        //pane.setContextMenu(contextmenu);
        borderpane.setCenter(pane);
        borderpane.setBottom(new FrontPanelConfiguration(config));
        borderpane.setLeft(frontpanelcontrols= new FrontPanelControls(controller, probecommands));
        borderpane.setTop(messagenode = new Text());
        Scene scene = new Scene(borderpane);
        LafePreferences.applyWindowSizePreferences(stage, clazz, windowsize);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Logic Analyser");
        stage.setOnHiding(e -> new ExecuteAndCatch(() -> saveWindowSizePreferences()));
        stage.show();
    }

    public void close() {
        stage.close();
    }

    public void refreshDisplay() {
        displaypane.refresh();
    }
    
    public void writestatusmessage(String message) {
        messagenode.setText(message);
    }
    
    public final void checkifprobeconnected(FrontPanelController controller, ProbeCommands probecommands) {
        frontpanelcontrols.checkifprobeconnected(controller, probecommands);
    }

    public void reset() {
        LafePreferences.clearWindowSizePreferences(clazz);
        LafePreferences.applyWindowSizePreferences(stage, clazz, windowsize);
    }

    private void setDefaultWindowWidth(double width) {
        Rectangle2D screenbounds = Screen.getPrimary().getVisualBounds();
        if (width > screenbounds.getWidth()) {
            windowsize = screenbounds;
            return;
        }
        windowsize = new Rectangle2D(
                screenbounds.getMinX(),
                screenbounds.getMinY(),
                width,
                screenbounds.getHeight()
        );
    }

    private void saveWindowSizePreferences() {
        LafePreferences.saveWindowSizePreferences(stage, clazz);
    }
}
