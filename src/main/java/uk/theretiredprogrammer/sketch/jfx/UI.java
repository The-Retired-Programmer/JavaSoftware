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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author richard
 */
public class UI {

    public static Button toolbarButton(String buttontext, EventHandler<ActionEvent> action) {
        Button button = new Button(buttontext);
        button.setDisable(false);
        button.setOnAction(action);
        return button;
    }

    public static Button toolbarButton(String imagename, String tooltip, EventHandler<ActionEvent> action) {
        Button button = new Button("", image(imagename));
        button.setDisable(false);
        button.setOnAction(action);
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    public static MenuItem menuitem(String itemtext, EventHandler<ActionEvent> action) {
        MenuItem menuitem = new MenuItem(itemtext);
        menuitem.setOnAction(action);
        return menuitem;
    }

    public static ContextMenu contextmenu(MenuItem... items) {
        ContextMenu contextmenu = new ContextMenu();
        contextmenu.getItems().addAll(items);
        return contextmenu;
    }

    public static ImageView image(String name) {
        return new ImageView(new Image(UI.class.getResourceAsStream(name)));
    }
}
