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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import uk.theretiredprogrammer.sketch.jfx.SketchPreferences;
import uk.theretiredprogrammer.sketch.jfx.UI;
import uk.theretiredprogrammer.sketch.jfx.fileselectordisplay.FileSelectorWindow.FileSelectorPane;

/**
 *
 * @author richard
 */
public class FolderList {

//    public final static String FILEROOT = "/Users/richard/Race training Scenarios/Race Sketches/";
    private final Consumer<PathWithShortName> onSelection;
    private final ObservableList<PathWithShortName> foldersList;
    private Consumer<String> refresh;

    public FolderList(Consumer<PathWithShortName> onSelection) {
        this.onSelection = onSelection;
        foldersList = SketchPreferences.getFoldersList(FileSelectorWindow.class);
        // temporary
//        foldersList = FXCollections.observableArrayList();
//        foldersList.add(new PathWithShortName(Path.of(FILEROOT)));
        foldersList.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refresh.accept("");
            }
        });
    }

    public void close() {
        SketchPreferences.saveFoldersList(foldersList, FileSelectorWindow.class);
    }

    public List<TitledPane> getPanes(Consumer<String> refresh) {
        this.refresh = refresh;
        return getPanes();
    }

    public List<TitledPane> getPanes() {
        List<TitledPane> panes = new ArrayList<>();
        for (var pn : foldersList) {
            ListView<PathWithShortName> filesview = new ListView<>(getpaths(pn));
            filesview.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> fileSelected(newValue));
            TitledPane titledpane = new TitledPane(pn.toString(), new ScrollPane(filesview));
            titledpane.setGraphic(UI.image("folder.png"));
            panes.add(titledpane);
        }
        return panes;
    }

    private ObservableList<PathWithShortName> getpaths(PathWithShortName dir) {
        ObservableList<PathWithShortName> listpaths = FXCollections.observableArrayList();
        if (Files.isDirectory(dir.getPath())) {
            try {
                try ( DirectoryStream<Path> jsonfilepaths = Files.newDirectoryStream(dir.getPath(), "*.json")) {
                    for (Path child : jsonfilepaths) {
                        listpaths.add(new PathWithShortName(child));
                    }
                }
            } catch (IOException ex) {
                // skip directory if problem
            }
        }
        return listpaths;
    }

    public void choose(Window parent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Additional Scenario Directory");
        File directory = dc.showDialog(parent);
        if (directory != null) {
            foldersList.add(new PathWithShortName(Path.of(directory.getAbsolutePath())));
        }
    }

    public void remove(FileSelectorPane fileselectorpane) {
        TitledPane expandedpane = fileselectorpane.getExpandedPane();
        if (expandedpane != null) {
            foldersList.removeIf((pn) -> pn.toString().equals(expandedpane.getText()));
        }
    }

    private void fileSelected(PathWithShortName p) {
        onSelection.accept(p);
    }
}
