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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
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
    private String title;
    private Node contentnode;
    private Consumer<WindowEvent> closeaction;
    private Rectangle2D windowsize;
    
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
    
    private static final double MINWINDOWWIDTH = 50;
    
    void setDefaultWindow() {
        windowsize = Screen.getPrimary().getVisualBounds();
    }
    
    void setDefaultWindowLeftOffsetAndWidth(double leftoffset, double width) {
        Rectangle2D screenbounds = Screen.getPrimary().getVisualBounds();
        if (leftoffset + width > screenbounds.getWidth()) {
            if (width > screenbounds.getWidth()) {
                windowsize = screenbounds;
                return;
            }
            leftoffset = screenbounds.getWidth() - width;
        }
        windowsize = new Rectangle2D(
                screenbounds.getMinX() + leftoffset,
                screenbounds.getMinY(),
                width,
                screenbounds.getHeight()
        );
    }
    
    void setDefaultWindowLeftAndRightOffsets(double leftoffset, double rightoffset) {
        Rectangle2D screenbounds = Screen.getPrimary().getVisualBounds();
        double offset = leftoffset + rightoffset;
        if (offset + MINWINDOWWIDTH > screenbounds.getWidth()) {
            if (MINWINDOWWIDTH > screenbounds.getWidth()) {
                windowsize = screenbounds;
                return;
            }
            double delta = screenbounds.getWidth() - (leftoffset + rightoffset);
            leftoffset -= delta * leftoffset / offset;
            rightoffset -= delta * rightoffset / offset;
        }
        windowsize = new Rectangle2D(
                screenbounds.getMinX() + leftoffset,
                screenbounds.getMinY(),
                screenbounds.getWidth() - leftoffset - rightoffset,
                screenbounds.getHeight()
        );
    }
    
    void setDefaultWindowWidth(double width) {
        setDefaultWindowLeftOffsetAndWidth(0, width);
    }
    
    void setDefaultWindowLeftOffset(double leftoffset) {
        Rectangle2D screenbounds = Screen.getPrimary().getVisualBounds();
        if (leftoffset + MINWINDOWWIDTH > screenbounds.getWidth()) {
            if (MINWINDOWWIDTH > screenbounds.getWidth()) {
                windowsize = screenbounds;
                return;
            }
            leftoffset = screenbounds.getWidth() - MINWINDOWWIDTH;
        }
        windowsize = new Rectangle2D(
                screenbounds.getMinX() + leftoffset,
                screenbounds.getMinY(),
                screenbounds.getWidth() - leftoffset,
                screenbounds.getHeight()
        );
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
        SketchPreferences.applyWindowSizePreferences(stage, clazz, windowsize);
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
    
    void resetWindows() {
        SketchPreferences.clearWindowSizePreferences(clazz);
        SketchPreferences.applyWindowSizePreferences(stage, clazz, windowsize);
        childwindows.forEach(window -> window.resetWindows());
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
