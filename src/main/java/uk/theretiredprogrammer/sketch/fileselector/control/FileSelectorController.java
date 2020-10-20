/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.fileselector.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.core.control.IOFailure;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.SketchPreferences;
import uk.theretiredprogrammer.sketch.display.control.DisplayController;
import uk.theretiredprogrammer.sketch.fileselector.entity.PathWithShortName;
import uk.theretiredprogrammer.sketch.fileselector.ui.FileSelectorWindow;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class FileSelectorController extends AbstractController<FileSelectorWindow> {

    private final List<DisplayController> displayscreated;
    private final ObservableList<PathWithShortName> recentFileList;
    private final ObservableList<PathWithShortName> folderList;

    public FileSelectorController(Stage stage) {
        this.displayscreated = new ArrayList<>();
        recentFileList = SketchPreferences.getRecentFileList(FileSelectorWindow.class);
        folderList = SketchPreferences.getFoldersList(FileSelectorWindow.class);
        folderList.addListener((ListChangeListener) (c) -> onfolderlistchange(c));
        setWindow(new FileSelectorWindow(this, stage));
    }

    private void onfolderlistchange(ListChangeListener.Change<PathWithShortName> lcl) {
        getWindow().refreshWindow();
    }

    @Override
    protected void whenWindowIsClosing() {
        for (var controller : displayscreated) {
            controller.close();
        }
    }
    
    @Override
    protected void whenWindowIsHiding() {
        SketchPreferences.saveFoldersList(folderList, FileSelectorWindow.class);
        SketchPreferences.saveRecentFileList(recentFileList, FileSelectorWindow.class);
    }

    public void removeparentchildrelationship(DisplayController childcontroller) {
        displayscreated.remove(childcontroller);
    }

    public ObservableList<PathWithShortName> getRecents() {
        return recentFileList;
    }

    public void clearRecents() {
        recentFileList.clear();
    }

    private boolean firstcall = true;

    public void recentSelected(PathWithShortName pn) {
        if (firstcall) {
            firstcall = false;
            selected(pn);
            firstcall = true;
        }
    }

    public ObservableList<PathWithShortName> getFolders() {
        return folderList;
    }

    public ObservableList<PathWithShortName> getFoldercontent(PathWithShortName folder) {
        ObservableList<PathWithShortName> listpaths = FXCollections.observableArrayList();
        if (Files.isDirectory(folder.getPath())) {
            try {
                try ( DirectoryStream<Path> jsonfilepaths = Files.newDirectoryStream(folder.getPath(), "*.json")) {
                    for (Path jsonfilepath : jsonfilepaths) {
                        listpaths.add(new PathWithShortName(jsonfilepath));
                    }
                }
                return listpaths;
            } catch (IOException ex) {
                throw new IOFailure(ex);
            }
        }
        throw new IllegalStateFailure("Folder List - Expected Folder");
    }

    public void addFolder(Window parent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Additional Scenario Directory");
        File directory = dc.showDialog(parent);
        if (directory != null) {
            folderList.add(new PathWithShortName(Path.of(directory.getAbsolutePath())));
        }
    }

    public void removefromfolderlist(String name) {
        folderList.removeIf((pn) -> pn.toString().equals(name));
    }

    public void newConfigfile() {
        getWindow().clearStatusbar();
        displayscreated.add(new DisplayController("newtemplate.json", "<newfile>", this));
    }

    public void selected(PathWithShortName pn) {
        getWindow().clearStatusbar();
        updateRecentFileList(pn);
        displayscreated.add(new DisplayController(pn, this));
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
                throw new IOFailure(ex);
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
}
