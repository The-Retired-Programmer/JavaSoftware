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
package uk.theretiredprogrammer.sketch.jfx.fileselectordisplay;

import uk.theretiredprogrammer.sketch.jfx.sketchdisplay.SketchWindow;
import java.io.IOException;
import javafx.scene.control.Accordion;
import javafx.scene.control.Separator;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.jfx.AbstractWindow;
import uk.theretiredprogrammer.sketch.jfx.UI;
import uk.theretiredprogrammer.sketch.jfx.fileselectordisplay.FileSelectorWindow.FileSelectorPane;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class FileSelectorWindow extends AbstractWindow {

    private final RecentFilesList recents;
    private final FolderList folders;

    public static FileSelectorWindow create(Stage stage) {
        return new FileSelectorWindow(stage);
    }

    private FileSelectorWindow(Stage stage) {
        super(FileSelectorWindow.class, stage);
        //
        recents = new RecentFilesList((pn -> selected(pn)));
        folders = new FolderList((pn -> selected(pn)));
        //
        setDefaultWindowWidth(400);
        setTitle("SKETCH Scenario Selector");
        FileSelectorPane fileselectorpane = new FileSelectorPane();
        setContent(fileselectorpane);
        addtoToolbar(
                UI.toolbarButton("folder_add.png", "Add Scenario Folder", ev -> folders.choose(stage)),
                UI.toolbarButton("folder_delete.png", "Remove Scenario Folder",
                        ev -> folders.remove(fileselectorpane)),
                UI.toolbarButton("page_add.png", "New Scenario", ev -> newConfigfile()),
                UI.toolbarButton("link_delete.png", "Clear Recent Scenarios List", ev -> recents.clear()),
                new Separator(),
                UI.toolbarButton("application_cascade.png", "Reset Windows Positions", ev -> resetWindows())
        );
        this.setOnCloseAction((e) -> {
            recents.close();
            folders.close();
        });
        show();
    }

    private void newConfigfile() {
        statusbarDisplay();
        Controller controller;
        try {
            controller = new Controller("newtemplate.json");
        } catch (IOException ex) {
            statusbarDisplay("<new>: " + ex.getLocalizedMessage());
            return;
        }
        SketchWindow.create("<new>", controller, this);
    }

    private void selected(PathWithShortName pn) {
        statusbarDisplay();
        Controller controller;
        try {
            controller = new Controller(pn.getPath());
        } catch (IOException ex) {
            statusbarDisplay(pn.toString() + ": " + ex.getLocalizedMessage());
            return;
        }
        recents.updateRecentFileList(pn);
        SketchWindow.create(pn.toString(), controller, this);
    }

    public class FileSelectorPane extends Accordion {

        public FileSelectorPane() {
            this.getPanes().add(recents.getPane());
            this.getPanes().addAll(folders.getPanes((s) -> this.refresh(s)));
        }

        private void refresh(String s) {
            this.getPanes().clear();
            this.getPanes().add(recents.getPane());
            this.getPanes().addAll(folders.getPanes());
        }
    }
}
