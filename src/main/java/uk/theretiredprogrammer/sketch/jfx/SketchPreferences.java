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

import uk.theretiredprogrammer.sketch.jfx.fileselectordisplay.PathWithShortName;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

/**
 *
 * @author richard
 */
public class SketchPreferences {

    // window sizes
    private static final String WINDOW_WIDTH = "windowWidth";
    private static final String WINDOW_HEIGHT = "windowHeight";
    private static final String WINDOW_X_POS = "windowXPos";
    private static final String WINDOW_Y_POS = "windowYPos";
    private static final String WINDOW_MAXIMIZED = "windowMaximized";

    public static void applyWindowSizePreferences(Stage stage, Class clazz, Rectangle2D windowsize) {
        String windowname = clazz.getSimpleName();
        try {
            Preferences packagePreferences = Preferences.userNodeForPackage(clazz);
            if (packagePreferences.nodeExists(windowname)) {
                Preferences stagePreferences = packagePreferences.node(windowname);
                boolean wasMaximized = stagePreferences.getBoolean(WINDOW_MAXIMIZED, false);
                if (wasMaximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setX(stagePreferences.getDouble(WINDOW_X_POS, windowsize.getMinX()));
                    stage.setY(stagePreferences.getDouble(WINDOW_Y_POS, windowsize.getMinY()));
                    stage.setWidth(stagePreferences.getDouble(WINDOW_WIDTH, windowsize.getWidth()));
                    stage.setHeight(stagePreferences.getDouble(WINDOW_HEIGHT, windowsize.getHeight()));
                }
            } else {
                stage.setX(windowsize.getMinX());
                stage.setY(windowsize.getMinY());
                stage.setWidth(windowsize.getWidth());
                stage.setHeight(windowsize.getHeight());
            }
        } catch (BackingStoreException ex) {
            System.out.println("Could not access preferences for window " + windowname + "\n" + ex.getLocalizedMessage());
        }
    }

    public static void saveWindowSizePreferences(Stage stage, Class clazz) {
        String windowname = clazz.getSimpleName();
        try {
            Preferences stagePreferences = Preferences.userNodeForPackage(clazz).node(windowname);
            if (stage.isMaximized()) {
                stagePreferences.putBoolean(WINDOW_MAXIMIZED, true);
            } else {
                stagePreferences.putBoolean(WINDOW_MAXIMIZED, false);
                stagePreferences.putDouble(WINDOW_X_POS, stage.getX());
                stagePreferences.putDouble(WINDOW_Y_POS, stage.getY());
                stagePreferences.putDouble(WINDOW_WIDTH, stage.getWidth());
                stagePreferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
            }
            stagePreferences.flush();
        } catch (final BackingStoreException ex) {
            System.out.println("Could not flush preferences for window " + windowname + "\n" + ex.getLocalizedMessage());
        }
    }
    
    public static void clearWindowSizePreferences(Class clazz) {
        String windowname = clazz.getSimpleName();
        try {
            Preferences stagePreferences = Preferences.userNodeForPackage(clazz).node(windowname);
            stagePreferences.removeNode();
            stagePreferences.flush();
        } catch (final BackingStoreException ex) {
            System.out.println("Could not flush preferences for window " + windowname + "\n" + ex.getLocalizedMessage());
        }
    }

    // recent File List
    public static ObservableList<PathWithShortName> getRecentFileList(Class clazz) {
        ObservableList<PathWithShortName> result = FXCollections.observableArrayList();
        try {
            Preferences packagePreferences = Preferences.userNodeForPackage(clazz);
            if (packagePreferences.nodeExists("RecentFileList")) {
                Preferences recentsPreferences = packagePreferences.node("RecentFileList");
                int count = recentsPreferences.keys().length;
                for (int index = 0; index < count; index++) {
                    String path = recentsPreferences.get(String.format("%03d", index), "DOES/NOT/EXIST");
                    if (!path.equals("DOES/NOT/EXIST")) {
                        result.add(index, new PathWithShortName(Path.of(path)));
                    }
                }
            }
        } catch (BackingStoreException ex) {
            System.out.println("Could not access preferences for RecentFileList\n" + ex.getLocalizedMessage());
        }
        return result;
    }

    public static void saveRecentFileList(ObservableList<PathWithShortName> recents, Class clazz) {
        try {
            Preferences recentPreferences = Preferences.userNodeForPackage(clazz).node("RecentFileList");
            recentPreferences.clear();
            int index = 0;
            for (PathWithShortName pn : recents) {
                recentPreferences.put(String.format("%03d", index), pn.getPath().toString());
                index++;
            }
            recentPreferences.flush();
        } catch (final BackingStoreException ex) {
            System.out.println("Could not flush preferences for RecentFileList\n" + ex.getLocalizedMessage());
        }
    }
    
    // recent Folders List
    public static ObservableList<PathWithShortName> getFoldersList(Class clazz) {
        ObservableList<PathWithShortName> result = FXCollections.observableArrayList();
        try {
            Preferences packagePreferences = Preferences.userNodeForPackage(clazz);
            if (packagePreferences.nodeExists("FoldersList")) {
                Preferences foldersPreferences = packagePreferences.node("FoldersList");
                int count = foldersPreferences.keys().length;
                for (int index = 0; index < count; index++) {
                    String path = foldersPreferences.get(String.format("%03d", index), "DOES/NOT/EXIST");
                    if (!path.equals("DOES/NOT/EXIST")) {
                        result.add(new PathWithShortName(Path.of(path)));
                    }
                }
            }
        } catch (BackingStoreException ex) {
            System.out.println("Could not access preferences for FoldersList\n" + ex.getLocalizedMessage());
        }
        return result;
    }

    public static void saveFoldersList(ObservableList<PathWithShortName> folders, Class clazz) {
        try {
            Preferences foldersPreferences = Preferences.userNodeForPackage(clazz).node("FoldersList");
            foldersPreferences.clear();
            int index = 0;
            for (PathWithShortName pn : folders) {
                foldersPreferences.put(String.format("%03d", index), pn.getPath().toString());
                index++;
            }
            foldersPreferences.flush();
        } catch (final BackingStoreException ex) {
            System.out.println("Could not flush preferences for FoldersList\n" + ex.getLocalizedMessage());
        }
    }
}
