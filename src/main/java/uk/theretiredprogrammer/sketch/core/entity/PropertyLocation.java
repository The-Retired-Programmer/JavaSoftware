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

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.ui.FieldBuilder;

public class PropertyLocation extends SimpleObjectProperty<Location> implements ModelProperty<Location> {

    public PropertyLocation(Location defaultvalue) {
        set(defaultvalue);
    }

    @Override
    public void setOnChange(Runnable onchange) {
    }

    @Override
    public Location parsevalue(JsonValue jvalue) {
        return ParseHelper.locationParse(jvalue);
    }

    @Override
    public JsonArray toJson() {
        return ParseHelper.locationToJson(get());
    }

    @Override
    public Node getField() {
        return getField(7);
    }

    @Override
    public Node getField(int size) {
        return FieldBuilder.getLocationField(size, this);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
