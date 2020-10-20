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
package uk.theretiredprogrammer.sketch.fileselector.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.core.control.Execute;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.fileselector.control.FileSelectorController;
import uk.theretiredprogrammer.sketch.core.entity.PathWithShortName;

/**
 *
 * @author richard
 */
public class FileSelectorWindow extends AbstractWindow<FileSelectorController> {

    private final FileSelectorPane fileselectorpane;

    public FileSelectorWindow(FileSelectorController controller, Stage stage) {
        super(FileSelectorWindow.class, stage, controller);
        //
        setDefaultWindowWidth(400);
        setTitle("SKETCH Scenario Selector");
        fileselectorpane = new FileSelectorPane();
        setContent(fileselectorpane);
        addtoMenubar(
                UI.menu("Scenarios",
                        UI.menuitem("New", ev -> controller.newConfigfile())
                ),
                UI.menu("Folders",
                        UI.menuitem("Add", ev -> controller.addFolder(stage)),
                        UI.menuitem("Remove", ev -> remove(fileselectorpane))
                ),
                UI.menu("Recent Scenarios List",
                        UI.menuitem("Clear List", ev -> controller.clearRecents())
                ),
                UI.menu("Windows",
                        UI.menuitem("Reset Positions and Sizes", ev -> resetWindows())
                )
        );
        addtoToolbar(
                UI.toolbarButton("folder_add.png", "Add Scenario Folder", ev -> controller.addFolder(stage)),
                UI.toolbarButton("folder_delete.png", "Remove Scenario Folder", ev -> remove(fileselectorpane)),
                UI.toolbarButton("page_add.png", "New Scenario", ev -> controller.newConfigfile()),
                UI.toolbarButton("link_delete.png", "Clear Recent Scenarios List", ev -> controller.clearRecents()),
                new Separator(),
                UI.toolbarButton("application_cascade.png", "Reset Windows Positions", ev -> resetWindows())
        );
        build();
        show();
    }

    public void refreshWindow() {
        fileselectorpane.refresh();
    }

    public class FileSelectorPane extends Accordion {

        public FileSelectorPane() {
            this.getPanes().add(getRecentsPane());
            this.getPanes().addAll(getFolderPanes());
        }

        private void refresh() {
            this.getPanes().clear();
            this.getPanes().add(getRecentsPane());
            this.getPanes().addAll(getFolderPanes());
        }
    }

    public TitledPane getRecentsPane() {
        FileSelectorController controller = getController();
        ListView<PathWithShortName> recentsview = new ListView<>(controller.getRecents());
        recentsview.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> new Execute(() -> controller.recentSelected(newValue)));
        TitledPane titledpane = new TitledPane("Recently Opened Scenarios", new ScrollPane(recentsview));
        titledpane.setGraphic(UI.image("link.png"));
        return titledpane;
    }

    public List<TitledPane> getFolderPanes() {
        FileSelectorController controller = getController();
        List<TitledPane> panes = new ArrayList<>();
        for (var pn : controller.getFolders()) {
            ListView<PathWithShortName> filesview = new ListView<>(controller.getFoldercontent(pn));
            filesview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> new Execute(() -> controller.selected(newValue)));
            TitledPane titledpane = new TitledPane(pn.toString(), new ScrollPane(filesview));
            titledpane.setGraphic(UI.image("folder.png"));
            panes.add(titledpane);
        }
        return panes;
    }

    private void remove(FileSelectorPane fileselectorpane) {
        TitledPane expandedpane = fileselectorpane.getExpandedPane();
        if (expandedpane != null) {
            getController().removefromfolderlist(expandedpane.getText());
        }
    }
}
