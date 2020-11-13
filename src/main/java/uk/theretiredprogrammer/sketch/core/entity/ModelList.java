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
import jakarta.json.JsonValue;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ModelList<P extends Model> implements Model {

    private Runnable onchange;
    private Consumer<P> setChildListener;

    protected abstract P createAndParse(JsonValue jobj);

    private final ObservableList<P> list = FXCollections.observableArrayList();

    @Override
    public final void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            ((JsonArray) jvalue).forEach(jval -> add(createAndParse(jval)));
        }
    }
    
    @Override
    public final void setOnChange(Runnable onchange) {
        this.onchange = onchange;
        list.forEach(member -> member.setOnChange(onchange));
    }

    public void addListChangeListener(ListChangeListener<P> listener) {
        list.addListener(listener);
    }

    // mark-> mark.addNameChangeListern(childlistener)
    public void addChildChangeListener(Consumer<P> setChildListener) {
        this.setChildListener = setChildListener;
        list.forEach(p -> setChildListener.accept(p));
    }

    @Override
    public final JsonArray toJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        list.stream().forEach(p -> jab.add(p.toJson()));
        return jab.build();
    }

    public Stream<P> stream() {
        return list.stream();
    }

    public void add(P value) {
        list.add(value);
        if (onchange != null) {
            value.setOnChange(onchange);
        }
        if (setChildListener != null) {
            setChildListener.accept(value);
        }
    }

    public void clear() {
        list.clear();
    }
}
