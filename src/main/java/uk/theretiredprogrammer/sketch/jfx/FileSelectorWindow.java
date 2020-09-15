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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
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
    
    public static FileSelectorWindow create(Stage stage){
        return new FileSelectorWindow(stage);
    }

    private FileSelectorWindow(Stage stage) {
        super(stage);
        FileSelectorPane fileselector = new FileSelectorPane((p) -> fileSelected(p));
        new WindowBuilder(FileSelectorWindow.class)
                .setDefaultWindowSize(100, 100, 400, 650)
                .setTitle("Race Training SKETCH Application - File Selector")
                .setContent(fileselector)
                .setOnCloseAction((e) -> closeIncludingChildren())
                .show(stage);
    }

    private void fileSelected(TreeItem<PathWithShortName> p) {
        Path path = p.getValue().getPath();
        SketchWindow.create(path.getFileName().toString(),new Controller(path), this);
    }

    private class FileSelectorPane extends Accordion {

        public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";

        public FileSelectorPane(Consumer<TreeItem<PathWithShortName>> selectionlistener) {
            this.getPanes().addAll(
                    getFileSelectorTitledPane(FILEROOT, selectionlistener)
            );
        }

        private TitledPane getFileSelectorTitledPane(String folder, Consumer<TreeItem<PathWithShortName>> selectionlistener) {
            var rootitem = builditem(new PathWithShortName(Path.of(folder)));
            TreeView<PathWithShortName> directoryview = new TreeView<>();
            directoryview.setRoot(rootitem);
            directoryview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> selectionlistener.accept(newValue));
            directoryview.setShowRoot(false);
            return new TitledPane(folder, new ScrollPane(directoryview));
        }

        private TreeItem<PathWithShortName> builditem(PathWithShortName path) {
            TreeItem<PathWithShortName> node = new TreeItem<>(path);
            if (Files.isDirectory(path.getPath())) {
                try {
                    for (Path child : Files.newDirectoryStream(path.getPath())) {
                        node.getChildren().add(builditem(new PathWithShortName(child)));
                    }
                } catch (IOException ex) {
                    // skip directory if problem
                }
            }
            return node;
        }
    }
}
