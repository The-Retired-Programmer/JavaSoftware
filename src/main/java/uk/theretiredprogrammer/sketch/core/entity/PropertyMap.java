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
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

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
        setOnChange((c) -> onchange.run());
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
            configs.values().forEach(config -> config.parse((JsonObject) jvalue));
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

    public class PropertyConfig<P extends PropertyAny, C extends Object> {

        public static final boolean MANDATORY = true;
        public static final boolean OPTIONAL = false;

        private final String key;
        private final boolean mandatoryparse;
        private final Function<String, P> creator;

        public PropertyConfig(String key, boolean mandatoryparse, Function<String, P> creator) {
            this.key = key;
            this.mandatoryparse = mandatoryparse;
            this.creator = creator;
        }

        public String getKey() {
            return key;
        }

        public P create() {
            return creator.apply(key);
        }

        public void parse(JsonObject parentobj) {
            if (mandatoryparse) {
                parseMandatoryProperty(key, parentobj);
            } else {
                parseOptionalProperty(key, parentobj);
            }
        }

        public final P getProperty(String messageleader) {
            P property = (P) propertymap.get(key);
            if (property == null) {
                throw new IllegalStateFailure(messageleader + " - missing key Property");
            }
            return property;
        }

        public P getProperty() {
            return getProperty("PropertyConfig - getProperty");
        }

        public C get(String messageleader) {
            P property = getProperty(messageleader);
            var val = property.get();
            if (val == null) {
                throw new IllegalStateFailure(messageleader + " - null value");
            }
            return (C) val;
        }

        public C get() {
            return get("PropertyConfig - get");
        }

        private void parseMandatoryProperty(String key, JsonObject parentobj) {
            if (!parseOptionalProperty(key, parentobj)) {
                throw new ParseFailure("Missing a Mandatory parameter: " + key);
            }
        }

        private boolean parseOptionalProperty(String key, JsonObject parentobj) {
            PropertyAny property = propertymap.get(key);
            if (property == null) {
                throw new IllegalStateFailure("Config parse - missing initialised Config");
            }
            JsonValue jvalue = parentobj.get(key);
            if (jvalue == null) {
                return false;
            }
            property.parse(jvalue);
            return true;
        }
    }
}
