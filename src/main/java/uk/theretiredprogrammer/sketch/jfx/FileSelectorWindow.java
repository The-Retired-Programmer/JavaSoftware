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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class FileSelectorWindow extends AbstractWindow {

    private ObservableList<PathWithShortName> recentFileList;

    public static FileSelectorWindow create(Stage stage) {
        return new FileSelectorWindow(stage);
    }

    private FileSelectorWindow(Stage stage) {
        super(FileSelectorWindow.class, stage);
        setDefaultWindowSize(100, 100, 400, 650);
        recentFileList = SketchPreferences.getRecentFileList(FileSelectorWindow.class);
        setTitle("SKETCH Scenario Selector");
        setContent(new FileSelectorPane((p) -> recentSelected(p), (p) -> fileSelected(p)));
        this.setOnCloseAction((e) -> SketchPreferences.saveRecentFileList(recentFileList, FileSelectorWindow.class));
        show();
    }

    private void fileSelected(TreeItem<PathWithShortName> p) {
        selected(p.getValue());
    }
    
    private boolean firstcall  = true;

    private void recentSelected(PathWithShortName pn) {
        if (firstcall) {
            firstcall = false;
            selected(pn);
            firstcall = true;
        }
    }
    
    private void selected(PathWithShortName pn) {
        statusbarClear();
        Controller controller;
        try {
            controller = new Controller(pn.getPath());
        } catch (IOException ex) {
            statusbarDisplay(pn.toString() + ": " + ex.getLocalizedMessage());
            return;
        }
        updateRecentFileList(pn);
        SketchWindow.create(pn.toString(), controller, this);
    }

    private void updateRecentFileList(PathWithShortName pn) {
        if (recentFileList.size() > 0) {
            List<Integer> removethese = new ArrayList<>();
            try {
                for (int i = 0; i < recentFileList.size(); i++) {
                    if (Files.isSameFile(recentFileList.get(i).getPath(), pn.getPath())) {
                        removethese.add(i);
                    }
                }
            } catch (IOException ex) {
            }
            if (!removethese.isEmpty()) {
                for (int i = removethese.size() - 1; i > -1; i--) {
                    recentFileList.remove((int) removethese.get(i));
                }
            }
            if (recentFileList.size() > 9) {
                for (int i = recentFileList.size() - 1; i > 8; i--) {
                    recentFileList.remove(i);
                }
            }
        }
        recentFileList.add(0, pn);
    }

    private class FileSelectorPane extends Accordion {

        public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";

        public FileSelectorPane(Consumer<PathWithShortName> recentselectionlistener,
                Consumer<TreeItem<PathWithShortName>> fileselectionlistener) {
            this.getPanes().addAll(
                    getRecentFilesTitledPane(recentselectionlistener),
                    getFileSelectorTitledPane(FILEROOT, fileselectionlistener)
            );
        }

        private TitledPane getRecentFilesTitledPane(Consumer<PathWithShortName> selectionlistener) {
            ListView<PathWithShortName> recentsview = new ListView<>(recentFileList);
            recentsview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> selectionlistener.accept(newValue));
            return new TitledPane("Recently Opened Scenarios", new ScrollPane(recentsview));
        }

        private TitledPane getFileSelectorTitledPane(String folder, Consumer<TreeItem<PathWithShortName>> selectionlistener) {
            var rootitem = buidDirectoryItem(new PathWithShortName(Path.of(folder)));
            TreeView<PathWithShortName> directoryview = new TreeView<>();
            directoryview.setRoot(rootitem);
            directoryview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> selectionlistener.accept(newValue));
            directoryview.setShowRoot(false);
            return new TitledPane(folder, new ScrollPane(directoryview));
        }

        private TreeItem<PathWithShortName> buidDirectoryItem(PathWithShortName path) {
            TreeItem<PathWithShortName> node = new TreeItem<>(path);
            if (Files.isDirectory(path.getPath())) {
                try {
                    try ( DirectoryStream<Path> jsonfilepaths = Files.newDirectoryStream(path.getPath(), "*.json")) {
                        for (Path child : jsonfilepaths) {
                            node.getChildren().add(buidDirectoryItem(new PathWithShortName(child)));
                        }
                    }
                } catch (IOException ex) {
                    // skip directory if problem
                }
            }
            return node;
        }
    }
}
