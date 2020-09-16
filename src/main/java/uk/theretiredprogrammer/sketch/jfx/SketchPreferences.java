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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
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

    static void applyWindowSizePreferences(Stage stage, Class clazz, int x, int y, int w, int h) {
        String windowname = clazz.getSimpleName();
        try {
            Preferences packagePreferences = Preferences.userNodeForPackage(clazz);
            if (packagePreferences.nodeExists(windowname)) {
                Preferences stagePreferences = packagePreferences.node(windowname);
                boolean wasMaximized = stagePreferences.getBoolean(WINDOW_MAXIMIZED, false);
                if (wasMaximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setX(stagePreferences.getDouble(WINDOW_X_POS, x));
                    stage.setY(stagePreferences.getDouble(WINDOW_Y_POS, y));
                    stage.setWidth(stagePreferences.getDouble(WINDOW_WIDTH, w));
                    stage.setHeight(stagePreferences.getDouble(WINDOW_HEIGHT, h));
                }
            } else {
                stage.setX(x);
                stage.setY(y);
                stage.setWidth(w);
                stage.setHeight(h);
            }
        } catch (BackingStoreException ex) {
            System.out.println("Could not access preferences for window " + windowname + "\n" + ex.getLocalizedMessage());
        }
    }

    static void saveWindowSizePreferences(Stage stage, Class clazz) {
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

    // recent File List
    static List<Path> getRecentFileList(Class clazz) {
        List<Path> result = new ArrayList<>();
        try {
            Preferences packagePreferences = Preferences.userNodeForPackage(clazz);
            if (packagePreferences.nodeExists("RecentFileList")) {
                Preferences recentPreferences = packagePreferences.node("RecentFileList");
                for (int index = 0; index < 10; index++) {
                    String path = recentPreferences.get(Integer.toString(index), "DOES/NOT/EXIST");
                    if (!path.equals("DOES/NOT/EXIST")) {
                        result.add(index, Path.of(path));
                    }
                }
            }
        } catch (BackingStoreException ex) {
            System.out.println("Could not access preferences for RecentFileList\n" + ex.getLocalizedMessage());
        }
        return result;
    }

    static void saveRecentFileList(List<Path> recents, Class clazz) {
        try {
            Preferences recentPreferences = Preferences.userNodeForPackage(clazz).node("RecentFileList");
            recentPreferences.clear();
            List<Path> recent10 = recents.subList(0, (recents.size() > 10 ? 10 : recents.size()));
            int index = 0;
            for (Path path : recent10) {
                recentPreferences.put(Integer.toString(index), path.toString());
                index++;
            }
            recentPreferences.flush();
        } catch (final BackingStoreException ex) {
            System.out.println("Could not flush preferences for RecentFileList\n" + ex.getLocalizedMessage());
        }
    }

}
