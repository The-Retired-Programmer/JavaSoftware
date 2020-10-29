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
package uk.theretiredprogrammer.sketch.core.ui;

import java.util.function.BiConsumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.theretiredprogrammer.sketch.core.control.ExecuteAndCatch;

/**
 *
 * @author richard
 */
public class UI {

    public static Button toolbarButton(String imagename, String tooltip, EventHandler<ActionEvent> action) {
        Button button = new Button("", image(imagename));
        button.setDisable(false);
        button.setOnAction((e) -> new ExecuteAndCatch(() -> action.handle(e)));
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    public static Menu menu(String name, MenuItem... menuitems) {
        Menu menu = new Menu(name);
        menu.getItems().addAll(menuitems);
        return menu;
    }

    public static MenuItem menuitem(String itemtext, EventHandler<ActionEvent> action) {
        MenuItem menuitem = new MenuItem(itemtext);
        menuitem.setOnAction((e) -> new ExecuteAndCatch(() -> action.handle(e)));
        return menuitem;
    }

    public static MenuItem menuitem(String itemtext) {
        return menuitem(itemtext, (e) -> {
        });
    }

    public static ImageView image(String name) {
        return new ImageView(new Image(UI.class.getResourceAsStream(name)));
    }

    public static ContextMenu contextMenu(MenuItem... menuitems) {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(menuitems);
        return menu;
    }
    
    public static DisplayContextMenu displayContextMenu(MenuItem... menuitems) {
        DisplayContextMenu menu = new DisplayContextMenu();
        menu.getItems().addAll(menuitems);
        return menu;
    }
    
    public static MenuItem contextMenuitem(String itemtext, BiConsumer<ActionEvent, ContextMenu> action) {
        MenuItem menuitem = new MenuItem(itemtext);
        menuitem.setOnAction((e) -> new ExecuteAndCatch(() -> action.accept(e,menuitem.getParentPopup())));
        return menuitem;
    }
}
