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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author richard
 */
public abstract class AbstractWindow {

    private AbstractWindow parentwindow = null;
    private final List<AbstractWindow> childwindows = new ArrayList<>();

    private Stage stage = new Stage();
    private final Class clazz;
    private final ToolBar toolbar = new ToolBar();
    ;
    private final Text statusbar = new Text("");
    private int x = 100;
    private int y = 100;
    private int w = 500;
    private int h = 500;
    private String title;
    private Node contentnode;
    private Consumer<WindowEvent> closeaction;

    AbstractWindow(Class clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    AbstractWindow(Class clazz, AbstractWindow parent) {
        this(clazz);
        parentwindow = parent;
        parent.childwindows.add(this);
    }

    AbstractWindow(Class clazz, Stage stage) {
        this(clazz);
        this.stage = stage;
    }

    void setDefaultWindowSize(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void addtoToolbar(Node... nodes) {
        toolbar.getItems().addAll(nodes);
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setContent(Node contentnode) {
        this.contentnode = contentnode;
    }

    void setScrollableContent(Node contentnode) {
        this.contentnode = new ScrollPane(contentnode);
    }

    void setOnCloseAction(Consumer<WindowEvent> closeaction) {
        this.closeaction = closeaction;
    }

    Stage show() {
        BorderPane borderpane = new BorderPane();
        borderpane.setTop(toolbar);
        borderpane.setCenter(contentnode);
        borderpane.setBottom(statusbar);
        Scene scene = new Scene(borderpane);
//        VBox vbox = new VBox();
//        vbox.getChildren().addAll(toolbar, contentnode, statusbar);
//        Scene scene = new Scene(vbox);
        SketchPreferences.applyWindowSizePreferences(stage, clazz, x, y, w, h);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        if (closeaction != null) {
            stage.setOnCloseRequest(e -> {
                closeaction.accept(e);
                closeRequest();
            });
        }
        stage.setOnHiding(e -> {
            SketchPreferences.saveWindowSizePreferences(stage, clazz);
            closeChildren();
        });
        stage.show();
        return stage;
    }
    
    void statusbarDisplay(String message) {
        statusbar.setText(message);
    }
    
    void statusbarClear() {
        statusbar.setText("");
    }
    
    private void closeChildren() {
        childwindows.forEach(window -> {
            window.stage.close();
        });
    }

    private void closeRequest() {
        if (parentwindow != null) {
            parentwindow.childwindows.remove(this);
        }
    }

    Button toolbarButton(String buttontext, EventHandler<ActionEvent> action) {
        Button button = new Button(buttontext);
        button.setDisable(false);
        button.setOnAction(action);
        return button;
    }
}
