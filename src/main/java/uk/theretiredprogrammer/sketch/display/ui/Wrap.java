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

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;

/**
 *
 * @author richard
 */
public class Wrap {
    
    public static Node contextMenu(Node element, ContextMenu contextmenu){
        // standard configuration
        element.setCursor(Cursor.CROSSHAIR);
        element.setOnContextMenuRequested(ev -> contextmenuhandler(ev, element, contextmenu));
        return element;
    }
    
    public static  Node[] contextMenu(Node[] elements, ContextMenu contextmenu){
        for (var element: elements){
            contextMenu(element, contextmenu);
        }
        return elements;
    }
    
    private static void contextmenuhandler(ContextMenuEvent event, Node element, ContextMenu contextmenu) {
        contextmenu.show(element, event.getScreenX(), event.getScreenY());
    }
    
}
