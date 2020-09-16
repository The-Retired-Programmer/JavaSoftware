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
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Accordion;
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

    private final List<Path> recentFileList;

    public static FileSelectorWindow create(Stage stage) {
        return new FileSelectorWindow(stage);
    }

    private FileSelectorWindow(Stage stage) {
        super(FileSelectorWindow.class, stage);
        setDefaultWindowSize(100, 100, 400, 650);
        recentFileList = SketchPreferences.getRecentFileList(FileSelectorWindow.class);
        setTitle("SKETCH Scenario Selector");
        setContent(new FileSelectorPane((p) -> fileSelected(p)));
        this.setOnCloseAction((e) -> SketchPreferences.saveRecentFileList(recentFileList, FileSelectorWindow.class));
        show();
    }

    private void fileSelected(TreeItem<PathWithShortName> p) {
        PathWithShortName pn = p.getValue();
        updateRecentFileList(pn);
        SketchWindow.create(pn.toString(), new Controller(pn.getPath()), this);
    }
    
    private void updateRecentFileList(PathWithShortName pn) {
        try {
            for (int index = 0; index < recentFileList.size(); index++) {
                if (Files.isSameFile(recentFileList.get(index), pn.getPath())) {
                    recentFileList.remove(index);
                    index--;
                }
            }
        } catch (IOException ex) {
            // skip if problems
        }
        while (recentFileList.size() > 9 ){
            recentFileList.remove(9);
        }
        recentFileList.add(0,pn.getPath());
    }

    private class FileSelectorPane extends Accordion {

        public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";

        public FileSelectorPane(Consumer<TreeItem<PathWithShortName>> selectionlistener) {
            this.getPanes().addAll(
                    getRecentFilesTitledPane(selectionlistener),
                    getFileSelectorTitledPane(FILEROOT, selectionlistener)
            );
        }

        private TitledPane getRecentFilesTitledPane(Consumer<TreeItem<PathWithShortName>> selectionlistener) {
            var rootitem = buidListItem(recentFileList);
            TreeView<PathWithShortName> recentsview = new TreeView<>();
            recentsview.setRoot(rootitem);
            recentsview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> selectionlistener.accept(newValue));
            recentsview.setShowRoot(false);
            return new TitledPane("Recently Opened Scenarios", new ScrollPane(recentsview));
        }

        private TreeItem<PathWithShortName> buidListItem(List<Path> list) {
            TreeItem<PathWithShortName> node = new TreeItem<>();
            list.forEach(child -> {
                node.getChildren().add(new TreeItem(new PathWithShortName(child)));
            });
            return node;
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
