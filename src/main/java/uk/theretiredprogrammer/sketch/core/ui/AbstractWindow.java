/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.core.ui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.core.control.ExecuteAndCatch;
import uk.theretiredprogrammer.sketch.core.control.SketchPreferences;

public abstract class AbstractWindow<C extends AbstractController> {

    public static final boolean SCROLLABLE = true;

    private final Stage stage;
    private final Class clazz;
    private final MenuBar menubar = new MenuBar();
    private final ToolBar toolbar = new ToolBar();
    private final Text statusbar = new Text("");
    private final HBox hcontrols = new HBox();
    private final VBox vcontrols = new VBox();
    private String title;
    private Node contentnode;
    private Rectangle2D windowsize;
    private boolean scrollable = false;
    private C controller;
    private final ContextMenu contextmenu = new ContextMenu();

    public AbstractWindow(Class clazz, Stage stage, C controller) {
        this.clazz = clazz;
        this.stage = stage;
        this.controller = controller;
    }

    public AbstractWindow(Class clazz, C controller) {
        this(clazz, new Stage(), controller);
    }

    protected C getController() {
        return controller;
    }

    public void close() {
        stage.close();
    }

    public void saveWindowSizePreferences() {
        SketchPreferences.saveWindowSizePreferences(stage, clazz);
    }

    private static final double MINWINDOWWIDTH = 50;

    public final void setDefaultWindow() {
        windowsize = Screen.getPrimary().getVisualBounds();
    }

    public final void setDefaultWindowLeftOffsetAndWidth(double leftoffset, double width) {
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

    public final void setDefaultWindowLeftAndRightOffsets(double leftoffset, double rightoffset) {
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

    public final void setDefaultWindowWidth(double width) {
        setDefaultWindowLeftOffsetAndWidth(0, width);
    }

    public final void setDefaultWindowLeftOffset(double leftoffset) {
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

    public final void addtoMenubar(Menu... nodes) {
        menubar.getMenus().addAll(nodes);
    }

    public final void addtoToolbar(Node... nodes) {
        toolbar.getItems().addAll(nodes);
    }

    public final void addToHControlArea(Node... nodes) {
        hcontrols.getChildren().addAll(nodes);
    }

    public final void addToVControlArea(Node... nodes) {
        vcontrols.getChildren().addAll(nodes);
    }

    public final void addtoContextMenuIfScrollable(MenuItem... menuitems) {
        contextmenu.getItems().addAll(menuitems);
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final void setContent(Node contentnode) {
        this.contentnode = contentnode;
        this.scrollable = false;
    }

    public final void setContent(Node contentnode, boolean scrollable) {
        this.contentnode = contentnode;
        this.scrollable = scrollable;
    }

    public final void build() {
        BorderPane borderpane = new BorderPane();
        borderpane.setTop(new VBox(menubar, toolbar));
        Node centrenode = scrollable ? createScrollableNode(contentnode) : contentnode;
        borderpane.setCenter(centrenode);
        borderpane.setBottom(new VBox(hcontrols, statusbar));
        borderpane.setRight(vcontrols);
        Scene scene = new Scene(borderpane);
        SketchPreferences.applyWindowSizePreferences(stage, clazz, windowsize);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.setOnCloseRequest(e -> new ExecuteAndCatch(() -> controller.windowHasExternalCloseRequest(e)));
        stage.setOnHiding(e -> new ExecuteAndCatch(() -> controller.windowIsHiding(e)));
        stage.setOnHidden(e -> new ExecuteAndCatch(() -> controller.windowIsHidden(e)));
    }

    private Node createScrollableNode(Node contentnode) {
        ScrollPane pane = new ScrollPane(contentnode);
        pane.setContextMenu(contextmenu);
        return pane;
    }

    public void show() {
        stage.show();
    }

    public final void resetWindows() {
        SketchPreferences.clearWindowSizePreferences(clazz);
        SketchPreferences.applyWindowSizePreferences(stage, clazz, windowsize);
    }

    public final void writeToStatusbar(String message) {
        if (Platform.isFxApplicationThread()) {
            statusbar.setText(message);
        } else {
            Platform.runLater(() -> statusbar.setText(message));
        }
    }

    public final void clearStatusbar() {
        writeToStatusbar("");
    }
}
