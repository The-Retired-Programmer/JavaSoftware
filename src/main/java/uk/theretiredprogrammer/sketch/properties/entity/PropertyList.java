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
package uk.theretiredprogrammer.sketch.properties.entity;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author richard
 * @param <P>
 */
public abstract class PropertyList<P extends PropertyAny> extends PropertyAny {

    final ObservableList<P> propertylist;

    public PropertyList() {
        propertylist = FXCollections.observableArrayList();
    }
    
    public void setOnChange(Runnable onchange) {
        propertylist.addListener((ListChangeListener<P>) (c) -> onchange.run());
    }
    
    public void setOnChange(ListChangeListener<P> ml) {
        propertylist.addListener(ml);
    }

    public final Stream<P> stream() {
        return propertylist.stream();
    }

    public final ObservableList<P> getList() {
        return propertylist;
    }

    public void add(P property) {
        propertylist.add(property);
    }

    public void remove(P property) {
        propertylist.remove(property);
    }

    @Override
    public final JsonArray toJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        propertylist.stream().forEach(p -> jab.add(p.toJson()));
        return jab.build();
    }
}
