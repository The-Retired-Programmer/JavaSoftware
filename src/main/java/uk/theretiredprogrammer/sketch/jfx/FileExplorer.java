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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author richard
 */
public class FileExplorer {

    public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";

    public TreeView getExplorerView() {
        var rootitem = builditem(Path.of(FILEROOT));
        TreeView treeView = new TreeView();
        treeView.setRoot(rootitem);
        //
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> System.out.println("Selected Text : " + newValue));
        //
        return treeView;
    }

    private TreeItem builditem(Path path) {
        TreeItem node = new TreeItem(path.getFileName());
        if (Files.isDirectory(path)) {
            try {
                for (Path child : Files.newDirectoryStream(path)) {
                    node.getChildren().add(builditem(child));
                }
            } catch (IOException ex) {
                // skip directory if problem
            }
        } else {
            // need to be able to click select filename
        }
        return node;
    }
}
