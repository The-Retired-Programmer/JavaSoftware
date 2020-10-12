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

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import uk.theretiredprogrammer.sketch.jfx.SketchPreferences;
import uk.theretiredprogrammer.sketch.jfx.UI;

/**
 *
 * @author richard
 */
public class RecentFilesList {

    private final ObservableList<PathWithShortName> recentFileList;
    private final Consumer<PathWithShortName> onSelection;
    private Consumer<String> refresh;

    public RecentFilesList(Consumer<PathWithShortName> onSelection) {
        recentFileList = SketchPreferences.getRecentFileList(FileSelectorWindow.class);
        this.onSelection = onSelection;
    }

    public void close() {
        SketchPreferences.saveRecentFileList(recentFileList, FileSelectorWindow.class);
    }

    public void clear() {
        recentFileList.clear();
    }

    public TitledPane getPane() {
        ListView<PathWithShortName> recentsview = new ListView<>(recentFileList);
        recentsview.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> recentSelected(newValue));
        TitledPane titledpane = new TitledPane("Recently Opened Scenarios", new ScrollPane(recentsview));
        titledpane.setGraphic(UI.image("link.png"));
        return titledpane;
    }

    private boolean firstcall = true;

    private void recentSelected(PathWithShortName pn) {
        if (firstcall) {
            firstcall = false;
            onSelection.accept(pn);
            firstcall = true;
        }
    }

    public void updateRecentFileList(PathWithShortName pn) {
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

}
