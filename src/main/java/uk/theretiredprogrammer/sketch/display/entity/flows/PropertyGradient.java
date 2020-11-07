/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.ParseHelper;
import uk.theretiredprogrammer.sketch.core.ui.FieldBuilder;

public class PropertyGradient extends SimpleObjectProperty<Gradient> implements ModelProperty<Gradient> {

    private static final ObservableList<String> types = FXCollections.observableArrayList();

    static {
        types.addAll("north", "south", "east", "west");
    }

    public PropertyGradient(Gradient defaultvalue) {
        set(defaultvalue);
    }

    @Override
    public void setOnChange(Runnable onchange) {
    }

    @Override
    public Gradient parsevalue(JsonValue value) {
        return ParseHelper.gradientParse(value);
    }

    @Override
    public JsonValue toJson() {
        return ParseHelper.gradientToJson(get());
    }

    @Override
    public Node getField() {
        return getField(7);
    }

    @Override
    public Node getField(int size) {
        return FieldBuilder.getGradientField(size, this, types);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
