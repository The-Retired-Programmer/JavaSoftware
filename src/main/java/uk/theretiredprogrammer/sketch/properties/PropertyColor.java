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
package uk.theretiredprogrammer.sketch.properties;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyColor extends PropertyItem {

    private Color colour;

    public final Color get() {
        return colour;
    }

    public final void set(Color colour) {
        this.colour = colour;
    }

    public final Color getValue() {
        return colour;
    }

    public final void setValue(Color colour) {
        this.colour = colour;
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        ColorPicker picker = new ColorPicker();
        picker.setValue(colour);
        picker.setOnAction(actionEvent -> {
            set(picker.getValue());
        });
        return picker;
    }

    @Override
    public JsonValue toJson() {
        return Json.createValue(ColorParser.color2String(colour));
    }
}
