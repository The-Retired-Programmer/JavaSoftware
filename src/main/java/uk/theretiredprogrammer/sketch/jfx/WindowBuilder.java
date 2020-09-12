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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author richard
 */
public class WindowBuilder {
    
    private final ToolBar toolbar;
    private Node contentnode;
    private String title;
    private Node statusbar;

    public WindowBuilder() {
        toolbar = new ToolBar();
        statusbar = null;
    }
    
    public WindowBuilder addtoToolbar(Node ... nodes) {
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
    
    public Stage show(Stage stage) {
        VBox vbox = new VBox();
        vbox.getChildren().addAll(toolbar, contentnode);
        if (statusbar != null) {
            vbox.getChildren().add(statusbar);
        }
        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.show();
        return stage;
    }
    
    public Stage show() {
        return show(new Stage());
    }
}
