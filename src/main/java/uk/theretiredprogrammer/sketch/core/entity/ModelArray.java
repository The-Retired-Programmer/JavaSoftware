/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public abstract class ModelArray<P extends Model> implements Model {

    private final ObservableList<P> list = FXCollections.observableArrayList();

    protected abstract P createAndParse(JsonObject jobj);

    @Override
    public final void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            ((JsonArray) jvalue).forEach(jval -> {
                if (jval.getValueType() != JsonValue.ValueType.OBJECT) {
                    throw new ParseFailure("Malformed Definition File - array contains items other than Objects");
                }
                list.add(createAndParse((JsonObject) jval));
            });
        }
    }

    @Override
    public final void setOnChange(Runnable onchange) {
        list.forEach(member -> member.setOnChange(onchange));
    }

    public final void setOnChange(ListChangeListener<P> listener) {
        list.addListener(listener);
    }

    @Override
    public final JsonArray toJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        list.stream().forEach(p -> jab.add(p.toJson()));
        return jab.build();
    }

    public final ObservableList<P> getProperties() {
        return list;
    }

    public abstract P get(String name);

    public final void add(P item) {
        list.add(item);
    }
}
