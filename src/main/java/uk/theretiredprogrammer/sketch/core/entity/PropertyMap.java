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
package uk.theretiredprogrammer.sketch.core.entity;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author richard
 */
public abstract class PropertyMap extends PropertyAny {

    public final ObservableMap<String, PropertyAny> propertymap;

    private final Map<String, PropertyConfig> configs = new LinkedHashMap<>();

    public PropertyMap() {
        propertymap = FXCollections.observableMap(new LinkedHashMap<>());
    }

    public void setOnChange(Runnable onchange) {
        propertymap.addListener((MapChangeListener<String, PropertyAny>) (c) -> onchange.run());
    }

    public void setOnChange(MapChangeListener<String, PropertyAny> ml) {
        propertymap.addListener(ml);
    }

    public final void addConfig(PropertyConfig... configvalues) {
        for (PropertyConfig config : configvalues) {
            configs.put(config.getKey(), config);
            add(config.create());
        }
    }

    @Override
    public void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.OBJECT) {
            for (PropertyConfig config : configs.values()) {
                config.parse(this, (JsonObject) jvalue);
            }
        }
    }

    public Stream<Entry<String, PropertyAny>> stream() {
        return propertymap.entrySet().stream();
    }

    public Stream<PropertyAny> streamvalues() {
        return propertymap.values().stream();
    }

    public Map<String, PropertyAny> getMap() {
        return propertymap;
    }

    public void add(PropertyAny property) {
        propertymap.put(property.getKey(), property);
    }

    public void remove(String name) {
        propertymap.remove(name);
    }

    @Override
    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        propertymap.entrySet().forEach(e -> job.add(e.getKey(), e.getValue().toJson()));
        return job.build();
    }
}
