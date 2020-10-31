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
package uk.theretiredprogrammer.sketch.display.ui;

import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.transform.Transform;
import uk.theretiredprogrammer.sketch.core.ui.DisplayContextMenu;

/**
 *
 * @author richard
 */
public class Wrap {

    public static Node contextMenu(Node element, ContextMenu contextmenu, Cursor selectioncursor) {
        element.setCursor(selectioncursor);
        element.setOnContextMenuRequested(ev -> contextmenuhandler(ev, element, contextmenu));
        return element;
    }

    public static Node[] contextMenu(Node[] elements, ContextMenu contextmenu, Cursor selectioncursor) {
        for (var element : elements) {
            contextMenu(element, contextmenu, selectioncursor);
        }
        return elements;
    }

    public static Node contextMenu(Node element, ContextMenu contextmenu) {
        element.setOnContextMenuRequested(ev -> contextmenuhandler(ev, element, contextmenu));
        return element;
    }

    public static Node[] contextMenu(Node[] elements, ContextMenu contextmenu) {
        for (var element : elements) {
            contextMenu(element, contextmenu);
        }
        return elements;
    }

    private static void contextmenuhandler(ContextMenuEvent event, Node element, ContextMenu contextmenu) {
        contextmenu.show(element, event.getScreenX(), event.getScreenY());
    }

    public static Node displayContextMenu(Node element, DisplayContextMenu contextmenu, Cursor selectioncursor) {
        element.setCursor(selectioncursor);
        element.setOnContextMenuRequested(ev -> displaycontextmenuhandler(ev, element, contextmenu));
        return element;
    }

    public static Node[] displayContextMenu(Node[] elements, DisplayContextMenu contextmenu, Cursor selectioncursor) {
        for (var element : elements) {
            contextMenu(element, contextmenu, selectioncursor);
        }
        return elements;
    }

    public static Node displayContextMenu(Node element, DisplayContextMenu contextmenu) {
        element.setOnContextMenuRequested(ev -> displaycontextmenuhandler(ev, element, contextmenu));
        return element;
    }

    public static Node[] displayContextMenu(Node[] elements, DisplayContextMenu contextmenu) {
        for (var element : elements) {
            displayContextMenu(element, contextmenu);
        }
        return elements;
    }

    private static void displaycontextmenuhandler(ContextMenuEvent event, Node element, DisplayContextMenu contextmenu) {
        contextmenu.location(event.getX(), event.getY());
        contextmenu.show(element, event.getScreenX(), event.getScreenY());
    }

    public static Node globalTransform(Node element, Transform... transforms) {
        element.getTransforms().addAll(0, List.of(transforms));
        return element;
    }

    public static Node[] globalTransform(Node[] elements, Transform... transforms) {
        for (var element : elements) {
            Wrap.globalTransform(element, transforms);
        }
        return elements;
    }
}
