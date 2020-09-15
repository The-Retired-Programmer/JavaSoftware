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

import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author richard
 */
public class WindowBuilder {
    
    private final ToolBar toolbar;
    private Node contentnode;
    private String title;
    private Node statusbar;
    private final Class clazz;
    private int x = 100;
    private int y = 100;
    private int w = 500;
    private int h = 500;
    private Consumer<WindowEvent> closeaction;
    
    public WindowBuilder(Class clazz) {
        this.clazz = clazz;
        toolbar = new ToolBar();
        statusbar = null;
    }
    
    public WindowBuilder setDefaultWindowSize(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    
    public WindowBuilder addtoToolbar(Node... nodes) {
        toolbar.getItems().addAll(nodes);
        return this;
    }
    
    public WindowBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public WindowBuilder setContent(Node contentnode) {
        this.contentnode = contentnode;
        return this;
    }
    
    public WindowBuilder setScrollableContent(Node contentnode) {
        this.contentnode = new ScrollPane(contentnode);
        return this;
    }
    
    public WindowBuilder setStatusbar(Node statusbar) {
        this.statusbar = statusbar;
        return this;
    }
    
   public WindowBuilder setOnCloseAction(Consumer<WindowEvent> closeaction) {
       this.closeaction = closeaction;
       return this;
   }
    
    public Stage show(Stage stage) {
        VBox vbox = new VBox();
        vbox.getChildren().addAll(toolbar, contentnode);
        if (statusbar != null) {
            vbox.getChildren().add(statusbar);
        }
        Scene scene = new Scene(vbox);
        applyWindowSizePreferences(stage, clazz, x, y, w, h);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.setOnCloseRequest(e -> {
                    saveWindowSizePreferences(stage, clazz);
                    if (closeaction != null) {
                        closeaction.accept(e);
                    }
                });
        stage.show();
        return stage;
    }
    
    private static final String WINDOW_WIDTH = "windowWidth";
    private static final String WINDOW_HEIGHT = "windowHeight";
    private static final String WINDOW_X_POS = "windowXPos";
    private static final String WINDOW_Y_POS = "windowYPos";
    private static final String WINDOW_MAXIMIZED = "windowMaximized";

    private void applyWindowSizePreferences(Stage stage, Class clazz, int x, int y, int w, int h) {
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

    private void saveWindowSizePreferences(Stage stage, Class clazz) {
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
}
