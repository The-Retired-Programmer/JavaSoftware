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
package uk.theretiredprogrammer.sketch.core.entity;

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.ui.FieldBuilder;

public class PropertyString extends SimpleStringProperty implements ModelProperty<String> {

    public PropertyString(String value) {
        set(value);
    }

    @Override
    public void setOnChange(Runnable onchange) {
        //setOnChange((c) -> onchange.run());
    }

    @Override
    public final String parsevalue(JsonValue value) {
        return ParseHelper.stringParse(value);
    }

    @Override
    public JsonValue toJson() {
        return ParseHelper.stringToJson(get());
    }

    @Override
    public Node getField() {
        return getField(0);
    }

    @Override
    public Node getField(int size) {
        return FieldBuilder.getStringField(this);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
