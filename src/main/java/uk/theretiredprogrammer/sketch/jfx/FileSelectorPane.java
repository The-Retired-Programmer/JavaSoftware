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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author richard
 */
public class FileSelectorPane extends TitledPane {

    public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";
    
    public FileSelectorPane(Consumer<TreeItem<Path>> selectionlistener) {
        var rootitem = builditem(Path.of(FILEROOT));
        TreeView<Path> directoryview = new TreeView<>();
        directoryview.setRoot(rootitem);
        directoryview.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionlistener.accept(newValue));
        directoryview.setShowRoot(false);
        this.setText(FILEROOT);
        this.setContent(new ScrollPane(directoryview));
    }

    private TreeItem<Path> builditem(Path path) {
        TreeItem<Path> node = new TreeItem<>(path);
        if (Files.isDirectory(path)) {
            try {
                for (Path child : Files.newDirectoryStream(path)) {
                    node.getChildren().add(builditem(child));
                }
            } catch (IOException ex) {
                // skip directory if problem
            }
        }
        return node;
    }
}
